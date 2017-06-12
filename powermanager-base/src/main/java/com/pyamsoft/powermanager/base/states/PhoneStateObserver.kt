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
import android.telephony.TelephonyManager
import com.pyamsoft.powermanager.model.StateObserver
import javax.inject.Inject

internal class PhoneStateObserver @Inject internal constructor(context: Context) : StateObserver {

  private val appContext = context.applicationContext
  private val telephonyManager = appContext.getSystemService(
      Context.TELEPHONY_SERVICE) as? TelephonyManager

  override fun enabled(): Boolean {
    val obj = telephonyManager
    if (obj == null) {
      throw IllegalAccessException(
          "Guard calls to enabled() with a call to if (!unknown()) { ... }")
    } else {
      return obj.callState != TelephonyManager.CALL_STATE_IDLE
    }
  }

  override fun unknown(): Boolean {
    return telephonyManager == null
  }

}

