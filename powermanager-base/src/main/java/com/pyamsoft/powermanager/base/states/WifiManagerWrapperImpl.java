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

import android.content.Context;
import android.net.wifi.WifiManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.pyamsoft.powermanager.base.logger.Logger;
import com.pyamsoft.powermanager.model.Connections;
import com.pyamsoft.powermanager.model.States;
import javax.inject.Inject;
import timber.log.Timber;

class WifiManagerWrapperImpl implements ConnectedDeviceFunctionWrapper {

  @Nullable private final WifiManager wifiManager;
  @NonNull private final Logger logger;

  @Inject WifiManagerWrapperImpl(@NonNull Context context, @NonNull Logger logger) {
    this.wifiManager =
        (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
    this.logger = logger;
  }

  private void toggle(boolean state) {
    if (wifiManager != null) {
      logger.i("Wifi: %s", state ? "enable" : "disable");
      wifiManager.setWifiEnabled(state);
    }
  }

  @Override public void enable() {
    toggle(true);
  }

  @Override public void disable() {
    toggle(false);
  }

  @NonNull @Override public States getState() {
    if (wifiManager == null) {
      Timber.w("Wifi state unknown");
      return States.UNKNOWN;
    } else {
      return wifiManager.isWifiEnabled() ? States.ENABLED : States.DISABLED;
    }
  }

  @Override @NonNull public Connections getConnectionState() {
    if (wifiManager == null) {
      Timber.w("Wifi connection state unknown");
      return Connections.UNKNOWN;
    } else {
      return wifiManager.getConnectionInfo() == null ? Connections.DISCONNECTED
          : Connections.CONNECTED;
    }
  }
}
