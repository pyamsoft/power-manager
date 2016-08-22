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

  @NonNull private final String presetDelayWifi;
  @NonNull private final String presetDelayData;
  @NonNull private final String presetDelayBluetooth;
  @NonNull private final String presetDelaySync;

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

  @NonNull private final String presetPeriodicDisableWifi;
  @NonNull private final String presetPeriodicDisableData;
  @NonNull private final String presetPeriodicDisableBluetooth;
  @NonNull private final String presetPeriodicDisableSync;

  @NonNull private final String periodicEnableWifi;
  @NonNull private final String periodicEnableData;
  @NonNull private final String periodicEnableBluetooth;
  @NonNull private final String periodicEnableSync;
  @NonNull private final String periodicEnableWifiDefault;
  @NonNull private final String periodicEnableDataDefault;
  @NonNull private final String periodicEnableBluetoothDefault;
  @NonNull private final String periodicEnableSyncDefault;

  @NonNull private final String presetPeriodicEnableWifi;
  @NonNull private final String presetPeriodicEnableData;
  @NonNull private final String presetPeriodicEnableBluetooth;
  @NonNull private final String presetPeriodicEnableSync;

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
  @NonNull private final String forceOutOfDoze;
  @NonNull private final String ignoreChargingDoze;
  @NonNull private final String dozeDelay;
  @NonNull private final String manageSensors;

  private final boolean forceDozeDefault;
  private final boolean forceOutOfDozeDefault;
  private final boolean ignoreChargingDozeDefault;
  @NonNull private final String dozeDelayDefault;
  private final boolean manageSensorsDefault;

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
