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
import com.pyamsoft.powermanager.base.preference.SyncPreferences;
import com.pyamsoft.powermanager.job.JobQueuer;
import com.pyamsoft.powermanager.model.StateObserver;
import javax.inject.Inject;

class ManagerSyncInteractor extends WearUnawareManagerInteractor {

  @NonNull private final SyncPreferences preferences;

  @Inject ManagerSyncInteractor(@NonNull SyncPreferences preferences,
      @NonNull StateObserver stateObserver, @NonNull JobQueuer jobQueuer) {
    super(jobQueuer, stateObserver);
    this.preferences = preferences;
  }

  @Override @CheckResult protected long getDelayTime() {
    return preferences.getMasterSyncDelay();
  }

  @Override @CheckResult protected boolean isPeriodic() {
    return preferences.isPeriodicSync();
  }

  @Override @CheckResult protected long getPeriodicEnableTime() {
    return preferences.getPeriodicEnableTimeSync();
  }

  @Override @CheckResult protected long getPeriodicDisableTime() {
    return preferences.getPeriodicDisableTimeSync();
  }

  @NonNull @Override public String getJobTag() {
    return JobQueuer.SYNC_JOB_TAG;
  }

  @Override public boolean isIgnoreWhileCharging() {
    return preferences.isIgnoreChargingSync();
  }

  @Override boolean isManaged() {
    return preferences.isSyncManaged();
  }

  @Override boolean isOriginalStateEnabled() {
    return preferences.isOriginalSync();
  }

  @Override public void setOriginalStateEnabled(boolean enabled) {
    preferences.setOriginalSync(enabled);
  }
}
