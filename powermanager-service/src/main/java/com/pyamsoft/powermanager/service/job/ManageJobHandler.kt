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
import com.pyamsoft.powermanager.base.preference.AirplanePreferences
import com.pyamsoft.powermanager.base.preference.BluetoothPreferences
import com.pyamsoft.powermanager.base.preference.DataPreferences
import com.pyamsoft.powermanager.base.preference.DataSaverPreferences
import com.pyamsoft.powermanager.base.preference.DozePreferences
import com.pyamsoft.powermanager.base.preference.PhonePreferences
import com.pyamsoft.powermanager.base.preference.SyncPreferences
import com.pyamsoft.powermanager.base.preference.WifiPreferences
import com.pyamsoft.powermanager.job.JobHandler
import com.pyamsoft.powermanager.job.JobQueuer
import com.pyamsoft.powermanager.job.JobRunner
import com.pyamsoft.powermanager.model.StateModifier
import com.pyamsoft.powermanager.model.StateObserver
import io.reactivex.Scheduler
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

@Singleton class ManageJobHandler @Inject internal constructor(
    @param:Named("delay") private val jobQueuer: JobQueuer,
    @param:Named("obs_charging") private val chargingObserver: StateObserver,
    @param:Named("obs_wear") private val wearableObserver: StateObserver,
    @param:Named("mod_wifi") private val wifiModifier: StateModifier,
    @param:Named("mod_data") private val dataModifier: StateModifier,
    @param:Named("mod_bluetooth") private val bluetoothModifier: StateModifier,
    @param:Named("mod_sync") private val syncModifier: StateModifier,
    @param:Named("mod_doze") private val dozeModifier: StateModifier,
    @param:Named("mod_airplane") private val airplaneModifier: StateModifier,
    @param:Named("mod_data_saver") private val dataSaverModifier: StateModifier,
    private val wifiPreferences: WifiPreferences, private val dataPreferences: DataPreferences,
    private val bluetoothPreferences: BluetoothPreferences,
    private val syncPreferences: SyncPreferences,
    private val airplanePreferences: AirplanePreferences,
    private val dozePreferences: DozePreferences,
    private val dataSaverPreferences: DataSaverPreferences,
    private val phonePreferences: PhonePreferences,
    @param:Named("obs_phone") private val phoneObserver: StateObserver,
    @param:Named("io") private val subScheduler: Scheduler) : JobHandler {

  @CheckResult override fun newRunner(stopper: () -> Boolean): JobRunner {
    return object : ManageJobRunner(jobQueuer, chargingObserver, wearableObserver, wifiModifier,
        dataModifier, bluetoothModifier, syncModifier, dozeModifier, airplaneModifier,
        dataSaverModifier, wifiPreferences, dataPreferences, bluetoothPreferences, syncPreferences,
        airplanePreferences, dozePreferences, dataSaverPreferences, phonePreferences, phoneObserver,
        subScheduler) {

      override val isStopped: Boolean
        get() = stopper.invoke()
    }
  }
}
