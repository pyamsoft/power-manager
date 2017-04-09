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

package com.pyamsoft.powermanager.base.preference;

import android.support.annotation.CheckResult;

public interface WifiPreferences {

  @CheckResult boolean isWearableManaged();

  @CheckResult boolean isOriginalWifi();

  void setOriginalWifi(boolean state);

  @CheckResult boolean isIgnoreChargingWifi();

  @CheckResult long getWifiDelay();

  void setWifiDelay(long time);

  @CheckResult boolean isWifiManaged();

  @CheckResult boolean isPeriodicWifi();

  @CheckResult long getPeriodicDisableTimeWifi();

  void setPeriodicDisableTimeWifi(long time);

  @CheckResult long getPeriodicEnableTimeWifi();

  void setPeriodicEnableTimeWifi(long time);
}