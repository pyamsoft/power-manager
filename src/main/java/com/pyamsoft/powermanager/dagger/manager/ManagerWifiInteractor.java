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

import android.content.Context;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import com.birbit.android.jobqueue.Job;
import com.pyamsoft.powermanager.PowerManagerPreferences;
import com.pyamsoft.powermanager.app.observer.InterestObserver;
import com.pyamsoft.powermanager.dagger.job.WifiManageJob;
import javax.inject.Inject;
import rx.Observable;

final class ManagerWifiInteractor extends ManagerBaseInteractor implements ManagerInteractor {

  @NonNull private final InterestObserver wifiObserver;

  @Inject ManagerWifiInteractor(@NonNull Context context,
      @NonNull PowerManagerPreferences preferences, @NonNull InterestObserver wifiObserver) {
    super(context, preferences);
    this.wifiObserver = wifiObserver;
  }

  @CheckResult long getDelayTime() {
    return getPreferences().getWifiDelay();
  }

  @CheckResult boolean isPeriodic() {
    return getPreferences().isPeriodicWifi();
  }

  @CheckResult long getPeriodicEnableTime() {
    return getPreferences().getPeriodicEnableTimeWifi();
  }

  @CheckResult long getPeriodicDisableTime() {
    return getPreferences().getPeriodicDisableTimeWifi();
  }

  @NonNull @Override protected Job createEnableJob() {
    return new WifiManageJob.WifiEnableJob(getDelayTime(), isPeriodic(), getPeriodicEnableTime(),
        getPeriodicDisableTime());
  }

  @NonNull @Override protected Job createDisableJob() {
    return new WifiManageJob.WifiDisableJob(getDelayTime(), isPeriodic(), getPeriodicEnableTime(),
        getPeriodicDisableTime());
  }

  @Override public void destroy() {
    destroy(WifiManageJob.JOB_TAG);
  }

  @NonNull @Override public Observable<Boolean> cancelJobs() {
    return cancelJobs(WifiManageJob.JOB_TAG);
  }

  @NonNull @Override public Observable<Boolean> isManaged() {
    return Observable.defer(() -> Observable.just(getPreferences().isWifiManaged()));
  }

  @NonNull @Override public Observable<Boolean> isIgnoreWhileCharging() {
    return Observable.defer(() -> Observable.just(getPreferences().isIgnoreChargingWifi()));
  }

  @NonNull @Override public Observable<Boolean> isEnabled() {
    return Observable.defer(() -> Observable.just(wifiObserver.is()));
  }
}