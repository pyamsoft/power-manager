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

package com.pyamsoft.powermanager.manager;

import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import com.pyamsoft.powermanager.base.preference.AirplanePreferences;
import com.pyamsoft.powermanager.base.preference.BluetoothPreferences;
import com.pyamsoft.powermanager.base.preference.DataPreferences;
import com.pyamsoft.powermanager.base.preference.DozePreferences;
import com.pyamsoft.powermanager.base.preference.ManagePreferences;
import com.pyamsoft.powermanager.base.preference.SyncPreferences;
import com.pyamsoft.powermanager.base.preference.WifiPreferences;
import com.pyamsoft.powermanager.job.JobQueuer;
import com.pyamsoft.powermanager.job.JobQueuerEntry;
import com.pyamsoft.powermanager.model.ConnectedStateObserver;
import com.pyamsoft.powermanager.model.StateObserver;
import io.reactivex.Completable;
import io.reactivex.Single;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import timber.log.Timber;

import static com.pyamsoft.powermanager.job.JobQueuer.MANAGED_TAG;

@Singleton class ManagerInteractor {

  @SuppressWarnings("WeakerAccess") @NonNull final JobQueuer jobQueuer;
  @NonNull private final StateObserver wifiObserver;
  @NonNull private final StateObserver dataObserver;
  @NonNull private final StateObserver bluetoothObserver;
  @NonNull private final StateObserver syncObserver;
  @NonNull private final StateObserver dozeObserver;
  @NonNull private final StateObserver airplaneObserver;

  @NonNull private final ManagePreferences preferences;
  @NonNull private final WifiPreferences wifiPreferences;
  @NonNull private final DataPreferences dataPreferences;
  @NonNull private final BluetoothPreferences bluetoothPreferences;
  @NonNull private final SyncPreferences syncPreferences;
  @NonNull private final AirplanePreferences airplanePreferences;
  @NonNull private final DozePreferences dozePreferences;

  @Inject ManagerInteractor(@NonNull @Named("instant") JobQueuer jobQueuer,
      @NonNull ManagePreferences preferences,
      @NonNull @Named("obs_wifi") ConnectedStateObserver wifiObserver,
      @NonNull @Named("obs_data") StateObserver dataObserver,
      @NonNull @Named("obs_bluetooth") ConnectedStateObserver bluetoothObserver,
      @NonNull @Named("obs_sync") StateObserver syncObserver,
      @NonNull @Named("obs_doze") StateObserver dozeObserver,
      @NonNull @Named("obs_airplane") StateObserver airplaneObserver,
      @NonNull WifiPreferences wifiPreferences, @NonNull DataPreferences dataPreferences,
      @NonNull BluetoothPreferences bluetoothPreferences, @NonNull SyncPreferences syncPreferences,
      @NonNull AirplanePreferences airplanePreferences, @NonNull DozePreferences dozePreferences) {
    this.jobQueuer = jobQueuer;
    this.preferences = preferences;
    this.wifiObserver = wifiObserver;
    this.dataObserver = dataObserver;
    this.bluetoothObserver = bluetoothObserver;
    this.syncObserver = syncObserver;
    this.airplaneObserver = airplaneObserver;
    this.dozeObserver = dozeObserver;
    this.wifiPreferences = wifiPreferences;
    this.dataPreferences = dataPreferences;
    this.bluetoothPreferences = bluetoothPreferences;
    this.syncPreferences = syncPreferences;
    this.airplanePreferences = airplanePreferences;
    this.dozePreferences = dozePreferences;
  }

  public void destroy() {
    jobQueuer.cancel(MANAGED_TAG);
  }

  /**
   * public
   */
  @NonNull @CheckResult Single<String> cancel() {
    return Single.fromCallable(() -> {
      destroy();
      return MANAGED_TAG;
    });
  }

  /**
   * public
   */
  @CheckResult @NonNull Single<String> queueEnable() {
    return Single.fromCallable(() -> {
      // Queue up an enable job
      jobQueuer.cancel(MANAGED_TAG);
      jobQueuer.queue(JobQueuerEntry.builder(MANAGED_TAG)
          .screenOn(true)
          .delay(0)
          .oneshot(true)
          .firstRun(true)
          .repeatingOffWindow(0L)
          .repeatingOnWindow(0L)
          .build());
      return MANAGED_TAG;
    }).doAfterSuccess(s -> eraseOriginalStates());
  }

  /**
   * public
   */
  @CheckResult @NonNull Single<String> queueDisable() {
    return Completable.fromAction(this::storeOriginalStates).andThen(Single.fromCallable(() -> {
      // Queue up a disable job
      jobQueuer.cancel(MANAGED_TAG);
      jobQueuer.queue(JobQueuerEntry.builder(MANAGED_TAG)
          .screenOn(false)
          .delay(getDelayTime())
          .oneshot(false)
          .firstRun(true)
          .repeatingOffWindow(getPeriodicDisableTime())
          .repeatingOnWindow(getPeriodicEnableTime())
          .build());
      return MANAGED_TAG;
    }));
  }

  @SuppressWarnings("WeakerAccess") void eraseOriginalStates() {
    wifiPreferences.setOriginalWifi(false);
    dataPreferences.setOriginalData(false);
    bluetoothPreferences.setOriginalBluetooth(false);
    syncPreferences.setOriginalSync(false);
    airplanePreferences.setOriginalAirplane(false);
    dozePreferences.setOriginalDoze(false);
    Timber.w("Erased original states, prepare for another Screen event");
  }

  @SuppressWarnings("WeakerAccess") void storeOriginalStates() {
    if (!wifiObserver.unknown()) {
      wifiPreferences.setOriginalWifi(wifiObserver.enabled());
    }

    if (!dataObserver.unknown()) {
      dataPreferences.setOriginalData(dataObserver.enabled());
    }

    if (!bluetoothObserver.unknown()) {
      bluetoothPreferences.setOriginalBluetooth(bluetoothObserver.enabled());
    }

    if (!syncObserver.unknown()) {
      syncPreferences.setOriginalSync(syncObserver.enabled());
    }

    if (!airplaneObserver.unknown()) {
      airplanePreferences.setOriginalAirplane(!airplaneObserver.enabled());
    }

    if (!dozeObserver.unknown()) {
      dozePreferences.setOriginalDoze(!dozeObserver.enabled());
    }

    Timber.w("Stored original states, prepare for Sleep");
  }

  @SuppressWarnings("WeakerAccess") @CheckResult long getDelayTime() {
    return preferences.getManageDelay();
  }

  @SuppressWarnings("WeakerAccess") @CheckResult long getPeriodicEnableTime() {
    return preferences.getPeriodicEnableTime();
  }

  @SuppressWarnings("WeakerAccess") @CheckResult long getPeriodicDisableTime() {
    return preferences.getPeriodicDisableTime();
  }
}
