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
import timber.log.Timber;

abstract class JobRunner {

  @NonNull private final JobQueuer jobQueuer;
  @NonNull private final StateObserver chargingObserver;

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

  JobRunner(@NonNull JobQueuer jobQueuer, @NonNull StateObserver chargingObserver,
      @NonNull StateModifier wifiModifier, @NonNull StateModifier dataModifier,
      @NonNull StateModifier bluetoothModifier, @NonNull StateModifier syncModifier,
      @NonNull StateModifier dozeModifier, @NonNull StateModifier airplaneModifier,
      @NonNull WifiPreferences wifiPreferences, @NonNull DataPreferences dataPreferences,
      @NonNull BluetoothPreferences bluetoothPreferences, @NonNull SyncPreferences syncPreferences,
      @NonNull AirplanePreferences airplanePreferences, @NonNull DozePreferences dozePreferences) {
    this.jobQueuer = jobQueuer;
    this.chargingObserver = chargingObserver;
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

  @CheckResult private boolean runJob(@NonNull String tag, boolean screenOn, boolean firstRun) {
    if (screenOn) {
      return runEnableJob(tag, firstRun);
    } else {
      return runDisableJob(tag, firstRun);
    }
  }

  @CheckResult private boolean runEnableJob(@NonNull String tag, boolean firstRun) {
    if (dozePreferences.isOriginalDoze() && (firstRun || dozePreferences.isPeriodicDoze())) {
      Timber.i("%s: Disable Doze", tag);
      dozeModifier.unset();
    }
    if (isStopped()) {
      Timber.w("%s: Stopped early", tag);
      return false;
    }

    if (airplanePreferences.isOriginalAirplane() && (firstRun
        || airplanePreferences.isPeriodicAirplane())) {
      Timber.i("%s: Disable Airplane mode", tag);
      airplaneModifier.unset();
    }
    if (isStopped()) {
      Timber.w("%s: Stopped early", tag);
      return false;
    }

    if (wifiPreferences.isOriginalWifi() && (firstRun || wifiPreferences.isPeriodicWifi())) {
      Timber.i("%s: Enable WiFi", tag);
      wifiModifier.set();
    }
    if (isStopped()) {
      Timber.w("%s: Stopped early", tag);
      return false;
    }

    if (dataPreferences.isOriginalData() && (firstRun || dataPreferences.isPeriodicData())) {
      Timber.i("%s: Enable Data", tag);
      dataModifier.set();
    }
    if (isStopped()) {
      Timber.w("%s: Stopped early", tag);
      return false;
    }

    if (bluetoothPreferences.isOriginalBluetooth() && (firstRun
        || bluetoothPreferences.isPeriodicBluetooth())) {
      Timber.i("%s: Enable Bluetooth", tag);
      bluetoothModifier.set();
    }
    if (isStopped()) {
      Timber.w("%s: Stopped early", tag);
      return false;
    }

    if (syncPreferences.isOriginalSync() && (firstRun || syncPreferences.isPeriodicSync())) {
      Timber.i("%s: Enable Sync", tag);
      syncModifier.set();
    }
    return true;
  }

  @CheckResult private boolean runDisableJob(@NonNull String tag, boolean firstRun) {
    final boolean isCharging = chargingObserver.enabled();
    if (isCharging && wifiPreferences.isIgnoreChargingWifi()) {
      Timber.w("Do not disable WiFi while device is charging");
    } else {
      if (wifiPreferences.isOriginalWifi() && (firstRun || wifiPreferences.isPeriodicWifi())) {
        Timber.i("%s: Disable WiFi", tag);
        wifiModifier.unset();
      }
    }
    if (isStopped()) {
      Timber.w("%s: Stopped early", tag);
      return false;
    }

    if (isCharging && dataPreferences.isIgnoreChargingData()) {
      Timber.w("Do not disable Data while device is charging");
    } else {
      if (dataPreferences.isOriginalData() && (firstRun || dataPreferences.isPeriodicData())) {
        Timber.i("%s: Disable Data", tag);
        dataModifier.unset();
      }
    }
    if (isStopped()) {
      Timber.w("%s: Stopped early", tag);
      return false;
    }

    if (isCharging && bluetoothPreferences.isIgnoreChargingBluetooth()) {
      Timber.w("Do not disable Bluetooth while device is charging");
    } else {
      if (bluetoothPreferences.isOriginalBluetooth() && (firstRun
          || bluetoothPreferences.isPeriodicBluetooth())) {
        Timber.i("%s: Disable Bluetooth", tag);
        bluetoothModifier.unset();
      }
    }
    if (isStopped()) {
      Timber.w("%s: Stopped early", tag);
      return false;
    }

    if (isCharging && syncPreferences.isIgnoreChargingSync()) {
      Timber.w("Do not disable Sync while device is charging");
    } else {
      if (syncPreferences.isOriginalSync() && (firstRun || syncPreferences.isPeriodicSync())) {
        Timber.i("%s: Disable Sync", tag);
        syncModifier.unset();
      }
    }
    if (isStopped()) {
      Timber.w("%s: Stopped early", tag);
      return false;
    }

    if (isCharging && airplanePreferences.isIgnoreChargingAirplane()) {
      Timber.w("Do not enable Airplane mode while device is charging");
    } else {
      if (airplanePreferences.isOriginalAirplane() && (firstRun
          || airplanePreferences.isPeriodicAirplane())) {
        Timber.i("%s: Enable Airplane mode", tag);
        airplaneModifier.set();
      }
    }
    if (isStopped()) {
      Timber.w("%s: Stopped early", tag);
      return false;
    }

    if (isCharging && dozePreferences.isIgnoreChargingDoze()) {
      Timber.w("Do not enable Doze mode while device is charging");
    } else {
      if (dozePreferences.isOriginalDoze() && (firstRun || dozePreferences.isPeriodicDoze())) {
        Timber.i("%s: Enable Doze mode", tag);
        dozeModifier.set();
      }
    }

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
        .oneshot(false)
        .firstRun(false)
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
    boolean screenOn = extras.getBoolean(BaseJobQueuer.KEY_SCREEN, true);
    long windowOnTime = extras.getLong(BaseJobQueuer.KEY_ON_WINDOW, 0);
    long windowOffTime = extras.getLong(BaseJobQueuer.KEY_OFF_WINDOW, 0);
    boolean oneshot = extras.getBoolean(BaseJobQueuer.KEY_ONESHOT, false);
    boolean firstRun = extras.getBoolean(BaseJobQueuer.KEY_FIRST_RUN, false);
    if (runJob(tag, screenOn, firstRun) && !oneshot) {
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
