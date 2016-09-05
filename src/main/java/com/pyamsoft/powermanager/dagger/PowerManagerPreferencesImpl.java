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
import android.support.annotation.NonNull;
import android.support.v7.app.NotificationCompat;
import com.pyamsoft.powermanager.PowerManagerPreferences;
import com.pyamsoft.powermanager.R;
import com.pyamsoft.pydroid.base.ApplicationPreferences;
import javax.inject.Inject;

class PowerManagerPreferencesImpl extends ApplicationPreferences
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

  @NonNull private final String periodicWifi;
  @NonNull private final String periodicData;
  @NonNull private final String periodicBluetooth;
  @NonNull private final String periodicSync;
  private final boolean periodicWifiDefault;
  private final boolean periodicDataDefault;
  private final boolean periodicBluetoothDefault;
  private final boolean periodicSyncDefault;

  @NonNull private final String periodicDisableWifi;
  @NonNull private final String periodicDisableData;
  @NonNull private final String periodicDisableBluetooth;
  @NonNull private final String periodicDisableSync;
  @NonNull private final String periodicDisableWifiDefault;
  @NonNull private final String periodicDisableDataDefault;
  @NonNull private final String periodicDisableBluetoothDefault;
  @NonNull private final String periodicDisableSyncDefault;

  @NonNull private final String periodicEnableWifi;
  @NonNull private final String periodicEnableData;
  @NonNull private final String periodicEnableBluetooth;
  @NonNull private final String periodicEnableSync;
  @NonNull private final String periodicEnableWifiDefault;
  @NonNull private final String periodicEnableDataDefault;
  @NonNull private final String periodicEnableBluetoothDefault;
  @NonNull private final String periodicEnableSyncDefault;

  @NonNull private final String ignoreChargingWifi;
  @NonNull private final String ignoreChargingData;
  @NonNull private final String ignoreChargingBluetooth;
  @NonNull private final String ignoreChargingSync;
  private final boolean ignoreChargingWifiDefault;
  private final boolean ignoreChargingDataDefault;
  private final boolean ignoreChargingBluetoothDefault;
  private final boolean ignoreChargingSyncDefault;

  @NonNull private final String manageWearable;
  private final boolean manageWearableDefault;

  @NonNull private final String fullNotification;
  private final boolean fullNotificationDefault;

  @NonNull private final String forceDoze;
  @NonNull private final String exclusiveDoze;
  @NonNull private final String ignoreChargingDoze;
  @NonNull private final String dozeDelay;
  @NonNull private final String manageSensors;

  private final boolean forceDozeDefault;
  private final boolean exclusiveDozeDefault;
  private final boolean ignoreChargingDozeDefault;
  @NonNull private final String dozeDelayDefault;
  private final boolean manageSensorsDefault;

  @Inject PowerManagerPreferencesImpl(@NonNull Context context) {
    super(context);
    manageWifi = getResources().getString(R.string.manage_wifi_key);
    manageData = getResources().getString(R.string.manage_data_key);
    manageBluetooth = getResources().getString(R.string.manage_bluetooth_key);
    manageSync = getResources().getString(R.string.manage_sync_key);
    manageWifiDefault = getResources().getBoolean(R.bool.manage_wifi_default);
    manageDataDefault = getResources().getBoolean(R.bool.manage_data_default);
    manageBluetoothDefault = getResources().getBoolean(R.bool.manage_bluetooth_default);
    manageSyncDefault = getResources().getBoolean(R.bool.manage_sync_default);

    delayWifi = getResources().getString(R.string.wifi_time_key);
    delayData = getResources().getString(R.string.data_time_key);
    delayBluetooth = getResources().getString(R.string.bluetooth_time_key);
    delaySync = getResources().getString(R.string.sync_time_key);
    delayWifiDefault = getResources().getString(R.string.wifi_time_default);
    delayDataDefault = getResources().getString(R.string.data_time_default);
    delayBluetoothDefault = getResources().getString(R.string.bluetooth_time_default);
    delaySyncDefault = getResources().getString(R.string.sync_time_default);

    periodicWifi = getResources().getString(R.string.periodic_wifi_key);
    periodicData = getResources().getString(R.string.periodic_data_key);
    periodicBluetooth = getResources().getString(R.string.periodic_bluetooth_key);
    periodicSync = getResources().getString(R.string.periodic_sync_key);
    periodicWifiDefault = getResources().getBoolean(R.bool.periodic_wifi_default);
    periodicDataDefault = getResources().getBoolean(R.bool.periodic_data_default);
    periodicBluetoothDefault = getResources().getBoolean(R.bool.periodic_bluetooth_default);
    periodicSyncDefault = getResources().getBoolean(R.bool.periodic_sync_default);

    periodicDisableWifi = getResources().getString(R.string.periodic_wifi_disable_key);
    periodicDisableData = getResources().getString(R.string.periodic_data_disable_key);
    periodicDisableBluetooth = getResources().getString(R.string.periodic_bluetooth_disable_key);
    periodicDisableSync = getResources().getString(R.string.periodic_sync_disable_key);
    periodicDisableWifiDefault = getResources().getString(R.string.periodic_wifi_disable_default);
    periodicDisableDataDefault = getResources().getString(R.string.periodic_data_disable_default);
    periodicDisableBluetoothDefault =
        getResources().getString(R.string.periodic_bluetooth_disable_default);
    periodicDisableSyncDefault = getResources().getString(R.string.periodic_sync_disable_default);

    periodicEnableWifi = getResources().getString(R.string.periodic_wifi_enable_key);
    periodicEnableData = getResources().getString(R.string.periodic_data_enable_key);
    periodicEnableBluetooth = getResources().getString(R.string.periodic_bluetooth_enable_key);
    periodicEnableSync = getResources().getString(R.string.periodic_sync_enable_key);
    periodicEnableWifiDefault = getResources().getString(R.string.periodic_wifi_enable_default);
    periodicEnableDataDefault = getResources().getString(R.string.periodic_data_enable_default);
    periodicEnableBluetoothDefault =
        getResources().getString(R.string.periodic_bluetooth_enable_default);
    periodicEnableSyncDefault = getResources().getString(R.string.periodic_sync_enable_default);

    ignoreChargingWifi = getResources().getString(R.string.ignore_charging_wifi_key);
    ignoreChargingData = getResources().getString(R.string.ignore_charging_data_key);
    ignoreChargingBluetooth = getResources().getString(R.string.ignore_charging_bluetooth_key);
    ignoreChargingSync = getResources().getString(R.string.ignore_charging_sync_key);
    ignoreChargingWifiDefault = getResources().getBoolean(R.bool.ignore_charging_wifi_default);
    ignoreChargingDataDefault = getResources().getBoolean(R.bool.ignore_charging_data_default);
    ignoreChargingBluetoothDefault =
        getResources().getBoolean(R.bool.ignore_charging_bluetooth_default);
    ignoreChargingSyncDefault = getResources().getBoolean(R.bool.ignore_charging_sync_default);

    manageWearable = getResources().getString(R.string.manage_wearable_key);
    manageWearableDefault = getResources().getBoolean(R.bool.manage_wearable_default);

    fullNotification = getResources().getString(R.string.full_notification_key);
    fullNotificationDefault = getResources().getBoolean(R.bool.full_notification_default);

    forceDoze = getResources().getString(R.string.manage_doze_key);
    exclusiveDoze = getResources().getString(R.string.exclusive_doze_key);
    ignoreChargingDoze = getResources().getString(R.string.ignore_charging_doze_key);
    dozeDelay = getResources().getString(R.string.doze_time_key);
    manageSensors = getResources().getString(R.string.sensors_doze_key);

    forceDozeDefault = getResources().getBoolean(R.bool.doze_default);
    exclusiveDozeDefault = getResources().getBoolean(R.bool.exclusive_doze_default);
    ignoreChargingDozeDefault = getResources().getBoolean(R.bool.ignore_charging_doze_default);
    dozeDelayDefault = getResources().getString(R.string.doze_time_default);
    manageSensorsDefault = getResources().getBoolean(R.bool.sensors_doze_default);
  }

  @Override public boolean isSensorsManaged() {
    return get(manageSensors, manageSensorsDefault);
  }

  @Override public long getDozeDelay() {
    return Long.parseLong(get(dozeDelay, dozeDelayDefault));
  }

  @Override public boolean isExclusiveDoze() {
    return get(exclusiveDoze, exclusiveDozeDefault);
  }

  @Override public boolean isIgnoreChargingDoze() {
    return get(ignoreChargingDoze, ignoreChargingDozeDefault);
  }

  @Override public boolean isFullNotificationEnabled() {
    return get(fullNotification, fullNotificationDefault);
  }

  @Override public boolean isDozeManaged() {
    return get(forceDoze, forceDozeDefault);
  }

  @Override public void setDozeManaged(boolean enable) {
    put(forceDoze, enable);
  }

  @Override public boolean isIgnoreChargingWifi() {
    return get(ignoreChargingWifi, ignoreChargingWifiDefault);
  }

  @Override public boolean isIgnoreChargingData() {
    return get(ignoreChargingData, ignoreChargingDataDefault);
  }

  @Override public boolean isIgnoreChargingBluetooth() {
    return get(ignoreChargingBluetooth, ignoreChargingBluetoothDefault);
  }

  @Override public boolean isIgnoreChargingSync() {
    return get(ignoreChargingSync, ignoreChargingSyncDefault);
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

  @Override public void setBluetoothManaged(boolean enable) {
    put(manageBluetooth, enable);
  }

  @Override public boolean isDataManaged() {
    return get(manageData, manageDataDefault);
  }

  @Override public void setDataManaged(boolean enable) {
    put(manageData, enable);
  }

  @Override public boolean isSyncManaged() {
    return get(manageSync, manageSyncDefault);
  }

  @Override public void setSyncManaged(boolean enable) {
    put(manageSync, enable);
  }

  @Override public boolean isWifiManaged() {
    return get(manageWifi, manageWifiDefault);
  }

  @Override public void setWifiManaged(boolean enable) {
    put(manageWifi, enable);
  }

  @Override public boolean isWearableManaged() {
    return get(manageWearable, manageWearableDefault);
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

  @Override public void setPeriodicWifi(boolean state) {
    put(periodicWifi, state);
  }

  @Override public boolean isPeriodicData() {
    return get(periodicData, periodicDataDefault);
  }

  @Override public void setPeriodicData(boolean state) {
    put(periodicData, state);
  }

  @Override public boolean isPeriodicBluetooth() {
    return get(periodicBluetooth, periodicBluetoothDefault);
  }

  @Override public void setPeriodicBluetooth(boolean state) {
    put(periodicBluetooth, state);
  }

  @Override public boolean isPeriodicSync() {
    return get(periodicSync, periodicSyncDefault);
  }

  @Override public void setPeriodicSync(boolean state) {
    put(periodicSync, state);
  }

  @Override public long getPeriodicDisableTimeWifi() {
    return Long.parseLong(get(periodicDisableWifi, periodicDisableWifiDefault));
  }

  @Override public void setPeriodicDisableTimeWifi(long time) {
    put(periodicDisableWifi, String.valueOf(time));
  }

  @Override public long getPeriodicDisableTimeData() {
    return Long.parseLong(get(periodicDisableData, periodicDisableDataDefault));
  }

  @Override public void setPeriodicDisableTimeData(long time) {
    put(periodicDisableData, String.valueOf(time));
  }

  @Override public long getPeriodicDisableTimeBluetooth() {
    return Long.parseLong(get(periodicDisableBluetooth, periodicDisableBluetoothDefault));
  }

  @Override public void setPeriodicDisableTimeBluetooth(long time) {
    put(periodicDisableBluetooth, String.valueOf(time));
  }

  @Override public long getPeriodicDisableTimeSync() {
    return Long.parseLong(get(periodicDisableSync, periodicDisableSyncDefault));
  }

  @Override public void setPeriodicDisableTimeSync(long time) {
    put(periodicDisableSync, String.valueOf(time));
  }

  @Override public long getPeriodicEnableTimeWifi() {
    return Long.parseLong(get(periodicEnableWifi, periodicEnableWifiDefault));
  }

  @Override public void setPeriodicEnableTimeWifi(long time) {
    put(periodicEnableWifi, String.valueOf(time));
  }

  @Override public long getPeriodicEnableTimeData() {
    return Long.parseLong(get(periodicEnableData, periodicEnableDataDefault));
  }

  @Override public void setPeriodicEnableTimeData(long time) {
    put(periodicEnableData, String.valueOf(time));
  }

  @Override public long getPeriodicEnableTimeBluetooth() {
    return Long.parseLong(get(periodicEnableBluetooth, periodicEnableBluetoothDefault));
  }

  @Override public void setPeriodicEnableTimeBluetooth(long time) {
    put(periodicEnableBluetooth, String.valueOf(time));
  }

  @Override public long getPeriodicEnableTimeSync() {
    return Long.parseLong(get(periodicEnableSync, periodicEnableSyncDefault));
  }

  @Override public void setPeriodicEnableTimeSync(long time) {
    put(periodicEnableSync, String.valueOf(time));
  }
}
