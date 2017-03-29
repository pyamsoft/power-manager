/*
 * Copyright 2016 Peter Kenji Yamanaka
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.pyamsoft.powermanager.manager;

import android.support.annotation.CallSuper;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.annotation.WorkerThread;
import com.pyamsoft.powermanager.base.PowerManagerPreferences;
import com.pyamsoft.powermanager.job.JobQueuer;
import com.pyamsoft.powermanager.model.JobQueuerEntry;
import com.pyamsoft.powermanager.model.QueuerType;
import com.pyamsoft.powermanager.model.StateInterestObserver;
import io.reactivex.Observable;
import timber.log.Timber;

abstract class ManagerInteractor {

  @SuppressWarnings("WeakerAccess") @NonNull final StateInterestObserver manageObserver;
  @SuppressWarnings("WeakerAccess") @NonNull final StateInterestObserver stateObserver;
  @SuppressWarnings("WeakerAccess") @NonNull final JobQueuer jobQueuer;
  @NonNull private final PowerManagerPreferences preferences;

  ManagerInteractor(@NonNull JobQueuer jobQueuer, @NonNull PowerManagerPreferences preferences,
      @NonNull StateInterestObserver manageObserver, @NonNull StateInterestObserver stateObserver) {
    this.jobQueuer = jobQueuer;
    this.stateObserver = stateObserver;
    this.manageObserver = manageObserver;
    this.preferences = preferences;
  }

  public void destroy() {
    jobQueuer.cancel(getJobTag());
  }

  @CheckResult @NonNull public Observable<Boolean> queueSet() {
    return isManaged().flatMap(managed -> {
      if (managed) {
        Timber.d("%s: Is original state enabled?", getJobTag());
        return isOriginalStateEnabled();
      } else {
        Timber.w("%s: Is not managed, return empty", getJobTag());
        return Observable.empty();
      }
    }).doOnNext(originalState -> {
      if (originalState) {
        queueEnableJob();
        Timber.d("%s: Unset original state", getJobTag());
        setOriginalStateEnabled(false);
      }
    });
  }

  @CheckResult @NonNull public Observable<Boolean> queueUnset() {
    return isManaged().map(baseResult -> {
      Timber.d("%s: Unset original state", getJobTag());
      setOriginalStateEnabled(false);
      return baseResult;
    }).flatMap(managed -> {
      if (managed) {
        Timber.d("%s: Is original state enabled?", getJobTag());
        return isEnabled();
      } else {
        Timber.w("%s: Is not managed, return empty", getJobTag());
        return Observable.empty();
      }
    }).map(enabled -> {
      Timber.d("%s: Set original state enabled: %s", getJobTag(), enabled);
      setOriginalStateEnabled(enabled);
      return enabled;
    }).flatMap(this::accountForWearableBeforeDisable).doOnNext(shouldQueue -> {
      // Only queue a disable job if the radio is not ignored
      if (shouldQueue) {
        queueDisableJob();
      }
    });
  }

  @NonNull @CheckResult public Observable<Boolean> cancelJobs() {
    return Observable.fromCallable(() -> {
      jobQueuer.cancel(getJobTag());
      return Boolean.TRUE;
    });
  }

  @WorkerThread @CallSuper public void queueEnableJob() {
    final QueuerType queuerType;
    final String jobTag = getJobTag();
    switch (jobTag) {
      case JobQueuer.AIRPLANE_JOB_TAG:
        queuerType = QueuerType.SCREEN_ON_DISABLE;
        break;
      case JobQueuer.DOZE_JOB_TAG:
        queuerType = QueuerType.SCREEN_ON_DISABLE;
        break;
      default:
        queuerType = QueuerType.SCREEN_ON_ENABLE;
    }

    // Queue up an enable job
    jobQueuer.cancel(jobTag);
    jobQueuer.queue(JobQueuerEntry.builder(jobTag)
        .type(queuerType)
        .delay(0)
        .repeating(false)
        .repeatingOffWindow(0L)
        .repeatingOnWindow(0L)
        .ignoreIfCharging(false)
        .build());
  }

  @WorkerThread @CallSuper public void queueDisableJob() {
    final QueuerType queuerType;
    final String jobTag = getJobTag();
    switch (jobTag) {
      case JobQueuer.AIRPLANE_JOB_TAG:
        queuerType = QueuerType.SCREEN_OFF_ENABLE;
        break;
      case JobQueuer.DOZE_JOB_TAG:
        queuerType = QueuerType.SCREEN_OFF_ENABLE;
        break;
      default:
        queuerType = QueuerType.SCREEN_OFF_DISABLE;
    }

    // Queue up a disable job
    jobQueuer.cancel(jobTag);
    jobQueuer.queue(JobQueuerEntry.builder(jobTag)
        .type(queuerType)
        .delay(getDelayTime() * 1000L)
        .repeating(isPeriodic())
        .repeatingOffWindow(getPeriodicDisableTime())
        .repeatingOnWindow(getPeriodicEnableTime())
        .ignoreIfCharging(isIgnoreWhileCharging())
        .build());
  }

  @CallSuper @CheckResult @NonNull PowerManagerPreferences getPreferences() {
    return preferences;
  }

  @CallSuper @NonNull private Observable<Boolean> isManaged() {
    return Observable.fromCallable(manageObserver::is);
  }

  @CallSuper @NonNull Observable<Boolean> isEnabled() {
    return Observable.fromCallable(stateObserver::is);
  }

  @CheckResult @NonNull abstract Observable<Boolean> isOriginalStateEnabled();

  @CheckResult abstract boolean isIgnoreWhileCharging();

  @CheckResult @NonNull abstract String getJobTag();

  @CheckResult abstract long getDelayTime();

  @CheckResult abstract boolean isPeriodic();

  @CheckResult abstract long getPeriodicEnableTime();

  @CheckResult abstract long getPeriodicDisableTime();

  abstract void setOriginalStateEnabled(boolean enabled);

  @CheckResult @NonNull
  abstract Observable<Boolean> accountForWearableBeforeDisable(boolean originalState);
}
