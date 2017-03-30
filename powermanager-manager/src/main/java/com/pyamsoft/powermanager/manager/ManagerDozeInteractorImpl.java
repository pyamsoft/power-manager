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
import com.pyamsoft.powermanager.model.overlord.StateObserver;
import io.reactivex.Observable;
import javax.inject.Inject;
import timber.log.Timber;

class ManagerDozeInteractorImpl extends WearUnawareManagerInteractor {

  @Inject ManagerDozeInteractorImpl(@NonNull PowerManagerPreferences preferences,
      @NonNull StateObserver stateObserver, @NonNull JobQueuer jobQueuer) {
    super(jobQueuer, preferences, stateObserver);
  }

  @Override @CheckResult protected long getDelayTime() {
    return getPreferences().getDozeDelay();
  }

  @Override @CheckResult protected boolean isPeriodic() {
    return getPreferences().isPeriodicDoze();
  }

  @Override @CheckResult protected long getPeriodicEnableTime() {
    return getPreferences().getPeriodicEnableTimeDoze();
  }

  @Override @CheckResult protected long getPeriodicDisableTime() {
    return getPreferences().getPeriodicDisableTimeDoze();
  }

  @NonNull @Override public String getJobTag() {
    return JobQueuer.DOZE_JOB_TAG;
  }

  @NonNull @Override public Observable<Boolean> isEnabled() {
    Timber.d("Invert getState for Doze");
    return super.isEnabled().map(aBoolean -> !aBoolean);
  }

  @Override public boolean isIgnoreWhileCharging() {
    return getPreferences().isIgnoreChargingDoze();
  }

  @Override boolean isManaged() {
    return getPreferences().isDozeManaged();
  }

  @Override boolean isOriginalStateEnabled() {
    return getPreferences().isOriginalDoze();
  }

  @Override public void setOriginalStateEnabled(boolean enabled) {
    getPreferences().setOriginalDoze(enabled);
  }
}
