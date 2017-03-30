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
import com.pyamsoft.powermanager.base.PowerManagerPreferences;
import com.pyamsoft.powermanager.job.JobQueuer;
import com.pyamsoft.powermanager.model.overlord.StateChangeObserver;
import io.reactivex.Observable;
import javax.inject.Inject;

class ManagerBluetoothInteractor extends WearAwareManagerInteractor {

  @Inject ManagerBluetoothInteractor(@NonNull PowerManagerPreferences preferences,
      @NonNull StateChangeObserver manageObserver,
      @NonNull StateChangeObserver stateObserver, @NonNull JobQueuer jobQueuer,
      @NonNull StateChangeObserver wearManageObserver,
      @NonNull StateChangeObserver wearStateObserver) {
    super(preferences, manageObserver, stateObserver, jobQueuer, wearManageObserver,
        wearStateObserver);
  }

  @Override @CheckResult protected long getDelayTime() {
    return getPreferences().getBluetoothDelay();
  }

  @Override @CheckResult protected boolean isPeriodic() {
    return getPreferences().isPeriodicBluetooth();
  }

  @Override @CheckResult protected long getPeriodicEnableTime() {
    return getPreferences().getPeriodicEnableTimeBluetooth();
  }

  @Override @CheckResult protected long getPeriodicDisableTime() {
    return getPreferences().getPeriodicDisableTimeBluetooth();
  }

  @NonNull @Override public String getJobTag() {
    return JobQueuer.BLUETOOTH_JOB_TAG;
  }

  @Override public boolean isIgnoreWhileCharging() {
    return getPreferences().isIgnoreChargingBluetooth();
  }

  @NonNull @Override public Observable<Boolean> isOriginalStateEnabled() {
    return Observable.defer(() -> Observable.just(getPreferences().isOriginalBluetooh()));
  }

  @Override public void setOriginalStateEnabled(boolean enabled) {
    getPreferences().setOriginalBluetooth(enabled);
  }
}
