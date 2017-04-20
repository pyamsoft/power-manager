/*
 * Copyright 2017 Peter Kenji Yamanaka
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

package com.pyamsoft.powermanager.overview;

import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import com.pyamsoft.powermanager.base.preference.AirplanePreferences;
import com.pyamsoft.powermanager.base.preference.BluetoothPreferences;
import com.pyamsoft.powermanager.base.preference.DataPreferences;
import com.pyamsoft.powermanager.base.preference.DozePreferences;
import com.pyamsoft.powermanager.base.preference.OnboardingPreferences;
import com.pyamsoft.powermanager.base.preference.SyncPreferences;
import com.pyamsoft.powermanager.base.preference.WearablePreferences;
import com.pyamsoft.powermanager.base.preference.WifiPreferences;
import io.reactivex.Single;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton class OverviewInteractor {

  @SuppressWarnings("WeakerAccess") @NonNull final OnboardingPreferences preferences;
  @SuppressWarnings("WeakerAccess") @NonNull final WearablePreferences wearablePreferences;
  @SuppressWarnings("WeakerAccess") @NonNull final WifiPreferences wifiPreferences;
  @SuppressWarnings("WeakerAccess") @NonNull final DataPreferences dataPreferences;
  @SuppressWarnings("WeakerAccess") @NonNull final BluetoothPreferences bluetoothPreferences;
  @SuppressWarnings("WeakerAccess") @NonNull final SyncPreferences syncPreferences;
  @SuppressWarnings("WeakerAccess") @NonNull final AirplanePreferences airplanePreferences;
  @SuppressWarnings("WeakerAccess") @NonNull final DozePreferences dozePreferences;

  @Inject OverviewInteractor(@NonNull OnboardingPreferences preferences,
      @NonNull WearablePreferences wearablePreferences, @NonNull WifiPreferences wifiPreferences,
      @NonNull DataPreferences dataPreferences, @NonNull BluetoothPreferences bluetoothPreferences,
      @NonNull SyncPreferences syncPreferences, @NonNull AirplanePreferences airplanePreferences,
      @NonNull DozePreferences dozePreferences) {
    this.preferences = preferences;
    this.wearablePreferences = wearablePreferences;
    this.wifiPreferences = wifiPreferences;
    this.dataPreferences = dataPreferences;
    this.bluetoothPreferences = bluetoothPreferences;
    this.syncPreferences = syncPreferences;
    this.airplanePreferences = airplanePreferences;
    this.dozePreferences = dozePreferences;
  }

  /**
   * public
   */
  @NonNull @CheckResult Single<Boolean> hasShownOnboarding() {
    return Single.fromCallable(preferences::isOverviewOnboardingShown).delay(1, TimeUnit.SECONDS);
  }

  /**
   * public
   */
  @CheckResult @NonNull Single<Boolean> isWifiManaged() {
    return Single.fromCallable(wifiPreferences::isWifiManaged);
  }

  /**
   * public
   */
  @CheckResult @NonNull Single<Boolean> isDataManaged() {
    return Single.fromCallable(dataPreferences::isDataManaged);
  }

  /**
   * public
   */
  @CheckResult @NonNull Single<Boolean> isBluetoothManaged() {
    return Single.fromCallable(bluetoothPreferences::isBluetoothManaged);
  }

  /**
   * public
   */
  @CheckResult @NonNull Single<Boolean> isSyncManaged() {
    return Single.fromCallable(syncPreferences::isSyncManaged);
  }

  /**
   * public
   */
  @CheckResult @NonNull Single<Boolean> isAirplaneManaged() {
    return Single.fromCallable(airplanePreferences::isAirplaneManaged);
  }

  /**
   * public
   */
  @CheckResult @NonNull Single<Boolean> isDozeManaged() {
    return Single.fromCallable(dozePreferences::isDozeManaged);
  }
}
