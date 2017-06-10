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
import com.pyamsoft.powermanager.base.preference.DataSaverPreferences
import com.pyamsoft.powermanager.base.preference.DozePreferences
import com.pyamsoft.powermanager.base.preference.SyncPreferences
import com.pyamsoft.powermanager.base.preference.WifiPreferences
import io.reactivex.Completable
import io.reactivex.Single
import javax.inject.Inject

internal class PollInteractor @Inject internal constructor(
    private val wifiPreferences: WifiPreferences, private val dataPreferences: DataPreferences,
    private val bluetoothPreferences: BluetoothPreferences,
    private val syncPreferences: SyncPreferences,
    private val airplanePreferences: AirplanePreferences,
    private val dozePreferences: DozePreferences,
    private val dataSaverPreferences: DataSaverPreferences,
    preferenceWrapper: TimePreferenceWrapper) : TimeInteractor(preferenceWrapper) {

  @CheckResult fun toggleAll(checked: Boolean): Completable {
    return Completable.fromAction {
      wifiPreferences.periodicWifi = checked
      dataPreferences.periodicData = checked
      bluetoothPreferences.periodicBluetooth = checked
      syncPreferences.periodicSync = checked
      airplanePreferences.periodicAirplane = checked
      dozePreferences.periodicDoze = checked
      dataSaverPreferences.periodicDataSaver = checked
    }
  }

  @CheckResult fun getCurrentState(): Single<Boolean> {
    return Single.fromCallable {
      wifiPreferences.periodicWifi && dataPreferences.periodicData && bluetoothPreferences.periodicBluetooth && syncPreferences.periodicSync && airplanePreferences.periodicAirplane && dozePreferences.periodicDoze && dataSaverPreferences.periodicDataSaver
    }
  }
}

