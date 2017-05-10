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

import android.support.annotation.NonNull;
import com.pyamsoft.powermanager.model.ConnectedStateObserver;
import com.pyamsoft.powermanager.model.Connections;
import com.pyamsoft.powermanager.model.States;
import javax.inject.Inject;
import timber.log.Timber;

class BluetoothStateObserver implements ConnectedStateObserver {

  @NonNull private final ConnectedDeviceFunctionWrapper wrapper;

  @Inject BluetoothStateObserver(@NonNull ConnectedDeviceFunctionWrapper wrapper) {
    this.wrapper = wrapper;
    Timber.d("New StateObserver for Bluetooth");
  }

  @Override public boolean enabled() {
    final boolean enabled = wrapper.getState() == States.ENABLED;
    Timber.d("Enabled: %s", enabled);
    return enabled;
  }

  @Override public boolean unknown() {
    final boolean unknown = wrapper.getState() == States.UNKNOWN;
    Timber.d("Unknown: %s", unknown);
    return unknown;
  }

  @Override public boolean connected() {
    final boolean connected = wrapper.getConnectionState() == Connections.CONNECTED;
    Timber.d("Connected: %s", connected);
    return connected;
  }

  @Override public boolean connectionUnknown() {
    final boolean unknown = wrapper.getConnectionState() == Connections.UNKNOWN;
    Timber.d("Connection unknown: %s", unknown);
    return unknown;
  }
}
