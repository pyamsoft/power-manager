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

package com.pyamsoft.powermanager.base;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.v7.app.NotificationCompat;
import android.support.v7.preference.PreferenceManager;
import com.pyamsoft.pydroid.app.OnRegisteredSharedPreferenceChangeListener;
import javax.inject.Inject;

class PowerManagerPreferencesImpl implements PowerManagerPreferences {

  @NonNull private static final String OVERVIEW_ONBOARD = "overview_onboard";
  @NonNull private static final String MANAGE_ONBOARD = "manage_onboard";
  @NonNull private static final String PERIOD_ONBOARD = "period_onboard";
  @NonNull private static final String SERVICE_ENABLED = "service_enabled";
  @NonNull private static final String ORIGINAL_WIFI = "original_wifi";
  @NonNull private static final String ORIGINAL_DATA = "original_data";
  @NonNull private static final String ORIGINAL_BLUETOOTH = "original_bluetooth";
  @NonNull private static final String ORIGINAL_SYNC = "original_sync";
  @NonNull private static final String ORIGINAL_AIRPLANE = "original_airplane";
  @NonNull private static final String ORIGINAL_DOZE = "original_doze";
  @NonNull private final SharedPreferences preferences;
  @NonNull private final String manageAirplane;
  @NonNull private final String manageWifi;
  @NonNull private final String manageData;
  @NonNull private final String manageBluetooth;
  @NonNull private final String manageSync;
  @NonNull private final String manageDoze;
  private final boolean manageAirplaneDefault;
  private final boolean manageWifiDefault;
  private final boolean manageDataDefault;
  private final boolean manageBluetoothDefault;
  private final boolean manageSyncDefault;
  private final boolean manageDozeDefault;
  @NonNull private final String delayAirplane;
  @NonNull private final String delayWifi;
  @NonNull private final String delayData;
  @NonNull private final String delayBluetooth;
  @NonNull private final String delaySync;
  @NonNull private final String delayDoze;
  @NonNull private final String delayAirplaneDefault;
  @NonNull private final String delayWifiDefault;
  @NonNull private final String delayDataDefault;
  @NonNull private final String delayBluetoothDefault;
  @NonNull private final String delaySyncDefault;
  @NonNull private final String delayDozeDefault;
  @NonNull private final String periodicDoze;
  @NonNull private final String periodicAirplane;
  @NonNull private final String periodicWifi;
  @NonNull private final String periodicData;
  @NonNull private final String periodicBluetooth;
  @NonNull private final String periodicSync;
  private final boolean periodicDozeDefault;
  private final boolean periodicAirplaneDefault;
  private final boolean periodicWifiDefault;
  private final boolean periodicDataDefault;
  private final boolean periodicBluetoothDefault;
  private final boolean periodicSyncDefault;
  @NonNull private final String periodicDisableDoze;
  @NonNull private final String periodicDisableAirplane;
  @NonNull private final String periodicDisableWifi;
  @NonNull private final String periodicDisableData;
  @NonNull private final String periodicDisableBluetooth;
  @NonNull private final String periodicDisableSync;
  @NonNull private final String periodicDisableDozeDefault;
  @NonNull private final String periodicDisableAirplaneDefault;
  @NonNull private final String periodicDisableWifiDefault;
  @NonNull private final String periodicDisableDataDefault;
  @NonNull private final String periodicDisableBluetoothDefault;
  @NonNull private final String periodicDisableSyncDefault;
  @NonNull private final String periodicEnableDoze;
  @NonNull private final String periodicEnableAirplane;
  @NonNull private final String periodicEnableWifi;
  @NonNull private final String periodicEnableData;
  @NonNull private final String periodicEnableBluetooth;
  @NonNull private final String periodicEnableSync;
  @NonNull private final String periodicEnableDozeDefault;
  @NonNull private final String periodicEnableAirplaneDefault;
  @NonNull private final String periodicEnableWifiDefault;
  @NonNull private final String periodicEnableDataDefault;
  @NonNull private final String periodicEnableBluetoothDefault;
  @NonNull private final String periodicEnableSyncDefault;
  @NonNull private final String ignoreChargingDoze;
  @NonNull private final String ignoreChargingAirplane;
  @NonNull private final String ignoreChargingWifi;
  @NonNull private final String ignoreChargingData;
  @NonNull private final String ignoreChargingBluetooth;
  @NonNull private final String ignoreChargingSync;
  private final boolean ignoreChargingDozeDefault;
  private final boolean ignoreChargingAirplaneDefault;
  private final boolean ignoreChargingWifiDefault;
  private final boolean ignoreChargingDataDefault;
  private final boolean ignoreChargingBluetoothDefault;
  private final boolean ignoreChargingSyncDefault;

  @NonNull private final String manageWearable;
  private final boolean manageWearableDefault;
  @NonNull private final String exclusiveDoze;
  private final boolean exclusiveDozeDefault;
  @NonNull private final String wearableDelay;
  @NonNull private final String wearableDelayDefault;
  @NonNull private final String startWhenOpen;
  private final boolean startWhenOpenDefault;
  @NonNull private final String useRoot;
  private final boolean useRootDefault;
  @NonNull private final String loggerEnabled;
  private final boolean loggerEnabledDefault;
  @NonNull private final String triggerPeriodKey;
  @NonNull private final String triggerPeriodDefault;

  @Inject PowerManagerPreferencesImpl(@NonNull Context context) {
    final Context appContext = context.getApplicationContext();
    preferences = PreferenceManager.getDefaultSharedPreferences(appContext);
    final Resources res = appContext.getResources();
    manageWifi = res.getString(R.string.manage_wifi_key);
    manageData = res.getString(R.string.manage_data_key);
    manageBluetooth = res.getString(R.string.manage_bluetooth_key);
    manageSync = res.getString(R.string.manage_sync_key);
    manageAirplane = res.getString(R.string.manage_airplane_key);
    manageDoze = res.getString(R.string.manage_doze_key);
    manageWifiDefault = res.getBoolean(R.bool.manage_wifi_default);
    manageDataDefault = res.getBoolean(R.bool.manage_data_default);
    manageBluetoothDefault = res.getBoolean(R.bool.manage_bluetooth_default);
    manageSyncDefault = res.getBoolean(R.bool.manage_sync_default);
    manageAirplaneDefault = res.getBoolean(R.bool.manage_airplane_default);
    manageDozeDefault = res.getBoolean(R.bool.manage_doze_default);

    delayDoze = res.getString(R.string.doze_time_key);
    delayAirplane = res.getString(R.string.airplane_time_key);
    delayWifi = res.getString(R.string.wifi_time_key);
    delayData = res.getString(R.string.data_time_key);
    delayBluetooth = res.getString(R.string.bluetooth_time_key);
    delaySync = res.getString(R.string.sync_time_key);
    delayDozeDefault = res.getString(R.string.doze_time_default);
    delayAirplaneDefault = res.getString(R.string.airplane_time_default);
    delayWifiDefault = res.getString(R.string.wifi_time_default);
    delayDataDefault = res.getString(R.string.data_time_default);
    delayBluetoothDefault = res.getString(R.string.bluetooth_time_default);
    delaySyncDefault = res.getString(R.string.sync_time_default);

    periodicDoze = res.getString(R.string.periodic_doze_key);
    periodicAirplane = res.getString(R.string.periodic_airplane_key);
    periodicWifi = res.getString(R.string.periodic_wifi_key);
    periodicData = res.getString(R.string.periodic_data_key);
    periodicBluetooth = res.getString(R.string.periodic_bluetooth_key);
    periodicSync = res.getString(R.string.periodic_sync_key);
    periodicDozeDefault = res.getBoolean(R.bool.periodic_doze_default);
    periodicAirplaneDefault = res.getBoolean(R.bool.periodic_airplane_default);
    periodicWifiDefault = res.getBoolean(R.bool.periodic_wifi_default);
    periodicDataDefault = res.getBoolean(R.bool.periodic_data_default);
    periodicBluetoothDefault = res.getBoolean(R.bool.periodic_bluetooth_default);
    periodicSyncDefault = res.getBoolean(R.bool.periodic_sync_default);

    periodicDisableDoze = res.getString(R.string.periodic_doze_disable_key);
    periodicDisableAirplane = res.getString(R.string.periodic_airplane_disable_key);
    periodicDisableWifi = res.getString(R.string.periodic_wifi_disable_key);
    periodicDisableData = res.getString(R.string.periodic_data_disable_key);
    periodicDisableBluetooth = res.getString(R.string.periodic_bluetooth_disable_key);
    periodicDisableSync = res.getString(R.string.periodic_sync_disable_key);
    periodicDisableDozeDefault = res.getString(R.string.periodic_doze_disable_default);
    periodicDisableAirplaneDefault = res.getString(R.string.periodic_airplane_disable_default);
    periodicDisableWifiDefault = res.getString(R.string.periodic_wifi_disable_default);
    periodicDisableDataDefault = res.getString(R.string.periodic_data_disable_default);
    periodicDisableBluetoothDefault = res.getString(R.string.periodic_bluetooth_disable_default);
    periodicDisableSyncDefault = res.getString(R.string.periodic_sync_disable_default);

    periodicEnableDoze = res.getString(R.string.periodic_doze_enable_key);
    periodicEnableAirplane = res.getString(R.string.periodic_airplane_enable_key);
    periodicEnableWifi = res.getString(R.string.periodic_wifi_enable_key);
    periodicEnableData = res.getString(R.string.periodic_data_enable_key);
    periodicEnableBluetooth = res.getString(R.string.periodic_bluetooth_enable_key);
    periodicEnableSync = res.getString(R.string.periodic_sync_enable_key);
    periodicEnableDozeDefault = res.getString(R.string.periodic_doze_enable_default);
    periodicEnableAirplaneDefault = res.getString(R.string.periodic_airplane_enable_default);
    periodicEnableWifiDefault = res.getString(R.string.periodic_wifi_enable_default);
    periodicEnableDataDefault = res.getString(R.string.periodic_data_enable_default);
    periodicEnableBluetoothDefault = res.getString(R.string.periodic_bluetooth_enable_default);
    periodicEnableSyncDefault = res.getString(R.string.periodic_sync_enable_default);

    ignoreChargingDoze = res.getString(R.string.ignore_charging_doze_key);
    ignoreChargingAirplane = res.getString(R.string.ignore_charging_airplane_key);
    ignoreChargingWifi = res.getString(R.string.ignore_charging_wifi_key);
    ignoreChargingData = res.getString(R.string.ignore_charging_data_key);
    ignoreChargingBluetooth = res.getString(R.string.ignore_charging_bluetooth_key);
    ignoreChargingSync = res.getString(R.string.ignore_charging_sync_key);
    ignoreChargingDozeDefault = res.getBoolean(R.bool.ignore_charging_doze_default);
    ignoreChargingAirplaneDefault = res.getBoolean(R.bool.ignore_charging_airplane_default);
    ignoreChargingWifiDefault = res.getBoolean(R.bool.ignore_charging_wifi_default);
    ignoreChargingDataDefault = res.getBoolean(R.bool.ignore_charging_data_default);
    ignoreChargingBluetoothDefault = res.getBoolean(R.bool.ignore_charging_bluetooth_default);
    ignoreChargingSyncDefault = res.getBoolean(R.bool.ignore_charging_sync_default);

    manageWearable = res.getString(R.string.manage_wearable_key);
    manageWearableDefault = res.getBoolean(R.bool.manage_wearable_default);

    exclusiveDoze = res.getString(R.string.exclusive_doze_key);
    exclusiveDozeDefault = res.getBoolean(R.bool.exclusive_doze_default);

    wearableDelay = res.getString(R.string.wearable_time_key);
    wearableDelayDefault = res.getString(R.string.wearable_time_default);

    startWhenOpen = res.getString(R.string.unsuspend_when_open_key);
    startWhenOpenDefault = res.getBoolean(R.bool.unsuspend_when_open_default);

    useRoot = res.getString(R.string.use_root_key);
    useRootDefault = res.getBoolean(R.bool.use_root_default);

    loggerEnabled = res.getString(R.string.logger_enabled);
    loggerEnabledDefault = res.getBoolean(R.bool.logger_enabled_default);

    triggerPeriodKey = res.getString(R.string.trigger_period_key);
    triggerPeriodDefault = res.getString(R.string.trigger_period_default);
  }

  @Override public boolean isOriginalWifi() {
    return preferences.getBoolean(ORIGINAL_WIFI, false);
  }

  @Override public void setOriginalWifi(boolean state) {
    preferences.edit().putBoolean(ORIGINAL_WIFI, state).apply();
  }

  @Override public boolean isOriginalData() {
    return preferences.getBoolean(ORIGINAL_DATA, false);
  }

  @Override public void setOriginalData(boolean state) {
    preferences.edit().putBoolean(ORIGINAL_DATA, state).apply();
  }

  @Override public boolean isOriginalBluetooh() {
    return preferences.getBoolean(ORIGINAL_BLUETOOTH, false);
  }

  @Override public boolean isOriginalSync() {
    return preferences.getBoolean(ORIGINAL_SYNC, false);
  }

  @Override public void setOriginalSync(boolean state) {
    preferences.edit().putBoolean(ORIGINAL_SYNC, state).apply();
  }

  @Override public boolean isOriginalAirplane() {
    return preferences.getBoolean(ORIGINAL_AIRPLANE, false);
  }

  @Override public void setOriginalAirplane(boolean state) {
    preferences.edit().putBoolean(ORIGINAL_AIRPLANE, state).apply();
  }

  @Override public boolean isOriginalDoze() {
    return preferences.getBoolean(ORIGINAL_DOZE, false);
  }

  @Override public void setOriginalDoze(boolean state) {
    preferences.edit().putBoolean(ORIGINAL_DOZE, state).apply();
  }

  @Override public void setOriginalBluetooth(boolean state) {
    preferences.edit().putBoolean(ORIGINAL_BLUETOOTH, state).apply();
  }

  @Override public boolean isServiceEnabled() {
    return preferences.getBoolean(SERVICE_ENABLED, true);
  }

  @Override public void setServiceEnabled(boolean enabled) {
    preferences.edit().putBoolean(SERVICE_ENABLED, enabled).apply();
  }

  @Override public long getTriggerPeriodTime() {
    return Long.parseLong(preferences.getString(triggerPeriodKey, triggerPeriodDefault));
  }

  @Override public boolean isLoggerEnabled() {
    return preferences.getBoolean(loggerEnabled, loggerEnabledDefault);
  }

  @Override public boolean isRootEnabled() {
    return preferences.getBoolean(useRoot, useRootDefault);
  }

  @Override public void resetRootEnabled() {
    preferences.edit().putBoolean(useRoot, useRootDefault).apply();
  }

  @Override public boolean isStartWhenOpen() {
    return preferences.getBoolean(startWhenOpen, startWhenOpenDefault);
  }

  @Override public boolean isPeriodicOnboardingShown() {
    return preferences.getBoolean(PERIOD_ONBOARD, false);
  }

  @Override public void setPeriodicOnboardingShown() {
    preferences.edit().putBoolean(PERIOD_ONBOARD, true).apply();
  }

  @Override public boolean isManageOnboardingShown() {
    return preferences.getBoolean(MANAGE_ONBOARD, false);
  }

  @Override public void setManageOnboardingShown() {
    preferences.edit().putBoolean(MANAGE_ONBOARD, true).apply();
  }

  @Override public boolean isOverviewOnboardingShown() {
    return preferences.getBoolean(OVERVIEW_ONBOARD, false);
  }

  @Override public void setOverviewOnboardingShown() {
    preferences.edit().putBoolean(OVERVIEW_ONBOARD, true).apply();
  }

  @Override public long getWearableDelay() {
    return Long.parseLong(preferences.getString(wearableDelay, wearableDelayDefault));
  }

  @Override public long getDozeDelay() {
    return Long.parseLong(preferences.getString(delayDoze, delayDozeDefault));
  }

  @Override public void setDozeDelay(long time) {
    preferences.edit().putString(delayDoze, Long.toString(time)).apply();
  }

  @Override public boolean isExclusiveDoze() {
    return preferences.getBoolean(exclusiveDoze, exclusiveDozeDefault);
  }

  @Override public boolean isIgnoreChargingDoze() {
    return preferences.getBoolean(ignoreChargingDoze, ignoreChargingDozeDefault);
  }

  @Override public boolean isDozeManaged() {
    return preferences.getBoolean(manageDoze, manageDozeDefault);
  }

  @Override public boolean isAirplaneManaged() {
    return preferences.getBoolean(manageAirplane, manageAirplaneDefault);
  }

  @Override public boolean isIgnoreChargingAirplane() {
    return preferences.getBoolean(ignoreChargingAirplane, ignoreChargingAirplaneDefault);
  }

  @Override public boolean isIgnoreChargingWifi() {
    return preferences.getBoolean(ignoreChargingWifi, ignoreChargingWifiDefault);
  }

  @Override public boolean isIgnoreChargingData() {
    return preferences.getBoolean(ignoreChargingData, ignoreChargingDataDefault);
  }

  @Override public boolean isIgnoreChargingBluetooth() {
    return preferences.getBoolean(ignoreChargingBluetooth, ignoreChargingBluetoothDefault);
  }

  @Override public boolean isIgnoreChargingSync() {
    return preferences.getBoolean(ignoreChargingSync, ignoreChargingSyncDefault);
  }

  @Override public long getWifiDelay() {
    return Long.parseLong(preferences.getString(delayWifi, delayWifiDefault));
  }

  @Override public void setWifiDelay(long time) {
    preferences.edit().putString(delayWifi, Long.toString(time)).apply();
  }

  @Override public long getAirplaneDelay() {
    return Long.parseLong(preferences.getString(delayAirplane, delayAirplaneDefault));
  }

  @Override public void setAirplaneDelay(long time) {
    preferences.edit().putString(delayAirplane, Long.toString(time)).apply();
  }

  @Override public long getDataDelay() {
    return Long.parseLong(preferences.getString(delayData, delayDataDefault));
  }

  @Override public void setDataDelay(long time) {
    preferences.edit().putString(delayData, Long.toString(time)).apply();
  }

  @Override public long getBluetoothDelay() {
    return Long.parseLong(preferences.getString(delayBluetooth, delayBluetoothDefault));
  }

  @Override public void setBluetoothDelay(long time) {
    preferences.edit().putString(delayBluetooth, Long.toString(time)).apply();
  }

  @Override public long getMasterSyncDelay() {
    return Long.parseLong(preferences.getString(delaySync, delaySyncDefault));
  }

  @Override public void setMasterSyncDelay(long time) {
    preferences.edit().putString(delaySync, Long.toString(time)).apply();
  }

  @Override public int getNotificationPriority() {
    return NotificationCompat.PRIORITY_MIN;
  }

  @Override public boolean isBluetoothManaged() {
    return preferences.getBoolean(manageBluetooth, manageBluetoothDefault);
  }

  @Override public boolean isDataManaged() {
    return preferences.getBoolean(manageData, manageDataDefault);
  }

  @Override public boolean isSyncManaged() {
    return preferences.getBoolean(manageSync, manageSyncDefault);
  }

  @Override public boolean isWifiManaged() {
    return preferences.getBoolean(manageWifi, manageWifiDefault);
  }

  @Override public boolean isWearableManaged() {
    return preferences.getBoolean(manageWearable, manageWearableDefault);
  }

  @SuppressLint("CommitPrefEdits") @Override public void clearAll() {
    preferences.edit().clear().commit();
  }

  @Override public boolean isPeriodicDoze() {
    return preferences.getBoolean(periodicDoze, periodicDozeDefault);
  }

  @Override public boolean isPeriodicWifi() {
    return preferences.getBoolean(periodicWifi, periodicWifiDefault);
  }

  @Override public boolean isPeriodicData() {
    return preferences.getBoolean(periodicData, periodicDataDefault);
  }

  @Override public boolean isPeriodicBluetooth() {
    return preferences.getBoolean(periodicBluetooth, periodicBluetoothDefault);
  }

  @Override public boolean isPeriodicSync() {
    return preferences.getBoolean(periodicSync, periodicSyncDefault);
  }

  @Override public boolean isPeriodicAirplane() {
    return preferences.getBoolean(periodicAirplane, periodicAirplaneDefault);
  }

  @Override public long getPeriodicDisableTimeDoze() {
    return Long.parseLong(preferences.getString(periodicDisableDoze, periodicDisableDozeDefault));
  }

  @Override public void setPeriodicDisableTimeDoze(long time) {
    preferences.edit().putString(periodicDisableDoze, Long.toString(time)).apply();
  }

  @Override public long getPeriodicDisableTimeAirplane() {
    return Long.parseLong(
        preferences.getString(periodicDisableAirplane, periodicDisableAirplaneDefault));
  }

  @Override public void setPeriodicDisableTimeAirplane(long time) {
    preferences.edit().putString(periodicDisableAirplane, Long.toString(time)).apply();
  }

  @Override public long getPeriodicDisableTimeWifi() {
    return Long.parseLong(preferences.getString(periodicDisableWifi, periodicDisableWifiDefault));
  }

  @Override public void setPeriodicDisableTimeWifi(long time) {
    preferences.edit().putString(periodicDisableWifi, Long.toString(time)).apply();
  }

  @Override public long getPeriodicDisableTimeData() {
    return Long.parseLong(preferences.getString(periodicDisableData, periodicDisableDataDefault));
  }

  @Override public void setPeriodicDisableTimeData(long time) {
    preferences.edit().putString(periodicDisableData, Long.toString(time)).apply();
  }

  @Override public long getPeriodicDisableTimeBluetooth() {
    return Long.parseLong(
        preferences.getString(periodicDisableBluetooth, periodicDisableBluetoothDefault));
  }

  @Override public void setPeriodicDisableTimeBluetooth(long time) {
    preferences.edit().putString(periodicDisableBluetooth, Long.toString(time)).apply();
  }

  @Override public long getPeriodicDisableTimeSync() {
    return Long.parseLong(preferences.getString(periodicDisableSync, periodicDisableSyncDefault));
  }

  @Override public void setPeriodicDisableTimeSync(long time) {
    preferences.edit().putString(periodicDisableSync, Long.toString(time)).apply();
  }

  @Override public long getPeriodicEnableTimeDoze() {
    return Long.parseLong(preferences.getString(periodicEnableDoze, periodicEnableDozeDefault));
  }

  @Override public void setPeriodicEnableTimeDoze(long time) {
    preferences.edit().putString(periodicEnableDoze, Long.toString(time)).apply();
  }

  @Override public long getPeriodicEnableTimeAirplane() {
    return Long.parseLong(
        preferences.getString(periodicEnableAirplane, periodicEnableAirplaneDefault));
  }

  @Override public void setPeriodicEnableTimeAirplane(long time) {
    preferences.edit().putString(periodicEnableAirplane, Long.toString(time)).apply();
  }

  @Override public long getPeriodicEnableTimeWifi() {
    return Long.parseLong(preferences.getString(periodicEnableWifi, periodicEnableWifiDefault));
  }

  @Override public void setPeriodicEnableTimeWifi(long time) {
    preferences.edit().putString(periodicEnableWifi, Long.toString(time)).apply();
  }

  @Override public long getPeriodicEnableTimeData() {
    return Long.parseLong(preferences.getString(periodicEnableData, periodicEnableDataDefault));
  }

  @Override public void setPeriodicEnableTimeData(long time) {
    preferences.edit().putString(periodicEnableData, Long.toString(time)).apply();
  }

  @Override public long getPeriodicEnableTimeBluetooth() {
    return Long.parseLong(
        preferences.getString(periodicEnableBluetooth, periodicEnableBluetoothDefault));
  }

  @Override public void setPeriodicEnableTimeBluetooth(long time) {
    preferences.edit().putString(periodicEnableBluetooth, Long.toString(time)).apply();
  }

  @Override public long getPeriodicEnableTimeSync() {
    return Long.parseLong(preferences.getString(periodicEnableSync, periodicEnableSyncDefault));
  }

  @Override public void setPeriodicEnableTimeSync(long time) {
    preferences.edit().putString(periodicEnableSync, Long.toString(time)).apply();
  }

  @Override public void register(@NonNull OnRegisteredSharedPreferenceChangeListener listener) {
    listener.register(preferences);
  }

  @Override public void unregister(@NonNull OnRegisteredSharedPreferenceChangeListener listener) {
    listener.unregister(preferences);
  }
}
