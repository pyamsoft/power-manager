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
  @NonNull private final String delayData;
  @NonNull private final String delayBluetooth;
  @NonNull private final String delaySync;
  @NonNull private final String delayWifiDefault;
  @NonNull private final String delayDataDefault;
  @NonNull private final String delayBluetoothDefault;
  @NonNull private final String delaySyncDefault;

  @NonNull private final String presetWifi;
  @NonNull private final String presetData;
  @NonNull private final String presetBluetooth;
  @NonNull private final String presetSync;

  @NonNull private final String periodicWifi;
  @NonNull private final String periodicData;
  @NonNull private final String periodicBluetooth;
  @NonNull private final String periodicSync;
  private final boolean periodicWifiDefault;
  private final boolean periodicDataDefault;
  private final boolean periodicBluetoothDefault;
  private final boolean periodicSyncDefault;

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

    delayWifi = appContext.getString(R.string.wifi_time_key);
    delayData = appContext.getString(R.string.data_time_key);
    delayBluetooth = appContext.getString(R.string.bluetooth_time_key);
    delaySync = appContext.getString(R.string.sync_time_key);
    delayWifiDefault = appContext.getString(R.string.wifi_time_default);
    delayDataDefault = appContext.getString(R.string.data_time_default);
    delayBluetoothDefault = appContext.getString(R.string.bluetooth_time_default);
    delaySyncDefault = appContext.getString(R.string.sync_time_default);

    presetWifi = appContext.getString(R.string.preset_delay_wifi_key);
    presetData = appContext.getString(R.string.preset_delay_data_key);
    presetBluetooth = appContext.getString(R.string.preset_delay_bluetooth_key);
    presetSync = appContext.getString(R.string.preset_delay_sync_key);

    periodicWifi = appContext.getString(R.string.periodic_wifi_key);
    periodicData = appContext.getString(R.string.periodic_data_key);
    periodicBluetooth = appContext.getString(R.string.periodic_bluetooth_key);
    periodicSync = appContext.getString(R.string.periodic_sync_key);
    periodicWifiDefault = resources.getBoolean(R.bool.periodic_wifi_default);
    periodicDataDefault = resources.getBoolean(R.bool.periodic_data_default);
    periodicBluetoothDefault = resources.getBoolean(R.bool.periodic_bluetooth_default);
    periodicSyncDefault = resources.getBoolean(R.bool.periodic_sync_default);

    manageWearable = appContext.getString(R.string.manage_wearable_key);
    manageWearableDefault = resources.getBoolean(R.bool.manage_wearable_default);
  }

  @Override public boolean isCustomTimeWifi() {
    return Long.parseLong(get(presetWifi, delayWifiDefault)) == -1;
  }

  @Override public boolean isCustomTimeData() {
    return Long.parseLong(get(presetData, delayDataDefault)) == -1;
  }

  @Override public boolean isCustomTimeBluetooth() {
    return Long.parseLong(get(presetBluetooth, delayBluetoothDefault)) == -1;
  }

  @Override public boolean isCustomTimeSync() {
    return Long.parseLong(get(presetSync, delaySyncDefault)) == -1;
  }

  @Override public long getWifiDelay() {
    return Long.parseLong(get(delayWifi, delayWifiDefault));
  }

  @Override public void setWifiDelay(long time) {
    put(delayWifi, Long.toString(time));
  }

  @Override public long getDataDelay() {
    return Long.parseLong(get(delayData, delayDataDefault));
  }

  @Override public void setDataDelay(long time) {
    put(delayData, Long.toString(time));
  }

  @Override public long getBluetoothDelay() {
    return Long.parseLong(get(delayBluetooth, delayBluetoothDefault));
  }

  @Override public void setBluetoothDelay(long time) {
    put(delayBluetooth, Long.toString(time));
  }

  @Override public long getMasterSyncDelay() {
    return Long.parseLong(get(delaySync, delaySyncDefault));
  }

  @Override public void setMasterSyncDelay(long time) {
    put(delaySync, Long.toString(time));
  }

  @Override public int getNotificationPriority() {
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

  @Override public void setWifiManaged(boolean enable) {
    put(manageWifi, enable);
  }

  @Override public void setDataManaged(boolean enable) {
    put(manageData, enable);
  }

  @Override public void setBluetoothManaged(boolean enable) {
    put(manageBluetooth, enable);
  }

  @Override public void setSyncManaged(boolean enable) {
    put(manageSync, enable);
  }

  @Override public void setWearableManaged(boolean enable) {
    put(manageWearable, enable);
  }

  @Override public void clearAll() {
    clear(true);
  }

  @Override public boolean isPeriodicWifi() {
    return get(periodicWifi, periodicWifiDefault);
  }

  @Override public boolean isPeriodicData() {
    return get(periodicData, periodicDataDefault);
  }

  @Override public boolean isPeriodicBluetooth() {
    return get(periodicBluetooth, periodicBluetoothDefault);
  }

  @Override public boolean isPeriodicSync() {
    return get(periodicSync, periodicSyncDefault);
  }

  @Override public long getPeriodicDisableTimeWifi() {
    return 0;
  }

  @Override public long getPeriodicDisableTimeData() {
    return 0;
  }

  @Override public long getPeriodicDisableTimeBluetooth() {
    return 0;
  }

  @Override public long getPeriodicDisableTimeSync() {
    return 0;
  }

  @Override public void setPeriodicDisableTimeWifi() {

  }

  @Override public void setPeriodicDisableTimeData() {

  }

  @Override public void setPeriodicDisableTimeBluetooth() {

  }

  @Override public void setPeriodicDisableTimeSync() {

  }

  @Override public long getPeriodicEnableTimeWifi() {
    return 0;
  }

  @Override public long getPeriodicEnableTimeData() {
    return 0;
  }

  @Override public long getPeriodicEnableTimeBluetooth() {
    return 0;
  }

  @Override public long getPeriodicEnableTimeSync() {
    return 0;
  }

  @Override public void setPeriodicEnableTimeWifi() {

  }

  @Override public void setPeriodicEnableTimeData() {

  }

  @Override public void setPeriodicEnableTimeBluetooth() {

  }

  @Override public void setPeriodicEnableTimeSync() {

  }
}
