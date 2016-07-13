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
import com.pyamsoft.powermanager.app.observer.InterestObserver;
import com.pyamsoft.powermanager.dagger.modifier.state.BluetoothStateModifier;
import com.pyamsoft.powermanager.dagger.observer.state.BluetoothStateObserver;
import com.pyamsoft.powermanager.dagger.observer.state.DaggerStateObserverComponent;
import javax.inject.Inject;
import rx.Observable;
import timber.log.Timber;

public final class ManagerInteractorBluetooth extends WearableManagerInteractorImpl {

  @NonNull private static final String TAG = "bluetooth_manager_job";
  @NonNull private final BluetoothStateObserver observer;
  @NonNull private final Context appContext;

  @Inject ManagerInteractorBluetooth(@NonNull PowerManagerPreferences preferences,
      @NonNull Context context, @NonNull BluetoothStateObserver observer) {
    super(context.getApplicationContext(), preferences);
    this.appContext = context.getApplicationContext();
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

  @Override @NonNull public Observable<Long> getPeriodicEnableTime() {
    return Observable.defer(
        () -> Observable.just(getPreferences().getPeriodicEnableTimeBluetooth()));
  }

  @Override @NonNull public Observable<Long> getPeriodicDisableTime() {
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
        (disable, enable) -> new EnableJob(appContext, delayTime, periodic, disable, enable));
  }

  @NonNull @Override
  public Observable<DeviceJob> createDisableJob(long delayTime, boolean periodic) {
    return Observable.zip(getPeriodicDisableTime(), getPeriodicEnableTime(),
        (disable, enable) -> new DisableJob(appContext, delayTime, periodic, disable, enable));
  }

  static final class EnableJob extends Job {

    protected EnableJob(@NonNull Context context, long delayTime, boolean periodic,
        long periodicDisableTime, long periodicEnableTime) {
      super(context, new Params(PRIORITY).setDelayMs(delayTime), JOB_TYPE_ENABLE, periodic,
          periodicDisableTime, periodicEnableTime);
    }
  }

  static final class DisableJob extends Job {

    protected DisableJob(@NonNull Context context, long delayTime, boolean periodic,
        long periodicDisableTime, long periodicEnableTime) {
      super(context, new Params(PRIORITY).setDelayMs(delayTime), JOB_TYPE_DISABLE, periodic,
          periodicDisableTime, periodicEnableTime);
    }
  }

  static abstract class Job extends DeviceJob {

    @NonNull private final BluetoothStateModifier modifier;
    @NonNull private final InterestObserver observer;

    protected Job(@NonNull Context context, @NonNull Params params, int jobType, boolean periodic,
        long periodicDisableTime, long periodicEnableTime) {
      super(context, params.addTags(ManagerInteractorBluetooth.TAG), jobType, periodic,
          periodicDisableTime, periodicEnableTime);
      modifier = new BluetoothStateModifier(getContext());
      observer = DaggerStateObserverComponent.builder()
          .powerManagerComponent(PowerManager.getInstance().getPowerManagerComponent())
          .build()
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
      return new DisableJob(getContext(), getPeriodicDisableTime() * 1000, true,
          getPeriodicDisableTime(), getPeriodicEnableTime());
    }

    @Override protected DeviceJob periodicEnableJob() {
      Timber.d("Periodic bluetooth enable job");
      return new EnableJob(getContext(), getPeriodicEnableTime() * 1000, true,
          getPeriodicDisableTime(), getPeriodicEnableTime());
    }
  }
}
