/*
 * Copyright 2013 - 2016 Peter Kenji Yamanaka
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

package com.pyamsoft.powermanager.backend.util;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Build;
import com.pyamsoft.pydroid.util.LogUtil;

public final class BatteryUtil {

  private static final String TAG = BatteryUtil.class.getSimpleName();
  private static BatteryUtil instance = null;
  private final IntentFilter filter;
  private double percent = -1;
  private boolean isCharging = false;
  private float temperature = -1;
  private Context context;

  private BatteryUtil(final Context context) {
    LogUtil.d(TAG, "Initialized BatteryUtil");
    filter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
    this.context = context.getApplicationContext();
  }

  public static BatteryUtil with(final Context context) {
    if (instance == null) {
      synchronized (BatteryUtil.class) {
        if (instance == null) {
          instance = new BatteryUtil(context);
        }
      }
    }
    return instance;
  }

  private static double getCurrentPercent(final Intent intent) {
    // The current level and scale is retrieved from the BatteryManager
    final double currentLevel = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
    return (currentLevel / intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)) * 100;
  }

  private static boolean getChargingStatus(final Intent intent) {
    // Device is charging if it is on any of the following power sources, or if its
    // status is reported as charging or full
    final int charging = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, 0);
    final int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, 0);
    final boolean currentPlugged = (charging == BatteryManager.BATTERY_PLUGGED_AC ||
        charging == BatteryManager.BATTERY_PLUGGED_USB ||
        isWirelessCharging(charging));
    final boolean currentStatus = (status == BatteryManager.BATTERY_STATUS_CHARGING
        || status == BatteryManager.BATTERY_STATUS_FULL);
    return (currentPlugged && currentStatus);
  }

  private static boolean isWirelessCharging(final int charge) {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
      return OldAndroid.isWirelesCharging();
    } else {
      return JellyBeanMR1.isWirelessCharging(charge);
    }
  }

  public final double getPercent() {
    return percent;
  }

  public final float getTemperature() {
    return temperature;
  }

  public final boolean isCharging() {
    return isCharging;
  }

  public final void updateBatteryInformation() {
    final Intent batteryIntent = context.getApplicationContext().registerReceiver(null, filter);
    if (batteryIntent != null) {
      isCharging = getChargingStatus(batteryIntent);
      percent = getCurrentPercent(batteryIntent);
      temperature = getCurrentTemperature(batteryIntent);
    }
  }

  private float getCurrentTemperature(final Intent intent) {
    return (float) intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1) / 10;
  }

  @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1) private static class JellyBeanMR1 {

    private static boolean isWirelessCharging(final int charge) {
      return charge == BatteryManager.BATTERY_PLUGGED_WIRELESS;
    }
  }

  private static class OldAndroid {

    @SuppressWarnings("SameReturnValue") private static boolean isWirelesCharging() {
      // Wireless charging is not implemented on Androids lower, simply
      // return 0 to do nothing
      return false;
    }
  }
}
