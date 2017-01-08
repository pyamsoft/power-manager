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

import android.support.annotation.NonNull;
import com.pyamsoft.powermanager.base.PowerManagerPreferences;
import com.pyamsoft.powermanager.model.BooleanInterestObserver;
import com.pyamsoft.pydroid.FuncNone;
import javax.inject.Inject;
import rx.Observable;
import timber.log.Timber;

class ManagerAirplaneInteractorImpl extends WearAwareManagerInteractorImpl {

  @Inject ManagerAirplaneInteractorImpl(@NonNull PowerManagerPreferences preferences,
      @NonNull BooleanInterestObserver manageObserver,
      @NonNull BooleanInterestObserver stateObserver,
      @NonNull BooleanInterestObserver wearManageObserver,
      @NonNull BooleanInterestObserver wearStateObserver) {
    super(preferences, manageObserver, stateObserver, wearManageObserver, wearStateObserver);
  }

  @Override protected long getDelayTime() {
    return getPreferences().getAirplaneDelay();
  }

  @Override protected boolean isPeriodic() {
    return getPreferences().isPeriodicAirplane();
  }

  @Override protected long getPeriodicEnableTime() {
    return getPreferences().getPeriodicEnableTimeAirplane();
  }

  @Override protected long getPeriodicDisableTime() {
    return getPreferences().getPeriodicDisableTimeAirplane();
  }

  @NonNull @Override public String getJobTag() {
    return AIRPLANE_JOB_TAG;
  }

  @NonNull @Override public Observable<Boolean> isEnabled() {
    Timber.d("Invert isEnabled for Airplane");
    return super.isEnabled().map(aBoolean -> !aBoolean);
  }

  @Override public void setOriginalStateEnabled(boolean enabled) {
    getPreferences().setOriginalAirplane(enabled);
  }

  @NonNull @Override public FuncNone<Boolean> isIgnoreWhileCharging() {
    return () -> getPreferences().isIgnoreChargingAirplane();
  }

  @NonNull @Override public Observable<Boolean> isOriginalStateEnabled() {
    return Observable.defer(() -> Observable.just(getPreferences().isOriginalAirplane()));
  }
}
