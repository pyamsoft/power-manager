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

import android.support.annotation.NonNull;
import com.pyamsoft.powermanager.base.preference.AirplanePreferences;
import com.pyamsoft.powermanager.base.preference.WearablePreferences;
import com.pyamsoft.powermanager.job.JobQueuer;
import com.pyamsoft.powermanager.model.StateObserver;
import io.reactivex.Single;
import javax.inject.Inject;
import timber.log.Timber;

class ManagerAirplaneInteractor extends WearAwareManagerInteractor {

  @NonNull private final AirplanePreferences preferences;

  @Inject ManagerAirplaneInteractor(@NonNull WearablePreferences wearablePreferences,
      @NonNull AirplanePreferences preferences, @NonNull StateObserver stateObserver,
      @NonNull JobQueuer jobQueuer, @NonNull StateObserver wearStateObserver) {
    super(wearablePreferences, stateObserver, jobQueuer, wearStateObserver);
    this.preferences = preferences;
  }

  @Override boolean isWearManaged() {
    return preferences.isWearableManaged();
  }

  @Override protected long getDelayTime() {
    return preferences.getAirplaneDelay();
  }

  @Override boolean isManaged() {
    return preferences.isAirplaneManaged();
  }

  @Override protected boolean isPeriodic() {
    return preferences.isPeriodicAirplane();
  }

  @Override protected long getPeriodicEnableTime() {
    return preferences.getPeriodicEnableTimeAirplane();
  }

  @Override protected long getPeriodicDisableTime() {
    return preferences.getPeriodicDisableTimeAirplane();
  }

  @NonNull @Override public String getJobTag() {
    return JobQueuer.AIRPLANE_JOB_TAG;
  }

  @NonNull @Override public Single<Boolean> isEnabled() {
    Timber.d("Invert getState for Airplane");
    return super.isEnabled().map(aBoolean -> !aBoolean);
  }

  @Override public boolean isIgnoreWhileCharging() {
    return preferences.isIgnoreChargingAirplane();
  }

  @Override public boolean isOriginalStateEnabled() {
    return preferences.isOriginalAirplane();
  }

  @Override public void setOriginalStateEnabled(boolean enabled) {
    preferences.setOriginalAirplane(enabled);
  }
}
