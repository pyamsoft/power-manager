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

package com.pyamsoft.powermanager.dagger.wrapper;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.os.Build;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import javax.inject.Inject;
import timber.log.Timber;

class BluetoothAdapterWrapperImpl implements BluetoothAdapterWrapper {

  @Nullable private final BluetoothAdapter adapter;

  @Inject BluetoothAdapterWrapperImpl(@NonNull Context context) {
    this.adapter = getBluetoothAdapter(context);
  }

  @CheckResult @Nullable private BluetoothAdapter getBluetoothAdapter(@NonNull Context context) {
    final BluetoothAdapter adapter;
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR2) {
      adapter = BluetoothAdapter.getDefaultAdapter();
    } else {
      final BluetoothManager bluetoothManager = (BluetoothManager) context.getApplicationContext()
          .getSystemService(Context.BLUETOOTH_SERVICE);
      adapter = bluetoothManager.getAdapter();
    }
    return adapter;
  }

  @Override public void enable() {
    if (adapter != null) {
      Timber.d("Bluetooth: enable");
      adapter.enable();
    }
  }

  @Override public void disable() {
    if (adapter != null) {
      Timber.d("Bluetooth: disable");
      adapter.disable();
    }
  }

  @Override @CheckResult public boolean isEnabled() {
    final boolean enabled = adapter != null && adapter.isEnabled();
    Timber.d("Bluetooth enabled: %s", enabled);
    return enabled;
  }
}
