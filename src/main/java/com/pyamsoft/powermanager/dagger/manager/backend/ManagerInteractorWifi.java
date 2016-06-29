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
import android.net.wifi.WifiManager;
import android.support.annotation.NonNull;
import com.birbit.android.jobqueue.Params;
import com.pyamsoft.powermanager.PowerManagerPreferences;
import javax.inject.Inject;
import rx.Observable;
import timber.log.Timber;

public final class ManagerInteractorWifi extends WearableManagerInteractorImpl {

  @NonNull private static final String TAG = "wifi_manager_job";
  @NonNull private final WifiManager wifiManager;
  @NonNull private final Context appContext;

  @Inject ManagerInteractorWifi(@NonNull PowerManagerPreferences preferences,
      @NonNull Context context, @NonNull WifiManager wifiManager) {
    super(context.getApplicationContext(), preferences);
    this.appContext = context.getApplicationContext();
    this.wifiManager = wifiManager;
    Timber.d("new ManagerInteractorWifi");
  }

  @Override public void cancelJobs() {
    cancelJobs(TAG);
  }

  @NonNull @Override public Observable<Boolean> isEnabled() {
    return Observable.defer(() -> Observable.just(wifiManager.isWifiEnabled()));
  }

  @NonNull @Override public Observable<Boolean> isManaged() {
    return Observable.defer(() -> Observable.just(getPreferences().isWifiManaged()));
  }

  @NonNull @Override public Observable<Boolean> isPeriodic() {
    return Observable.defer(() -> Observable.just(getPreferences().isPeriodicWifi()));
  }

  @Override @NonNull public Observable<Long> getPeriodicEnableTime() {
    return Observable.defer(() -> Observable.just(getPreferences().getPeriodicEnableTimeWifi()));
  }

  @Override @NonNull public Observable<Long> getPeriodicDisableTime() {
    return Observable.defer(() -> Observable.just(getPreferences().getPeriodicDisableTimeWifi()));
  }

  @NonNull @Override public Observable<Long> getDelayTime() {
    return Observable.defer(() -> Observable.just(getPreferences().getWifiDelay()));
  }

  @NonNull @Override public Observable<Boolean> isChargingIgnore() {
    return Observable.defer(() -> Observable.just(getPreferences().isIgnoreChargingWifi()));
  }

  @NonNull @Override
  public Observable<DeviceJob> createEnableJob(long delayTime, boolean periodic) {
    return Observable.zip(getPeriodicDisableTime(), getPeriodicEnableTime(),
        isOriginalStateEnabled(),
        (disable, enable, original) -> new EnableJob(appContext, delayTime, original, periodic,
            disable, enable));
  }

  @NonNull @Override
  public Observable<DeviceJob> createDisableJob(long delayTime, boolean periodic) {
    return Observable.zip(getPeriodicDisableTime(), getPeriodicEnableTime(),
        isOriginalStateEnabled(),
        (disable, enable, original) -> new DisableJob(appContext, delayTime, original, periodic,
            disable, enable));
  }

  static final class EnableJob extends Job {

    protected EnableJob(@NonNull Context context, long delayTime, boolean originalState,
        boolean periodic, long periodicDisableTime, long periodicEnableTime) {
      super(context, new Params(PRIORITY).setDelayMs(delayTime), JOB_TYPE_ENABLE, originalState,
          periodic, periodicDisableTime, periodicEnableTime);
    }
  }

  static final class DisableJob extends Job {

    protected DisableJob(@NonNull Context context, long delayTime, boolean originalState,
        boolean periodic, long periodicDisableTime, long periodicEnableTime) {
      super(context, new Params(PRIORITY).setDelayMs(delayTime), JOB_TYPE_DISABLE, originalState,
          periodic, periodicDisableTime, periodicEnableTime);
    }
  }

  static abstract class Job extends DeviceJob {

    @NonNull private final WifiManager wifiManager;

    protected Job(@NonNull Context context, @NonNull Params params, int jobType,
        boolean originalState, boolean periodic, long periodicDisableTime,
        long periodicEnableTime) {
      super(context, params.addTags(ManagerInteractorWifi.TAG), jobType, originalState, periodic,
          periodicDisableTime, periodicEnableTime);
      wifiManager = (WifiManager) getContext().getSystemService(Context.WIFI_SERVICE);
    }

    @Override protected void callEnable() {
      Timber.d("Enable wifi");
      wifiManager.setWifiEnabled(true);
    }

    @Override protected void callDisable() {
      Timber.d("Disable wifi");
      wifiManager.setWifiEnabled(false);
    }

    @Override protected boolean isEnabled() {
      Timber.d("isWifiEnabled");
      return wifiManager.isWifiEnabled();
    }

    @Override protected DeviceJob periodicDisableJob() {
      Timber.d("Periodic wifi disable job");
      return new DisableJob(getContext(), getPeriodicDisableTime() * 1000, isOriginalState(), true,
          getPeriodicDisableTime(), getPeriodicEnableTime());
    }

    @Override protected DeviceJob periodicEnableJob() {
      Timber.d("Periodic wifi enable job");
      return new EnableJob(getContext(), getPeriodicEnableTime() * 1000, isOriginalState(), true,
          getPeriodicDisableTime(), getPeriodicEnableTime());
    }
  }
}
