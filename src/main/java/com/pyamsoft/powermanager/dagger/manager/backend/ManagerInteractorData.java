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
import com.pyamsoft.powermanager.dagger.modifier.state.DaggerStateModifierComponent;
import com.pyamsoft.powermanager.dagger.observer.state.DaggerStateObserverComponent;
import com.pyamsoft.powermanager.dagger.observer.state.DataStateObserver;
import javax.inject.Inject;
import rx.Observable;
import timber.log.Timber;

public final class ManagerInteractorData extends ManagerInteractorBase {

  @NonNull private static final String TAG = "data_manager_job";
  @NonNull private final InterestObserver observer;

  @Inject ManagerInteractorData(@NonNull PowerManagerPreferences preferences,
      @NonNull Context context, @NonNull DataStateObserver observer) {
    super(context, preferences);
    this.observer = observer;
    Timber.d("new ManagerInteractorData");
  }

  @Override @NonNull public Observable<ManagerInteractor> cancelJobs() {
    return cancelJobs(TAG);
  }

  @Override public void setManaged(boolean enabled) {
    getPreferences().setDataManaged(enabled);
  }

  @NonNull @Override public Observable<Boolean> isEnabled() {
    return Observable.defer(() -> Observable.just(observer.is()));
  }

  @NonNull @Override public Observable<Boolean> isManaged() {
    return Observable.defer(() -> Observable.just(getPreferences().isDataManaged()));
  }

  @NonNull @Override public Observable<Boolean> isPeriodic() {
    return Observable.defer(() -> Observable.just(getPreferences().isPeriodicData()));
  }

  @Override @NonNull public Observable<Long> getPeriodicEnableTime() {
    return Observable.defer(() -> Observable.just(getPreferences().getPeriodicEnableTimeData()));
  }

  @Override @NonNull public Observable<Long> getPeriodicDisableTime() {
    return Observable.defer(() -> Observable.just(getPreferences().getPeriodicDisableTimeData()));
  }

  @NonNull @Override public Observable<Long> getDelayTime() {
    return Observable.defer(() -> Observable.just(getPreferences().getDataDelay()));
  }

  @NonNull @Override public Observable<Boolean> isChargingIgnore() {
    return Observable.defer(() -> Observable.just(getPreferences().isIgnoreChargingData()));
  }

  @NonNull @Override
  public Observable<DeviceJob> createEnableJob(long delayTime, boolean periodic) {
    return Observable.zip(getPeriodicDisableTime(), getPeriodicEnableTime(),
        (disable, enable) -> new EnableJob(getAppContext(), delayTime, periodic, disable, enable));
  }

  @NonNull @Override
  public Observable<DeviceJob> createDisableJob(long delayTime, boolean periodic) {
    return Observable.zip(getPeriodicDisableTime(), getPeriodicEnableTime(),
        (disable, enable) -> new DisableJob(getAppContext(), delayTime, periodic, disable, enable));
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

    @NonNull private final InterestModifier modifier;
    @NonNull private final InterestObserver observer;

    protected Job(@NonNull Context context, @NonNull Params params, int jobType, boolean periodic,
        long periodicDisableTime, long periodicEnableTime) {
      super(context, params.addTags(ManagerInteractorData.TAG), jobType, periodic,
          periodicDisableTime, periodicEnableTime);
      modifier = DaggerStateModifierComponent.builder()
          .powerManagerComponent(PowerManager.getInstance().getPowerManagerComponent())
          .build()
          .provideDataStateModifier();
      observer = DaggerStateObserverComponent.builder()
          .powerManagerComponent(PowerManager.getInstance().getPowerManagerComponent())
          .build()
          .provideDataStateObserver();
    }

    @Override protected void callEnable() {
      Timber.d("Enable data");
      modifier.set();
    }

    @Override protected void callDisable() {
      Timber.d("Disable data");
      modifier.unset();
    }

    @Override protected boolean isEnabled() {
      Timber.d("isDataEnabled");
      return observer.is();
    }

    @Override protected DeviceJob periodicDisableJob() {
      Timber.d("Periodic data disable job");
      return new DisableJob(getContext(), getPeriodicDisableTime() * 1000, true,
          getPeriodicDisableTime(), getPeriodicEnableTime());
    }

    @Override protected DeviceJob periodicEnableJob() {
      Timber.d("Periodic data enable job");
      return new EnableJob(getContext(), getPeriodicEnableTime() * 1000, true,
          getPeriodicDisableTime(), getPeriodicEnableTime());
    }
  }
}
