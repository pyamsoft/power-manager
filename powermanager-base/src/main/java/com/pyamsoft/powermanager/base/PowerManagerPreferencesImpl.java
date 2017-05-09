/*
 * Copyright 2017 Peter Kenji Yamanaka
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
import com.pyamsoft.powermanager.base.preference.AirplanePreferences;
import com.pyamsoft.powermanager.base.preference.BluetoothPreferences;
import com.pyamsoft.powermanager.base.preference.ClearPreferences;
import com.pyamsoft.powermanager.base.preference.DataPreferences;
import com.pyamsoft.powermanager.base.preference.DozePreferences;
import com.pyamsoft.powermanager.base.preference.LoggerPreferences;
import com.pyamsoft.powermanager.base.preference.ManagePreferences;
import com.pyamsoft.powermanager.base.preference.OnboardingPreferences;
import com.pyamsoft.powermanager.base.preference.RootPreferences;
import com.pyamsoft.powermanager.base.preference.ServicePreferences;
import com.pyamsoft.powermanager.base.preference.SyncPreferences;
import com.pyamsoft.powermanager.base.preference.TriggerPreferences;
import com.pyamsoft.powermanager.base.preference.WearablePreferences;
import com.pyamsoft.powermanager.base.preference.WifiPreferences;
import javax.inject.Inject;
import timber.log.Timber;

class PowerManagerPreferencesImpl
    implements WifiPreferences, ClearPreferences, WearablePreferences, AirplanePreferences,
    BluetoothPreferences, DataPreferences, DozePreferences, SyncPreferences, LoggerPreferences,
    OnboardingPreferences, RootPreferences, ServicePreferences, TriggerPreferences,
    ManagePreferences {

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

  @NonNull private final String globalManageDelayKey;
  @NonNull private final String globalManageEnableKey;
  @NonNull private final String globalManageDisableKey;
  @NonNull private final String globalManageDelayDefault;
  @NonNull private final String globalManageEnableDefault;
  @NonNull private final String globalManageDisableDefault;
  private final long defaultGlobalDelayValue;
  private final long defaultGlobalEnableValue;
  private final long defaultGlobalDisableValue;
  private final long defaultTriggerPeriodValue;

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
    defaultTriggerPeriodValue = Long.valueOf(triggerPeriodDefault);

    globalManageDelayKey = res.getString(R.string.key_manage_delay_time);
    globalManageEnableKey = res.getString(R.string.key_manage_enable_time);
    globalManageDisableKey = res.getString(R.string.key_manage_disable_time);
    globalManageDelayDefault = res.getString(R.string.default_manage_delay_time);
    globalManageEnableDefault = res.getString(R.string.default_manage_enable_time);
    globalManageDisableDefault = res.getString(R.string.default_manage_disable_time);

    defaultGlobalDelayValue = Long.valueOf(globalManageDelayDefault);
    defaultGlobalEnableValue = Long.valueOf(globalManageEnableDefault);
    defaultGlobalDisableValue = Long.valueOf(globalManageDisableDefault);
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

  @Override public boolean isOriginalBluetooth() {
    return preferences.getBoolean(ORIGINAL_BLUETOOTH, false);
  }

  @Override public void setOriginalBluetooth(boolean state) {
    preferences.edit().putBoolean(ORIGINAL_BLUETOOTH, state).apply();
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

  @Override public boolean isServiceEnabled() {
    return preferences.getBoolean(SERVICE_ENABLED, true);
  }

  @Override public void setServiceEnabled(boolean enabled) {
    preferences.edit().putBoolean(SERVICE_ENABLED, enabled).apply();
  }

  @Override public long getTriggerPeriodTime() {
    String rawPref = preferences.getString(triggerPeriodKey, triggerPeriodDefault);
    long delay;
    try {
      delay = Long.valueOf(rawPref);
    } catch (Exception e) {
      Timber.e(e, "Error assigning trigger period to long");
      delay = defaultTriggerPeriodValue;
    }
    return delay;
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

  @SuppressLint("ApplySharedPref") @Override public void clearAll() {
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

  @Override public long getManageDelay() {
    String rawPref = preferences.getString(globalManageDelayKey, globalManageDelayDefault);
    long delay;
    try {
      delay = Long.valueOf(rawPref);
    } catch (Exception e) {
      Timber.e(e, "Error assigning global delay to long");
      delay = defaultGlobalDelayValue;
    }
    return delay;
  }

  @Override public void setManageDelay(long time) {
    preferences.edit().putString(globalManageDelayKey, Long.valueOf(time).toString()).apply();
  }

  @Override public long getPeriodicDisableTime() {
    String rawPref = preferences.getString(globalManageDisableKey, globalManageDisableDefault);
    long delay;
    try {
      delay = Long.valueOf(rawPref);
    } catch (Exception e) {
      Timber.e(e, "Error assigning global disable to long");
      delay = defaultGlobalDisableValue;
    }
    return delay;
  }

  @Override public void setPeriodicDisableTime(long time) {
    preferences.edit().putString(globalManageDisableKey, Long.valueOf(time).toString()).apply();
  }

  @Override public long getPeriodicEnableTime() {
    String rawPref = preferences.getString(globalManageEnableKey, globalManageEnableDefault);
    long delay;
    try {
      delay = Long.valueOf(rawPref);
    } catch (Exception e) {
      Timber.e(e, "Error assigning global enable to long");
      delay = defaultGlobalEnableValue;
    }
    return delay;
  }

  @Override public void setPeriodicEnableTime(long time) {
    preferences.edit().putString(globalManageEnableKey, Long.valueOf(time).toString()).apply();
  }
}
