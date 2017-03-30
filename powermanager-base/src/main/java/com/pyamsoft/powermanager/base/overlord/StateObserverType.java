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

package com.pyamsoft.powermanager.base.overlord;

import android.bluetooth.BluetoothAdapter;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.PowerManager;
import android.provider.Settings;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;

enum StateObserverType {
  WIFI(WifiManager.WIFI_STATE_CHANGED_ACTION), DATA("mobile_data"), BLUETOOTH(
      BluetoothAdapter.ACTION_STATE_CHANGED), SYNC(""), AIRPLANE(
      Settings.Global.AIRPLANE_MODE_ON), DOZE(
      isDozeAvailable() ? PowerManager.ACTION_DEVICE_IDLE_MODE_CHANGED : "");

  @NonNull private final String action;

  StateObserverType(@NonNull String action) {
    this.action = action;
  }

  @CheckResult private static boolean isDozeAvailable() {
    return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
  }

  @NonNull @CheckResult String action() {
    return action;
  }
}
