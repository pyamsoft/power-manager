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

import android.content.SharedPreferences
import com.pyamsoft.powermanager.base.preference.AirplanePreferences
import com.pyamsoft.powermanager.base.preference.BluetoothPreferences
import com.pyamsoft.powermanager.base.preference.DataPreferences
import com.pyamsoft.powermanager.base.preference.DataSaverPreferences
import com.pyamsoft.powermanager.base.preference.DozePreferences
import com.pyamsoft.powermanager.base.preference.ManagePreferences
import com.pyamsoft.powermanager.base.preference.SyncPreferences
import com.pyamsoft.powermanager.base.preference.WifiPreferences
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module class TimeSingletonModule {

  @Singleton @Provides internal fun provideManageDelayInteractor(
      preferences: ManagePreferences): TimeInteractor {
    return TimeInteractor(object : TimePreferenceWrapper {
      override val time: Long
        get() = preferences.manageDelay
      override val isCustom: Boolean
        get() = preferences.customManageDelay

      override fun setTime(time: Long, custom: Boolean) {
        preferences.manageDelay = time
        preferences.customManageDelay = custom
      }

      override fun registerTimeChanges(
          listener: (Long) -> Unit): SharedPreferences.OnSharedPreferenceChangeListener {
        return preferences.registerDelayChanges(listener)
      }

      override fun unregisterTimeChanges(
          listener: SharedPreferences.OnSharedPreferenceChangeListener) {
        preferences.unregisterDelayChanges(listener)
      }
    })
  }

  @Singleton @Provides internal fun provideManageDisableInteractor(wifiPreferences: WifiPreferences,
      dataPreferences: DataPreferences, bluetothPreferences: BluetoothPreferences,
      syncPreferences: SyncPreferences, airplanePreferences: AirplanePreferences,
      dataSaverPreferences: DataSaverPreferences, dozePreferences: DozePreferences,
      preferences: ManagePreferences): PollInteractor {
    return PollInteractor(wifiPreferences, dataPreferences, bluetoothPreferences, syncPreferences,
        airplanePreferences, dozePreferences, dataSaverPreferences, object : TimePreferenceWrapper {
      override val isCustom: Boolean
        get() = preferences.customDisableTime
      override val time: Long
        get() = preferences.periodicDisableTime

      override fun setTime(time: Long, custom: Boolean) {
        preferences.periodicDisableTime = time
        preferences.customDisableTime = custom
      }

      override fun registerTimeChanges(
          listener: (Long) -> Unit): SharedPreferences.OnSharedPreferenceChangeListener {
        return preferences.registerDisableChanges(listener)
      }

      override fun unregisterTimeChanges(
          listener: SharedPreferences.OnSharedPreferenceChangeListener) {
        preferences.unregisterDisableChanges(listener)
      }
    })
  }
}
