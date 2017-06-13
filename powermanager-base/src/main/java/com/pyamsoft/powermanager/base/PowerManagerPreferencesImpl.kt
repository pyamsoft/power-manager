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

package com.pyamsoft.powermanager.base

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import android.preference.PreferenceManager
import android.support.v7.app.NotificationCompat
import com.pyamsoft.powermanager.base.preference.AirplanePreferences
import com.pyamsoft.powermanager.base.preference.BluetoothPreferences
import com.pyamsoft.powermanager.base.preference.ClearPreferences
import com.pyamsoft.powermanager.base.preference.DataPreferences
import com.pyamsoft.powermanager.base.preference.DataSaverPreferences
import com.pyamsoft.powermanager.base.preference.DozePreferences
import com.pyamsoft.powermanager.base.preference.LoggerPreferences
import com.pyamsoft.powermanager.base.preference.ManagePreferences
import com.pyamsoft.powermanager.base.preference.OnboardingPreferences
import com.pyamsoft.powermanager.base.preference.PhonePreferences
import com.pyamsoft.powermanager.base.preference.RootPreferences
import com.pyamsoft.powermanager.base.preference.ServicePreferences
import com.pyamsoft.powermanager.base.preference.SyncPreferences
import com.pyamsoft.powermanager.base.preference.TriggerPreferences
import com.pyamsoft.powermanager.base.preference.WearablePreferences
import com.pyamsoft.powermanager.base.preference.WifiPreferences
import com.pyamsoft.powermanager.base.preference.WorkaroundPreferences
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Inject

internal class PowerManagerPreferencesImpl @Inject constructor(
    context: Context) : WifiPreferences, ClearPreferences, WearablePreferences, AirplanePreferences, BluetoothPreferences, DataPreferences, DozePreferences, SyncPreferences, LoggerPreferences, OnboardingPreferences, RootPreferences, ServicePreferences, TriggerPreferences, ManagePreferences, DataSaverPreferences, PhonePreferences, WorkaroundPreferences {
  private val preferences: SharedPreferences
  private val keyManageAirplane: String
  private val keyManageWifi: String
  private val keyManageData: String
  private val keyManageBluetooth: String
  private val keyManageSync: String
  private val keyManageDoze: String
  private val keyManageDataSaver: String
  private val manageAirplaneDefault: Boolean
  private val manageWifiDefault: Boolean
  private val manageDataDefault: Boolean
  private val manageBluetoothDefault: Boolean
  private val manageSyncDefault: Boolean
  private val manageDozeDefault: Boolean
  private val manageDataSaverDefault: Boolean
  private val keyPeriodicDoze: String
  private val keyPeriodicAirplane: String
  private val keyPeriodicWifi: String
  private val keyPeriodicData: String
  private val keyPeriodicBluetooth: String
  private val keyPeriodicSync: String
  private val keyPeriodicDataSaver: String
  private val periodicDozeDefault: Boolean
  private val periodicAirplaneDefault: Boolean
  private val periodicWifiDefault: Boolean
  private val periodicDataDefault: Boolean
  private val periodicBluetoothDefault: Boolean
  private val periodicSyncDefault: Boolean
  private val periodicDataSaverDefault: Boolean
  private val keyIgnoreChargingDoze: String
  private val keyIgnoreChargingAirplane: String
  private val keyIgnoreChargingWifi: String
  private val keyIgnoreChargingData: String
  private val keyIgnoreChargingBluetooth: String
  private val keyIgnoreChargingSync: String
  private val keyIgnoreChargingDataSaver: String
  private val ignoreChargingDozeDefault: Boolean
  private val ignoreChargingAirplaneDefault: Boolean
  private val ignoreChargingWifiDefault: Boolean
  private val ignoreChargingDataDefault: Boolean
  private val ignoreChargingBluetoothDefault: Boolean
  private val ignoreChargingSyncDefault: Boolean
  private val ignoreChargingDataSaverDefault: Boolean
  private val keyIgnoreWearDoze: String
  private val keyIgnoreWearAirplane: String
  private val keyIgnoreWearWifi: String
  private val keyIgnoreWearData: String
  private val keyIgnoreWearBluetooth: String
  private val keyIgnoreWearSync: String
  private val keyIgnoreWearDataSaver: String
  private val ignoreWearDozeDefault: Boolean
  private val ignoreWearAirplaneDefault: Boolean
  private val ignoreWearWifiDefault: Boolean
  private val ignoreWearDataDefault: Boolean
  private val ignoreWearBluetoothDefault: Boolean
  private val ignoreWearSyncDefault: Boolean
  private val ignoreWearDataSaverDefault: Boolean
  private val keyWearableDelay: String
  private val wearableDelayDefault: String
  private val keyStartWhenOpen: String
  private val startWhenOpenDefault: Boolean
  private val keyUseRoot: String
  private val useRootDefault: Boolean
  private val keyLoggerEnabled: String
  private val loggerEnabledDefault: Boolean
  private val keyTriggerPeriod: String
  private val triggerPeriodDefault: String
  private val defaultTriggerPeriodValue: Long
  private val keyIgnorePhone: String
  private val ignorePhoneDefault: Boolean
  private val keyWorkaroundData: String
  private val workaroundDataDefault: Boolean

  init {
    val appContext = context.applicationContext
    preferences = PreferenceManager.getDefaultSharedPreferences(appContext)
    val res = appContext.resources
    keyManageWifi = res.getString(R.string.manage_wifi_key)
    keyManageData = res.getString(R.string.manage_data_key)
    keyManageBluetooth = res.getString(R.string.manage_bluetooth_key)
    keyManageSync = res.getString(R.string.manage_sync_key)
    keyManageAirplane = res.getString(R.string.manage_airplane_key)
    keyManageDoze = res.getString(R.string.manage_doze_key)
    keyManageDataSaver = res.getString(R.string.manage_data_saver_key)
    manageWifiDefault = res.getBoolean(R.bool.manage_wifi_default)
    manageDataDefault = res.getBoolean(R.bool.manage_data_default)
    manageBluetoothDefault = res.getBoolean(R.bool.manage_bluetooth_default)
    manageSyncDefault = res.getBoolean(R.bool.manage_sync_default)
    manageAirplaneDefault = res.getBoolean(R.bool.manage_airplane_default)
    manageDozeDefault = res.getBoolean(R.bool.manage_doze_default)
    manageDataSaverDefault = res.getBoolean(R.bool.manage_data_saver_default)

    keyPeriodicDoze = res.getString(R.string.periodic_doze_key)
    keyPeriodicAirplane = res.getString(R.string.periodic_airplane_key)
    keyPeriodicWifi = res.getString(R.string.periodic_wifi_key)
    keyPeriodicData = res.getString(R.string.periodic_data_key)
    keyPeriodicBluetooth = res.getString(R.string.periodic_bluetooth_key)
    keyPeriodicSync = res.getString(R.string.periodic_sync_key)
    keyPeriodicDataSaver = res.getString(R.string.periodic_data_saver_key)
    periodicDozeDefault = res.getBoolean(R.bool.periodic_doze_default)
    periodicAirplaneDefault = res.getBoolean(R.bool.periodic_airplane_default)
    periodicWifiDefault = res.getBoolean(R.bool.periodic_wifi_default)
    periodicDataDefault = res.getBoolean(R.bool.periodic_data_default)
    periodicBluetoothDefault = res.getBoolean(R.bool.periodic_bluetooth_default)
    periodicSyncDefault = res.getBoolean(R.bool.periodic_sync_default)
    periodicDataSaverDefault = res.getBoolean(R.bool.periodic_data_saver_default)

    keyIgnoreChargingDoze = res.getString(R.string.ignore_charging_doze_key)
    keyIgnoreChargingAirplane = res.getString(R.string.ignore_charging_airplane_key)
    keyIgnoreChargingWifi = res.getString(R.string.ignore_charging_wifi_key)
    keyIgnoreChargingData = res.getString(R.string.ignore_charging_data_key)
    keyIgnoreChargingBluetooth = res.getString(R.string.ignore_charging_bluetooth_key)
    keyIgnoreChargingSync = res.getString(R.string.ignore_charging_sync_key)
    keyIgnoreChargingDataSaver = res.getString(R.string.ignore_charging_data_saver_key)
    ignoreChargingDozeDefault = res.getBoolean(R.bool.ignore_charging_doze_default)
    ignoreChargingAirplaneDefault = res.getBoolean(R.bool.ignore_charging_airplane_default)
    ignoreChargingWifiDefault = res.getBoolean(R.bool.ignore_charging_wifi_default)
    ignoreChargingDataDefault = res.getBoolean(R.bool.ignore_charging_data_default)
    ignoreChargingBluetoothDefault = res.getBoolean(R.bool.ignore_charging_bluetooth_default)
    ignoreChargingSyncDefault = res.getBoolean(R.bool.ignore_charging_sync_default)
    ignoreChargingDataSaverDefault = res.getBoolean(R.bool.ignore_charging_data_saver_default)

    keyIgnoreWearDoze = res.getString(R.string.ignore_wear_doze_key)
    keyIgnoreWearAirplane = res.getString(R.string.ignore_wear_airplane_key)
    keyIgnoreWearWifi = res.getString(R.string.ignore_wear_wifi_key)
    keyIgnoreWearData = res.getString(R.string.ignore_wear_data_key)
    keyIgnoreWearBluetooth = res.getString(R.string.ignore_wear_bluetooth_key)
    keyIgnoreWearSync = res.getString(R.string.ignore_wear_sync_key)
    keyIgnoreWearDataSaver = res.getString(R.string.ignore_wear_data_saver_key)
    ignoreWearDozeDefault = res.getBoolean(R.bool.ignore_wear_doze_default)
    ignoreWearAirplaneDefault = res.getBoolean(R.bool.ignore_wear_airplane_default)
    ignoreWearWifiDefault = res.getBoolean(R.bool.ignore_wear_wifi_default)
    ignoreWearDataDefault = res.getBoolean(R.bool.ignore_wear_data_default)
    ignoreWearBluetoothDefault = res.getBoolean(R.bool.ignore_wear_bluetooth_default)
    ignoreWearSyncDefault = res.getBoolean(R.bool.ignore_wear_sync_default)
    ignoreWearDataSaverDefault = res.getBoolean(R.bool.ignore_wear_data_saver_default)

    keyWearableDelay = res.getString(R.string.wearable_time_key)
    wearableDelayDefault = res.getString(R.string.wearable_time_default)

    keyStartWhenOpen = res.getString(R.string.unsuspend_when_open_key)
    startWhenOpenDefault = res.getBoolean(R.bool.unsuspend_when_open_default)

    keyUseRoot = res.getString(R.string.use_root_key)
    useRootDefault = res.getBoolean(R.bool.use_root_default)

    keyLoggerEnabled = res.getString(R.string.logger_enabled)
    loggerEnabledDefault = res.getBoolean(R.bool.logger_enabled_default)

    keyTriggerPeriod = res.getString(R.string.trigger_period_key)
    triggerPeriodDefault = res.getString(R.string.trigger_period_default)
    defaultTriggerPeriodValue = Integer.valueOf(triggerPeriodDefault)!!.toLong()

    keyIgnorePhone = res.getString(R.string.key_ignore_phone_call)
    ignorePhoneDefault = res.getBoolean(R.bool.default_ignore_phone_call)

    keyWorkaroundData = res.getString(R.string.key_workaround_data)
    workaroundDataDefault = res.getBoolean(R.bool.workaround_data_default)
  }

  override var originalWifi: Boolean
    get() = preferences.getBoolean(KEY_ORIGINAL_WIFI, false)
    set(state) = preferences.edit().putBoolean(KEY_ORIGINAL_WIFI, state).apply()
  override var originalData: Boolean
    get() = preferences.getBoolean(KEY_ORIGINAL_DATA, false)
    set(state) = preferences.edit().putBoolean(KEY_ORIGINAL_DATA, state).apply()
  override var originalBluetooth: Boolean
    get() = preferences.getBoolean(KEY_ORIGINAL_BLUETOOTH, false)
    set(state) = preferences.edit().putBoolean(KEY_ORIGINAL_BLUETOOTH, state).apply()
  override var originalSync: Boolean
    get() = preferences.getBoolean(KEY_ORIGINAL_SYNC, false)
    set(state) = preferences.edit().putBoolean(KEY_ORIGINAL_SYNC, state).apply()
  override var originalAirplane: Boolean
    get() = preferences.getBoolean(KEY_ORIGINAL_AIRPLANE, false)
    set(state) = preferences.edit().putBoolean(KEY_ORIGINAL_AIRPLANE, state).apply()
  override var originalDoze: Boolean
    get() = preferences.getBoolean(KEY_ORIGINAL_DOZE, false)
    set(state) = preferences.edit().putBoolean(KEY_ORIGINAL_DOZE, state).apply()
  override var serviceEnabled: Boolean
    get() = preferences.getBoolean(KEY_SERVICE_ENABLED, true)
    set(enabled) = preferences.edit().putBoolean(KEY_SERVICE_ENABLED, enabled).apply()
  override val triggerPeriodTime: Long
    get() {
      val rawPref = preferences.getString(keyTriggerPeriod, triggerPeriodDefault)
      var delay: Long
      try {
        delay = java.lang.Long.valueOf(rawPref)!!
      } catch (e: Exception) {
        Timber.e(e, "Error assigning trigger period to int")
        delay = defaultTriggerPeriodValue
      }

      if (delay < TRIGGER_MINIMUM) {
        delay = TRIGGER_MINIMUM
        preferences.edit().putString(keyTriggerPeriod, TRIGGER_MINIMUM.toString()).apply()
      }
      if (delay > TRIGGER_MAXIMUM) {
        delay = TRIGGER_MAXIMUM
        preferences.edit().putString(keyTriggerPeriod, TRIGGER_MINIMUM.toString()).apply()
      }
      return delay
    }
  override var loggerEnabled: Boolean
    get() = preferences.getBoolean(keyLoggerEnabled, loggerEnabledDefault)
    set(b) = preferences.edit().putBoolean(keyLoggerEnabled, b).apply()
  override var rootEnabled: Boolean
    get() = preferences.getBoolean(keyUseRoot, useRootDefault)
    set(b) = preferences.edit().putBoolean(keyUseRoot, b).apply()

  override fun resetRootEnabled() {
    rootEnabled = false
  }

  override val startWhenOpen: Boolean
    get() = preferences.getBoolean(keyStartWhenOpen, startWhenOpenDefault)
  override var periodicOnboardingShown: Boolean
    get() = preferences.getBoolean(KEY_PERIOD_ONBOARD, false)
    set(b) = preferences.edit().putBoolean(KEY_PERIOD_ONBOARD, b).apply()
  override var manageOnboardingShown: Boolean
    get() = preferences.getBoolean(KEY_MANAGE_ONBOARD, false)
    set(b) = preferences.edit().putBoolean(KEY_MANAGE_ONBOARD, b).apply()
  override var overviewOnboardingShown: Boolean
    get() = preferences.getBoolean(KEY_OVERVIEW_ONBOARD, false)
    set(b) = preferences.edit().putBoolean(KEY_OVERVIEW_ONBOARD, b).apply()
  override val wearableDelay: Long
    get() = java.lang.Long.parseLong(preferences.getString(keyWearableDelay, wearableDelayDefault))
  override var ignoreChargingDoze: Boolean
    get() = preferences.getBoolean(keyIgnoreChargingDoze, ignoreChargingDozeDefault)
    set(state) = preferences.edit().putBoolean(keyIgnoreChargingDoze, state).apply()
  override var dozeManaged: Boolean
    get() = preferences.getBoolean(keyManageDoze, manageDozeDefault)
    set(state) = preferences.edit().putBoolean(keyManageDoze, state).apply()
  override var ignoreWearDoze: Boolean
    get() = preferences.getBoolean(keyIgnoreWearDoze, ignoreWearDozeDefault)
    set(state) = preferences.edit().putBoolean(keyIgnoreWearDoze, state).apply()
  override var airplaneManaged: Boolean
    get() = preferences.getBoolean(keyManageAirplane, manageAirplaneDefault)
    set(state) = preferences.edit().putBoolean(keyManageAirplane, state).apply()
  override var ignoreChargingAirplane: Boolean
    get() = preferences.getBoolean(keyIgnoreChargingAirplane, ignoreChargingAirplaneDefault)
    set(state) = preferences.edit().putBoolean(keyIgnoreChargingAirplane, state).apply()
  override var ignoreWearAirplane: Boolean
    get() = preferences.getBoolean(keyIgnoreWearAirplane, ignoreWearAirplaneDefault)
    set(state) = preferences.edit().putBoolean(keyIgnoreWearAirplane, state).apply()
  override var ignoreChargingWifi: Boolean
    get() = preferences.getBoolean(keyIgnoreChargingWifi, ignoreChargingWifiDefault)
    set(state) = preferences.edit().putBoolean(keyIgnoreChargingWifi, state).apply()
  override var ignoreChargingData: Boolean
    get() = preferences.getBoolean(keyIgnoreChargingData, ignoreChargingDataDefault)
    set(state) = preferences.edit().putBoolean(keyIgnoreChargingData, state).apply()
  override var ignoreChargingBluetooth: Boolean
    get() = preferences.getBoolean(keyIgnoreChargingBluetooth, ignoreChargingBluetoothDefault)
    set(state) = preferences.edit().putBoolean(keyIgnoreChargingBluetooth, state).apply()
  override var ignoreChargingSync: Boolean
    get() = preferences.getBoolean(keyIgnoreChargingSync, ignoreChargingSyncDefault)
    set(state) = preferences.edit().putBoolean(keyIgnoreChargingSync, state).apply()
  override val notificationPriority: Int
    get() = NotificationCompat.PRIORITY_MIN
  override var bluetoothManaged: Boolean
    get() = preferences.getBoolean(keyManageBluetooth, manageBluetoothDefault)
    set(state) = preferences.edit().putBoolean(keyManageBluetooth, state).apply()
  override var dataManaged: Boolean
    get() = preferences.getBoolean(keyManageData, manageDataDefault)
    set(state) = preferences.edit().putBoolean(keyManageData, state).apply()
  override var syncManaged: Boolean
    get() = preferences.getBoolean(keyManageSync, manageSyncDefault)
    set(state) = preferences.edit().putBoolean(keyManageSync, state).apply()
  override var wifiManaged: Boolean
    get() = preferences.getBoolean(keyManageWifi, manageWifiDefault)
    set(state) = preferences.edit().putBoolean(keyManageWifi, state).apply()

  @SuppressLint("CommitPrefEdits") override fun clearAll() {
    preferences.edit().clear().commit()
  }

  override var periodicDoze: Boolean
    get() = preferences.getBoolean(keyPeriodicDoze, periodicDozeDefault)
    set(state) = preferences.edit().putBoolean(keyPeriodicDoze, state).apply()
  override var periodicWifi: Boolean
    get() = preferences.getBoolean(keyPeriodicWifi, periodicWifiDefault)
    set(state) = preferences.edit().putBoolean(keyPeriodicWifi, state).apply()
  override var ignoreWearWifi: Boolean
    get() = preferences.getBoolean(keyIgnoreWearWifi, ignoreWearWifiDefault)
    set(state) = preferences.edit().putBoolean(keyIgnoreWearWifi, state).apply()
  override var periodicData: Boolean
    get() = preferences.getBoolean(keyPeriodicData, periodicDataDefault)
    set(state) = preferences.edit().putBoolean(keyPeriodicData, state).apply()
  override var ignoreWearData: Boolean
    get() = preferences.getBoolean(keyIgnoreWearData, ignoreWearDataDefault)
    set(state) = preferences.edit().putBoolean(keyIgnoreWearData, state).apply()
  override var periodicBluetooth: Boolean
    get() = preferences.getBoolean(keyPeriodicBluetooth, periodicBluetoothDefault)
    set(state) = preferences.edit().putBoolean(keyPeriodicBluetooth, state).apply()
  override var ignoreWearBluetooth: Boolean
    get() = preferences.getBoolean(keyIgnoreWearBluetooth, ignoreWearBluetoothDefault)
    set(state) = preferences.edit().putBoolean(keyIgnoreWearBluetooth, state).apply()
  override var periodicSync: Boolean
    get() = preferences.getBoolean(keyPeriodicSync, periodicSyncDefault)
    set(state) = preferences.edit().putBoolean(keyPeriodicSync, state).apply()
  override var ignoreWearSync: Boolean
    get() = preferences.getBoolean(keyIgnoreWearSync, ignoreWearSyncDefault)
    set(state) = preferences.edit().putBoolean(keyIgnoreWearSync, state).apply()
  override var periodicAirplane: Boolean
    get() = preferences.getBoolean(keyPeriodicAirplane, periodicAirplaneDefault)
    set(state) = preferences.edit().putBoolean(keyPeriodicAirplane, state).apply()

  override var manageDelay: Long
    get() {
      var delay = preferences.getLong(KEY_MANAGE_DELAY_TIME, MANAGE_DELAY_DEFAULT)
      if (delay < DELAY_MINIMUM) {
        delay = DELAY_MINIMUM
        manageDelay = delay
      }
      if (delay > DELAY_MAXIMUM) {
        delay = DELAY_MAXIMUM
        manageDelay = delay
      }
      return delay
    }
    set(value) {
      var time = value
      if (time < DELAY_MINIMUM) {
        time = DELAY_MINIMUM
      }
      if (time > DELAY_MAXIMUM) {
        time = DELAY_MAXIMUM
      }

      preferences.edit().putLong(KEY_MANAGE_DELAY_TIME, time).apply()
    }
  override var customManageDelay: Boolean
    get() = preferences.getBoolean(CUSTOM_MANAGE_DELAY, false)
    set(custom) = preferences.edit().putBoolean(CUSTOM_MANAGE_DELAY, custom).apply()
  override var customDisableTime: Boolean
    get() = preferences.getBoolean(CUSTOM_MANAGE_DISABLE, false)
    set(custom) = preferences.edit().putBoolean(CUSTOM_MANAGE_DISABLE, custom).apply()
  override var periodicDisableTime: Long
    get() {
      var delay = preferences.getLong(KEY_MANAGE_DISABLE_TIME, MANAGE_DISABLE_DEFAULT)
      if (delay < PERIOD_MINIMUM) {
        delay = PERIOD_MINIMUM
        periodicDisableTime = delay
      }
      if (delay > PERIOD_MAXIMUM) {
        delay = PERIOD_MAXIMUM
        periodicDisableTime = delay
      }
      return delay
    }
    set(value) {
      var time = value
      if (time < PERIOD_MINIMUM) {
        time = PERIOD_MINIMUM
      }
      if (time > PERIOD_MAXIMUM) {
        time = PERIOD_MAXIMUM
      }

      preferences.edit().putLong(KEY_MANAGE_DISABLE_TIME, time).apply()
    }
  override val periodicEnableTime: Long
    get() = MANAGE_ENABLE_DEFAULT
  override var originalDataSaver: Boolean
    get() = preferences.getBoolean(KEY_ORIGINAL_DATA_SAVER, false)
    set(value) = preferences.edit().putBoolean(KEY_ORIGINAL_DATA_SAVER, value).apply()
  override var ignoreChargingDataSaver: Boolean
    get() = preferences.getBoolean(keyIgnoreChargingDataSaver, ignoreChargingDataSaverDefault)
    set(value) = preferences.edit().putBoolean(keyIgnoreChargingDataSaver, value).apply()
  override var dataSaverManaged: Boolean
    get() = preferences.getBoolean(keyManageDataSaver, manageDataSaverDefault)
    set(value) = preferences.edit().putBoolean(keyManageDataSaver, value).apply()
  override var periodicDataSaver: Boolean
    get() = preferences.getBoolean(keyPeriodicDataSaver, periodicDataSaverDefault)
    set(value) = preferences.edit().putBoolean(keyPeriodicDataSaver, value).apply()
  override var ignoreWearDataSaver: Boolean
    get() = preferences.getBoolean(keyIgnoreWearDataSaver, ignoreWearDataSaverDefault)
    set(value) = preferences.edit().putBoolean(keyIgnoreWearDataSaver, value).apply()

  override fun registerDelayChanges(
      listener: (Long) -> Unit): SharedPreferences.OnSharedPreferenceChangeListener {
    val preferenceListener: OnSharedPreferenceChangeListener = OnSharedPreferenceChangeListener { _, key ->
      if (KEY_MANAGE_DELAY_TIME == key) {
        listener(manageDelay)
      }
    }
    preferences.registerOnSharedPreferenceChangeListener(preferenceListener)
    return preferenceListener
  }

  override fun unregisterDelayChanges(
      listener: SharedPreferences.OnSharedPreferenceChangeListener) {
    preferences.unregisterOnSharedPreferenceChangeListener(listener)
  }

  override fun registerDisableChanges(
      listener: (Long) -> Unit): SharedPreferences.OnSharedPreferenceChangeListener {
    val preferenceListener: OnSharedPreferenceChangeListener = OnSharedPreferenceChangeListener { _, key ->
      if (KEY_MANAGE_DISABLE_TIME == key) {
        listener(periodicDisableTime)
      }
    }
    preferences.registerOnSharedPreferenceChangeListener(preferenceListener)
    return preferenceListener
  }

  override fun unregisterDisableChanges(
      listener: SharedPreferences.OnSharedPreferenceChangeListener) {
    preferences.unregisterOnSharedPreferenceChangeListener(listener)
  }

  override fun isIgnoreDuringPhoneCall(): Boolean {
    return preferences.getBoolean(keyIgnorePhone, ignorePhoneDefault)
  }

  override fun isDataWorkaroundEnabled(): Boolean {
    return preferences.getBoolean(keyWorkaroundData, workaroundDataDefault)
  }

  companion object {
    @JvmStatic private val DELAY_MINIMUM = TimeUnit.SECONDS.toSeconds(5)
    @JvmStatic private val DELAY_MAXIMUM = TimeUnit.MINUTES.toSeconds(5)
    @JvmStatic private val PERIOD_MINIMUM = TimeUnit.MINUTES.toSeconds(1)
    @JvmStatic private val PERIOD_MAXIMUM = TimeUnit.HOURS.toSeconds(1)
    @JvmStatic private val TRIGGER_MINIMUM = TimeUnit.MINUTES.toSeconds(15)
    @JvmStatic private val TRIGGER_MAXIMUM = TimeUnit.HOURS.toSeconds(2)
    @JvmStatic private val MANAGE_DELAY_DEFAULT = TimeUnit.SECONDS.toSeconds(30)
    @JvmStatic private val MANAGE_DISABLE_DEFAULT = TimeUnit.MINUTES.toSeconds(5)
    @JvmStatic private val MANAGE_ENABLE_DEFAULT = TimeUnit.MINUTES.toSeconds(1)
    private const val CUSTOM_MANAGE_DELAY = "pm7_custom_manage_delay"
    private const val CUSTOM_MANAGE_DISABLE = "pm7_custom_manage_disable"
    private const val KEY_MANAGE_DELAY_TIME = "pm7_global_manage_delay_time"
    private const val KEY_MANAGE_DISABLE_TIME = "pm7_global_manage_disable_time"
    private const val KEY_OVERVIEW_ONBOARD = "pm7_overview_onboard"
    private const val KEY_MANAGE_ONBOARD = "pm7_manage_onboard"
    private const val KEY_PERIOD_ONBOARD = "pm7_period_onboard"
    private const val KEY_SERVICE_ENABLED = "pm7_service_enabled"
    private const val KEY_ORIGINAL_WIFI = "pm7_original_wifi"
    private const val KEY_ORIGINAL_DATA = "pm7_original_data"
    private const val KEY_ORIGINAL_BLUETOOTH = "pm7_original_bluetooth"
    private const val KEY_ORIGINAL_SYNC = "pm7_original_sync"
    private const val KEY_ORIGINAL_AIRPLANE = "pm7_original_airplane"
    private const val KEY_ORIGINAL_DOZE = "pm7_original_doze"
    private const val KEY_ORIGINAL_DATA_SAVER = "pm7_original_data_saver"
  }
}
