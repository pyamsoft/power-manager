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
import com.pyamsoft.powermanager.base.wrapper.ConnectedDeviceFunctionWrapper;
import com.pyamsoft.powermanager.job.JobQueuer;
import com.pyamsoft.powermanager.model.overlord.StateChangeObserver;
import io.reactivex.Observable;
import javax.inject.Inject;

class ManagerWifiInteractor extends WearAwareManagerInteractor {

  @NonNull final ConnectedDeviceFunctionWrapper wrapper;

  @Inject ManagerWifiInteractor(@NonNull ConnectedDeviceFunctionWrapper wrapper,
      @NonNull PowerManagerPreferences preferences, @NonNull StateChangeObserver manageObserver,
      @NonNull StateChangeObserver stateObserver, @NonNull JobQueuer jobQueuer,
      @NonNull StateChangeObserver wearManageObserver,
      @NonNull StateChangeObserver wearStateObserver) {
    super(preferences, manageObserver, stateObserver, jobQueuer, wearManageObserver,
        wearStateObserver);
    this.wrapper = wrapper;
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

  @NonNull @Override
  protected Observable<Boolean> accountForWearableBeforeDisable(boolean originalState) {
    return super.accountForWearableBeforeDisable(originalState).map(originalResult -> {
      // TODO check preferences
      // If wifi doesn't have an existing connection, we forcefully continue the stream so that WiFi is turned off
      if (wrapper.isConnected()) {
        return Boolean.TRUE;
      } else {
        return originalResult;
      }
    });
  }

  @Override public void setOriginalStateEnabled(boolean enabled) {
    getPreferences().setOriginalWifi(enabled);
  }
}
