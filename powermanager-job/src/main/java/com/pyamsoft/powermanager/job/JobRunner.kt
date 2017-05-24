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

package com.pyamsoft.powermanager.job

import android.os.Build
import android.support.annotation.CheckResult
import com.evernote.android.job.util.support.PersistableBundleCompat
import com.pyamsoft.powermanager.base.preference.AirplanePreferences
import com.pyamsoft.powermanager.base.preference.BluetoothPreferences
import com.pyamsoft.powermanager.base.preference.DataPreferences
import com.pyamsoft.powermanager.base.preference.DozePreferences
import com.pyamsoft.powermanager.base.preference.RootPreferences
import com.pyamsoft.powermanager.base.preference.SyncPreferences
import com.pyamsoft.powermanager.base.preference.WifiPreferences
import com.pyamsoft.powermanager.model.PermissionObserver
import com.pyamsoft.powermanager.model.StateModifier
import com.pyamsoft.powermanager.model.StateObserver
import timber.log.Timber

internal abstract class JobRunner(private val jobQueuer: JobQueuer,
    private val chargingObserver: StateObserver,
    private val wifiModifier: StateModifier, private val dataModifier: StateModifier,
    private val bluetoothModifier: StateModifier, private val syncModifier: StateModifier,
    private val dozeModifier: StateModifier, private val airplaneModifier: StateModifier,
    private val wifiPreferences: WifiPreferences, private val dataPreferences: DataPreferences,
    private val bluetoothPreferences: BluetoothPreferences,
    private val syncPreferences: SyncPreferences,
    private val airplanePreferences: AirplanePreferences,
    private val dozePreferences: DozePreferences,
    private val rootPreferences: RootPreferences,
    private val rootPermissionObserver: PermissionObserver,
    private val dozePermissionObserver: PermissionObserver) {

  @CheckResult private fun runJob(tag: String, screenOn: Boolean, firstRun: Boolean): Boolean {
    checkTag(tag)
    if (screenOn) {
      return runEnableJob(tag, firstRun)
    } else {
      return runDisableJob(tag, firstRun)
    }
  }

  private fun checkTag(tag: String) {
    if (tag != JobQueuer.ENABLE_TAG && tag == JobQueuer.ENABLE_TAG) {
      throw IllegalArgumentException("Illegal tag for JobRunner: " + tag)
    }
  }

  @CheckResult private fun runEnableJob(tag: String, firstRun: Boolean): Boolean {
    var didSomething = false
    if (dozePreferences.originalDoze
        && (firstRun || dozePreferences.periodicDoze)
        && rootPreferences.rootEnabled
        && dozePermissionObserver.hasPermission()) {
      Timber.i("%s: Disable Doze", tag)
      dozeModifier.unset()
      didSomething = true
    }
    if (isStopped) {
      Timber.w("%s: Stopped early", tag)
      return false
    }

    if (airplanePreferences.originalAirplane
        && (firstRun || airplanePreferences.periodicAirplane)
        && rootPreferences.rootEnabled
        && rootPermissionObserver.hasPermission()) {
      Timber.i("%s: Disable Airplane mode", tag)
      airplaneModifier.unset()
      didSomething = true
    }
    if (isStopped) {
      Timber.w("%s: Stopped early", tag)
      return false
    }

    if (wifiPreferences.originalWifi && (firstRun || wifiPreferences.periodicWifi)) {
      Timber.i("%s: Enable WiFi", tag)
      wifiModifier.set()
      didSomething = true
    }
    if (isStopped) {
      Timber.w("%s: Stopped early", tag)
      return false
    }

    if (dataPreferences.originalData
        && (firstRun || dataPreferences.periodicData)
        && rootPreferences.rootEnabled
        && rootPermissionObserver.hasPermission()) {
      Timber.i("%s: Enable Data", tag)
      dataModifier.set()
      didSomething = true
    }
    if (isStopped) {
      Timber.w("%s: Stopped early", tag)
      return false
    }

    if (bluetoothPreferences.originalBluetooth && (firstRun || bluetoothPreferences.periodicBluetooth)) {
      Timber.i("%s: Enable Bluetooth", tag)
      bluetoothModifier.set()
      didSomething = true
    }
    if (isStopped) {
      Timber.w("%s: Stopped early", tag)
      return false
    }

    if (syncPreferences.originalSync && (firstRun || syncPreferences.periodicSync)) {
      Timber.i("%s: Enable Sync", tag)
      syncModifier.set()
      didSomething = true
    }

    return isJobRepeatRequired(didSomething)
  }

  @CheckResult private fun runDisableJob(tag: String, firstRun: Boolean): Boolean {
    var didSomething = false
    val isCharging = chargingObserver.enabled()
    if (isCharging && wifiPreferences.ignoreChargingWifi) {
      Timber.w("Do not disable WiFi while device is charging")
    } else {
      if (wifiPreferences.originalWifi && (firstRun || wifiPreferences.periodicWifi)) {
        Timber.i("%s: Disable WiFi", tag)
        wifiModifier.unset()
        didSomething = true
      }
    }
    if (isStopped) {
      Timber.w("%s: Stopped early", tag)
      return false
    }

    if (isCharging && dataPreferences.ignoreChargingData) {
      Timber.w("Do not disable Data while device is charging")
    } else {
      if (dataPreferences.originalData
          && (firstRun || dataPreferences.periodicData)
          && rootPreferences.rootEnabled
          && rootPermissionObserver.hasPermission()) {
        Timber.i("%s: Disable Data", tag)
        dataModifier.unset()
        didSomething = true
      }
    }
    if (isStopped) {
      Timber.w("%s: Stopped early", tag)
      return false
    }

    if (isCharging && bluetoothPreferences.ignoreChargingBluetooth) {
      Timber.w("Do not disable Bluetooth while device is charging")
    } else {
      if (bluetoothPreferences.originalBluetooth && (firstRun || bluetoothPreferences.periodicBluetooth)) {
        Timber.i("%s: Disable Bluetooth", tag)
        bluetoothModifier.unset()
        didSomething = true
      }
    }
    if (isStopped) {
      Timber.w("%s: Stopped early", tag)
      return false
    }

    if (isCharging && syncPreferences.ignoreChargingSync) {
      Timber.w("Do not disable Sync while device is charging")
    } else {
      if (syncPreferences.originalSync && (firstRun || syncPreferences.periodicSync)) {
        Timber.i("%s: Disable Sync", tag)
        syncModifier.unset()
        didSomething = true
      }
    }
    if (isStopped) {
      Timber.w("%s: Stopped early", tag)
      return false
    }

    if (isCharging && airplanePreferences.ignoreChargingAirplane) {
      Timber.w("Do not enable Airplane mode while device is charging")
    } else {
      if (airplanePreferences.originalAirplane
          && (firstRun || airplanePreferences.periodicAirplane)
          && rootPreferences.rootEnabled
          && rootPermissionObserver.hasPermission()) {
        Timber.i("%s: Enable Airplane mode", tag)
        airplaneModifier.set()
        didSomething = true
      }
    }
    if (isStopped) {
      Timber.w("%s: Stopped early", tag)
      return false
    }

    if (isCharging && dozePreferences.ignoreChargingDoze) {
      Timber.w("Do not enable Doze mode while device is charging")
    } else {
      if (dozePreferences.originalDoze
          && (firstRun || dozePreferences.periodicDoze)
          && rootPreferences.rootEnabled
          && dozePermissionObserver.hasPermission()) {
        Timber.i("%s: Enable Doze mode", tag)
        dozeModifier.set()
        didSomething = true
      }
    }

    return isJobRepeatRequired(didSomething)
  }

  @CheckResult private fun isJobRepeatRequired(didSomething: Boolean): Boolean {
    val repeatWifi = wifiPreferences.wifiManaged && wifiPreferences.periodicWifi
    var repeatData = dataPreferences.dataManaged && dataPreferences.periodicData
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      repeatData = repeatData and rootPermissionObserver.hasPermission()
    }
    val repeatBluetooth = bluetoothPreferences.bluetoothManaged && bluetoothPreferences.periodicBluetooth
    val repeatSync = syncPreferences.syncManaged && syncPreferences.periodicSync
    val repeatAirplane = airplanePreferences.airplaneManaged
        && airplanePreferences.periodicAirplane
        && rootPermissionObserver.hasPermission()
    val repeatDoze = dozePreferences.dozeManaged
        && dozePreferences.periodicDoze
        && dozePermissionObserver.hasPermission()
    return didSomething && (repeatWifi
        || repeatData
        || repeatBluetooth
        || repeatSync
        || repeatAirplane
        || repeatDoze)
  }

  private fun repeatIfRequired(tag: String, screenOn: Boolean, windowOnTime: Long,
      windowOffTime: Long) {
    val newDelayTime: Long
    // Switch them
    if (screenOn) {
      newDelayTime = windowOnTime
    } else {
      newDelayTime = windowOffTime
    }

    val newTag: String
    if (tag == JobQueuer.DISABLE_TAG) {
      newTag = JobQueuer.ENABLE_TAG
    } else {
      newTag = JobQueuer.DISABLE_TAG
    }

    val entry = JobQueuerEntry.builder(newTag)
        .oneshot(false)
        .firstRun(false)
        .screenOn(!screenOn)
        .delay(newDelayTime)
        .repeatingOffWindow(windowOffTime)
        .repeatingOnWindow(windowOnTime)
        .build()

    jobQueuer.queue(entry)
  }

  /**
   * Runs the Job. Called either by managed jobs or directly by the JobQueuer
   */
  fun run(tag: String, extras: PersistableBundleCompat) {
    val screenOn = extras.getBoolean(BaseJobQueuer.KEY_SCREEN, true)
    val windowOnTime = extras.getLong(BaseJobQueuer.KEY_ON_WINDOW, 0)
    val windowOffTime = extras.getLong(BaseJobQueuer.KEY_OFF_WINDOW, 0)
    val oneshot = extras.getBoolean(BaseJobQueuer.KEY_ONESHOT, false)
    val firstRun = extras.getBoolean(BaseJobQueuer.KEY_FIRST_RUN, false)
    if (runJob(tag, screenOn, firstRun) && !oneshot) {
      repeatIfRequired(tag, screenOn, windowOnTime, windowOffTime)
    }
  }

  /**
   * Override in the actual ManagedJobs to call Job.isCancelled();

   * If it is not a managed job it never isStopped, always run to completion
   */
  @get:CheckResult internal abstract val isStopped: Boolean
}
