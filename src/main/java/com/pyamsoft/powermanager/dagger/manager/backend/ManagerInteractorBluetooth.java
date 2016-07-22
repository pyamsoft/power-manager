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

package com.pyamsoft.powermanager.dagger.manager.backend;

import android.content.Context;
import android.support.annotation.NonNull;
import com.birbit.android.jobqueue.Params;
import com.pyamsoft.powermanager.PowerManager;
import com.pyamsoft.powermanager.PowerManagerPreferences;
import com.pyamsoft.powermanager.app.modifier.InterestModifier;
import com.pyamsoft.powermanager.app.observer.InterestObserver;
import com.pyamsoft.powermanager.dagger.observer.state.BluetoothStateObserver;
import javax.inject.Inject;
import rx.Observable;
import timber.log.Timber;

public final class ManagerInteractorBluetooth extends WearableManagerInteractorImpl {

  @NonNull private static final String TAG = "bluetooth_manager_job";
  @NonNull private final InterestObserver observer;

  @Inject ManagerInteractorBluetooth(@NonNull PowerManagerPreferences preferences,
      @NonNull Context context, @NonNull BluetoothStateObserver observer) {
    super(context, preferences);
    this.observer = observer;
    Timber.d("new ManagerInteractorBluetooth");
  }

  @Override @NonNull public Observable<ManagerInteractor> cancelJobs() {
    return cancelJobs(TAG);
  }

  @Override public void setManaged(boolean enabled) {
    getPreferences().setBluetoothManaged(enabled);
  }

  @NonNull @Override public Observable<Boolean> isEnabled() {
    return Observable.defer(() -> Observable.just(observer.is()));
  }

  @NonNull @Override public Observable<Boolean> isManaged() {
    return Observable.defer(() -> Observable.just(getPreferences().isBluetoothManaged()));
  }

  @NonNull @Override public Observable<Boolean> isPeriodic() {
    return Observable.defer(() -> Observable.just(getPreferences().isPeriodicBluetooth()));
  }

  @Override @NonNull Observable<Long> getPeriodicEnableTime() {
    return Observable.defer(
        () -> Observable.just(getPreferences().getPeriodicEnableTimeBluetooth()));
  }

  @Override @NonNull Observable<Long> getPeriodicDisableTime() {
    return Observable.defer(
        () -> Observable.just(getPreferences().getPeriodicDisableTimeBluetooth()));
  }

  @NonNull @Override public Observable<Long> getDelayTime() {
    return Observable.defer(() -> Observable.just(getPreferences().getBluetoothDelay()));
  }

  @NonNull @Override public Observable<Boolean> isChargingIgnore() {
    return Observable.defer(() -> Observable.just(getPreferences().isIgnoreChargingBluetooth()));
  }

  @NonNull @Override
  public Observable<DeviceJob> createEnableJob(long delayTime, boolean periodic) {
    return Observable.zip(getPeriodicDisableTime(), getPeriodicEnableTime(),
        (disable, enable) -> new EnableJob(delayTime, periodic, disable, enable));
  }

  @NonNull @Override
  public Observable<DeviceJob> createDisableJob(long delayTime, boolean periodic) {
    return Observable.zip(getPeriodicDisableTime(), getPeriodicEnableTime(),
        (disable, enable) -> new DisableJob(delayTime, periodic, disable, enable));
  }

  static final class EnableJob extends Job {

    EnableJob(long delayTime, boolean periodic, long periodicDisableTime, long periodicEnableTime) {
      super(new Params(PRIORITY).setDelayMs(delayTime), JOB_TYPE_ENABLE, periodic,
          periodicDisableTime, periodicEnableTime);
    }
  }

  static final class DisableJob extends Job {

    DisableJob(long delayTime, boolean periodic, long periodicDisableTime,
        long periodicEnableTime) {
      super(new Params(PRIORITY).setDelayMs(delayTime), JOB_TYPE_DISABLE, periodic,
          periodicDisableTime, periodicEnableTime);
    }
  }

  static abstract class Job extends DeviceJob {

    @NonNull private final InterestModifier modifier;
    @NonNull private final InterestObserver observer;

    Job(@NonNull Params params, int jobType, boolean periodic, long periodicDisableTime,
        long periodicEnableTime) {
      super(params.addTags(ManagerInteractorBluetooth.TAG), jobType, periodic, periodicDisableTime,
          periodicEnableTime);
      modifier = PowerManager.getInstance()
          .getPowerManagerComponent()
          .plusStateModifier()
          .provideBluetoothStateModifier();
      observer = PowerManager.getInstance()
          .getPowerManagerComponent()
          .plusStateObserver()
          .provideBluetoothStateObserver();
    }

    @Override protected void callEnable() {
      Timber.d("Enable bluetooth");
      modifier.set();
    }

    @Override protected void callDisable() {
      Timber.d("Disable bluetooth");
      modifier.unset();
    }

    @Override protected boolean isEnabled() {
      Timber.d("isBluetoothEnabled");
      return observer.is();
    }

    @Override protected DeviceJob periodicDisableJob() {
      Timber.d("Periodic bluetooth disable job");
      return new DisableJob(getPeriodicDisableTime() * 1000, true, getPeriodicDisableTime(),
          getPeriodicEnableTime());
    }

    @Override protected DeviceJob periodicEnableJob() {
      Timber.d("Periodic bluetooth enable job");
      return new EnableJob(getPeriodicEnableTime() * 1000, true, getPeriodicDisableTime(),
          getPeriodicEnableTime());
    }
  }
}
