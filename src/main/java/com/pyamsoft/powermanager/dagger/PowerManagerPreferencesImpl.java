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

package com.pyamsoft.powermanager.dagger;

import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.v7.app.NotificationCompat;
import com.pyamsoft.powermanager.PowerManagerPreferences;
import com.pyamsoft.powermanager.R;
import com.pyamsoft.pydroid.base.app.ApplicationPreferences;
import javax.inject.Inject;

final class PowerManagerPreferencesImpl extends ApplicationPreferences
    implements PowerManagerPreferences {

  @NonNull private final String manageWifi;
  @NonNull private final String manageData;
  @NonNull private final String manageBluetooth;
  @NonNull private final String manageSync;
  private final boolean manageWifiDefault;
  private final boolean manageDataDefault;
  private final boolean manageBluetoothDefault;
  private final boolean manageSyncDefault;

  @NonNull private final String delayWifi;
  @NonNull private final String delayWifiDefault;
  @NonNull private final String delayDataDefault;
  @NonNull private final String delayBluetoothDefault;
  @NonNull private final String delaySyncDefault;

  @NonNull private final String manageWearable;
  private final boolean manageWearableDefault;

  @Inject protected PowerManagerPreferencesImpl(@NonNull Context context) {
    super(context);
    final Context appContext = context.getApplicationContext();
    final Resources resources = appContext.getResources();
    manageWifi = appContext.getString(R.string.manage_wifi_key);
    manageData = appContext.getString(R.string.manage_data_key);
    manageBluetooth = appContext.getString(R.string.manage_bluetooth_key);
    manageSync = appContext.getString(R.string.manage_sync_key);
    manageWifiDefault = resources.getBoolean(R.bool.manage_wifi_default);
    manageDataDefault = resources.getBoolean(R.bool.manage_data_default);
    manageBluetoothDefault = resources.getBoolean(R.bool.manage_bluetooth_default);
    manageSyncDefault = resources.getBoolean(R.bool.manage_sync_default);

    manageWearable = appContext.getString(R.string.manage_wearable_key);
    manageWearableDefault = resources.getBoolean(R.bool.manage_wearable_default);

    delayWifi = appContext.getString(R.string.wifi_time_key);
    delayWifiDefault = appContext.getString(R.string.wifi_time_default);
    delayDataDefault = appContext.getString(R.string.data_time_default);
    delayBluetoothDefault = appContext.getString(R.string.bluetooth_time_default);
    delaySyncDefault = appContext.getString(R.string.sync_time_default);
  }

  @Override public long getWifiDelay() {
    return Long.parseLong(get(delayWifi, delayWifiDefault));
  }

  @Override public void setWifiDelay(long time) {
    put(delayWifi, time);
  }

  @Override public long getDataDelay() {
    return Long.parseLong(get(KEY_DELAY_DATA, delayDataDefault));
  }

  @Override public void setDataDelay(long time) {
    put(KEY_DELAY_DATA, time);
  }

  @Override public long getBluetoothDelay() {
    return Long.parseLong(get(KEY_DELAY_BLUETOOTH, delayBluetoothDefault));
  }

  @Override public void setBluetoothDelay(long time) {
    put(KEY_DELAY_BLUETOOTH, time);
  }

  @Override public long getMasterSyncDelay() {
    return Long.parseLong(get(KEY_DELAY_SYNC, delaySyncDefault));
  }

  @Override public void setMasterSyncDelay(long time) {
    put(KEY_DELAY_SYNC, time);
  }

  @Override public int getNotificationPriority() {
    // TODO
    return NotificationCompat.PRIORITY_MIN;
  }

  @Override public boolean isBluetoothManaged() {
    return get(manageBluetooth, manageBluetoothDefault);
  }

  @Override public boolean isDataManaged() {
    return get(manageData, manageDataDefault);
  }

  @Override public boolean isSyncManaged() {
    return get(manageSync, manageSyncDefault);
  }

  @Override public boolean isWifiManaged() {
    return get(manageWifi, manageWifiDefault);
  }

  @Override public boolean isWearableManaged() {
    return get(manageWearable, manageWearableDefault);
  }

  @Override public void setWearableManaged(boolean enable) {
    put(manageWearable, enable);
  }
}
