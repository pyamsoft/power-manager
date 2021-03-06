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

import com.pyamsoft.powermanager.model.ConnectedStateObserver
import com.pyamsoft.powermanager.model.Connections
import com.pyamsoft.powermanager.model.States
import timber.log.Timber
import javax.inject.Inject

internal class WifiStateObserver @Inject internal constructor(
    private val wrapper: ConnectedDeviceFunctionWrapper) : ConnectedStateObserver {

  init {
    Timber.d("New StateObserver for Wifi")
  }

  override fun enabled(): Boolean {
    val enabled = wrapper.state === States.ENABLED
    Timber.d("Enabled: %s", enabled)
    return enabled
  }

  override fun unknown(): Boolean {
    val unknown = wrapper.state === States.UNKNOWN
    Timber.d("Unknown: %s", unknown)
    return unknown
  }

  override fun connected(): Boolean {
    val connected = wrapper.connectionState === Connections.CONNECTED
    Timber.d("Connected: %s", connected)
    return connected
  }

  override fun connectionUnknown(): Boolean {
    val unknown = wrapper.connectionState === Connections.UNKNOWN
    Timber.d("Connection unknown: %s", unknown)
    return unknown
  }
}
