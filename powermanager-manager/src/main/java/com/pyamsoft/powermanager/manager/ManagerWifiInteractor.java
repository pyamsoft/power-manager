/*
 * Copyright 2017 Peter Kenji Yamanaka
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
import com.pyamsoft.powermanager.base.preference.WearablePreferences;
import com.pyamsoft.powermanager.base.preference.WifiPreferences;
import com.pyamsoft.powermanager.job.JobQueuer;
import com.pyamsoft.powermanager.model.ConnectedStateObserver;
import com.pyamsoft.powermanager.model.StateObserver;
import io.reactivex.Maybe;
import javax.inject.Inject;
import timber.log.Timber;

class ManagerWifiInteractor extends WearAwareManagerInteractor {

  @SuppressWarnings("WeakerAccess") @NonNull final WifiPreferences wifiPreferences;
  @SuppressWarnings("WeakerAccess") @NonNull final ConnectedStateObserver wifiStateObserver;

  @Inject ManagerWifiInteractor(@NonNull WifiPreferences wifiPreferences,
      @NonNull WearablePreferences preferences, @NonNull ConnectedStateObserver stateObserver,
      @NonNull JobQueuer jobQueuer, @NonNull StateObserver wearStateObserver) {
    super(preferences, stateObserver, jobQueuer, wearStateObserver);
    this.wifiPreferences = wifiPreferences;
    this.wifiStateObserver = stateObserver;
  }

  @Override boolean isWearManaged() {
    return wifiPreferences.isWearableManaged();
  }

  @Override @CheckResult protected long getDelayTime() {
    return wifiPreferences.getWifiDelay();
  }

  @Override @CheckResult protected boolean isPeriodic() {
    return wifiPreferences.isPeriodicWifi();
  }

  @Override @CheckResult protected long getPeriodicEnableTime() {
    return wifiPreferences.getPeriodicEnableTimeWifi();
  }

  @Override @CheckResult protected long getPeriodicDisableTime() {
    return wifiPreferences.getPeriodicDisableTimeWifi();
  }

  @NonNull @Override public String getJobTag() {
    return JobQueuer.WIFI_JOB_TAG;
  }

  @Override public boolean isIgnoreWhileCharging() {
    return wifiPreferences.isIgnoreChargingWifi();
  }

  @Override boolean isManaged() {
    return wifiPreferences.isWifiManaged();
  }

  @Override boolean isOriginalStateEnabled() {
    return wifiPreferences.isOriginalWifi();
  }

  @Override public void setOriginalStateEnabled(boolean enabled) {
    wifiPreferences.setOriginalWifi(enabled);
  }

  @NonNull @Override
  protected Maybe<Boolean> accountForWearableBeforeDisable(boolean originalState) {
    return super.accountForWearableBeforeDisable(originalState).map(originalResult -> {
      Timber.d("Check for active connection here");
      // TODO check preferences
      // If wifi doesn't have an existing connection, we forcefully continue the stream so that WiFi is turned off
      if (wifiStateObserver.connected()) {
        Timber.i("Wifi is not connected, force continue the manage stream");
        return Boolean.TRUE;
      } else {
        return originalResult;
      }
    });
  }
}
