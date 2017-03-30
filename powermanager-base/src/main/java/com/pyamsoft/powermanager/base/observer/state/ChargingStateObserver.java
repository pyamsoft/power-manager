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

package com.pyamsoft.powermanager.base.observer.state;

import android.content.Context;
import android.content.Intent;
import android.os.BatteryManager;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import javax.inject.Inject;
import timber.log.Timber;

class ChargingStateObserver extends BroadcastStateObserver {

  @Inject ChargingStateObserver(@NonNull Context context) {
    super(context, Intent.ACTION_BATTERY_CHANGED);
    Timber.d("new ChargingStateObserver instance");
  }

  @CheckResult private int getStatus() {
    final Intent batteryStatus = getAppContext().registerReceiver(null, getFilter());
    int status;
    if (batteryStatus == null) {
      Timber.e("NULL BatteryStatus Intent, return Unknown");
      status = BatteryManager.BATTERY_STATUS_UNKNOWN;
    } else {
      // Are we charging / charged?
      status = batteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS,
          BatteryManager.BATTERY_STATUS_UNKNOWN);
    }

    return status;
  }

  @CheckResult private boolean isCharging() {
    int status = getStatus();
    return status == BatteryManager.BATTERY_STATUS_CHARGING
        || status == BatteryManager.BATTERY_STATUS_FULL;
  }

  @Override public boolean enabled() {
    return isCharging();
  }

  @Override public boolean unknown() {
    return getStatus() == BatteryManager.BATTERY_STATUS_UNKNOWN;
  }
}
