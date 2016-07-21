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

package com.pyamsoft.powermanager.dagger.observer.manage;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import com.pyamsoft.powermanager.PowerManagerPreferences;
import com.pyamsoft.powermanager.R;
import com.pyamsoft.powermanager.app.observer.InterestObserver;
import timber.log.Timber;

abstract class ManagePreferenceObserver<V>
    implements SharedPreferences.OnSharedPreferenceChangeListener, InterestObserver<V> {

  @NonNull private final String KEY_WIFI;
  @NonNull private final String KEY_DATA;
  @NonNull private final String KEY_BLUETOOTH;
  @NonNull private final String KEY_SYNC;
  @NonNull private final String KEY_WEAR;
  @NonNull private final PowerManagerPreferences preferences;
  @NonNull private final String key;
  private boolean registered;

  protected ManagePreferenceObserver(@NonNull Context context,
      @NonNull PowerManagerPreferences preferences, @NonNull String key) {
    this.preferences = preferences;
    this.key = key;

    KEY_WIFI = context.getString(R.string.manage_wifi_key);
    KEY_DATA = context.getString(R.string.manage_data_key);
    KEY_BLUETOOTH = context.getString(R.string.manage_bluetooth_key);
    KEY_SYNC = context.getString(R.string.manage_sync_key);
    KEY_WEAR = context.getString(R.string.manage_wearable_key);
  }

  @Override
  public final void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
    if (key.equals(s)) {
      Timber.d("Received preference change for key: %s", s);
      onChange();
    } else {
      Timber.e("Received preference change for other key: %s", s);
    }
  }

  @Override public final void register() {
    if (!registered) {
      Timber.d("Register new state observer for: %s", key);
      preferences.register(this);
      registered = true;
    } else {
      Timber.e("Already registered");
    }
  }

  @Override public final void unregister() {
    if (registered) {
      Timber.d("Unregister new state observer");
      preferences.unregister(this);
      registered = false;
    } else {
      Timber.e("Already unregistered");
    }
  }

  @Override public final boolean is() {
    boolean result;
    if (key.equals(KEY_WIFI)) {
      result = preferences.isWifiManaged();
    } else if (key.equals(KEY_DATA)) {
      result = preferences.isDataManaged();
    } else if (key.equals(KEY_BLUETOOTH)) {
      result = preferences.isBluetoothManaged();
    } else if (key.equals(KEY_SYNC)) {
      result = preferences.isSyncManaged();
    } else if (key.equals(KEY_WEAR)) {
      result = preferences.isWearableManaged();
    } else {
      throw new RuntimeException("Unsupported key: " + key);
    }

    return result;
  }

  abstract void onChange();
}
