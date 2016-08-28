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

class PowerManagerPreferencesImpl extends ApplicationPreferences
    implements PowerManagerPreferences {

  @NonNull final String manageWifi;
  @NonNull final String manageData;
  @NonNull final String manageBluetooth;
  @NonNull final String manageSync;
  final boolean manageWifiDefault;
  final boolean manageDataDefault;
  final boolean manageBluetoothDefault;
  final boolean manageSyncDefault;

  @NonNull final String delayWifi;
  @NonNull final String delayData;
  @NonNull final String delayBluetooth;
  @NonNull final String delaySync;
  @NonNull final String delayWifiDefault;
  @NonNull final String delayDataDefault;
  @NonNull final String delayBluetoothDefault;
  @NonNull final String delaySyncDefault;

  @NonNull final String presetDelayWifi;
  @NonNull final String presetDelayData;
  @NonNull final String presetDelayBluetooth;
  @NonNull final String presetDelaySync;

  @NonNull final String periodicWifi;
  @NonNull final String periodicData;
  @NonNull final String periodicBluetooth;
  @NonNull final String periodicSync;
  final boolean periodicWifiDefault;
  final boolean periodicDataDefault;
  final boolean periodicBluetoothDefault;
  final boolean periodicSyncDefault;

  @NonNull final String periodicDisableWifi;
  @NonNull final String periodicDisableData;
  @NonNull final String periodicDisableBluetooth;
  @NonNull final String periodicDisableSync;
  @NonNull final String periodicDisableWifiDefault;
  @NonNull final String periodicDisableDataDefault;
  @NonNull final String periodicDisableBluetoothDefault;
  @NonNull final String periodicDisableSyncDefault;

  @NonNull final String presetPeriodicDisableWifi;
  @NonNull final String presetPeriodicDisableData;
  @NonNull final String presetPeriodicDisableBluetooth;
  @NonNull final String presetPeriodicDisableSync;

  @NonNull final String periodicEnableWifi;
  @NonNull final String periodicEnableData;
  @NonNull final String periodicEnableBluetooth;
  @NonNull final String periodicEnableSync;
  @NonNull final String periodicEnableWifiDefault;
  @NonNull final String periodicEnableDataDefault;
  @NonNull final String periodicEnableBluetoothDefault;
  @NonNull final String periodicEnableSyncDefault;

  @NonNull final String presetPeriodicEnableWifi;
  @NonNull final String presetPeriodicEnableData;
  @NonNull final String presetPeriodicEnableBluetooth;
  @NonNull final String presetPeriodicEnableSync;

  @NonNull final String ignoreChargingWifi;
  @NonNull final String ignoreChargingData;
  @NonNull final String ignoreChargingBluetooth;
  @NonNull final String ignoreChargingSync;
  final boolean ignoreChargingWifiDefault;
  final boolean ignoreChargingDataDefault;
  final boolean ignoreChargingBluetoothDefault;
  final boolean ignoreChargingSyncDefault;

  @NonNull final String manageWearable;
  final boolean manageWearableDefault;

  @NonNull final String fullNotification;
  final boolean fullNotificationDefault;

  @NonNull final String forceDoze;
  @NonNull final String forceOutOfDoze;
  @NonNull final String ignoreChargingDoze;
  @NonNull final String dozeDelay;
  @NonNull final String manageSensors;

  final boolean forceDozeDefault;
  final boolean forceOutOfDozeDefault;
  final boolean ignoreChargingDozeDefault;
  @NonNull final String dozeDelayDefault;
  final boolean manageSensorsDefault;

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

    presetDelayWifi = appContext.getString(R.string.preset_delay_wifi_key);
    presetDelayData = appContext.getString(R.string.preset_delay_data_key);
    presetDelayBluetooth = appContext.getString(R.string.preset_delay_bluetooth_key);
    presetDelaySync = appContext.getString(R.string.preset_delay_sync_key);

    periodicWifi = appContext.getString(R.string.periodic_wifi_key);
    periodicData = appContext.getString(R.string.periodic_data_key);
    periodicBluetooth = appContext.getString(R.string.periodic_bluetooth_key);
    periodicSync = appContext.getString(R.string.periodic_sync_key);
    periodicWifiDefault = resources.getBoolean(R.bool.periodic_wifi_default);
    periodicDataDefault = resources.getBoolean(R.bool.periodic_data_default);
    periodicBluetoothDefault = resources.getBoolean(R.bool.periodic_bluetooth_default);
    periodicSyncDefault = resources.getBoolean(R.bool.periodic_sync_default);

    periodicDisableWifi = appContext.getString(R.string.periodic_wifi_disable_key);
    periodicDisableData = appContext.getString(R.string.periodic_data_disable_key);
    periodicDisableBluetooth = appContext.getString(R.string.periodic_bluetooth_disable_key);
    periodicDisableSync = appContext.getString(R.string.periodic_sync_disable_key);
    periodicDisableWifiDefault = appContext.getString(R.string.periodic_wifi_disable_default);
    periodicDisableDataDefault = appContext.getString(R.string.periodic_data_disable_default);
    periodicDisableBluetoothDefault =
        appContext.getString(R.string.periodic_bluetooth_disable_default);
    periodicDisableSyncDefault = appContext.getString(R.string.periodic_sync_disable_default);

    presetPeriodicDisableWifi = appContext.getString(R.string.preset_periodic_wifi_disable_key);
    presetPeriodicDisableData = appContext.getString(R.string.preset_periodic_data_disable_key);
    presetPeriodicDisableBluetooth =
        appContext.getString(R.string.preset_periodic_bluetooth_disable_key);
    presetPeriodicDisableSync = appContext.getString(R.string.preset_periodic_sync_disable_key);

    periodicEnableWifi = appContext.getString(R.string.periodic_wifi_enable_key);
    periodicEnableData = appContext.getString(R.string.periodic_data_enable_key);
    periodicEnableBluetooth = appContext.getString(R.string.periodic_bluetooth_enable_key);
    periodicEnableSync = appContext.getString(R.string.periodic_sync_enable_key);
    periodicEnableWifiDefault = appContext.getString(R.string.periodic_wifi_enable_default);
    periodicEnableDataDefault = appContext.getString(R.string.periodic_data_enable_default);
    periodicEnableBluetoothDefault =
        appContext.getString(R.string.periodic_bluetooth_enable_default);
    periodicEnableSyncDefault = appContext.getString(R.string.periodic_sync_enable_default);

    presetPeriodicEnableWifi = appContext.getString(R.string.preset_periodic_wifi_enable_key);
    presetPeriodicEnableData = appContext.getString(R.string.preset_periodic_data_enable_key);
    presetPeriodicEnableBluetooth =
        appContext.getString(R.string.preset_periodic_bluetooth_enable_key);
    presetPeriodicEnableSync = appContext.getString(R.string.preset_periodic_sync_enable_key);

    ignoreChargingWifi = appContext.getString(R.string.ignore_charging_wifi_key);
    ignoreChargingData = appContext.getString(R.string.ignore_charging_data_key);
    ignoreChargingBluetooth = appContext.getString(R.string.ignore_charging_bluetooth_key);
    ignoreChargingSync = appContext.getString(R.string.ignore_charging_sync_key);
    ignoreChargingWifiDefault = resources.getBoolean(R.bool.ignore_charging_wifi_default);
    ignoreChargingDataDefault = resources.getBoolean(R.bool.ignore_charging_data_default);
    ignoreChargingBluetoothDefault = resources.getBoolean(R.bool.ignore_charging_bluetooth_default);
    ignoreChargingSyncDefault = resources.getBoolean(R.bool.ignore_charging_sync_default);

    manageWearable = appContext.getString(R.string.manage_wearable_key);
    manageWearableDefault = resources.getBoolean(R.bool.manage_wearable_default);

    fullNotification = appContext.getString(R.string.full_notification_key);
    fullNotificationDefault = resources.getBoolean(R.bool.full_notification_default);

    forceDoze = appContext.getString(R.string.manage_doze_key);
    forceOutOfDoze = appContext.getString(R.string.force_out_doze_key);
    ignoreChargingDoze = appContext.getString(R.string.ignore_charging_doze_key);
    dozeDelay = appContext.getString(R.string.doze_time_key);
    manageSensors = appContext.getString(R.string.sensors_doze_key);

    forceDozeDefault = resources.getBoolean(R.bool.doze_default);
    forceOutOfDozeDefault = resources.getBoolean(R.bool.force_out_doze_default);
    ignoreChargingDozeDefault = resources.getBoolean(R.bool.ignore_charging_doze_default);
    dozeDelayDefault = appContext.getString(R.string.doze_time_default);
    manageSensorsDefault = resources.getBoolean(R.bool.sensors_doze_default);
  }

  @Override public boolean isSensorsManaged() {
    return get(manageSensors, manageSensorsDefault);
  }

  @Override public long getDozeDelay() {
    return Long.parseLong(get(dozeDelay, dozeDelayDefault));
  }

  @Override public boolean isForceOutDoze() {
    return get(forceOutOfDoze, forceOutOfDozeDefault);
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

  @Override public boolean isCustomDelayTimeWifi() {
    return Long.parseLong(get(presetDelayWifi, delayWifiDefault)) == -1;
  }

  @Override public boolean isCustomDelayTimeData() {
    return Long.parseLong(get(presetDelayData, delayDataDefault)) == -1;
  }

  @Override public boolean isCustomDelayTimeBluetooth() {
    return Long.parseLong(get(presetDelayBluetooth, delayBluetoothDefault)) == -1;
  }

  @Override public boolean isCustomDelayTimeSync() {
    return Long.parseLong(get(presetDelaySync, delaySyncDefault)) == -1;
  }

  @Override public boolean isCustomPeriodicDisableTimeWifi() {
    return Long.parseLong(get(presetPeriodicDisableWifi, periodicDisableWifiDefault)) == -1;
  }

  @Override public boolean isCustomPeriodicDisableTimeData() {
    return Long.parseLong(get(presetPeriodicDisableData, periodicDisableDataDefault)) == -1;
  }

  @Override public boolean isCustomPeriodicDisableTimeBluetooth() {
    return Long.parseLong(get(presetPeriodicDisableBluetooth, periodicDisableBluetoothDefault))
        == -1;
  }

  @Override public boolean isCustomPeriodicDisableTimeSync() {
    return Long.parseLong(get(presetPeriodicDisableSync, periodicDisableSyncDefault)) == -1;
  }

  @Override public boolean isCustomPeriodicEnableTimeWifi() {
    return Long.parseLong(get(presetPeriodicEnableWifi, periodicEnableWifiDefault)) == -1;
  }

  @Override public boolean isCustomPeriodicEnableTimeData() {
    return Long.parseLong(get(presetPeriodicEnableData, periodicEnableDataDefault)) == -1;
  }

  @Override public boolean isCustomPeriodicEnableTimeBluetooth() {
    return Long.parseLong(get(presetPeriodicEnableBluetooth, periodicEnableBluetoothDefault)) == -1;
  }

  @Override public boolean isCustomPeriodicEnableTimeSync() {
    return Long.parseLong(get(presetPeriodicEnableSync, periodicEnableSyncDefault)) == -1;
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
