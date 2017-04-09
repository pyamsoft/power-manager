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

package com.pyamsoft.powermanager.manager;

import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import com.pyamsoft.powermanager.base.preference.BluetoothPreferences;
import com.pyamsoft.powermanager.base.preference.WearablePreferences;
import com.pyamsoft.powermanager.job.JobQueuer;
import com.pyamsoft.powermanager.model.StateObserver;
import javax.inject.Inject;

class ManagerBluetoothInteractor extends WearAwareManagerInteractor {

  @NonNull private final BluetoothPreferences preferences;

  @Inject ManagerBluetoothInteractor(@NonNull WearablePreferences wearablePreferences,
      @NonNull BluetoothPreferences preferences, @NonNull StateObserver stateObserver,
      @NonNull JobQueuer jobQueuer, @NonNull StateObserver wearStateObserver) {
    super(wearablePreferences, stateObserver, jobQueuer, wearStateObserver);
    this.preferences = preferences;
  }

  @Override boolean isWearManaged() {
    return preferences.isWearableManaged();
  }

  @Override @CheckResult protected long getDelayTime() {
    return preferences.getBluetoothDelay();
  }

  @Override @CheckResult protected boolean isPeriodic() {
    return preferences.isPeriodicBluetooth();
  }

  @Override @CheckResult protected long getPeriodicEnableTime() {
    return preferences.getPeriodicEnableTimeBluetooth();
  }

  @Override @CheckResult protected long getPeriodicDisableTime() {
    return preferences.getPeriodicDisableTimeBluetooth();
  }

  @NonNull @Override public String getJobTag() {
    return JobQueuer.BLUETOOTH_JOB_TAG;
  }

  @Override public boolean isIgnoreWhileCharging() {
    return preferences.isIgnoreChargingBluetooth();
  }

  @Override boolean isManaged() {
    return preferences.isBluetoothManaged();
  }

  @Override boolean isOriginalStateEnabled() {
    return preferences.isOriginalBluetooth();
  }

  @Override public void setOriginalStateEnabled(boolean enabled) {
    preferences.setOriginalBluetooth(enabled);
  }
}
