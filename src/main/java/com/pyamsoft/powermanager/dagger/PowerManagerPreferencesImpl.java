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
import com.pyamsoft.pydroid.app.ApplicationPreferences;
import javax.inject.Inject;

class PowerManagerPreferencesImpl extends ApplicationPreferences
    implements PowerManagerPreferences {

  @NonNull private static final String overviewOnboard = "overview_onboard";
  @NonNull private static final String manageOnboard = "manage_onboard";
  @NonNull private static final String periodOnboard = "period_onboard";
  @NonNull private static final String foregroundServiceEnabled = "foregroundServiceEnabled";
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

  @Inject PowerManagerPreferencesImpl(@NonNull Context context) {
    super(context);
    manageWifi = getResources().getString(R.string.manage_wifi_key);
    manageData = getResources().getString(R.string.manage_data_key);
    manageBluetooth = getResources().getString(R.string.manage_bluetooth_key);
    manageSync = getResources().getString(R.string.manage_sync_key);
    manageAirplane = getResources().getString(R.string.manage_airplane_key);
    manageDoze = getResources().getString(R.string.manage_doze_key);
    manageWifiDefault = getResources().getBoolean(R.bool.manage_wifi_default);
    manageDataDefault = getResources().getBoolean(R.bool.manage_data_default);
    manageBluetoothDefault = getResources().getBoolean(R.bool.manage_bluetooth_default);
    manageSyncDefault = getResources().getBoolean(R.bool.manage_sync_default);
    manageAirplaneDefault = getResources().getBoolean(R.bool.manage_airplane_default);
    manageDozeDefault = getResources().getBoolean(R.bool.manage_doze_default);

    delayDoze = getResources().getString(R.string.doze_time_key);
    delayAirplane = getResources().getString(R.string.airplane_time_key);
    delayWifi = getResources().getString(R.string.wifi_time_key);
    delayData = getResources().getString(R.string.data_time_key);
    delayBluetooth = getResources().getString(R.string.bluetooth_time_key);
    delaySync = getResources().getString(R.string.sync_time_key);
    delayDozeDefault = getResources().getString(R.string.doze_time_default);
    delayAirplaneDefault = getResources().getString(R.string.airplane_time_default);
    delayWifiDefault = getResources().getString(R.string.wifi_time_default);
    delayDataDefault = getResources().getString(R.string.data_time_default);
    delayBluetoothDefault = getResources().getString(R.string.bluetooth_time_default);
    delaySyncDefault = getResources().getString(R.string.sync_time_default);

    periodicDoze = getResources().getString(R.string.periodic_doze_key);
    periodicAirplane = getResources().getString(R.string.periodic_airplane_key);
    periodicWifi = getResources().getString(R.string.periodic_wifi_key);
    periodicData = getResources().getString(R.string.periodic_data_key);
    periodicBluetooth = getResources().getString(R.string.periodic_bluetooth_key);
    periodicSync = getResources().getString(R.string.periodic_sync_key);
    periodicDozeDefault = getResources().getBoolean(R.bool.periodic_doze_default);
    periodicAirplaneDefault = getResources().getBoolean(R.bool.periodic_airplane_default);
    periodicWifiDefault = getResources().getBoolean(R.bool.periodic_wifi_default);
    periodicDataDefault = getResources().getBoolean(R.bool.periodic_data_default);
    periodicBluetoothDefault = getResources().getBoolean(R.bool.periodic_bluetooth_default);
    periodicSyncDefault = getResources().getBoolean(R.bool.periodic_sync_default);

    periodicDisableDoze = getResources().getString(R.string.periodic_doze_disable_key);
    periodicDisableAirplane = getResources().getString(R.string.periodic_airplane_disable_key);
    periodicDisableWifi = getResources().getString(R.string.periodic_wifi_disable_key);
    periodicDisableData = getResources().getString(R.string.periodic_data_disable_key);
    periodicDisableBluetooth = getResources().getString(R.string.periodic_bluetooth_disable_key);
    periodicDisableSync = getResources().getString(R.string.periodic_sync_disable_key);
    periodicDisableDozeDefault = getResources().getString(R.string.periodic_doze_disable_default);
    periodicDisableAirplaneDefault =
        getResources().getString(R.string.periodic_airplane_disable_default);
    periodicDisableWifiDefault = getResources().getString(R.string.periodic_wifi_disable_default);
    periodicDisableDataDefault = getResources().getString(R.string.periodic_data_disable_default);
    periodicDisableBluetoothDefault =
        getResources().getString(R.string.periodic_bluetooth_disable_default);
    periodicDisableSyncDefault = getResources().getString(R.string.periodic_sync_disable_default);

    periodicEnableDoze = getResources().getString(R.string.periodic_doze_enable_key);
    periodicEnableAirplane = getResources().getString(R.string.periodic_airplane_enable_key);
    periodicEnableWifi = getResources().getString(R.string.periodic_wifi_enable_key);
    periodicEnableData = getResources().getString(R.string.periodic_data_enable_key);
    periodicEnableBluetooth = getResources().getString(R.string.periodic_bluetooth_enable_key);
    periodicEnableSync = getResources().getString(R.string.periodic_sync_enable_key);
    periodicEnableDozeDefault = getResources().getString(R.string.periodic_doze_enable_default);
    periodicEnableAirplaneDefault =
        getResources().getString(R.string.periodic_airplane_enable_default);
    periodicEnableWifiDefault = getResources().getString(R.string.periodic_wifi_enable_default);
    periodicEnableDataDefault = getResources().getString(R.string.periodic_data_enable_default);
    periodicEnableBluetoothDefault =
        getResources().getString(R.string.periodic_bluetooth_enable_default);
    periodicEnableSyncDefault = getResources().getString(R.string.periodic_sync_enable_default);

    ignoreChargingDoze = getResources().getString(R.string.ignore_charging_doze_key);
    ignoreChargingAirplane = getResources().getString(R.string.ignore_charging_airplane_key);
    ignoreChargingWifi = getResources().getString(R.string.ignore_charging_wifi_key);
    ignoreChargingData = getResources().getString(R.string.ignore_charging_data_key);
    ignoreChargingBluetooth = getResources().getString(R.string.ignore_charging_bluetooth_key);
    ignoreChargingSync = getResources().getString(R.string.ignore_charging_sync_key);
    ignoreChargingDozeDefault = getResources().getBoolean(R.bool.ignore_charging_doze_default);
    ignoreChargingAirplaneDefault =
        getResources().getBoolean(R.bool.ignore_charging_airplane_default);
    ignoreChargingWifiDefault = getResources().getBoolean(R.bool.ignore_charging_wifi_default);
    ignoreChargingDataDefault = getResources().getBoolean(R.bool.ignore_charging_data_default);
    ignoreChargingBluetoothDefault =
        getResources().getBoolean(R.bool.ignore_charging_bluetooth_default);
    ignoreChargingSyncDefault = getResources().getBoolean(R.bool.ignore_charging_sync_default);

    manageWearable = getResources().getString(R.string.manage_wearable_key);
    manageWearableDefault = getResources().getBoolean(R.bool.manage_wearable_default);

    exclusiveDoze = getResources().getString(R.string.exclusive_doze_key);
    exclusiveDozeDefault = getResources().getBoolean(R.bool.exclusive_doze_default);

    wearableDelay = getResources().getString(R.string.wearable_time_key);
    wearableDelayDefault = getResources().getString(R.string.wearable_time_default);

    startWhenOpen = getResources().getString(R.string.unsuspend_when_open_key);
    startWhenOpenDefault = getResources().getBoolean(R.bool.unsuspend_when_open_default);

    useRoot = getResources().getString(R.string.use_root_key);
    useRootDefault = getResources().getBoolean(R.bool.use_root_default);

    loggerEnabled = getResources().getString(R.string.logger_enabled);
    loggerEnabledDefault = getResources().getBoolean(R.bool.logger_enabled_default);
  }

  @Override public boolean isLoggerEnabled() {
    return get(loggerEnabled, loggerEnabledDefault);
  }

  @Override public boolean isForegroundServiceEnabled() {
    return get(foregroundServiceEnabled, true);
  }

  @Override public void setForegroundServiceEnabled(boolean state) {
    put(foregroundServiceEnabled, state);
  }

  @Override public boolean isRootEnabled() {
    return get(useRoot, useRootDefault);
  }

  @Override public void resetRootEnabled() {
    put(useRoot, useRootDefault);
  }

  @Override public boolean isStartWhenOpen() {
    return get(startWhenOpen, startWhenOpenDefault);
  }

  @Override public boolean isPeriodicOnboardingShown() {
    return get(periodOnboard, false);
  }

  @Override public void setPeriodicOnboardingShown() {
    put(periodOnboard, true);
  }

  @Override public boolean isManageOnboardingShown() {
    return get(manageOnboard, false);
  }

  @Override public void setManageOnboardingShown() {
    put(manageOnboard, true);
  }

  @Override public boolean isOverviewOnboardingShown() {
    return get(overviewOnboard, false);
  }

  @Override public void setOverviewOnboardingShown() {
    put(overviewOnboard, true);
  }

  @Override public long getWearableDelay() {
    return Long.parseLong(get(wearableDelay, wearableDelayDefault));
  }

  @Override public long getDozeDelay() {
    return Long.parseLong(get(delayDoze, delayDozeDefault));
  }

  @Override public void setDozeDelay(long time) {
    put(delayDoze, Long.toString(time));
  }

  @Override public boolean isExclusiveDoze() {
    return get(exclusiveDoze, exclusiveDozeDefault);
  }

  @Override public boolean isIgnoreChargingDoze() {
    return get(ignoreChargingDoze, ignoreChargingDozeDefault);
  }

  @Override public boolean isDozeManaged() {
    return get(manageDoze, manageDozeDefault);
  }

  @Override public boolean isAirplaneManaged() {
    return get(manageAirplane, manageAirplaneDefault);
  }

  @Override public boolean isIgnoreChargingAirplane() {
    return get(ignoreChargingAirplane, ignoreChargingAirplaneDefault);
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

  @Override public long getAirplaneDelay() {
    return Long.parseLong(get(delayAirplane, delayAirplaneDefault));
  }

  @Override public void setAirplaneDelay(long time) {
    put(delayAirplane, Long.toString(time));
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

  @Override public void clearAll() {
    clear(true);
  }

  @Override public boolean isPeriodicDoze() {
    return get(periodicDoze, periodicDozeDefault);
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

  @Override public boolean isPeriodicAirplane() {
    return get(periodicAirplane, periodicAirplaneDefault);
  }

  @Override public long getPeriodicDisableTimeDoze() {
    return Long.parseLong(get(periodicDisableDoze, periodicDisableDozeDefault));
  }

  @Override public void setPeriodicDisableTimeDoze(long time) {
    put(periodicDisableDoze, Long.toString(time));
  }

  @Override public long getPeriodicDisableTimeAirplane() {
    return Long.parseLong(get(periodicDisableAirplane, periodicDisableAirplaneDefault));
  }

  @Override public void setPeriodicDisableTimeAirplane(long time) {
    put(periodicDisableAirplane, Long.toString(time));
  }

  @Override public long getPeriodicDisableTimeWifi() {
    return Long.parseLong(get(periodicDisableWifi, periodicDisableWifiDefault));
  }

  @Override public void setPeriodicDisableTimeWifi(long time) {
    put(periodicDisableWifi, Long.toString(time));
  }

  @Override public long getPeriodicDisableTimeData() {
    return Long.parseLong(get(periodicDisableData, periodicDisableDataDefault));
  }

  @Override public void setPeriodicDisableTimeData(long time) {
    put(periodicDisableData, Long.toString(time));
  }

  @Override public long getPeriodicDisableTimeBluetooth() {
    return Long.parseLong(get(periodicDisableBluetooth, periodicDisableBluetoothDefault));
  }

  @Override public void setPeriodicDisableTimeBluetooth(long time) {
    put(periodicDisableBluetooth, Long.toString(time));
  }

  @Override public long getPeriodicDisableTimeSync() {
    return Long.parseLong(get(periodicDisableSync, periodicDisableSyncDefault));
  }

  @Override public void setPeriodicDisableTimeSync(long time) {
    put(periodicDisableSync, Long.toString(time));
  }

  @Override public long getPeriodicEnableTimeDoze() {
    return Long.parseLong(get(periodicEnableDoze, periodicEnableDozeDefault));
  }

  @Override public void setPeriodicEnableTimeDoze(long time) {
    put(periodicEnableDoze, Long.toString(time));
  }

  @Override public long getPeriodicEnableTimeAirplane() {
    return Long.parseLong(get(periodicEnableAirplane, periodicEnableAirplaneDefault));
  }

  @Override public void setPeriodicEnableTimeAirplane(long time) {
    put(periodicEnableAirplane, Long.toString(time));
  }

  @Override public long getPeriodicEnableTimeWifi() {
    return Long.parseLong(get(periodicEnableWifi, periodicEnableWifiDefault));
  }

  @Override public void setPeriodicEnableTimeWifi(long time) {
    put(periodicEnableWifi, Long.toString(time));
  }

  @Override public long getPeriodicEnableTimeData() {
    return Long.parseLong(get(periodicEnableData, periodicEnableDataDefault));
  }

  @Override public void setPeriodicEnableTimeData(long time) {
    put(periodicEnableData, Long.toString(time));
  }

  @Override public long getPeriodicEnableTimeBluetooth() {
    return Long.parseLong(get(periodicEnableBluetooth, periodicEnableBluetoothDefault));
  }

  @Override public void setPeriodicEnableTimeBluetooth(long time) {
    put(periodicEnableBluetooth, Long.toString(time));
  }

  @Override public long getPeriodicEnableTimeSync() {
    return Long.parseLong(get(periodicEnableSync, periodicEnableSyncDefault));
  }

  @Override public void setPeriodicEnableTimeSync(long time) {
    put(periodicEnableSync, Long.toString(time));
  }
}
