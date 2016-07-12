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

package com.pyamsoft.powermanager.app.observer;

import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import timber.log.Timber;

public class DataStateObserver extends StateObserver {

  @NonNull private static final String SETTINGS_MOBILE_DATA = "mobile_data";
  @NonNull private final DataStateObserverView view;
  @NonNull private final Uri uri;

  public DataStateObserver(@NonNull Context context, @NonNull DataStateObserverView view) {
    super(context);
    this.view = view;
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
      uri = Settings.Global.getUriFor(Settings.Global.WIFI_ON);
    } else {
      uri = Settings.Secure.getUriFor(Settings.Secure.WIFI_ON);
    }
  }

  @Override public boolean deliverSelfNotifications() {
    return false;
  }

  @Override public void onChange(boolean selfChange) {
    onChange(selfChange, null);
  }

  @Override public void onChange(boolean selfChange, Uri uri) {
    Timber.d("onChange. SELF: %s URI: %s", selfChange, uri);
    if (isEnabled()) {
      view.onDataStateEnabled();
    } else {
      view.onDataStateDisabled();
    }
  }

  @SuppressWarnings("deprecation") @Override boolean isEnabled() {
    boolean enabled;
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
      enabled =
          Settings.Global.getInt(getAppContext().getContentResolver(), SETTINGS_MOBILE_DATA, 0)
              == 1;
    } else {
      enabled =
          Settings.Secure.getInt(getAppContext().getContentResolver(), SETTINGS_MOBILE_DATA, 0)
              == 1;
    }
    return enabled;
  }

  @Override public void register() {
    register(uri);
  }

  public interface DataStateObserverView {

    void onDataStateEnabled();

    void onDataStateDisabled();
  }
}
