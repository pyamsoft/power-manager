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

package com.pyamsoft.powermanager.base.states

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.support.annotation.CheckResult
import com.pyamsoft.powermanager.model.StateObserver
import timber.log.Timber
import javax.inject.Inject

internal class ChargingStateObserver @Inject constructor(context: Context) : StateObserver {

  private val appContext: Context = context.applicationContext
  private val filter: IntentFilter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)

  init {
    Timber.d("new ChargingStateObserver instance")
  }

  private // Are we charging / charged?
  val status: Int
    @CheckResult get() {
      val batteryStatus = appContext.registerReceiver(null, filter)
      val status: Int
      if (batteryStatus == null) {
        Timber.e("NULL BatteryStatus Intent, return Unknown")
        status = BatteryManager.BATTERY_STATUS_UNKNOWN
      } else {
        status = batteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS,
            BatteryManager.BATTERY_STATUS_UNKNOWN)
      }

      return status
    }

  private val isCharging: Boolean
    @CheckResult get() {
      val status = status
      return status == BatteryManager.BATTERY_STATUS_CHARGING || status == BatteryManager.BATTERY_STATUS_FULL
    }

  override fun enabled(): Boolean {
    return isCharging
  }

  override fun unknown(): Boolean {
    return status == BatteryManager.BATTERY_STATUS_UNKNOWN
  }
}
