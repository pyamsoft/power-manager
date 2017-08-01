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

package com.pyamsoft.powermanager.service.job

import android.support.annotation.CheckResult
import com.evernote.android.job.util.support.PersistableBundleCompat
import com.pyamsoft.powermanager.base.preference.AirplanePreferences
import com.pyamsoft.powermanager.base.preference.BluetoothPreferences
import com.pyamsoft.powermanager.base.preference.DataPreferences
import com.pyamsoft.powermanager.base.preference.DataSaverPreferences
import com.pyamsoft.powermanager.base.preference.DozePreferences
import com.pyamsoft.powermanager.base.preference.PhonePreferences
import com.pyamsoft.powermanager.base.preference.SyncPreferences
import com.pyamsoft.powermanager.base.preference.WifiPreferences
import com.pyamsoft.powermanager.job.JobQueuer
import com.pyamsoft.powermanager.job.JobRunner
import com.pyamsoft.powermanager.model.StateModifier
import com.pyamsoft.powermanager.model.StateObserver
import io.reactivex.Completable
import io.reactivex.Scheduler
import io.reactivex.disposables.CompositeDisposable
import timber.log.Timber
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit.SECONDS

internal abstract class ManageJobRunner(private val jobQueuer: JobQueuer,
    private val chargingObserver: StateObserver, private val wearableObserver: StateObserver,
    private val wifiModifier: StateModifier, private val dataModifier: StateModifier,
    private val bluetoothModifier: StateModifier, private val syncModifier: StateModifier,
    private val dozeModifier: StateModifier, private val airplaneModifier: StateModifier,
    private val dataSaverModifier: StateModifier, private val wifiPreferences: WifiPreferences,
    private val dataPreferences: DataPreferences,
    private val bluetoothPreferences: BluetoothPreferences,
    private val syncPreferences: SyncPreferences,
    private val airplanePreferences: AirplanePreferences,
    private val dozePreferences: DozePreferences,
    private val dataSaverPreferences: DataSaverPreferences,
    private val phonePreferences: PhonePreferences, private val phoneObserver: StateObserver,
    private val subScheduler: Scheduler) : JobRunner {
  private val composite = CompositeDisposable()
  private val wifiConditions = object : ManageConditions {
    override val tag: String
      get() = "Wifi"
    override val ignoreCharging: Boolean
      get() = wifiPreferences.ignoreChargingWifi
    override val ignoreWearable: Boolean
      get() = wifiPreferences.ignoreWearWifi
    override val managed: Boolean
      get() = wifiPreferences.wifiManaged
    override val original: Boolean
      get() = wifiPreferences.originalWifi
    override val periodic: Boolean
      get() = wifiPreferences.periodicWifi
  }
  private val dozeConditions = object : ManageConditions {
    override val tag: String
      get() = "Doze Mode"
    override val ignoreCharging: Boolean
      get() = dozePreferences.ignoreChargingDoze
    override val ignoreWearable: Boolean
      get() = dozePreferences.ignoreWearDoze
    override val managed: Boolean
      get() = dozePreferences.dozeManaged
    override val original: Boolean
      get() = dozePreferences.originalDoze
    override val periodic: Boolean
      get() = dozePreferences.periodicDoze
  }
  private val airplaneConditions = object : ManageConditions {
    override val tag: String
      get() = "Airplane Mode"
    override val managed: Boolean
      get() = airplanePreferences.airplaneManaged
    override val ignoreCharging: Boolean
      get() = airplanePreferences.ignoreChargingAirplane
    override val ignoreWearable: Boolean
      get() = airplanePreferences.ignoreWearAirplane
    override val original: Boolean
      get() = airplanePreferences.originalAirplane
    override val periodic: Boolean
      get() = airplanePreferences.periodicAirplane
  }
  private val dataConditions = object : ManageConditions {
    override val tag: String
      get() = "Data"
    override val ignoreCharging: Boolean
      get() = dataPreferences.ignoreChargingData
    override val ignoreWearable: Boolean
      get() = dataPreferences.ignoreWearData
    override val managed: Boolean
      get() = dataPreferences.dataManaged
    override val original: Boolean
      get() = dataPreferences.originalData
    override val periodic: Boolean
      get() = dataPreferences.periodicData
  }
  private val bluetoothConditions = object : ManageConditions {
    override val tag: String
      get() = "Bluetooth"
    override val ignoreCharging: Boolean
      get() = bluetoothPreferences.ignoreChargingBluetooth
    override val ignoreWearable: Boolean
      get() = bluetoothPreferences.ignoreWearBluetooth
    override val managed: Boolean
      get() = bluetoothPreferences.bluetoothManaged
    override val original: Boolean
      get() = bluetoothPreferences.originalBluetooth
    override val periodic: Boolean
      get() = bluetoothPreferences.periodicBluetooth
  }
  private val syncConditions = object : ManageConditions {
    override val tag: String
      get() = "Sync"
    override val ignoreCharging: Boolean
      get() = syncPreferences.ignoreChargingSync
    override val ignoreWearable: Boolean
      get() = syncPreferences.ignoreWearSync
    override val managed: Boolean
      get() = syncPreferences.syncManaged
    override val original: Boolean
      get() = syncPreferences.originalSync
    override val periodic: Boolean
      get() = syncPreferences.periodicSync
  }
  private val dataSaverConditions = object : ManageConditions {
    override val tag: String
      get() = "Data Saver"
    override val ignoreCharging: Boolean
      get() = dataSaverPreferences.ignoreChargingDataSaver
    override val ignoreWearable: Boolean
      get() = dataSaverPreferences.ignoreWearDataSaver
    override val managed: Boolean
      get() = dataSaverPreferences.dataSaverManaged
    override val original: Boolean
      get() = dataSaverPreferences.originalDataSaver
    override val periodic: Boolean
      get() = dataSaverPreferences.periodicDataSaver
  }

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
    val latch = CountDownLatch(6)
    didSomething = disable(firstRun, latch, isCharging = false, isWearableConnected = false,
        modifier = dozeModifier, conditions = dozeConditions) || didSomething
    if (isStopped) {
      Timber.w("%s: Stopped early", tag)
      return false
    }

    didSomething = disable(firstRun, latch, isCharging = false, isWearableConnected = false,
        modifier = airplaneModifier, conditions = airplaneConditions) || didSomething
    if (isStopped) {
      Timber.w("%s: Stopped early", tag)
      return false
    }

    didSomething = disable(firstRun, latch, isCharging = false, isWearableConnected = false,
        modifier = dataSaverModifier, conditions = dataSaverConditions) || didSomething
    if (isStopped) {
      Timber.w("%s: Stopped early", tag)
      return false
    }

    didSomething = enable(firstRun, latch, isCharging = false, isWearableConnected = false,
        modifier = wifiModifier, conditions = wifiConditions) || didSomething
    if (isStopped) {
      Timber.w("%s: Stopped early", tag)
      return false
    }

    didSomething = enable(firstRun, latch, isCharging = false, isWearableConnected = false,
        modifier = dataModifier, conditions = dataConditions) || didSomething
    if (isStopped) {
      Timber.w("%s: Stopped early", tag)
      return false
    }

    didSomething = enable(firstRun, latch, isCharging = false, isWearableConnected = false,
        modifier = bluetoothModifier, conditions = bluetoothConditions) || didSomething
    if (isStopped) {
      Timber.w("%s: Stopped early", tag)
      return false
    }

    didSomething = enable(firstRun, latch, isCharging = false, isWearableConnected = false,
        modifier = syncModifier, conditions = syncConditions) || didSomething
    if (isStopped) {
      Timber.w("%s: Stopped early", tag)
      return false
    }

    await(latch)
    return isJobRepeatRequired(didSomething)
  }

  private fun enable(firstRun: Boolean, latch: CountDownLatch, isCharging: Boolean,
      isWearableConnected: Boolean, modifier: StateModifier,
      conditions: ManageConditions): Boolean {
    if (isCharging && conditions.ignoreCharging) {
      Timber.w("Do not disable %s while device is charging", conditions.tag)
      latch.countDown()
      return false
    } else if (isWearableConnected && conditions.ignoreWearable) {
      Timber.w("Do not disable %s while wearable is connected", conditions.tag)
      latch.countDown()
      return false
    } else if (conditions.managed && conditions.original && (firstRun || conditions.periodic)) {
      composite.add(Completable.fromAction {
        modifier.set()
      }.subscribeOn(subScheduler).observeOn(subScheduler).doAfterTerminate {
        latch.countDown()
      }.subscribe({
        Timber.d("ENABLE: %s", conditions.tag)
      }, { Timber.e(it, "Error enabling %s", conditions.tag) }))
      return true
    } else {
      Timber.w("Not managed: %s", conditions.tag)
      latch.countDown()
      return false
    }
  }

  private fun disable(firstRun: Boolean, latch: CountDownLatch, isCharging: Boolean,
      isWearableConnected: Boolean, modifier: StateModifier,
      conditions: ManageConditions): Boolean {
    if (isCharging && conditions.ignoreCharging) {
      Timber.w("Do not disable %s while device is charging", conditions.tag)
      latch.countDown()
      return false
    } else if (isWearableConnected && conditions.ignoreWearable) {
      Timber.w("Do not disable %s while wearable is connected", conditions.tag)
      latch.countDown()
      return false
    } else if (conditions.managed && conditions.original && (firstRun || conditions.periodic)) {
      composite.add(Completable.fromAction {
        modifier.unset()
      }.subscribeOn(subScheduler).observeOn(subScheduler).doAfterTerminate {
        latch.countDown()
      }.subscribe({
        Timber.d("DISABLE: %s", conditions.tag)
      }, { Timber.e(it, "Error disabling %s", conditions.tag) }))
      return true
    } else {
      Timber.w("Not managed: %s", conditions.tag)
      latch.countDown()
      return false
    }
  }

  @CheckResult private fun runDisableJob(tag: String, firstRun: Boolean): Boolean {
    if (phonePreferences.isIgnoreDuringPhoneCall()) {
      if (!phoneObserver.unknown()) {
        if (phoneObserver.enabled()) {
          Timber.w("Do not manage, device is in a phone call.")
          return false
        }
      }
    }

    var didSomething = false
    val isCharging = chargingObserver.enabled()
    val isWearableConnected = wearableObserver.enabled()
    val latch = CountDownLatch(6)

    didSomething = disable(firstRun, latch, isCharging, isWearableConnected,
        modifier = wifiModifier, conditions = wifiConditions) || didSomething
    if (isStopped) {
      Timber.w("%s: Stopped early", tag)
      return false
    }

    didSomething = disable(firstRun, latch, isCharging, isWearableConnected,
        modifier = dataModifier, conditions = dataConditions) || didSomething
    if (isStopped) {
      Timber.w("%s: Stopped early", tag)
      return false
    }

    didSomething = disable(firstRun, latch, isCharging, isWearableConnected,
        modifier = bluetoothModifier, conditions = bluetoothConditions) || didSomething
    if (isStopped) {
      Timber.w("%s: Stopped early", tag)
      return false
    }

    didSomething = disable(firstRun, latch, isCharging, isWearableConnected,
        modifier = syncModifier, conditions = syncConditions) || didSomething
    if (isStopped) {
      Timber.w("%s: Stopped early", tag)
      return false
    }

    didSomething = enable(firstRun, latch, isCharging, isWearableConnected,
        modifier = airplaneModifier, conditions = airplaneConditions) || didSomething
    if (isStopped) {
      Timber.w("%s: Stopped early", tag)
      return false
    }

    didSomething = enable(firstRun, latch, isCharging, isWearableConnected, modifier = dozeModifier,
        conditions = dozeConditions) || didSomething
    if (isStopped) {
      Timber.w("%s: Stopped early", tag)
      return false
    }

    didSomething = enable(firstRun, latch, isCharging, isWearableConnected,
        modifier = dataSaverModifier, conditions = dataSaverConditions) || didSomething
    if (isStopped) {
      Timber.w("%s: Stopped early", tag)
      return false
    }

    await(latch)
    return isJobRepeatRequired(didSomething)
  }

  private fun await(latch: CountDownLatch) {
    Timber.d("Wait for Latch... (30 seconds)")
    try {
      latch.await(30L, SECONDS)
    } catch (e: InterruptedException) {
      Timber.e(e, "Timer was interrupted")
    }

    Timber.d("Job complete, clear composite")
    composite.clear()
  }

  @CheckResult private fun isJobRepeatRequired(didSomething: Boolean): Boolean {
    val repeatWifi = wifiPreferences.wifiManaged && wifiPreferences.periodicWifi
    val repeatData = dataPreferences.dataManaged && dataPreferences.periodicData
    val repeatBluetooth = bluetoothPreferences.bluetoothManaged && bluetoothPreferences.periodicBluetooth
    val repeatSync = syncPreferences.syncManaged && syncPreferences.periodicSync
    val repeatAirplane = airplanePreferences.airplaneManaged && airplanePreferences.periodicAirplane
    val repeatDoze = dozePreferences.dozeManaged && dozePreferences.periodicDoze
    val repeatDataSaver = dataSaverPreferences.dataSaverManaged && dataSaverPreferences.periodicDataSaver
    return didSomething && (repeatWifi || repeatData || repeatBluetooth || repeatSync || repeatAirplane || repeatDoze || repeatDataSaver)
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
    val entry = ManageJobQueuerEntry(tag = newTag,
        firstRun = false, oneShot = false, screenOn = !screenOn, delay = newDelayTime,
        repeatingOffWindow = windowOffTime, repeatingOnWindow = windowOnTime)

    jobQueuer.queue(entry)
  }

  /**
   * Runs the Job. Called either by managed jobs or directly by the JobQueuer
   */
  override fun run(tag: String, extras: PersistableBundleCompat) {
    val screenOn = extras.getBoolean(
        ManageJobQueuerEntry.KEY_SCREEN, true)
    val windowOnTime = extras.getLong(
        ManageJobQueuerEntry.KEY_ON_WINDOW, 0)
    val windowOffTime = extras.getLong(
        ManageJobQueuerEntry.KEY_OFF_WINDOW, 0)
    val oneshot = extras.getBoolean(
        ManageJobQueuerEntry.KEY_ONESHOT, false)
    val firstRun = extras.getBoolean(
        ManageJobQueuerEntry.KEY_FIRST_RUN, false)
    if (runJob(tag, screenOn, firstRun) && !oneshot) {
      repeatIfRequired(tag, screenOn, windowOnTime, windowOffTime)
    }
  }

  /**
   * Override in the actual ManagedJobs to call Job.isCancelled();

   * If it is not a managed job it never isStopped, always run to completion
   */
  @get:CheckResult internal abstract val isStopped: Boolean

  private interface ManageConditions {
    val tag: String
      @get:CheckResult get
    val ignoreCharging: Boolean
      @get:CheckResult get
    val ignoreWearable: Boolean
      @get:CheckResult get
    val managed: Boolean
      @get:CheckResult get
    val original: Boolean
      @get:CheckResult get
    val periodic: Boolean
      @get:CheckResult get
  }
}
