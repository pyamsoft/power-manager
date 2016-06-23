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

package com.pyamsoft.powermanager;

import android.support.annotation.CheckResult;

public interface PowerManagerPreferences {

  @CheckResult boolean isCustomDelayTimeWifi();

  @CheckResult boolean isCustomDelayTimeData();

  @CheckResult boolean isCustomDelayTimeBluetooth();

  @CheckResult boolean isCustomDelayTimeSync();

  @CheckResult boolean isCustomPeriodicDisableTimeWifi();

  @CheckResult boolean isCustomPeriodicDisableTimeData();

  @CheckResult boolean isCustomPeriodicDisableTimeBluetooth();

  @CheckResult boolean isCustomPeriodicDisableTimeSync();

  @CheckResult boolean isCustomPeriodicEnableTimeWifi();

  @CheckResult boolean isCustomPeriodicEnableTimeData();

  @CheckResult boolean isCustomPeriodicEnableTimeBluetooth();

  @CheckResult boolean isCustomPeriodicEnableTimeSync();

  @CheckResult long getWifiDelay();

  void setWifiDelay(long time);

  @CheckResult long getDataDelay();

  void setDataDelay(long time);

  @CheckResult long getBluetoothDelay();

  void setBluetoothDelay(long time);

  @CheckResult long getMasterSyncDelay();

  void setMasterSyncDelay(long time);

  @CheckResult int getNotificationPriority();

  @CheckResult boolean isWifiManaged();

  @CheckResult boolean isDataManaged();

  @CheckResult boolean isBluetoothManaged();

  @CheckResult boolean isSyncManaged();

  @CheckResult boolean isWearableManaged();

  void setWifiManaged(boolean enable);

  void setDataManaged(boolean enable);

  void setBluetoothManaged(boolean enable);

  void setSyncManaged(boolean enable);

  void setWearableManaged(boolean enable);

  void clearAll();

  @CheckResult boolean isPeriodicWifi();

  @CheckResult boolean isPeriodicData();

  @CheckResult boolean isPeriodicBluetooth();

  @CheckResult boolean isPeriodicSync();

  @CheckResult long getPeriodicDisableTimeWifi();

  @CheckResult long getPeriodicDisableTimeData();

  @CheckResult long getPeriodicDisableTimeBluetooth();

  @CheckResult long getPeriodicDisableTimeSync();

  void setPeriodicDisableTimeWifi(long time);

  void setPeriodicDisableTimeData(long time);

  void setPeriodicDisableTimeBluetooth(long time);

  void setPeriodicDisableTimeSync(long time);

  @CheckResult long getPeriodicEnableTimeWifi();

  @CheckResult long getPeriodicEnableTimeData();

  @CheckResult long getPeriodicEnableTimeBluetooth();

  @CheckResult long getPeriodicEnableTimeSync();

  void setPeriodicEnableTimeWifi(long time);

  void setPeriodicEnableTimeData(long time);

  void setPeriodicEnableTimeBluetooth(long time);

  void setPeriodicEnableTimeSync(long time);
}
