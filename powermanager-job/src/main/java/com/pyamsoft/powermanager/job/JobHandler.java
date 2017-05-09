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
import com.pyamsoft.powermanager.base.preference.AirplanePreferences;
import com.pyamsoft.powermanager.base.preference.BluetoothPreferences;
import com.pyamsoft.powermanager.base.preference.DataPreferences;
import com.pyamsoft.powermanager.base.preference.DozePreferences;
import com.pyamsoft.powermanager.base.preference.RootPreferences;
import com.pyamsoft.powermanager.base.preference.SyncPreferences;
import com.pyamsoft.powermanager.base.preference.WifiPreferences;
import com.pyamsoft.powermanager.model.PermissionObserver;
import com.pyamsoft.powermanager.model.StateModifier;
import com.pyamsoft.powermanager.model.StateObserver;
import com.pyamsoft.pydroid.function.FuncNone;
import javax.inject.Inject;
import javax.inject.Named;

public class JobHandler {

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
  @NonNull private final RootPreferences rootPreferences;

  @NonNull private final PermissionObserver rootPermissionObserver;
  @NonNull private final PermissionObserver dozePermissionObserver;

  @Inject JobHandler(@NonNull @Named("delay") JobQueuer jobQueuer,
      @NonNull @Named("obs_charging") StateObserver chargingObserver,
      @NonNull @Named("mod_wifi") StateModifier wifiModifier,
      @NonNull @Named("mod_data") StateModifier dataModifier,
      @NonNull @Named("mod_bluetooth") StateModifier bluetoothModifier,
      @NonNull @Named("mod_sync") StateModifier syncModifier,
      @NonNull @Named("mod_doze") StateModifier dozeModifier,
      @NonNull @Named("mod_airplane") StateModifier airplaneModifier,
      @NonNull WifiPreferences wifiPreferences, @NonNull DataPreferences dataPreferences,
      @NonNull BluetoothPreferences bluetoothPreferences, @NonNull SyncPreferences syncPreferences,
      @NonNull AirplanePreferences airplanePreferences, @NonNull DozePreferences dozePreferences,
      @NonNull RootPreferences rootPreferences,
      @NonNull @Named("obs_root_permission") PermissionObserver rootPermissionObserver,
      @NonNull @Named("obs_doze_permission") PermissionObserver dozePermissionObserver) {
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
    this.rootPreferences = rootPreferences;
    this.rootPermissionObserver = rootPermissionObserver;
    this.dozePermissionObserver = dozePermissionObserver;
  }

  @CheckResult @NonNull JobRunner newRunner(@NonNull FuncNone<Boolean> stopper) {
    return new JobRunner(jobQueuer, chargingObserver, wifiModifier, dataModifier, bluetoothModifier,
        syncModifier, dozeModifier, airplaneModifier, wifiPreferences, dataPreferences,
        bluetoothPreferences, syncPreferences, airplanePreferences, dozePreferences,
        rootPreferences, rootPermissionObserver, dozePermissionObserver) {
      @Override boolean isStopped() {
        return stopper.call();
      }
    };
  }
}
