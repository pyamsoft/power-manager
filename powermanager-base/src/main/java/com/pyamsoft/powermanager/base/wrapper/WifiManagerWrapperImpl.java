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

package com.pyamsoft.powermanager.base.wrapper;

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

  private void toggle(boolean state) {
    if (wifiManager != null) {
      Timber.i("Wifi: %s", state ? "enable" : "disable");
      wifiManager.setWifiEnabled(state);
    }
  }

  @Override public void enable() {
    toggle(true);
  }

  @Override public void disable() {
    toggle(false);
  }

  @Override @CheckResult public boolean isEnabled() {
    return wifiManager != null && wifiManager.isWifiEnabled();
  }
}
