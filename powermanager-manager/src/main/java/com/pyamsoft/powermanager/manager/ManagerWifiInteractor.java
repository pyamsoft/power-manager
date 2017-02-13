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

import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import com.pyamsoft.powermanager.base.PowerManagerPreferences;
import com.pyamsoft.powermanager.job.JobQueuer;
import com.pyamsoft.powermanager.model.BooleanInterestObserver;
import javax.inject.Inject;
import rx.Observable;

class ManagerWifiInteractor extends WearAwareManagerInteractor {

  @Inject ManagerWifiInteractor(@NonNull PowerManagerPreferences preferences,
      @NonNull BooleanInterestObserver manageObserver,
      @NonNull BooleanInterestObserver stateObserver, @NonNull JobQueuer jobQueuer,
      @NonNull BooleanInterestObserver wearManageObserver,
      @NonNull BooleanInterestObserver wearStateObserver) {
    super(preferences, manageObserver, stateObserver, jobQueuer, wearManageObserver,
        wearStateObserver);
  }

  @Override @CheckResult protected long getDelayTime() {
    return getPreferences().getWifiDelay();
  }

  @Override @CheckResult protected boolean isPeriodic() {
    return getPreferences().isPeriodicWifi();
  }

  @Override @CheckResult protected long getPeriodicEnableTime() {
    return getPreferences().getPeriodicEnableTimeWifi();
  }

  @Override @CheckResult protected long getPeriodicDisableTime() {
    return getPreferences().getPeriodicDisableTimeWifi();
  }

  @NonNull @Override public String getJobTag() {
    return JobQueuer.WIFI_JOB_TAG;
  }

  @Override public boolean isIgnoreWhileCharging() {
    return getPreferences().isIgnoreChargingWifi();
  }

  @NonNull @Override public Observable<Boolean> isOriginalStateEnabled() {
    return Observable.fromCallable(() -> getPreferences().isOriginalWifi());
  }

  @Override public void setOriginalStateEnabled(boolean enabled) {
    getPreferences().setOriginalWifi(enabled);
  }
}
