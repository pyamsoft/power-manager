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

package com.pyamsoft.powermanager.dagger.manager.custom;

import android.content.Context;
import android.support.annotation.NonNull;
import com.pyamsoft.powermanager.PowerManagerPreferences;
import com.pyamsoft.powermanager.R;
import javax.inject.Inject;

final class ManagerPeriodicInteractorImpl implements ManagerPeriodicInteractor {

  @NonNull private final PowerManagerPreferences preferences;
  @NonNull private final String KEY_DISABLE_WIFI;
  @NonNull private final String KEY_DISABLE_DATA;
  @NonNull private final String KEY_DISABLE_BLUETOOTH;
  @NonNull private final String KEY_DISABLE_SYNC;
  @NonNull private final String KEY_ENABLE_WIFI;
  @NonNull private final String KEY_ENABLE_DATA;
  @NonNull private final String KEY_ENABLE_BLUETOOTH;
  @NonNull private final String KEY_ENABLE_SYNC;

  @Inject ManagerPeriodicInteractorImpl(@NonNull Context context,
      @NonNull PowerManagerPreferences preferences) {
    this.preferences = preferences;
    final Context appContext = context.getApplicationContext();
    KEY_DISABLE_WIFI = appContext.getString(R.string.periodic_wifi_disable_key);
    KEY_DISABLE_DATA = appContext.getString(R.string.periodic_data_disable_key);
    KEY_DISABLE_BLUETOOTH = appContext.getString(R.string.periodic_bluetooth_disable_key);
    KEY_DISABLE_SYNC = appContext.getString(R.string.periodic_sync_disable_key);
    KEY_ENABLE_WIFI = appContext.getString(R.string.periodic_wifi_enable_key);
    KEY_ENABLE_DATA = appContext.getString(R.string.periodic_data_enable_key);
    KEY_ENABLE_BLUETOOTH = appContext.getString(R.string.periodic_bluetooth_enable_key);
    KEY_ENABLE_SYNC = appContext.getString(R.string.periodic_sync_enable_key);
  }

  @Override public void setPeriodicTime(@NonNull String key, long time) {
    if (key.equals(KEY_DISABLE_WIFI)) {
      preferences.setPeriodicDisableTimeWifi(time);
    } else if (key.equals(KEY_DISABLE_DATA)) {
      preferences.setPeriodicDisableTimeData(time);
    } else if (key.equals(KEY_DISABLE_BLUETOOTH)) {
      preferences.setPeriodicDisableTimeBluetooth(time);
    } else if (key.equals(KEY_DISABLE_SYNC)) {
      preferences.setPeriodicDisableTimeSync(time);
    } else if (key.equals(KEY_ENABLE_WIFI)) {
      preferences.setPeriodicEnableTimeWifi(time);
    } else if (key.equals(KEY_ENABLE_DATA)) {
      preferences.setPeriodicEnableTimeData(time);
    } else if (key.equals(KEY_ENABLE_BLUETOOTH)) {
      preferences.setPeriodicEnableTimeBluetooth(time);
    } else if (key.equals(KEY_ENABLE_SYNC)) {
      preferences.setPeriodicEnableTimeSync(time);
    } else {
      throw new IllegalStateException("Invalid KEY: " + key);
    }
  }

  @Override public long getPeriodicTime(@NonNull String key) {
    long time;
    if (key.equals(KEY_DISABLE_WIFI)) {
      time = preferences.getPeriodicDisableTimeWifi();
    } else if (key.equals(KEY_DISABLE_DATA)) {
      time = preferences.getPeriodicDisableTimeData();
    } else if (key.equals(KEY_DISABLE_BLUETOOTH)) {
      time = preferences.getPeriodicDisableTimeBluetooth();
    } else if (key.equals(KEY_DISABLE_SYNC)) {
      time = preferences.getPeriodicDisableTimeSync();
    } else if (key.equals(KEY_ENABLE_WIFI)) {
      time = preferences.getPeriodicEnableTimeWifi();
    } else if (key.equals(KEY_ENABLE_DATA)) {
      time = preferences.getPeriodicEnableTimeData();
    } else if (key.equals(KEY_ENABLE_BLUETOOTH)) {
      time = preferences.getPeriodicEnableTimeBluetooth();
    } else if (key.equals(KEY_ENABLE_SYNC)) {
      time = preferences.getPeriodicEnableTimeSync();
    } else {
      throw new IllegalStateException("Invalid KEY: " + key);
    }
    return time;
  }
}
