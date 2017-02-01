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

package com.pyamsoft.powermanager.job;

import android.content.Context;
import android.support.annotation.NonNull;
import com.evernote.android.job.Job;
import com.evernote.android.job.JobCreator;
import com.evernote.android.job.JobManager;
import com.pyamsoft.powermanager.base.db.PowerTriggerDB;
import com.pyamsoft.powermanager.model.BooleanInterestModifier;
import com.pyamsoft.powermanager.model.BooleanInterestObserver;
import com.pyamsoft.powermanager.model.Logger;
import dagger.Module;
import dagger.Provides;
import javax.inject.Named;

@Module public class JobModule {

  @Provides JobQueuer provideJobQueuer(@NonNull JobManager jobManager,
      @NonNull @Named("logger_wifi") Logger loggerWifi,
      @NonNull @Named("logger_data") Logger loggerData,
      @NonNull @Named("logger_bluetooth") Logger loggerBluetooth,
      @NonNull @Named("logger_sync") Logger loggerSync,
      @NonNull @Named("logger_doze") Logger loggerDoze,
      @NonNull @Named("logger_airplane") Logger loggerAirplane,
      @NonNull @Named("obs_wifi_state") BooleanInterestObserver stateObserverWifi,
      @NonNull @Named("obs_data_state") BooleanInterestObserver stateObserverData,
      @NonNull @Named("obs_bluetooth_state") BooleanInterestObserver stateObserverBluetooth,
      @NonNull @Named("obs_sync_state") BooleanInterestObserver stateObserverSync,
      @NonNull @Named("obs_doze_state") BooleanInterestObserver stateObserverDoze,
      @NonNull @Named("obs_airplane_state") BooleanInterestObserver stateObserverAirplane,
      @NonNull @Named("mod_wifi_state") BooleanInterestModifier stateModifierWifi,
      @NonNull @Named("mod_data_state") BooleanInterestModifier stateModifierData,
      @NonNull @Named("mod_bluetooth_state") BooleanInterestModifier stateModifierBluetooth,
      @NonNull @Named("mod_sync_state") BooleanInterestModifier stateModifierSync,
      @NonNull @Named("mod_doze_state") BooleanInterestModifier stateModifierDoze,
      @NonNull @Named("mod_airplane_state") BooleanInterestModifier stateModifierAirplane) {
    return new JobQueuerImpl(jobManager, loggerWifi, loggerData, loggerBluetooth, loggerSync,
        loggerDoze, loggerAirplane, stateObserverWifi, stateObserverData, stateObserverBluetooth,
        stateObserverSync, stateObserverDoze, stateObserverAirplane, stateModifierWifi,
        stateModifierData, stateModifierBluetooth, stateModifierSync, stateModifierDoze,
        stateModifierAirplane);
  }

  @Provides JobCreator provideJobCreator(@NonNull @Named("logger_wifi") Logger loggerWifi,
      @NonNull @Named("logger_data") Logger loggerData,
      @NonNull @Named("logger_bluetooth") Logger loggerBluetooth,
      @NonNull @Named("logger_sync") Logger loggerSync,
      @NonNull @Named("logger_doze") Logger loggerDoze,
      @NonNull @Named("logger_airplane") Logger loggerAirplane,
      @NonNull @Named("logger_trigger") Logger loggerTrigger,
      @NonNull @Named("obs_wifi_state") BooleanInterestObserver stateObserverWifi,
      @NonNull @Named("obs_data_state") BooleanInterestObserver stateObserverData,
      @NonNull @Named("obs_bluetooth_state") BooleanInterestObserver stateObserverBluetooth,
      @NonNull @Named("obs_sync_state") BooleanInterestObserver stateObserverSync,
      @NonNull @Named("obs_doze_state") BooleanInterestObserver stateObserverDoze,
      @NonNull @Named("obs_airplane_state") BooleanInterestObserver stateObserverAirplane,
      @NonNull @Named("mod_wifi_state") BooleanInterestModifier stateModifierWifi,
      @NonNull @Named("mod_data_state") BooleanInterestModifier stateModifierData,
      @NonNull @Named("mod_bluetooth_state") BooleanInterestModifier stateModifierBluetooth,
      @NonNull @Named("mod_sync_state") BooleanInterestModifier stateModifierSync,
      @NonNull @Named("mod_doze_state") BooleanInterestModifier stateModifierDoze,
      @NonNull @Named("mod_airplane_state") BooleanInterestModifier stateModifierAirplane,
      @NonNull PowerTriggerDB powerTriggerDB,
      @Named("obs_charging_state") @NonNull BooleanInterestObserver chargingObserver) {
    return tag -> {
      final Job job;
      switch (tag) {
        case JobQueuer.WIFI_JOB_TAG:
          job = new WifiJob.ManagedJob(loggerWifi, stateObserverWifi, stateModifierWifi);
          break;
        case JobQueuer.DATA_JOB_TAG:
          job = new DataJob.ManagedJob(loggerData, stateObserverData, stateModifierData);
          break;
        case JobQueuer.BLUETOOTH_JOB_TAG:
          job = new BluetoothJob.ManagedJob(loggerBluetooth, stateObserverBluetooth,
              stateModifierBluetooth);
          break;
        case JobQueuer.SYNC_JOB_TAG:
          job = new SyncJob.ManagedJob(loggerSync, stateObserverSync, stateModifierSync);
          break;
        case JobQueuer.DOZE_JOB_TAG:
          job = new DozeJob.ManagedJob(loggerDoze, stateObserverDoze, stateModifierDoze);
          break;
        case JobQueuer.AIRPLANE_JOB_TAG:
          job = new AirplaneJob.ManagedJob(loggerAirplane, stateObserverAirplane,
              stateModifierAirplane);
          break;
        case JobQueuer.TRIGGER_JOB_TAG:
          job = new TriggerJob(powerTriggerDB, chargingObserver, loggerTrigger, stateObserverWifi,
              stateObserverData, stateObserverBluetooth, stateObserverSync, stateModifierWifi,
              stateModifierData, stateModifierBluetooth, stateModifierSync);
          break;
        default:
          job = null;
      }
      return job;
    };
  }

  @Provides JobManager provideJobManager(@NonNull Context context, @NonNull JobCreator creator) {
    JobManager.create(context.getApplicationContext());
    JobManager.instance().removeJobCreator(creator);
    JobManager.instance().addJobCreator(creator);
    return JobManager.instance();
  }
}
