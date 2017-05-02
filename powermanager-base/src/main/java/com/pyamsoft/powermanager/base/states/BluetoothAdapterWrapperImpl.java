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

package com.pyamsoft.powermanager.base.states;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.pyamsoft.powermanager.base.logger.Logger;
import com.pyamsoft.powermanager.model.Connections;
import com.pyamsoft.powermanager.model.States;
import java.util.List;
import javax.inject.Inject;
import timber.log.Timber;

class BluetoothAdapterWrapperImpl implements ConnectedDeviceFunctionWrapper {

  @NonNull private static final int[] BLUETOOTH_ADAPTER_PROFILES =
      { BluetoothProfile.A2DP, BluetoothProfile.HEADSET, BluetoothProfile.HEALTH };
  @NonNull private static final int[] BLUETOOTH_MANAGER_PROFILES =
      { BluetoothProfile.GATT, BluetoothProfile.GATT_SERVER };
  @NonNull private static final int[] BLUETOOTH_CONNECTED_STATES =
      { BluetoothAdapter.STATE_CONNECTED, BluetoothAdapter.STATE_CONNECTING };

  @NonNull private final Logger logger;
  @Nullable private final BluetoothAdapter adapter;
  @NonNull private final BluetoothManager bluetoothManager;

  @Inject BluetoothAdapterWrapperImpl(@NonNull Context context, @NonNull Logger logger) {
    this.bluetoothManager = (BluetoothManager) context.getApplicationContext()
        .getSystemService(Context.BLUETOOTH_SERVICE);
    this.adapter = bluetoothManager.getAdapter();
    this.logger = logger;
  }

  private void toggle(boolean state) {
    if (adapter != null) {
      logger.i("Bluetooth: %s", state ? "enable" : "disable");
      if (state) {
        adapter.enable();
      } else {
        adapter.disable();
      }
    }
  }

  @Override public void enable() {
    toggle(true);
  }

  @Override public void disable() {
    toggle(false);
  }

  @NonNull @Override public States getState() {
    if (adapter == null) {
      Timber.w("Bluetooth state unknown");
      return States.UNKNOWN;
    } else {
      return adapter.isEnabled() ? States.ENABLED : States.DISABLED;
    }
  }

  @NonNull @Override public Connections getConnectionState() {
    if (adapter == null) {
      Timber.w("Bluetooth connection state unknown");
      return Connections.UNKNOWN;
    } else {
      for (int profile : BLUETOOTH_ADAPTER_PROFILES) {
        // Check if we are connected to any profiles
        final int connectionState = adapter.getProfileConnectionState(profile);
        if (connectionState == BluetoothAdapter.STATE_CONNECTED
            || connectionState == BluetoothAdapter.STATE_CONNECTING) {
          // Connected to profile
          Timber.d("Connected to bluetooth adapter profile: %d", profile);
          return Connections.CONNECTED;
        }
      }

      // Check if we are connected to any devices
      for (int profile : BLUETOOTH_MANAGER_PROFILES) {
        // Get a list of devices that are connected/connecting
        List<BluetoothDevice> devices = bluetoothManager.getDevicesMatchingConnectionStates(profile,
            BLUETOOTH_CONNECTED_STATES);

        // Docs say list will be empty, null check just to be safe
        if (devices != null) {
          if (devices.size() > 0) {
            // Connected to device
            Timber.d("Connected to bluetooth manager device: %s (%d)", devices.get(0).getName(),
                profile);
            return Connections.CONNECTED;
          }
        }
      }

      // We are not connected to anything
      Timber.d("Bluetooth not connected");
      return Connections.DISCONNECTED;
    }
  }
}
