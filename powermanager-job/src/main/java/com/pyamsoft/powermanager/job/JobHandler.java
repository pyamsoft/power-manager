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

package com.pyamsoft.powermanager.job;

import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import com.evernote.android.job.util.support.PersistableBundleCompat;
import com.pyamsoft.powermanager.base.preference.AirplanePreferences;
import com.pyamsoft.powermanager.base.preference.BluetoothPreferences;
import com.pyamsoft.powermanager.base.preference.DataPreferences;
import com.pyamsoft.powermanager.base.preference.DozePreferences;
import com.pyamsoft.powermanager.base.preference.SyncPreferences;
import com.pyamsoft.powermanager.base.preference.WifiPreferences;
import com.pyamsoft.powermanager.model.StateModifier;
import com.pyamsoft.powermanager.model.StateObserver;
import com.pyamsoft.pydroid.function.FuncNone;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

@Singleton class JobHandler {

  @NonNull private final JobQueuer jobQueuer;
  @NonNull private final StateObserver chargingObserver;

  @NonNull private final StateObserver wifiObserver;
  @NonNull private final StateObserver dataObserver;
  @NonNull private final StateObserver bluetoothObserver;
  @NonNull private final StateObserver syncObserver;
  @NonNull private final StateObserver dozeObserver;
  @NonNull private final StateObserver airplaneObserver;

  @NonNull private final StateModifier wifiModifier;
  @NonNull private final StateModifier dataModifier;
  @NonNull private final StateModifier bluetoothModifier;
  @NonNull private final StateModifier syncModifier;
  @NonNull private final StateModifier dozeModifier;
  @NonNull private final StateModifier airplaneModifier;

  @NonNull private final WifiPreferences wifiPreferences;
  @NonNull private final DataPreferences dataPreferences;
  @NonNull private final BluetoothPreferences bluetoothPreferences;
  @NonNull private final SyncPreferences syncPreferences;
  @NonNull private final AirplanePreferences airplanePreferences;
  @NonNull private final DozePreferences dozePreferences;

  @Inject JobHandler(@NonNull JobQueuer jobQueuer,
      @NonNull @Named("obs_charging") StateObserver chargingObserver,
      @NonNull @Named("obs_wifi") StateObserver wifiObserver,
      @NonNull @Named("obs_data") StateObserver dataObserver,
      @NonNull @Named("obs_bluetooth") StateObserver bluetoothObserver,
      @NonNull @Named("obs_sync") StateObserver syncObserver,
      @NonNull @Named("obs_doze") StateObserver dozeObserver,
      @NonNull @Named("obs_airplane") StateObserver airplaneObserver,
      @NonNull @Named("mod_wifi") StateModifier wifiModifier,
      @NonNull @Named("mod_data") StateModifier dataModifier,
      @NonNull @Named("mod_bluetooth") StateModifier bluetoothModifier,
      @NonNull @Named("mod_sync") StateModifier syncModifier,
      @NonNull @Named("mod_doze") StateModifier dozeModifier,
      @NonNull @Named("mod_airplane") StateModifier airplaneModifier,
      @NonNull WifiPreferences wifiPreferences, @NonNull DataPreferences dataPreferences,
      @NonNull BluetoothPreferences bluetoothPreferences, @NonNull SyncPreferences syncPreferences,
      @NonNull AirplanePreferences airplanePreferences, @NonNull DozePreferences dozePreferences) {
    this.jobQueuer = jobQueuer;
    this.chargingObserver = chargingObserver;
    this.wifiObserver = wifiObserver;
    this.dataObserver = dataObserver;
    this.bluetoothObserver = bluetoothObserver;
    this.syncObserver = syncObserver;
    this.dozeObserver = dozeObserver;
    this.airplaneObserver = airplaneObserver;
    this.wifiModifier = wifiModifier;
    this.dataModifier = dataModifier;
    this.bluetoothModifier = bluetoothModifier;
    this.syncModifier = syncModifier;
    this.dozeModifier = dozeModifier;
    this.airplaneModifier = airplaneModifier;
    this.wifiPreferences = wifiPreferences;
    this.dataPreferences = dataPreferences;
    this.bluetoothPreferences = bluetoothPreferences;
    this.syncPreferences = syncPreferences;
    this.airplanePreferences = airplanePreferences;
    this.dozePreferences = dozePreferences;
  }

  @CheckResult @NonNull Runner newRunner(@NonNull FuncNone<Boolean> stopper) {
    return new Runner(jobQueuer, chargingObserver, wifiObserver, dataObserver, bluetoothObserver,
        syncObserver, dozeObserver, airplaneObserver, wifiModifier, dataModifier, bluetoothModifier,
        syncModifier, dozeModifier, airplaneModifier, wifiPreferences, dataPreferences,
        bluetoothPreferences, syncPreferences, airplanePreferences, dozePreferences) {
      @Override boolean isStopped() {
        return stopper.call();
      }
    };
  }

  static abstract class Runner {

    @NonNull private final JobQueuer jobQueuer;
    @NonNull private final StateObserver chargingObserver;

    @NonNull private final StateObserver wifiObserver;
    @NonNull private final StateObserver dataObserver;
    @NonNull private final StateObserver bluetoothObserver;
    @NonNull private final StateObserver syncObserver;
    @NonNull private final StateObserver dozeObserver;
    @NonNull private final StateObserver airplaneObserver;

    @NonNull private final StateModifier wifiModifier;
    @NonNull private final StateModifier dataModifier;
    @NonNull private final StateModifier bluetoothModifier;
    @NonNull private final StateModifier syncModifier;
    @NonNull private final StateModifier dozeModifier;
    @NonNull private final StateModifier airplaneModifier;

    @NonNull private final WifiPreferences wifiPreferences;
    @NonNull private final DataPreferences dataPreferences;
    @NonNull private final BluetoothPreferences bluetoothPreferences;
    @NonNull private final SyncPreferences syncPreferences;
    @NonNull private final AirplanePreferences airplanePreferences;
    @NonNull private final DozePreferences dozePreferences;

    Runner(@NonNull JobQueuer jobQueuer, @NonNull StateObserver chargingObserver,
        @NonNull StateObserver wifiObserver, @NonNull StateObserver dataObserver,
        @NonNull StateObserver bluetoothObserver, @NonNull StateObserver syncObserver,
        @NonNull StateObserver dozeObserver, @NonNull StateObserver airplaneObserver,
        @NonNull StateModifier wifiModifier, @NonNull StateModifier dataModifier,
        @NonNull StateModifier bluetoothModifier, @NonNull StateModifier syncModifier,
        @NonNull StateModifier dozeModifier, @NonNull StateModifier airplaneModifier,
        @NonNull WifiPreferences wifiPreferences, @NonNull DataPreferences dataPreferences,
        @NonNull BluetoothPreferences bluetoothPreferences,
        @NonNull SyncPreferences syncPreferences, @NonNull AirplanePreferences airplanePreferences,
        @NonNull DozePreferences dozePreferences) {
      this.jobQueuer = jobQueuer;
      this.chargingObserver = chargingObserver;
      this.wifiObserver = wifiObserver;
      this.dataObserver = dataObserver;
      this.bluetoothObserver = bluetoothObserver;
      this.syncObserver = syncObserver;
      this.dozeObserver = dozeObserver;
      this.airplaneObserver = airplaneObserver;
      this.wifiModifier = wifiModifier;
      this.dataModifier = dataModifier;
      this.bluetoothModifier = bluetoothModifier;
      this.syncModifier = syncModifier;
      this.dozeModifier = dozeModifier;
      this.airplaneModifier = airplaneModifier;
      this.wifiPreferences = wifiPreferences;
      this.dataPreferences = dataPreferences;
      this.bluetoothPreferences = bluetoothPreferences;
      this.syncPreferences = syncPreferences;
      this.airplanePreferences = airplanePreferences;
      this.dozePreferences = dozePreferences;
    }

    @CheckResult private boolean runJob(@NonNull String tag, boolean screenOn) {
      if (screenOn) {
        return runEnableJob(tag);
      } else {
        return runDisableJob(tag);
      }
    }

    @CheckResult private boolean runEnableJob(@NonNull String tag) {
      // TODO
      return false;
    }

    @CheckResult private boolean runDisableJob(@NonNull String tag) {
      // TODO
      return true;
    }

    private void repeatIfRequired(@NonNull String tag, boolean screenOn, long windowOnTime,
        long windowOffTime) {
      final long newDelayTime;
      // Switch them
      if (screenOn) {
        newDelayTime = windowOnTime * 1000L;
      } else {
        newDelayTime = windowOffTime * 1000L;
      }

      final JobQueuerEntry entry = JobQueuerEntry.builder(tag)
          .screenOn(!screenOn)
          .delay(newDelayTime)
          .repeatingOffWindow(windowOffTime)
          .repeatingOnWindow(windowOnTime)
          .build();

      jobQueuer.cancel(tag);
      jobQueuer.queue(entry);
    }

    /**
     * Runs the Job. Called either by managed jobs or directly by the JobQueuer
     */
    void run(@NonNull String tag, @NonNull PersistableBundleCompat extras) {
      boolean screenOn = extras.getBoolean(JobQueuerImpl.KEY_SCREEN, true);
      long windowOnTime = extras.getLong(JobQueuerImpl.KEY_ON_WINDOW, 0);
      long windowOffTime = extras.getLong(JobQueuerImpl.KEY_OFF_WINDOW, 0);
      if (runJob(tag, screenOn)) {
        repeatIfRequired(tag, screenOn, windowOnTime, windowOffTime);
      }
    }

    /**
     * Override in the actual ManagedJobs to call Job.isCancelled();
     *
     * If it is not a managed job it never isStopped, always run to completion
     */
    @CheckResult abstract boolean isStopped();
  }
}
