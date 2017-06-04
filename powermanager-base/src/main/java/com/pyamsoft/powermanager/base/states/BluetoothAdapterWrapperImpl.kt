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

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothProfile
import android.content.Context
import com.pyamsoft.powermanager.base.logger.Logger
import com.pyamsoft.powermanager.model.Connections
import com.pyamsoft.powermanager.model.States
import timber.log.Timber
import javax.inject.Inject

internal class BluetoothAdapterWrapperImpl @Inject internal constructor(context: Context,
    private val logger: Logger) : ConnectedDeviceFunctionWrapper {
  private val adapter: BluetoothAdapter?
  private val bluetoothManager: BluetoothManager = context.applicationContext.getSystemService(
      Context.BLUETOOTH_SERVICE) as BluetoothManager

  init {
    this.adapter = bluetoothManager.adapter
  }

  private fun toggle(state: Boolean) {
    if (adapter != null) {
      logger.i("Bluetooth: %s", if (state) "enable" else "disable")
      if (state) {
        adapter.enable()
      } else {
        adapter.disable()
      }
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
      if (adapter == null) {
        Timber.w("Bluetooth state unknown")
        return States.UNKNOWN
      } else {
        return if (adapter.isEnabled) States.ENABLED else States.DISABLED
      }
    }
  override // Check if we are connected to any profiles
      // Connected to profile
      // Check if we are connected to any devices
      // Get a list of devices that are connected/connecting
      // Docs say list will be empty, null check just to be safe
      // Connected to device
      // We are not connected to anything
  val connectionState: Connections
    get() {
      if (adapter == null) {
        Timber.w("Bluetooth connection state unknown")
        return Connections.UNKNOWN
      } else {
        for (profile in BLUETOOTH_ADAPTER_PROFILES) {
          val connectionState = adapter.getProfileConnectionState(profile)
          if (connectionState == BluetoothAdapter.STATE_CONNECTED || connectionState == BluetoothAdapter.STATE_CONNECTING) {
            Timber.d("Connected to bluetooth adapter profile: %d", profile)
            return Connections.CONNECTED
          }
        }
        for (profile in BLUETOOTH_MANAGER_PROFILES) {
          val devices = bluetoothManager.getDevicesMatchingConnectionStates(profile,
              BLUETOOTH_CONNECTED_STATES)
          if (devices != null) {
            if (devices.size > 0) {
              Timber.d("Connected to bluetooth manager device: %s (%d)", devices[0].name, profile)
              return Connections.CONNECTED
            }
          }
        }
        Timber.d("Bluetooth not connected")
        return Connections.DISCONNECTED
      }
    }

  companion object {
    private val BLUETOOTH_ADAPTER_PROFILES = intArrayOf(BluetoothProfile.A2DP,
        BluetoothProfile.HEADSET, BluetoothProfile.HEALTH)
    private val BLUETOOTH_MANAGER_PROFILES = intArrayOf(BluetoothProfile.GATT,
        BluetoothProfile.GATT_SERVER)
    private val BLUETOOTH_CONNECTED_STATES = intArrayOf(BluetoothAdapter.STATE_CONNECTED,
        BluetoothAdapter.STATE_CONNECTING)
  }
}
