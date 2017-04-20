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
import com.pyamsoft.powermanager.base.preference.DataPreferences;
import com.pyamsoft.powermanager.job.JobQueuer;
import com.pyamsoft.powermanager.model.StateObserver;
import javax.inject.Inject;

class ManagerDataInteractorImpl extends WearUnawareManagerInteractor {

  @NonNull private final DataPreferences preferences;

  @Inject ManagerDataInteractorImpl(@NonNull DataPreferences preferences,
      @NonNull StateObserver stateObserver, @NonNull JobQueuer jobQueuer) {
    super(jobQueuer, stateObserver);
    this.preferences = preferences;
  }

  @Override @CheckResult protected long getDelayTime() {
    return preferences.getDataDelay();
  }

  @Override @CheckResult protected boolean isPeriodic() {
    return preferences.isPeriodicData();
  }

  @Override @CheckResult protected long getPeriodicEnableTime() {
    return preferences.getPeriodicEnableTimeData();
  }

  @Override @CheckResult protected long getPeriodicDisableTime() {
    return preferences.getPeriodicDisableTimeData();
  }

  @NonNull @Override public String getJobTag() {
    return JobQueuer.DATA_JOB_TAG;
  }

  @Override public boolean isIgnoreWhileCharging() {
    return preferences.isIgnoreChargingData();
  }

  @Override boolean isManaged() {
    return preferences.isDataManaged();
  }

  @Override boolean isOriginalStateEnabled() {
    return preferences.isOriginalData();
  }

  @Override public void setOriginalStateEnabled(boolean enabled) {
    preferences.setOriginalData(enabled);
  }
}
