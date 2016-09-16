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

import android.content.Context;
import android.net.wifi.WifiManager;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import javax.inject.Inject;
import timber.log.Timber;

class WifiManagerWrapperImpl implements DeviceFunctionWrapper {

  @Nullable private final WifiManager wifiManager;

  @Inject WifiManagerWrapperImpl(@NonNull Context context) {
    this.wifiManager =
        (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
  }

  @Override public void enable() {
    if (wifiManager != null) {
      Timber.d("Wifi: enable");
      wifiManager.setWifiEnabled(true);
    }
  }

  @Override public void disable() {
    if (wifiManager != null) {
      Timber.d("Wifi: disable");
      wifiManager.setWifiEnabled(false);
    }
  }

  @Override @CheckResult public boolean isEnabled() {
    final boolean enabled = wifiManager != null && wifiManager.isWifiEnabled();
    Timber.d("Wifi enabled: %s", enabled);
    return enabled;
  }
}
