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
import android.support.annotation.NonNull;
import com.pyamsoft.powermanager.PowerManagerPreferences;
import com.pyamsoft.powermanager.R;
import javax.inject.Inject;

final class ManagerSettingsInteractorImpl implements ManagerSettingsInteractor {

  @NonNull private final PowerManagerPreferences preferences;
  @NonNull private final String KEY_DELAY_WIFI;
  @NonNull private final String KEY_DELAY_DATA;
  @NonNull private final String KEY_DELAY_BLUETOOTH;
  @NonNull private final String KEY_DELAY_SYNC;

  @Inject ManagerSettingsInteractorImpl(@NonNull Context context,
      @NonNull PowerManagerPreferences preferences) {
    this.preferences = preferences;
    final Context appContext = context.getApplicationContext();
    KEY_DELAY_WIFI = appContext.getString(R.string.wifi_time_key);
    KEY_DELAY_DATA = appContext.getString(R.string.data_time_key);
    KEY_DELAY_BLUETOOTH = appContext.getString(R.string.bluetooth_time_key);
    KEY_DELAY_SYNC = appContext.getString(R.string.sync_time_key);
  }

  @Override public void setDelayTime(@NonNull String key, long time) {
    if (key.equals(KEY_DELAY_WIFI)) {
      preferences.setWifiDelay(time);
    } else if (key.equals(KEY_DELAY_DATA)) {
      preferences.setDataDelay(time);
    } else if (key.equals(KEY_DELAY_BLUETOOTH)) {
      preferences.setBluetoothDelay(time);
    } else if (key.equals(KEY_DELAY_SYNC)) {
      preferences.setMasterSyncDelay(time);
    } else {
      throw new IllegalStateException("Invalid KEY: " + key);
    }
  }

  @Override public long getDelayTime(@NonNull String key) {
    long time;
    if (key.equals(KEY_DELAY_WIFI)) {
      time = preferences.getWifiDelay();
    } else if (key.equals(KEY_DELAY_DATA)) {
      time = preferences.getDataDelay();
    } else if (key.equals(KEY_DELAY_BLUETOOTH)) {
      time = preferences.getBluetoothDelay();
    } else if (key.equals(KEY_DELAY_SYNC)) {
      time = preferences.getMasterSyncDelay();
    } else {
      throw new IllegalStateException("Invalid KEY: " + key);
    }
    return time;
  }
}
