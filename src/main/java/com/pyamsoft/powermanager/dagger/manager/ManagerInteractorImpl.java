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

package com.pyamsoft.powermanager.dagger.manager;

import android.support.annotation.CallSuper;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.annotation.WorkerThread;
import com.pyamsoft.powermanager.PowerManagerPreferences;
import com.pyamsoft.powermanager.app.observer.BooleanInterestObserver;
import com.pyamsoft.powermanager.dagger.queuer.Queuer;
import com.pyamsoft.powermanager.dagger.queuer.QueuerType;
import rx.Observable;

abstract class ManagerInteractorImpl implements ManagerInteractor {

  @SuppressWarnings("WeakerAccess") @NonNull final BooleanInterestObserver manageObserver;
  @SuppressWarnings("WeakerAccess") @NonNull final BooleanInterestObserver stateObserver;
  @SuppressWarnings("WeakerAccess") @NonNull final Queuer queuer;
  @NonNull private final PowerManagerPreferences preferences;

  ManagerInteractorImpl(@NonNull Queuer queuer, @NonNull PowerManagerPreferences preferences,
      @NonNull BooleanInterestObserver manageObserver,
      @NonNull BooleanInterestObserver stateObserver) {
    this.queuer = queuer;
    this.stateObserver = stateObserver;
    this.manageObserver = manageObserver;
    this.preferences = preferences;
  }

  @Override public void destroy() {
    queuer.cancel();
  }

  @Override @NonNull @CheckResult public Observable<Boolean> cancelJobs() {
    return Observable.defer(() -> {
      destroy();
      return Observable.just(true);
    });
  }

  @WorkerThread @CallSuper @Override public void queueEnableJob() {
    final QueuerType queuerType;
    switch (getJobTag()) {
      case ManagerInteractor.AIRPLANE_JOB_TAG:
        queuerType = QueuerType.SCREEN_ON_DISABLE;
        break;
      case ManagerInteractor.DOZE_JOB_TAG:
        queuerType = QueuerType.SCREEN_ON_DISABLE;
        break;
      default:
        queuerType = QueuerType.SCREEN_ON_ENABLE;
    }

    // Queue up an enable job
    queuer.cancel();
    queuer.setType(queuerType)
        .setDelayTime(100L)
        .setPeriodic(false)
        .setPeriodicEnableTime(0L)
        .setPeriodicDisableTime(0L)
        .setIgnoreCharging(false)
        .queue();
  }

  @WorkerThread @CallSuper @Override public void queueDisableJob() {
    final QueuerType queuerType;
    switch (getJobTag()) {
      case ManagerInteractor.AIRPLANE_JOB_TAG:
        queuerType = QueuerType.SCREEN_OFF_ENABLE;
        break;
      case ManagerInteractor.DOZE_JOB_TAG:
        queuerType = QueuerType.SCREEN_OFF_ENABLE;
        break;
      default:
        queuerType = QueuerType.SCREEN_OFF_DISABLE;
    }

    // Queue up a disable job
    queuer.cancel();
    queuer.setType(queuerType)
        .setDelayTime(getDelayTime() * 1000L)
        .setPeriodic(isPeriodic())
        .setPeriodicEnableTime(getPeriodicEnableTime())
        .setPeriodicDisableTime(getPeriodicDisableTime())
        .setIgnoreCharging(isIgnoreWhileCharging().call())
        .queue();
  }

  @CallSuper @CheckResult @NonNull PowerManagerPreferences getPreferences() {
    return preferences;
  }

  @CallSuper @NonNull @Override public Observable<Boolean> isManaged() {
    return Observable.defer(() -> Observable.just(manageObserver.is()));
  }

  @CallSuper @NonNull @Override public Observable<Boolean> isEnabled() {
    return Observable.defer(() -> Observable.just(stateObserver.is()));
  }

  @CheckResult protected abstract long getDelayTime();

  @CheckResult protected abstract boolean isPeriodic();

  @CheckResult protected abstract long getPeriodicEnableTime();

  @CheckResult protected abstract long getPeriodicDisableTime();
}
