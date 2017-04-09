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
import com.pyamsoft.powermanager.model.StateObserver;
import io.reactivex.Observable;
import javax.inject.Inject;
import timber.log.Timber;

class ManagerDozeInteractorImpl extends WearUnawareManagerInteractor {

  @NonNull private final PowerManagerPreferences preferences;

  @Inject ManagerDozeInteractorImpl(@NonNull PowerManagerPreferences preferences,
      @NonNull StateObserver stateObserver, @NonNull JobQueuer jobQueuer) {
    super(jobQueuer, stateObserver);
    this.preferences = preferences;
  }

  @Override @CheckResult protected long getDelayTime() {
    return preferences.getDozeDelay();
  }

  @Override @CheckResult protected boolean isPeriodic() {
    return preferences.isPeriodicDoze();
  }

  @Override @CheckResult protected long getPeriodicEnableTime() {
    return preferences.getPeriodicEnableTimeDoze();
  }

  @Override @CheckResult protected long getPeriodicDisableTime() {
    return preferences.getPeriodicDisableTimeDoze();
  }

  @NonNull @Override public String getJobTag() {
    return JobQueuer.DOZE_JOB_TAG;
  }

  @NonNull @Override public Observable<Boolean> isEnabled() {
    Timber.d("Invert getState for Doze");
    return super.isEnabled().map(aBoolean -> !aBoolean);
  }

  @Override public boolean isIgnoreWhileCharging() {
    return preferences.isIgnoreChargingDoze();
  }

  @Override boolean isManaged() {
    return preferences.isDozeManaged();
  }

  @Override boolean isOriginalStateEnabled() {
    return preferences.isOriginalDoze();
  }

  @Override public void setOriginalStateEnabled(boolean enabled) {
    preferences.setOriginalDoze(enabled);
  }
}
