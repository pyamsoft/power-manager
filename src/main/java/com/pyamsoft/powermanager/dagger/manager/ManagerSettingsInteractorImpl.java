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

package com.pyamsoft.powermanager.dagger.manager;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import com.pyamsoft.powermanager.PowerManagerPreferences;
import com.pyamsoft.powermanager.R;
import rx.Observable;

public abstract class ManagerSettingsInteractorImpl implements ManagerSettingsInteractor {

  @NonNull private final Context appContext;
  @NonNull private final PowerManagerPreferences preferences;
  @NonNull protected final String KEY_MANAGE_WIFI;
  @NonNull protected final String KEY_MANAGE_DATA;
  @NonNull protected final String KEY_MANAGE_BLUETOOTH;
  @NonNull protected final String KEY_MANAGE_SYNC;

  protected ManagerSettingsInteractorImpl(@NonNull Context context,
      @NonNull PowerManagerPreferences preferences) {
    appContext = context.getApplicationContext();
    this.preferences = preferences;
    KEY_MANAGE_WIFI = appContext.getString(R.string.manage_wifi_key);
    KEY_MANAGE_DATA = appContext.getString(R.string.manage_data_key);
    KEY_MANAGE_BLUETOOTH = appContext.getString(R.string.manage_bluetooth_key);
    KEY_MANAGE_SYNC = appContext.getString(R.string.manage_sync_key);
  }

  @CheckResult @NonNull public Context getAppContext() {
    return appContext;
  }

  @CheckResult @NonNull public PowerManagerPreferences getPreferences() {
    return preferences;
  }

  @Override public final void registerSharedPreferenceChangeListener(
      @NonNull SharedPreferences.OnSharedPreferenceChangeListener listener, @NonNull String key) {
    if (!key.equals(KEY_MANAGE_WIFI) && !key.equals(KEY_MANAGE_DATA) && !key.equals(
        KEY_MANAGE_BLUETOOTH) && !key.equals(KEY_MANAGE_SYNC)) {
      throw new IllegalStateException("Invalid key");
    }
    preferences.register(listener);
  }

  @Override public final void unregisterSharedPreferenceChangeListener(
      @NonNull SharedPreferences.OnSharedPreferenceChangeListener listener) {
    preferences.unregister(listener);
  }

  @NonNull @Override public Observable<Boolean> isManaged(@NonNull String key) {
    return Observable.defer(() -> {
      boolean managed;
      if (key.equals(KEY_MANAGE_WIFI)) {
        managed = preferences.isWifiManaged();
      } else if (key.equals(KEY_MANAGE_DATA)) {
        managed = preferences.isDataManaged();
      } else if (key.equals(KEY_MANAGE_BLUETOOTH)) {
        managed = preferences.isBluetoothManaged();
      } else if (key.equals(KEY_MANAGE_SYNC)) {
        managed = preferences.isSyncManaged();
      } else {
        throw new IllegalStateException("Invalid key");
      }
      return Observable.just(managed);
    });
  }
}

