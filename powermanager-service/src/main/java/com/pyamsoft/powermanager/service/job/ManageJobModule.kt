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

import com.evernote.android.job.JobManager
import com.pyamsoft.powermanager.base.preference.AirplanePreferences
import com.pyamsoft.powermanager.base.preference.BluetoothPreferences
import com.pyamsoft.powermanager.base.preference.DataPreferences
import com.pyamsoft.powermanager.base.preference.DataSaverPreferences
import com.pyamsoft.powermanager.base.preference.DozePreferences
import com.pyamsoft.powermanager.base.preference.PhonePreferences
import com.pyamsoft.powermanager.base.preference.SyncPreferences
import com.pyamsoft.powermanager.base.preference.WifiPreferences
import com.pyamsoft.powermanager.job.InstantJobQueuerImpl
import com.pyamsoft.powermanager.job.JobQueuer
import com.pyamsoft.powermanager.model.StateModifier
import com.pyamsoft.powermanager.model.StateObserver
import dagger.Module
import dagger.Provides
import io.reactivex.Scheduler
import javax.inject.Named
import javax.inject.Singleton

@Module class ManageJobModule {

  @Singleton @Provides internal fun provideManageJobHandler(@Named("delay") jobQueuer: JobQueuer,
      @Named("obs_charging") chargingObserver: StateObserver,
      @Named("obs_wear") wearableObserver: StateObserver,
      @Named("mod_wifi") wifiModifier: StateModifier,
      @Named("mod_data") dataModifier: StateModifier,
      @Named("mod_bluetooth") bluetoothModifier: StateModifier,
      @Named("mod_sync") syncModifier: StateModifier,
      @Named("mod_doze") dozeModifier: StateModifier,
      @Named("mod_airplane") airplaneModifier: StateModifier,
      @Named("mod_data_saver") dataSaverModifier: StateModifier, wifiPreferences: WifiPreferences,
      dataPreferences: DataPreferences, bluetoothPreferences: BluetoothPreferences,
      syncPreferences: SyncPreferences, airplanePreferences: AirplanePreferences,
      dozePreferences: DozePreferences, dataSaverPreferences: DataSaverPreferences,
      phonePreferences: PhonePreferences, @Named("obs_phone") phoneObserver: StateObserver,
      @Named("io") subScheduler: Scheduler): ManageJobHandler {
    return ManageJobHandler(jobQueuer, chargingObserver, wearableObserver, wifiModifier,
        dataModifier, bluetoothModifier, syncModifier, dozeModifier, airplaneModifier,
        dataSaverModifier, wifiPreferences, dataPreferences, bluetoothPreferences, syncPreferences,
        airplanePreferences, dozePreferences, dataSaverPreferences, phonePreferences, phoneObserver,
        subScheduler)
  }

  @Singleton @Provides @Named("manage_instant") internal fun provideInstantJobQueuer(
      jobManager: JobManager, jobHandler: ManageJobHandler): JobQueuer {
    return InstantJobQueuerImpl(jobManager, jobHandler)
  }
}
