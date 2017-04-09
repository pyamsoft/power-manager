/*
 * Copyright 2016 Peter Kenji Yamanaka
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
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.pyamsoft.powermanager.base.logger.Logger;
import com.pyamsoft.powermanager.model.States;
import javax.inject.Inject;

class BluetoothAdapterWrapperImpl implements DeviceFunctionWrapper {

  @NonNull private final Logger logger;
  @Nullable private final BluetoothAdapter adapter;

  @Inject BluetoothAdapterWrapperImpl(@NonNull Context context, @NonNull Logger logger) {
    this.adapter = getBluetoothAdapter(context);
    this.logger = logger;
  }

  @CheckResult @Nullable private BluetoothAdapter getBluetoothAdapter(@NonNull Context context) {
    BluetoothManager bluetoothManager = (BluetoothManager) context.getApplicationContext()
        .getSystemService(Context.BLUETOOTH_SERVICE);
    return bluetoothManager.getAdapter();
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
      return States.UNKNOWN;
    } else {
      return adapter.isEnabled() ? States.ENABLED : States.DISABLED;
    }
  }
}