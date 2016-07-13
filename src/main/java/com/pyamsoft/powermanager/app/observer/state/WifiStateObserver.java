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

package com.pyamsoft.powermanager.app.observer.state;

import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import timber.log.Timber;

public class WifiStateObserver extends StateContentObserver {

  @NonNull private final View view;

  public WifiStateObserver(@NonNull Context context, @NonNull View view) {
    super(context);
    this.view = view;

    Uri uri;
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
      uri = Settings.Global.getUriFor(Settings.Global.WIFI_ON);
    } else {
      //noinspection deprecation
      uri = Settings.Secure.getUriFor(Settings.Secure.WIFI_ON);
    }
    setUri(uri);
  }

  @Override public void onChange(boolean selfChange, Uri uri) {
    Timber.d("onChange. SELF: %s URI: %s", selfChange, uri);
    if (is()) {
      view.onWifiStateEnabled();
    } else {
      view.onWifiStateDisabled();
    }
  }

  @Override public boolean is() {
    boolean enabled;
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
      enabled =
          Settings.Global.getInt(getAppContext().getContentResolver(), Settings.Global.WIFI_ON, 0)
              == 1;
    } else {
      //noinspection deprecation
      enabled =
          Settings.Secure.getInt(getAppContext().getContentResolver(), Settings.Secure.WIFI_ON, 0)
              == 1;
    }
    return enabled;
  }

  public interface View {

    void onWifiStateEnabled();

    void onWifiStateDisabled();
  }
}