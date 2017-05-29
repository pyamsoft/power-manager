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

package com.pyamsoft.powermanager.manage

import android.support.annotation.CheckResult
import com.pyamsoft.powermanager.base.preference.AirplanePreferences
import com.pyamsoft.powermanager.base.preference.BluetoothPreferences
import com.pyamsoft.powermanager.base.preference.DataPreferences
import com.pyamsoft.powermanager.base.preference.DozePreferences
import com.pyamsoft.powermanager.base.preference.SyncPreferences
import com.pyamsoft.powermanager.base.preference.WifiPreferences
import io.reactivex.Completable
import io.reactivex.Single
import javax.inject.Inject

internal class PollInteractor @Inject internal constructor(
    internal val wifiPreferences: WifiPreferences, internal val dataPreferences: DataPreferences,
    internal val bluetoothPreferences: BluetoothPreferences,
    internal val syncPreferences: SyncPreferences,
    internal val airplanePreferences: AirplanePreferences,
    internal val dozePreferences: DozePreferences,
    preferenceWrapper: TimePreferenceWrapper) : TimeInteractor(preferenceWrapper) {

  @CheckResult fun toggleAll(checked: Boolean): Completable {
    return Completable.fromAction {
      wifiPreferences.periodicWifi = checked
      dataPreferences.periodicData = checked
      bluetoothPreferences.periodicBluetooth = checked
      syncPreferences.periodicSync = checked
      airplanePreferences.periodicAirplane = checked
      dozePreferences.periodicDoze = checked
    }
  }

  @CheckResult fun getCurrentState(): Single<Boolean> {
    return Single.fromCallable {
      wifiPreferences.periodicWifi && dataPreferences.periodicData && bluetoothPreferences.periodicBluetooth && syncPreferences.periodicSync && airplanePreferences.periodicAirplane && dozePreferences.periodicDoze
    }
  }
}

