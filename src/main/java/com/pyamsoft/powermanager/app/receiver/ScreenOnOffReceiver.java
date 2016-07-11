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

package com.pyamsoft.powermanager.app.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import com.pyamsoft.powermanager.PowerManager;
import com.pyamsoft.powermanager.app.manager.backend.ManagerBluetooth;
import com.pyamsoft.powermanager.app.manager.backend.ManagerData;
import com.pyamsoft.powermanager.app.manager.backend.ManagerSync;
import com.pyamsoft.powermanager.app.manager.backend.ManagerWifi;
import com.pyamsoft.powermanager.dagger.manager.backend.DaggerManagerComponent;
import javax.inject.Inject;
import timber.log.Timber;

public final class ScreenOnOffReceiver extends BroadcastReceiver {

  @NonNull private final IntentFilter screenFilter;
  @Inject ManagerWifi managerWifi;
  @Inject ManagerData managerData;
  @Inject ManagerBluetooth managerBluetooth;
  @Inject ManagerSync managerSync;
  @NonNull private final IntentFilter batteryFilter;
  private boolean isRegistered;

  public ScreenOnOffReceiver() {
    screenFilter = new IntentFilter(Intent.ACTION_SCREEN_OFF);
    screenFilter.addAction(Intent.ACTION_SCREEN_ON);
    batteryFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
    isRegistered = false;

    DaggerManagerComponent.builder()
        .powerManagerComponent(PowerManager.getInstance().getPowerManagerComponent())
        .build()
        .inject(this);
  }

  @Override public final void onReceive(final Context context, final Intent intent) {
    if (null != intent) {
      final String action = intent.getAction();
      switch (action) {
        case Intent.ACTION_SCREEN_OFF:
          Timber.d("Screen off event");
          disableManagers(context);
          break;
        case Intent.ACTION_SCREEN_ON:
          Timber.d("Screen on event");
          enableManagers();
          break;
        default:
      }
    }
  }

  private void enableManagers() {
    Timber.d("Enable all managed managers");
    managerWifi.enable();
    managerData.enable();
    managerBluetooth.enable();
    managerSync.enable();
  }

  @CheckResult private boolean getCurrentChargingState(@NonNull Context context) {
    final Intent batteryStatus = context.registerReceiver(null, batteryFilter);
    int status;
    if (batteryStatus == null) {
      Timber.e("NULL BatteryStatus Intent, return Unknown");
      status = BatteryManager.BATTERY_STATUS_UNKNOWN;
    } else {
      // Are we charging / charged?
      status = batteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS,
          BatteryManager.BATTERY_STATUS_UNKNOWN);
    }

    return status == BatteryManager.BATTERY_STATUS_CHARGING
        || status == BatteryManager.BATTERY_STATUS_FULL;
  }

  private void disableManagers(@NonNull Context context) {
    Timber.d("Disable all managed managers");
    final boolean charging = getCurrentChargingState(context);
    managerWifi.disable(charging);
    managerData.disable(charging);
    managerBluetooth.disable(charging);
    managerSync.disable(charging);
  }

  public final void register(@NonNull Context context) {
    cleanup();
    if (!isRegistered) {
      context.getApplicationContext().registerReceiver(this, screenFilter);
      isRegistered = true;
    }
  }

  private void cleanup() {
    managerWifi.cleanup();
    managerData.cleanup();
    managerBluetooth.cleanup();
    managerSync.cleanup();
  }

  public final void unregister(@NonNull Context context) {
    if (isRegistered) {
      context.getApplicationContext().unregisterReceiver(this);
      isRegistered = false;
    }
    cleanup();
  }
}

