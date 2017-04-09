/*
 * Copyright 2016 Peter Kenji Yamanaka
 *
 * Licensed under the Apache License, Version 2.0 (the "License");;
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

package com.pyamsoft.powermanager.base;

import android.support.annotation.CheckResult;

public interface PowerManagerPreferences {

  @CheckResult boolean isOriginalData();

  void setOriginalData(boolean state);

  @CheckResult boolean isOriginalBluetooh();

  @CheckResult boolean isOriginalSync();

  void setOriginalSync(boolean state);

  @CheckResult boolean isOriginalAirplane();

  void setOriginalAirplane(boolean state);

  @CheckResult boolean isOriginalDoze();

  void setOriginalDoze(boolean state);

  void setOriginalBluetooth(boolean state);

  @CheckResult boolean isServiceEnabled();

  void setServiceEnabled(boolean enabled);

  @CheckResult long getTriggerPeriodTime();

  @CheckResult boolean isLoggerEnabled();

  @CheckResult boolean isRootEnabled();

  void resetRootEnabled();

  @CheckResult boolean isStartWhenOpen();

  @CheckResult boolean isPeriodicOnboardingShown();

  void setPeriodicOnboardingShown();

  @CheckResult boolean isManageOnboardingShown();

  void setManageOnboardingShown();

  @CheckResult boolean isOverviewOnboardingShown();

  void setOverviewOnboardingShown();

  long getWearableDelay();

  @CheckResult long getDozeDelay();

  void setDozeDelay(long time);

  @CheckResult boolean isIgnoreChargingDoze();

  @CheckResult boolean isDozeManaged();

  @CheckResult boolean isAirplaneManaged();

  @CheckResult boolean isIgnoreChargingAirplane();

  @CheckResult boolean isIgnoreChargingData();

  @CheckResult boolean isIgnoreChargingBluetooth();

  @CheckResult boolean isIgnoreChargingSync();

  @CheckResult long getAirplaneDelay();

  void setAirplaneDelay(long time);

  @CheckResult long getDataDelay();

  void setDataDelay(long time);

  @CheckResult long getBluetoothDelay();

  void setBluetoothDelay(long time);

  @CheckResult long getMasterSyncDelay();

  void setMasterSyncDelay(long time);

  @CheckResult int getNotificationPriority();

  @CheckResult boolean isBluetoothManaged();

  @CheckResult boolean isDataManaged();

  @CheckResult boolean isSyncManaged();

  @CheckResult boolean isWearableManaged();

  void clearAll();

  @CheckResult boolean isPeriodicDoze();

  @CheckResult boolean isPeriodicData();

  @CheckResult boolean isPeriodicBluetooth();

  @CheckResult boolean isPeriodicSync();

  @CheckResult boolean isPeriodicAirplane();

  @CheckResult long getPeriodicDisableTimeDoze();

  void setPeriodicDisableTimeDoze(long time);

  @CheckResult long getPeriodicDisableTimeAirplane();

  void setPeriodicDisableTimeAirplane(long time);

  @CheckResult long getPeriodicDisableTimeData();

  void setPeriodicDisableTimeData(long time);

  @CheckResult long getPeriodicDisableTimeBluetooth();

  void setPeriodicDisableTimeBluetooth(long time);

  @CheckResult long getPeriodicDisableTimeSync();

  void setPeriodicDisableTimeSync(long time);

  @CheckResult long getPeriodicEnableTimeDoze();

  void setPeriodicEnableTimeDoze(long time);

  @CheckResult long getPeriodicEnableTimeAirplane();

  void setPeriodicEnableTimeAirplane(long time);

  @CheckResult long getPeriodicEnableTimeData();

  void setPeriodicEnableTimeData(long time);

  @CheckResult long getPeriodicEnableTimeBluetooth();

  void setPeriodicEnableTimeBluetooth(long time);

  @CheckResult long getPeriodicEnableTimeSync();

  void setPeriodicEnableTimeSync(long time);
}
