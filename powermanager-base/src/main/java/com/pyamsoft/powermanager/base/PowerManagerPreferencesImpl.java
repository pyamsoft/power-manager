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
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;
import timber.log.Timber;

class PowerManagerPreferencesImpl
    implements WifiPreferences, ClearPreferences, WearablePreferences, AirplanePreferences,
    BluetoothPreferences, DataPreferences, DozePreferences, SyncPreferences, LoggerPreferences,
    OnboardingPreferences, RootPreferences, ServicePreferences, TriggerPreferences,
    ManagePreferences {

  private static final long DELAY_MINIMUM = TimeUnit.SECONDS.toSeconds(5);
  private static final long PERIOD_MINIMUM = TimeUnit.MINUTES.toSeconds(1);
  private static final long TRIGGER_MINIMUM = TimeUnit.MINUTES.toSeconds(15);

  private static final long MANAGE_DELAY_DEFAULT = TimeUnit.SECONDS.toSeconds(30);
  private static final long MANAGE_DISABLE_DEFAULT = TimeUnit.MINUTES.toSeconds(5);
  private static final long MANAGE_ENABLE_DEFAULT = TimeUnit.MINUTES.toSeconds(1);

  @NonNull private static final String CUSTOM_MANAGE_DELAY = "pm7_custom_manage_delay";
  @NonNull private static final String KEY_MANAGE_DELAY_TIME = "pm7_global_manage_delay_time";
  @NonNull private static final String KEY_MANAGE_DISABLE_TIME = "pm7_global_manage_disable_time";
  @NonNull private static final String KEY_OVERVIEW_ONBOARD = "pm7_overview_onboard";
  @NonNull private static final String KEY_MANAGE_ONBOARD = "pm7_manage_onboard";
  @NonNull private static final String KEY_PERIOD_ONBOARD = "pm7_period_onboard";
  @NonNull private static final String KEY_SERVICE_ENABLED = "pm7_service_enabled";
  @NonNull private static final String KEY_ORIGINAL_WIFI = "pm7_original_wifi";
  @NonNull private static final String KEY_ORIGINAL_DATA = "pm7_original_data";
  @NonNull private static final String KEY_ORIGINAL_BLUETOOTH = "pm7_original_bluetooth";
  @NonNull private static final String KEY_ORIGINAL_SYNC = "pm7_original_sync";
  @NonNull private static final String KEY_ORIGINAL_AIRPLANE = "pm7_original_airplane";
  @NonNull private static final String KEY_ORIGINAL_DOZE = "pm7_original_doze";
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
  @NonNull private final String ignoreWearDoze;
  @NonNull private final String ignoreWearAirplane;
  @NonNull private final String ignoreWearWifi;
  @NonNull private final String ignoreWearData;
  @NonNull private final String ignoreWearBluetooth;
  @NonNull private final String ignoreWearSync;
  private final boolean ignoreWearDozeDefault;
  private final boolean ignoreWearAirplaneDefault;
  private final boolean ignoreWearWifiDefault;
  private final boolean ignoreWearDataDefault;
  private final boolean ignoreWearBluetoothDefault;
  private final boolean ignoreWearSyncDefault;
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

    ignoreWearDoze = res.getString(R.string.ignore_wear_doze_key);
    ignoreWearAirplane = res.getString(R.string.ignore_wear_airplane_key);
    ignoreWearWifi = res.getString(R.string.ignore_wear_wifi_key);
    ignoreWearData = res.getString(R.string.ignore_wear_data_key);
    ignoreWearBluetooth = res.getString(R.string.ignore_wear_bluetooth_key);
    ignoreWearSync = res.getString(R.string.ignore_wear_sync_key);
    ignoreWearDozeDefault = res.getBoolean(R.bool.ignore_wear_doze_default);
    ignoreWearAirplaneDefault = res.getBoolean(R.bool.ignore_wear_airplane_default);
    ignoreWearWifiDefault = res.getBoolean(R.bool.ignore_wear_wifi_default);
    ignoreWearDataDefault = res.getBoolean(R.bool.ignore_wear_data_default);
    ignoreWearBluetoothDefault = res.getBoolean(R.bool.ignore_wear_bluetooth_default);
    ignoreWearSyncDefault = res.getBoolean(R.bool.ignore_wear_sync_default);

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
    defaultTriggerPeriodValue = Integer.valueOf(triggerPeriodDefault);
  }

  @Override public boolean isOriginalWifi() {
    return preferences.getBoolean(KEY_ORIGINAL_WIFI, false);
  }

  @Override public void setOriginalWifi(boolean state) {
    preferences.edit().putBoolean(KEY_ORIGINAL_WIFI, state).apply();
  }

  @Override public boolean isOriginalData() {
    return preferences.getBoolean(KEY_ORIGINAL_DATA, false);
  }

  @Override public void setOriginalData(boolean state) {
    preferences.edit().putBoolean(KEY_ORIGINAL_DATA, state).apply();
  }

  @Override public boolean isOriginalBluetooth() {
    return preferences.getBoolean(KEY_ORIGINAL_BLUETOOTH, false);
  }

  @Override public void setOriginalBluetooth(boolean state) {
    preferences.edit().putBoolean(KEY_ORIGINAL_BLUETOOTH, state).apply();
  }

  @Override public boolean isOriginalSync() {
    return preferences.getBoolean(KEY_ORIGINAL_SYNC, false);
  }

  @Override public void setOriginalSync(boolean state) {
    preferences.edit().putBoolean(KEY_ORIGINAL_SYNC, state).apply();
  }

  @Override public boolean isOriginalAirplane() {
    return preferences.getBoolean(KEY_ORIGINAL_AIRPLANE, false);
  }

  @Override public void setOriginalAirplane(boolean state) {
    preferences.edit().putBoolean(KEY_ORIGINAL_AIRPLANE, state).apply();
  }

  @Override public boolean isOriginalDoze() {
    return preferences.getBoolean(KEY_ORIGINAL_DOZE, false);
  }

  @Override public void setOriginalDoze(boolean state) {
    preferences.edit().putBoolean(KEY_ORIGINAL_DOZE, state).apply();
  }

  @Override public boolean isServiceEnabled() {
    return preferences.getBoolean(KEY_SERVICE_ENABLED, true);
  }

  @Override public void setServiceEnabled(boolean enabled) {
    preferences.edit().putBoolean(KEY_SERVICE_ENABLED, enabled).apply();
  }

  @Override public long getTriggerPeriodTime() {
    String rawPref = preferences.getString(triggerPeriodKey, triggerPeriodDefault);
    long delay;
    try {
      delay = Long.valueOf(rawPref);
    } catch (Exception e) {
      Timber.e(e, "Error assigning trigger period to int");
      delay = defaultTriggerPeriodValue;
    }

    if (delay < TRIGGER_MINIMUM) {
      delay = TRIGGER_MINIMUM;
      preferences.edit().putString(triggerPeriodKey, String.valueOf(TRIGGER_MINIMUM)).apply();
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
    return preferences.getBoolean(KEY_PERIOD_ONBOARD, false);
  }

  @Override public void setPeriodicOnboardingShown() {
    preferences.edit().putBoolean(KEY_PERIOD_ONBOARD, true).apply();
  }

  @Override public boolean isManageOnboardingShown() {
    return preferences.getBoolean(KEY_MANAGE_ONBOARD, false);
  }

  @Override public void setManageOnboardingShown() {
    preferences.edit().putBoolean(KEY_MANAGE_ONBOARD, true).apply();
  }

  @Override public boolean isOverviewOnboardingShown() {
    return preferences.getBoolean(KEY_OVERVIEW_ONBOARD, false);
  }

  @Override public void setOverviewOnboardingShown() {
    preferences.edit().putBoolean(KEY_OVERVIEW_ONBOARD, true).apply();
  }

  @Override public long getWearableDelay() {
    return Long.parseLong(preferences.getString(wearableDelay, wearableDelayDefault));
  }

  @Override public boolean isIgnoreChargingDoze() {
    return preferences.getBoolean(ignoreChargingDoze, ignoreChargingDozeDefault);
  }

  @Override public void setIgnoreChargingDoze(boolean state) {
    preferences.edit().putBoolean(ignoreChargingDoze, state).apply();
  }

  @Override public boolean isDozeManaged() {
    return preferences.getBoolean(manageDoze, manageDozeDefault);
  }

  @Override public void setDozeManaged(boolean state) {
    preferences.edit().putBoolean(manageDoze, state).apply();
  }

  @Override public boolean isIgnoreWearDoze() {
    return preferences.getBoolean(ignoreWearDoze, ignoreWearDozeDefault);
  }

  @Override public void setIgnoreWearDoze(boolean state) {
    preferences.edit().putBoolean(ignoreWearDoze, state).apply();
  }

  @Override public boolean isAirplaneManaged() {
    return preferences.getBoolean(manageAirplane, manageAirplaneDefault);
  }

  @Override public void setAirplaneManaged(boolean state) {
    preferences.edit().putBoolean(manageAirplane, state).apply();
  }

  @Override public boolean isIgnoreChargingAirplane() {
    return preferences.getBoolean(ignoreChargingAirplane, ignoreChargingAirplaneDefault);
  }

  @Override public void setIgnoreChargingAirplane(boolean state) {
    preferences.edit().putBoolean(ignoreChargingAirplane, state).apply();
  }

  @Override public boolean isIgnoreWearAirplane() {
    return preferences.getBoolean(ignoreWearAirplane, ignoreWearAirplaneDefault);
  }

  @Override public void setIgnoreWearAirplane(boolean state) {
    preferences.edit().putBoolean(ignoreWearAirplane, state).apply();
  }

  @Override public boolean isIgnoreChargingWifi() {
    return preferences.getBoolean(ignoreChargingWifi, ignoreChargingWifiDefault);
  }

  @Override public void setIgnoreChargingWifi(boolean state) {
    preferences.edit().putBoolean(ignoreChargingWifi, state).apply();
  }

  @Override public boolean isIgnoreChargingData() {
    return preferences.getBoolean(ignoreChargingData, ignoreChargingDataDefault);
  }

  @Override public void setIgnoreChargingData(boolean state) {
    preferences.edit().putBoolean(ignoreChargingData, state).apply();
  }

  @Override public boolean isIgnoreChargingBluetooth() {
    return preferences.getBoolean(ignoreChargingBluetooth, ignoreChargingBluetoothDefault);
  }

  @Override public void setIgnoreChargingBluetooth(boolean state) {
    preferences.edit().putBoolean(ignoreChargingBluetooth, state).apply();
  }

  @Override public boolean isIgnoreChargingSync() {
    return preferences.getBoolean(ignoreChargingSync, ignoreChargingSyncDefault);
  }

  @Override public void setIgnoreChargingSync(boolean state) {
    preferences.edit().putBoolean(ignoreChargingSync, state).apply();
  }

  @Override public int getNotificationPriority() {
    return NotificationCompat.PRIORITY_MIN;
  }

  @Override public boolean isBluetoothManaged() {
    return preferences.getBoolean(manageBluetooth, manageBluetoothDefault);
  }

  @Override public void setBluetoothManaged(boolean state) {
    preferences.edit().putBoolean(manageBluetooth, state).apply();
  }

  @Override public boolean isDataManaged() {
    return preferences.getBoolean(manageData, manageDataDefault);
  }

  @Override public void setDataManaged(boolean state) {
    preferences.edit().putBoolean(manageData, state).apply();
  }

  @Override public boolean isSyncManaged() {
    return preferences.getBoolean(manageSync, manageSyncDefault);
  }

  @Override public void setSyncManaged(boolean state) {
    preferences.edit().putBoolean(manageSync, state).apply();
  }

  @Override public boolean isWifiManaged() {
    return preferences.getBoolean(manageWifi, manageWifiDefault);
  }

  @Override public void setWifiManaged(boolean state) {
    preferences.edit().putBoolean(manageWifi, state).apply();
  }

  @SuppressLint("ApplySharedPref") @Override public void clearAll() {
    preferences.edit().clear().commit();
  }

  @Override public boolean isPeriodicDoze() {
    return preferences.getBoolean(periodicDoze, periodicDozeDefault);
  }

  @Override public void setPeriodicDoze(boolean state) {
    preferences.edit().putBoolean(periodicDoze, state).apply();
  }

  @Override public boolean isPeriodicWifi() {
    return preferences.getBoolean(periodicWifi, periodicWifiDefault);
  }

  @Override public void setPeriodicWifi(boolean state) {
    preferences.edit().putBoolean(periodicWifi, state).apply();
  }

  @Override public boolean isIgnoreWearWifi() {
    return preferences.getBoolean(ignoreWearWifi, ignoreWearWifiDefault);
  }

  @Override public void setIgnoreWearWifi(boolean state) {
    preferences.edit().putBoolean(ignoreWearWifi, state).apply();
  }

  @Override public boolean isPeriodicData() {
    return preferences.getBoolean(periodicData, periodicDataDefault);
  }

  @Override public void setPeriodicData(boolean state) {
    preferences.edit().putBoolean(periodicData, state).apply();
  }

  @Override public boolean isIgnoreWearData() {
    return preferences.getBoolean(ignoreWearData, ignoreWearDataDefault);
  }

  @Override public void setIgnoreWearData(boolean state) {
    preferences.edit().putBoolean(ignoreWearData, state).apply();
  }

  @Override public boolean isPeriodicBluetooth() {
    return preferences.getBoolean(periodicBluetooth, periodicBluetoothDefault);
  }

  @Override public void setPeriodicBluetooth(boolean state) {
    preferences.edit().putBoolean(periodicBluetooth, state).apply();
  }

  @Override public boolean isIgnoreWearBluetooth() {
    return preferences.getBoolean(ignoreWearBluetooth, ignoreWearBluetoothDefault);
  }

  @Override public void setIgnoreWearBluetooth(boolean state) {
    preferences.edit().putBoolean(ignoreWearBluetooth, state).apply();
  }

  @Override public boolean isPeriodicSync() {
    return preferences.getBoolean(periodicSync, periodicSyncDefault);
  }

  @Override public void setPeriodicSync(boolean state) {
    preferences.edit().putBoolean(periodicSync, state).apply();
  }

  @Override public boolean isIgnoreWearSync() {
    return preferences.getBoolean(ignoreWearSync, ignoreWearSyncDefault);
  }

  @Override public void setIgnoreWearSync(boolean state) {
    preferences.edit().putBoolean(ignoreWearSync, state).apply();
  }

  @Override public boolean isPeriodicAirplane() {
    return preferences.getBoolean(periodicAirplane, periodicAirplaneDefault);
  }

  @Override public void setPeriodicAirplane(boolean state) {
    preferences.edit().putBoolean(periodicAirplane, state).apply();
  }

  @Override public long getManageDelay() {
    long delay = preferences.getLong(KEY_MANAGE_DELAY_TIME, MANAGE_DELAY_DEFAULT);
    if (delay < DELAY_MINIMUM) {
      delay = DELAY_MINIMUM;
      setManageDelay(delay);
    }
    return delay;
  }

  @Override public void setManageDelay(long time) {
    if (time < DELAY_MINIMUM) {
      time = DELAY_MINIMUM;
    }
    preferences.edit().putLong(KEY_MANAGE_DELAY_TIME, time).apply();
  }

  @Override public boolean isCustomManageDelay() {
    return preferences.getBoolean(CUSTOM_MANAGE_DELAY, false);
  }

  @Override public void setCustomManageDelay(boolean custom) {
    preferences.edit().putBoolean(CUSTOM_MANAGE_DELAY, custom).apply();
  }

  @Override public long getPeriodicDisableTime() {
    long delay = preferences.getLong(KEY_MANAGE_DISABLE_TIME, MANAGE_DISABLE_DEFAULT);
    if (delay < PERIOD_MINIMUM) {
      delay = PERIOD_MINIMUM;
      setPeriodicDisableTime(delay);
    }
    return delay;
  }

  @Override public void setPeriodicDisableTime(long time) {
    if (time < PERIOD_MINIMUM) {
      time = PERIOD_MINIMUM;
    }
    preferences.edit().putLong(KEY_MANAGE_DISABLE_TIME, time).apply();
  }

  @Override public long getPeriodicEnableTime() {
    return MANAGE_ENABLE_DEFAULT;
  }

  @NonNull @Override public SharedPreferences.OnSharedPreferenceChangeListener registerDelayChanges(
      @NonNull DelayTimeChangeListener listener) {
    SharedPreferences.OnSharedPreferenceChangeListener preferenceListener =
        (sharedPreferences, key) -> {
          if (KEY_MANAGE_DELAY_TIME.equals(key)) {
            listener.onDelayTimeChanged(getManageDelay());
          }
        };
    preferences.registerOnSharedPreferenceChangeListener(preferenceListener);
    return preferenceListener;
  }

  @Override public void unregisterDelayChanges(
      @NonNull SharedPreferences.OnSharedPreferenceChangeListener listener) {
    preferences.unregisterOnSharedPreferenceChangeListener(listener);
  }
}
