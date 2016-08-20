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

package com.pyamsoft.powermanager.dagger.modifier.state;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.pyamsoft.powermanager.PowerManagerPreferences;
import javax.inject.Inject;
import rx.Scheduler;
import timber.log.Timber;

class WifiStateModifier extends StateModifier {

  @Nullable private final WifiManager wifiManager;

  @Inject WifiStateModifier(@NonNull Context context, @NonNull PowerManagerPreferences preferences,
      @NonNull Scheduler subscribeScheduler, @NonNull Scheduler observeScheduler) {
    super(context, preferences, subscribeScheduler, observeScheduler);
    wifiManager =
        (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
  }

  @Override void set(@NonNull Context context, @NonNull PowerManagerPreferences preferences) {
    if (wifiManager != null) {
      Timber.d("setWifiEnabled: true");
      wifiManager.setWifiEnabled(true);
    }
  }

  @Override void unset(@NonNull Context context, @NonNull PowerManagerPreferences preferences) {
    if (wifiManager != null) {
      Timber.d("setWifiEnabled: false");
      wifiManager.setWifiEnabled(false);
    }
  }
}
