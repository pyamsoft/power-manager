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

package com.pyamsoft.powermanager.manager

import android.support.annotation.CheckResult
import com.pyamsoft.powermanager.base.preference.AirplanePreferences
import com.pyamsoft.powermanager.base.preference.BluetoothPreferences
import com.pyamsoft.powermanager.base.preference.DataPreferences
import com.pyamsoft.powermanager.base.preference.DozePreferences
import com.pyamsoft.powermanager.base.preference.ManagePreferences
import com.pyamsoft.powermanager.base.preference.SyncPreferences
import com.pyamsoft.powermanager.base.preference.WifiPreferences
import com.pyamsoft.powermanager.job.JobQueuer
import com.pyamsoft.powermanager.job.JobQueuerEntry
import com.pyamsoft.powermanager.model.ConnectedStateObserver
import com.pyamsoft.powermanager.model.StateObserver
import io.reactivex.Completable
import io.reactivex.Single
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

@Singleton internal class ManagerInteractor @Inject constructor(
    @param:Named("instant") val jobQueuer: JobQueuer,
    private val preferences: ManagePreferences,
    @param:Named("obs_wifi") private val wifiObserver: ConnectedStateObserver,
    @param:Named("obs_data") private val dataObserver: StateObserver,
    @param:Named("obs_bluetooth") private val bluetoothObserver: ConnectedStateObserver,
    @param:Named("obs_sync") private val syncObserver: StateObserver,
    @param:Named("obs_doze") private val dozeObserver: StateObserver,
    @param:Named("obs_airplane") private val airplaneObserver: StateObserver,
    private val wifiPreferences: WifiPreferences, private val dataPreferences: DataPreferences,
    private val bluetoothPreferences: BluetoothPreferences,
    private val syncPreferences: SyncPreferences,
    private val airplanePreferences: AirplanePreferences,
    private val dozePreferences: DozePreferences) {

  /**
   * public
   */
  fun destroy() {
    jobQueuer.cancel(JobQueuer.ENABLE_TAG)
    jobQueuer.cancel(JobQueuer.DISABLE_TAG)
  }

  /**
   * public
   */
  @CheckResult fun queueEnable(): Single<String> {
    return Single.fromCallable {
      val tag = JobQueuer.ENABLE_TAG
      // Queue up an enable job
      jobQueuer.cancel(tag)
      jobQueuer.queue(JobQueuerEntry.builder(tag)
          .screenOn(true)
          .delay(0)
          .oneshot(true)
          .firstRun(true)
          .repeatingOffWindow(0L)
          .repeatingOnWindow(0L)
          .build())
      return@fromCallable tag
    }.doAfterSuccess { eraseOriginalStates() }
  }

  /**
   * public
   */
  @CheckResult fun queueDisable(): Single<String> {
    return Completable.fromAction({ storeOriginalStates() }).andThen(
        Single.fromCallable {
          val tag = JobQueuer.DISABLE_TAG
          // Queue up a disable job
          jobQueuer.cancel(tag)
          jobQueuer.queue(JobQueuerEntry.builder(tag)
              .screenOn(false)
              .delay(delayTime)
              .oneshot(false)
              .firstRun(true)
              .repeatingOffWindow(periodicDisableTime)
              .repeatingOnWindow(periodicEnableTime)
              .build())
          return@fromCallable tag
        })
  }

  internal fun eraseOriginalStates() {
    wifiPreferences.originalWifi = false
    dataPreferences.originalData = false
    bluetoothPreferences.originalBluetooth = false
    syncPreferences.originalSync = false
    airplanePreferences.originalAirplane = false
    dozePreferences.originalDoze = false
    Timber.w("Erased original states, prepare for another Screen event")
  }

  internal fun storeOriginalStates() {
    if (!wifiObserver.unknown()) {
      wifiPreferences.originalWifi = wifiObserver.enabled()
    }

    if (!dataObserver.unknown()) {
      dataPreferences.originalData = dataObserver.enabled()
    }

    if (!bluetoothObserver.unknown()) {
      bluetoothPreferences.originalBluetooth = bluetoothObserver.enabled()
    }

    if (!syncObserver.unknown()) {
      syncPreferences.originalSync = syncObserver.enabled()
    }

    if (!airplaneObserver.unknown()) {
      airplanePreferences.originalAirplane = !airplaneObserver.enabled()
    }

    if (!dozeObserver.unknown()) {
      dozePreferences.originalDoze = !dozeObserver.enabled()
    }

    Timber.w("Stored original states, prepare for Sleep")
  }

  val delayTime: Long
    @CheckResult get() = preferences.manageDelay

  val periodicEnableTime: Long
    @CheckResult get() = preferences.periodicEnableTime

  val periodicDisableTime: Long
    @CheckResult get() = preferences.periodicDisableTime
}
