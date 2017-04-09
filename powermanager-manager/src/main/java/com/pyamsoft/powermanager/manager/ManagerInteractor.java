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
import com.pyamsoft.powermanager.job.JobQueuer;
import com.pyamsoft.powermanager.job.JobQueuerEntry;
import com.pyamsoft.powermanager.job.QueuerType;
import com.pyamsoft.powermanager.model.StateObserver;
import io.reactivex.Observable;
import timber.log.Timber;

abstract class ManagerInteractor {

  @SuppressWarnings("WeakerAccess") @NonNull final StateObserver stateObserver;
  @SuppressWarnings("WeakerAccess") @NonNull final JobQueuer jobQueuer;

  ManagerInteractor(@NonNull JobQueuer jobQueuer, @NonNull StateObserver stateObserver) {
    this.jobQueuer = jobQueuer;
    this.stateObserver = stateObserver;
  }

  public void destroy() {
    jobQueuer.cancel(getJobTag());
  }

  /**
   * public
   */
  @CheckResult @NonNull Observable<Boolean> queueSet() {
    return Observable.fromCallable(this::isManaged).flatMap(managed -> {
      if (managed) {
        Timber.d("%s: Is original state enabled?", getJobTag());
        return Observable.just(isOriginalStateEnabled());
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

  /**
   * public
   */
  @CheckResult @NonNull Observable<Boolean> queueUnset() {
    return Observable.fromCallable(this::isManaged).doOnNext(managed -> {
      Timber.d("%s: Unset original state", getJobTag());
      setOriginalStateEnabled(false);
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

  /**
   * public
   */
  @NonNull @CheckResult Observable<Boolean> cancelJobs() {
    return Observable.fromCallable(() -> {
      jobQueuer.cancel(getJobTag());
      return Boolean.TRUE;
    });
  }

  @SuppressWarnings("WeakerAccess") void queueEnableJob() {
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

  @SuppressWarnings("WeakerAccess") void queueDisableJob() {
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

  @CallSuper @NonNull Observable<Boolean> isEnabled() {
    return Observable.fromCallable(stateObserver::enabled);
  }

  @CheckResult abstract boolean isManaged();

  @CheckResult abstract boolean isOriginalStateEnabled();

  abstract void setOriginalStateEnabled(boolean enabled);

  @CheckResult abstract boolean isIgnoreWhileCharging();

  @CheckResult @NonNull abstract String getJobTag();

  @CheckResult abstract long getDelayTime();

  @CheckResult abstract boolean isPeriodic();

  @CheckResult abstract long getPeriodicEnableTime();

  @CheckResult abstract long getPeriodicDisableTime();

  @CheckResult @NonNull
  abstract Observable<Boolean> accountForWearableBeforeDisable(boolean originalState);
}
