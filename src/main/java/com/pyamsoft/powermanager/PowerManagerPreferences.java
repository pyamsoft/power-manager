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

  @CheckResult boolean isCustomTimeWifi();

  @CheckResult boolean isCustomTimeData();

  @CheckResult boolean isCustomTimeBluetooth();

  @CheckResult boolean isCustomTimeSync();

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
}
