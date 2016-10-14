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

import android.content.SharedPreferences;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;

public interface PowerManagerPreferences {

  @CheckResult boolean isManageOnboardingShown();

  void setManageOnboardingShown();

  @CheckResult boolean isOverviewOnboardingShown();

  void setOverviewOnboardingShown();

  @CheckResult long getWearableDelay();

  @CheckResult boolean isSensorsManaged();

  @CheckResult long getDozeDelay();

  @CheckResult boolean isDozeManaged();

  void setDozeManaged(boolean enable);

  @CheckResult boolean isIgnoreChargingDoze();

  @CheckResult boolean isIgnoreChargingWifi();

  @CheckResult boolean isIgnoreChargingData();

  @CheckResult boolean isIgnoreChargingBluetooth();

  @CheckResult boolean isIgnoreChargingSync();

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

  void setWifiManaged(boolean enable);

  @CheckResult boolean isDataManaged();

  void setDataManaged(boolean enable);

  @CheckResult boolean isBluetoothManaged();

  void setBluetoothManaged(boolean enable);

  @CheckResult boolean isSyncManaged();

  void setSyncManaged(boolean enable);

  @CheckResult boolean isWearableManaged();

  void setWearableManaged(boolean enable);

  void clearAll();

  @CheckResult boolean isPeriodicWifi();

  void setPeriodicWifi(boolean state);

  @CheckResult boolean isPeriodicData();

  void setPeriodicData(boolean state);

  @CheckResult boolean isPeriodicBluetooth();

  void setPeriodicBluetooth(boolean state);

  @CheckResult boolean isPeriodicSync();

  void setPeriodicSync(boolean state);

  @CheckResult long getPeriodicDisableTimeWifi();

  void setPeriodicDisableTimeWifi(long time);

  @CheckResult long getPeriodicDisableTimeData();

  void setPeriodicDisableTimeData(long time);

  @CheckResult long getPeriodicDisableTimeBluetooth();

  void setPeriodicDisableTimeBluetooth(long time);

  @CheckResult long getPeriodicDisableTimeSync();

  void setPeriodicDisableTimeSync(long time);

  @CheckResult long getPeriodicEnableTimeWifi();

  void setPeriodicEnableTimeWifi(long time);

  @CheckResult long getPeriodicEnableTimeData();

  void setPeriodicEnableTimeData(long time);

  @CheckResult long getPeriodicEnableTimeBluetooth();

  void setPeriodicEnableTimeBluetooth(long time);

  @CheckResult long getPeriodicEnableTimeSync();

  void setPeriodicEnableTimeSync(long time);

  void register(@NonNull SharedPreferences.OnSharedPreferenceChangeListener listener);

  void unregister(@NonNull SharedPreferences.OnSharedPreferenceChangeListener listener);

  @CheckResult boolean isExclusiveDoze();
}
