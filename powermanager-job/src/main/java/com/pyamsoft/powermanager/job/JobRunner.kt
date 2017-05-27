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

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
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
import com.pyamsoft.powermanager.model.StateModifier
import com.pyamsoft.powermanager.model.StateObserver
import io.reactivex.Completable
import io.reactivex.Scheduler
import io.reactivex.disposables.CompositeDisposable
import timber.log.Timber
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit.SECONDS

internal abstract class JobRunner(context: Context, private val jobQueuer: JobQueuer,
    private val chargingObserver: StateObserver, private val wifiModifier: StateModifier,
    private val dataModifier: StateModifier, private val bluetoothModifier: StateModifier,
    private val syncModifier: StateModifier, private val dozeModifier: StateModifier,
    private val airplaneModifier: StateModifier, private val wifiPreferences: WifiPreferences,
    private val dataPreferences: DataPreferences,
    private val bluetoothPreferences: BluetoothPreferences,
    private val syncPreferences: SyncPreferences,
    private val airplanePreferences: AirplanePreferences,
    private val dozePreferences: DozePreferences, private val rootPreferences: RootPreferences,
    private val subScheduler: Scheduler) {
  private val appContext = context.applicationContext
  private val composite = CompositeDisposable()

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
    didSomething = disable(firstRun, latch, false, modifier = dozeModifier,
        conditions = object : ManageConditions {
          override val tag: String
            get() = "Doze Mode"
          override val ignoreCharging: Boolean
            get() = dozePreferences.ignoreChargingDoze
          override val managed: Boolean
            get() = dozePreferences.dozeManaged
          override val original: Boolean
            get() = dozePreferences.originalDoze
          override val periodic: Boolean
            get() = dozePreferences.periodicDoze
          override val permission: Boolean
            get() {
              if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                return false
              } else if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
                return rootPreferences.rootEnabled
              } else {
                return appContext.applicationContext.checkCallingOrSelfPermission(
                    Manifest.permission.DUMP) == PackageManager.PERMISSION_GRANTED
              }
            }
        }) || didSomething
    if (isStopped) {
      Timber.w("%s: Stopped early", tag)
      return false
    }

    didSomething = disable(firstRun, latch, false, modifier = airplaneModifier,
        conditions = object : ManageConditions {
          override val tag: String
            get() = "Airplane Mode"
          override val managed: Boolean
            get() = airplanePreferences.airplaneManaged
          override val ignoreCharging: Boolean
            get() = airplanePreferences.ignoreChargingAirplane
          override val original: Boolean
            get() = airplanePreferences.originalAirplane
          override val periodic: Boolean
            get() = airplanePreferences.periodicAirplane
          override val permission: Boolean
            get() = rootPreferences.rootEnabled
        }) || didSomething
    if (isStopped) {
      Timber.w("%s: Stopped early", tag)
      return false
    }

    didSomething = enable(firstRun, latch, false, modifier = wifiModifier,
        conditions = object : ManageConditions {
          override val tag: String
            get() = "Wifi"
          override val ignoreCharging: Boolean
            get() = wifiPreferences.ignoreChargingWifi
          override val managed: Boolean
            get() = wifiPreferences.wifiManaged
          override val original: Boolean
            get() = wifiPreferences.originalWifi
          override val periodic: Boolean
            get() = wifiPreferences.periodicWifi
          override val permission: Boolean
            get() = true
        }) || didSomething
    if (isStopped) {
      Timber.w("%s: Stopped early", tag)
      return false
    }

    didSomething = enable(firstRun, latch, false, modifier = dataModifier,
        conditions = object : ManageConditions {
          override val tag: String
            get() = "Data"
          override val ignoreCharging: Boolean
            get() = dataPreferences.ignoreChargingData
          override val managed: Boolean
            get() = dataPreferences.dataManaged
          override val original: Boolean
            get() = dataPreferences.originalData
          override val periodic: Boolean
            get() = dataPreferences.periodicData
          override val permission: Boolean
            get() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) rootPreferences.rootEnabled else true
        }) || didSomething
    if (isStopped) {
      Timber.w("%s: Stopped early", tag)
      return false
    }

    didSomething = enable(firstRun, latch, false, modifier = bluetoothModifier,
        conditions = object : ManageConditions {
          override val tag: String
            get() = "Bluetooth"
          override val ignoreCharging: Boolean
            get() = bluetoothPreferences.ignoreChargingBluetooth
          override val managed: Boolean
            get() = bluetoothPreferences.bluetoothManaged
          override val original: Boolean
            get() = bluetoothPreferences.originalBluetooth
          override val periodic: Boolean
            get() = bluetoothPreferences.periodicBluetooth
          override val permission: Boolean
            get() = true
        }) || didSomething
    if (isStopped) {
      Timber.w("%s: Stopped early", tag)
      return false
    }

    didSomething = enable(firstRun, latch, false, modifier = syncModifier,
        conditions = object : ManageConditions {
          override val tag: String
            get() = "Sync"
          override val ignoreCharging: Boolean
            get() = syncPreferences.ignoreChargingSync
          override val managed: Boolean
            get() = syncPreferences.syncManaged
          override val original: Boolean
            get() = syncPreferences.originalSync
          override val periodic: Boolean
            get() = syncPreferences.periodicSync
          override val permission: Boolean
            get() = true
        }) || didSomething

    await(latch)
    return isJobRepeatRequired(didSomething)
  }

  private fun enable(firstRun: Boolean, latch: CountDownLatch, isCharging: Boolean,
      modifier: StateModifier, conditions: ManageConditions): Boolean {
    if (isCharging && conditions.ignoreCharging) {
      Timber.w("Do not disable %s while device is charging", conditions.tag)
      latch.countDown()
      return false
    } else {
      if (conditions.managed && conditions.original && (firstRun || conditions.periodic) && conditions.permission) {
        composite.add(Completable.fromAction {
          Timber.d("ENABLE: %s", conditions.tag)
          modifier.set()
        }.subscribeOn(subScheduler).observeOn(subScheduler).subscribe({
          latch.countDown()
        }, { Timber.e(it, "Error enabling %s", conditions.tag) }))
        return true
      } else {
        Timber.w("Not managed: %s", conditions.tag)
        latch.countDown()
        return false
      }
    }
  }

  private fun disable(firstRun: Boolean, latch: CountDownLatch, isCharging: Boolean,
      modifier: StateModifier, conditions: ManageConditions): Boolean {
    if (isCharging && conditions.ignoreCharging) {
      Timber.w("Do not disable %s while device is charging", conditions.tag)
      latch.countDown()
      return false
    } else {
      if (conditions.managed && conditions.original && (firstRun || conditions.periodic) && conditions.permission) {
        composite.add(Completable.fromAction {
          Timber.d("DISABLE: %s", conditions.tag)
          modifier.unset()
        }.subscribeOn(subScheduler).observeOn(subScheduler).subscribe({
          latch.countDown()
        }, { Timber.e(it, "Error disabling %s", conditions.tag) }))
        return true
      } else {
        Timber.w("Not managed: %s", conditions.tag)
        latch.countDown()
        return false
      }
    }
  }

  @CheckResult private fun runDisableJob(tag: String, firstRun: Boolean): Boolean {
    var didSomething = false
    val isCharging = chargingObserver.enabled()
    val latch = CountDownLatch(6)

    didSomething = disable(firstRun, latch, isCharging, modifier = wifiModifier,
        conditions = object : ManageConditions {
          override val tag: String
            get() = "Wifi"
          override val ignoreCharging: Boolean
            get() = wifiPreferences.ignoreChargingWifi
          override val managed: Boolean
            get() = wifiPreferences.wifiManaged
          override val original: Boolean
            get() = wifiPreferences.originalWifi
          override val periodic: Boolean
            get() = wifiPreferences.periodicWifi
          override val permission: Boolean
            get() = true
        }) || didSomething
    if (isStopped) {
      Timber.w("%s: Stopped early", tag)
      return false
    }

    didSomething = disable(firstRun, latch, isCharging, modifier = dataModifier,
        conditions = object : ManageConditions {
          override val tag: String
            get() = "Data"
          override val ignoreCharging: Boolean
            get() = dataPreferences.ignoreChargingData
          override val managed: Boolean
            get() = dataPreferences.dataManaged
          override val original: Boolean
            get() = dataPreferences.originalData
          override val periodic: Boolean
            get() = dataPreferences.periodicData
          override val permission: Boolean
            get() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) rootPreferences.rootEnabled else true
        }) || didSomething
    if (isStopped) {
      Timber.w("%s: Stopped early", tag)
      return false
    }

    didSomething = disable(firstRun, latch, isCharging, modifier = bluetoothModifier,
        conditions = object : ManageConditions {
          override val tag: String
            get() = "Bluetooth"
          override val ignoreCharging: Boolean
            get() = bluetoothPreferences.ignoreChargingBluetooth
          override val managed: Boolean
            get() = bluetoothPreferences.bluetoothManaged
          override val original: Boolean
            get() = bluetoothPreferences.originalBluetooth
          override val periodic: Boolean
            get() = bluetoothPreferences.periodicBluetooth
          override val permission: Boolean
            get() = true
        }) || didSomething
    if (isStopped) {
      Timber.w("%s: Stopped early", tag)
      return false
    }

    didSomething = disable(firstRun, latch, isCharging, modifier = syncModifier,
        conditions = object : ManageConditions {
          override val tag: String
            get() = "Sync"
          override val ignoreCharging: Boolean
            get() = syncPreferences.ignoreChargingSync
          override val managed: Boolean
            get() = syncPreferences.syncManaged
          override val original: Boolean
            get() = syncPreferences.originalSync
          override val periodic: Boolean
            get() = syncPreferences.periodicSync
          override val permission: Boolean
            get() = true
        }) || didSomething
    if (isStopped) {
      Timber.w("%s: Stopped early", tag)
      return false
    }

    didSomething = enable(firstRun, latch, isCharging, modifier = airplaneModifier,
        conditions = object : ManageConditions {
          override val tag: String
            get() = "Airplane Mode"
          override val managed: Boolean
            get() = airplanePreferences.airplaneManaged
          override val ignoreCharging: Boolean
            get() = airplanePreferences.ignoreChargingAirplane
          override val original: Boolean
            get() = airplanePreferences.originalAirplane
          override val periodic: Boolean
            get() = airplanePreferences.periodicAirplane
          override val permission: Boolean
            get() = rootPreferences.rootEnabled
        }) || didSomething
    if (isStopped) {
      Timber.w("%s: Stopped early", tag)
      return false
    }

    didSomething = enable(firstRun, latch, isCharging,  modifier = dozeModifier,
        conditions = object : ManageConditions {
          override val tag: String
            get() = "Doze Mode"
          override val managed: Boolean
            get() = dozePreferences.dozeManaged
          override val ignoreCharging: Boolean
            get() = dozePreferences.ignoreChargingDoze
          override val original: Boolean
            get() = dozePreferences.originalDoze
          override val periodic: Boolean
            get() = dozePreferences.periodicDoze
          override val permission: Boolean
            get() {
              if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                return false
              } else if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
                return rootPreferences.rootEnabled
              } else {
                return appContext.applicationContext.checkCallingOrSelfPermission(
                    Manifest.permission.DUMP) == PackageManager.PERMISSION_GRANTED
              }
            }
        }) || didSomething

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
    return didSomething && (repeatWifi || repeatData || repeatBluetooth || repeatSync || repeatAirplane || repeatDoze)
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
    val entry = JobQueuerEntry.builder(newTag).oneshot(false).firstRun(false).screenOn(
        !screenOn).delay(newDelayTime).repeatingOffWindow(windowOffTime).repeatingOnWindow(
        windowOnTime).build()

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

  internal interface ManageConditions {
    val tag: String
      @get:CheckResult get
    val ignoreCharging: Boolean
      @get:CheckResult get
    val managed: Boolean
      @get:CheckResult get
    val original: Boolean
      @get:CheckResult get
    val periodic: Boolean
      @get:CheckResult get
    val permission: Boolean
      @get:CheckResult get
  }
}
