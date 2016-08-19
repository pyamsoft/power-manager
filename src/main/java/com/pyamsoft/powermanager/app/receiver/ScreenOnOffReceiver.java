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

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import com.pyamsoft.powermanager.Singleton;
import com.pyamsoft.powermanager.app.manager.backend.ManagerBluetooth;
import com.pyamsoft.powermanager.app.manager.backend.ManagerData;
import com.pyamsoft.powermanager.app.manager.backend.ManagerDoze;
import com.pyamsoft.powermanager.app.manager.backend.ManagerSync;
import com.pyamsoft.powermanager.app.manager.backend.ManagerWifi;
import javax.inject.Inject;
import timber.log.Timber;

public final class ScreenOnOffReceiver extends BroadcastReceiver {

  @NonNull private final static IntentFilter SCREEN_FILTER;
  @NonNull private final static IntentFilter BATTERY_FILTER;

  static {
    SCREEN_FILTER = new IntentFilter(Intent.ACTION_SCREEN_OFF);
    SCREEN_FILTER.addAction(Intent.ACTION_SCREEN_ON);
    BATTERY_FILTER = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
  }

  @NonNull private final Context appContext;
  @Inject ManagerWifi managerWifi;
  @Inject ManagerData managerData;
  @Inject ManagerBluetooth managerBluetooth;
  @Inject ManagerSync managerSync;
  @Inject ManagerDoze managerDoze;
  private boolean isRegistered;

  public ScreenOnOffReceiver(@NonNull Context context) {
    this.appContext = context.getApplicationContext();
    isRegistered = false;

    Singleton.Dagger.with(appContext).plusManager().inject(this);
  }

  @CheckResult public static boolean getCurrentChargingState(@NonNull Context context) {
    final Intent batteryStatus = context.registerReceiver(null, BATTERY_FILTER);
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

  @Override public final void onReceive(final Context context, final Intent intent) {
    if (null != intent) {
      final String action = intent.getAction();
      final boolean hasDump = ManagerDoze.checkDumpsysPermission(context);
      final boolean charging = getCurrentChargingState(context);
      switch (action) {
        case Intent.ACTION_SCREEN_OFF:
          Timber.d("Screen off event");
          disableManagers(charging, hasDump);
          break;
        case Intent.ACTION_SCREEN_ON:
          Timber.d("Screen on event");
          enableManagers(hasDump);
          break;
        default:
          Timber.e("Invalid event: %s", action);
      }
    }
  }

  private void enableManagers(boolean hasDump) {
    Timber.d("Enable all managed managers");
    managerWifi.enable();
    managerData.enable();
    managerBluetooth.enable();
    managerSync.enable();

    if (hasDump) {
      Timber.d("Device is currently dozing, disable Doze");
      managerDoze.enable();
    } else {
      Timber.e("Missing permission %s", Manifest.permission.DUMP);
    }
  }

  private void disableManagers(boolean charging, boolean hasDump) {
    Timber.d("Disable all managed managers");
    managerWifi.disable(charging);
    managerData.disable(charging);
    managerBluetooth.disable(charging);
    managerSync.disable(charging);

    if (hasDump) {
      Timber.d("Device is currently not dozing, enable Doze");
      managerDoze.disable(charging);
    } else {
      Timber.e("Missing permission %s", Manifest.permission.DUMP);
    }
  }

  public final void register() {
    if (!isRegistered) {
      cleanup();
      appContext.registerReceiver(this, SCREEN_FILTER);
      isRegistered = true;
    }
  }

  private void cleanup() {
    if (ManagerDoze.checkDumpsysPermission(appContext)
        && ManagerDoze.isDozeAvailable()
        && managerDoze.isDozeEnabled()) {
      ManagerDoze.executeDumpsys(appContext, ManagerDoze.DUMPSYS_DOZE_END);

      if (managerDoze.isSensorsManaged()) {
        ManagerDoze.executeDumpsys(appContext, ManagerDoze.DUMPSYS_SENSOR_ENABLE);
        managerDoze.fixSensorDisplayRotationBug();
      }
    }

    managerWifi.cleanup();
    managerData.cleanup();
    managerBluetooth.cleanup();
    managerSync.cleanup();
    managerDoze.cleanup();
  }

  public final void unregister() {
    if (isRegistered) {
      appContext.unregisterReceiver(this);
      cleanup();
      isRegistered = false;
    }
  }
}

