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
import com.pyamsoft.powermanager.base.jobs.JobQueuer;
import com.pyamsoft.powermanager.model.BooleanInterestModifier;
import com.pyamsoft.powermanager.model.BooleanInterestObserver;
import com.pyamsoft.powermanager.model.JobQueuerEntry;
import com.pyamsoft.powermanager.model.Logger;
import com.pyamsoft.powermanager.model.QueuerType;
import rx.Observable;

abstract class ManagerInteractorImpl implements ManagerInteractor {

  @NonNull final BooleanInterestObserver manageObserver;
  @NonNull final BooleanInterestObserver stateObserver;
  @NonNull private final BooleanInterestModifier stateModifier;
  @NonNull private final PowerManagerPreferences preferences;
  @NonNull private final JobQueuer jobQueuer;
  @NonNull private final BooleanInterestObserver chargingObserver;
  @NonNull private final Logger logger;

  ManagerInteractorImpl(@NonNull JobQueuer jobQueuer, @NonNull PowerManagerPreferences preferences,
      @NonNull BooleanInterestObserver manageObserver,
      @NonNull BooleanInterestObserver stateObserver,
      @NonNull BooleanInterestModifier stateModifier,
      @NonNull BooleanInterestObserver chargingObserver, @NonNull Logger logger) {
    this.jobQueuer = jobQueuer;
    this.stateObserver = stateObserver;
    this.manageObserver = manageObserver;
    this.preferences = preferences;
    this.stateModifier = stateModifier;
    this.chargingObserver = chargingObserver;
    this.logger = logger;
  }

  @Override public void destroy() {
    jobQueuer.cancel(getJobTag(), JobQueuer.ALL_JOB_TAG);
  }

  @Override @NonNull @CheckResult public Observable<Boolean> cancelJobs() {
    return Observable.fromCallable(() -> {
      destroy();
      return Boolean.TRUE;
    });
  }

  @WorkerThread @CallSuper @Override public void queueEnableJob() {
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
        .delay(100L)
        .repeating(false)
        .repeatingOffWindow(0L)
        .repeatingOnWindow(0L)
        .ignoreIfCharging(false)
        .logger(logger)
        .chargingObserver(chargingObserver)
        .observer(stateObserver)
        .modifier(stateModifier)
        .build());
  }

  @WorkerThread @CallSuper @Override public void queueDisableJob() {
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
        .logger(logger)
        .repeating(isPeriodic())
        .repeatingOffWindow(getPeriodicDisableTime())
        .repeatingOnWindow(getPeriodicEnableTime())
        .ignoreIfCharging(isIgnoreWhileCharging())
        .chargingObserver(chargingObserver)
        .observer(stateObserver)
        .modifier(stateModifier)
        .build());
  }

  @CallSuper @CheckResult @NonNull PowerManagerPreferences getPreferences() {
    return preferences;
  }

  @CallSuper @NonNull @Override public Observable<Boolean> isManaged() {
    return Observable.fromCallable(manageObserver::is);
  }

  @CallSuper @NonNull @Override public Observable<Boolean> isEnabled() {
    return Observable.fromCallable(stateObserver::is);
  }

  @CheckResult protected abstract long getDelayTime();

  @CheckResult protected abstract boolean isPeriodic();

  @CheckResult protected abstract long getPeriodicEnableTime();

  @CheckResult protected abstract long getPeriodicDisableTime();
}
