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
import android.content.Intent;
import android.support.annotation.NonNull;
import com.pyamsoft.powermanager.PowerManagerPreferences;
import com.pyamsoft.powermanager.R;
import com.pyamsoft.powermanager.app.service.ForegroundService;
import javax.inject.Inject;

final class ManagerSettingsInteractorImpl implements ManagerSettingsInteractor {

  @NonNull private final Context appContext;
  @NonNull private final PowerManagerPreferences preferences;
  @NonNull private final String KEY_MANAGE_WIFI;
  @NonNull private final String KEY_MANAGE_DATA;
  @NonNull private final String KEY_MANAGE_BLUETOOTH;
  @NonNull private final String KEY_MANAGE_SYNC;
  @NonNull private final String KEY_PERIODIC_WIFI;
  @NonNull private final String KEY_PERIODIC_DATA;
  @NonNull private final String KEY_PERIODIC_BLUETOOTH;
  @NonNull private final String KEY_PERIODIC_SYNC;

  @Inject ManagerSettingsInteractorImpl(@NonNull Context context,
      @NonNull PowerManagerPreferences preferences) {
    appContext = context.getApplicationContext();
    this.preferences = preferences;
    KEY_MANAGE_WIFI = appContext.getString(R.string.manage_wifi_key);
    KEY_MANAGE_DATA = appContext.getString(R.string.manage_data_key);
    KEY_MANAGE_BLUETOOTH = appContext.getString(R.string.manage_bluetooth_key);
    KEY_MANAGE_SYNC = appContext.getString(R.string.manage_sync_key);
    KEY_PERIODIC_WIFI = appContext.getString(R.string.periodic_wifi_key);
    KEY_PERIODIC_DATA = appContext.getString(R.string.periodic_data_key);
    KEY_PERIODIC_BLUETOOTH = appContext.getString(R.string.periodic_bluetooth_key);
    KEY_PERIODIC_SYNC = appContext.getString(R.string.periodic_sync_key);
  }

  @Override public boolean isCustomDelayTime(@NonNull String key) {
    boolean custom;
    if (key.equals(KEY_MANAGE_WIFI)) {
      custom = preferences.isCustomDelayTimeWifi();
    } else if (key.equals(KEY_MANAGE_DATA)) {
      custom = preferences.isCustomDelayTimeData();
    } else if (key.equals(KEY_MANAGE_BLUETOOTH)) {
      custom = preferences.isCustomDelayTimeBluetooth();
    } else if (key.equals(KEY_MANAGE_SYNC)) {
      custom = preferences.isCustomDelayTimeSync();
    } else {
      throw new IllegalStateException("Invalid key");
    }
    return custom;
  }

  @Override public boolean isCustomPeriodicDisableTime(@NonNull String key) {
    boolean custom;
    if (key.equals(KEY_PERIODIC_WIFI)) {
      custom = preferences.isCustomPeriodicDisableTimeWifi();
    } else if (key.equals(KEY_PERIODIC_DATA)) {
      custom = preferences.isCustomPeriodicDisableTimeData();
    } else if (key.equals(KEY_PERIODIC_BLUETOOTH)) {
      custom = preferences.isCustomPeriodicDisableTimeBluetooth();
    } else if (key.equals(KEY_PERIODIC_SYNC)) {
      custom = preferences.isCustomPeriodicDisableTimeSync();
    } else {
      throw new IllegalStateException("Invalid key");
    }
    return custom;
  }

  @Override public boolean isCustomPeriodicEnableTime(@NonNull String key) {
    boolean custom;
    if (key.equals(KEY_PERIODIC_WIFI)) {
      custom = preferences.isCustomPeriodicEnableTimeWifi();
    } else if (key.equals(KEY_PERIODIC_DATA)) {
      custom = preferences.isCustomPeriodicEnableTimeData();
    } else if (key.equals(KEY_PERIODIC_BLUETOOTH)) {
      custom = preferences.isCustomPeriodicEnableTimeBluetooth();
    } else if (key.equals(KEY_PERIODIC_SYNC)) {
      custom = preferences.isCustomPeriodicEnableTimeSync();
    } else {
      throw new IllegalStateException("Invalid key");
    }
    return custom;
  }

  @Override public void updateNotificationOnManageStateChange() {
    final Intent serviceUpdateIntent = new Intent(appContext, ForegroundService.class);
    appContext.startService(serviceUpdateIntent);
  }
}

