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
import android.net.wifi.WifiManager
import com.pyamsoft.powermanager.base.logger.Logger
import com.pyamsoft.powermanager.model.Connections
import com.pyamsoft.powermanager.model.States
import timber.log.Timber
import javax.inject.Inject

internal class WifiManagerWrapperImpl @Inject internal constructor(context: Context,
    private val logger: Logger) : ConnectedDeviceFunctionWrapper {
  private val wifiManager: WifiManager?

  init {
    this.wifiManager = context.applicationContext.getSystemService(
        Context.WIFI_SERVICE) as WifiManager
  }

  private fun toggle(state: Boolean) {
    if (wifiManager != null) {
      logger.i("Wifi: %s", if (state) "enable" else "disable")
      wifiManager.isWifiEnabled = state
    }
  }

  override fun enable() {
    toggle(true)
  }

  override fun disable() {
    toggle(false)
  }

  override val state: States
    get() {
      if (wifiManager == null) {
        Timber.w("Wifi state unknown")
        return States.UNKNOWN
      } else {
        return if (wifiManager.isWifiEnabled) States.ENABLED else States.DISABLED
      }
    }
  override val connectionState: Connections
    get() {
      if (wifiManager == null) {
        Timber.w("Wifi connection state unknown")
        return Connections.UNKNOWN
      } else {
        return if (wifiManager.connectionInfo == null) Connections.DISCONNECTED
        else Connections.CONNECTED
      }
    }
}
