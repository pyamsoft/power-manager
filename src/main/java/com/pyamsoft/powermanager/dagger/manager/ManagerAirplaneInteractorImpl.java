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

package com.pyamsoft.powermanager.dagger.manager;

import android.support.annotation.NonNull;
import com.pyamsoft.powermanager.PowerManagerPreferences;
import com.pyamsoft.powermanager.app.logger.Logger;
import com.pyamsoft.powermanager.app.observer.BooleanInterestObserver;
import com.pyamsoft.powermanager.app.observer.PermissionObserver;
import com.pyamsoft.powermanager.dagger.queuer.Queuer;
import com.pyamsoft.pydroid.FuncNone;
import javax.inject.Inject;
import rx.Observable;
import timber.log.Timber;

class ManagerAirplaneInteractorImpl extends WearAwareManagerInteractorImpl {

  @NonNull private final PermissionObserver rootPermissionObserver;

  @Inject ManagerAirplaneInteractorImpl(@NonNull Queuer queuer,
      @NonNull PowerManagerPreferences preferences, @NonNull BooleanInterestObserver manageObserver,
      @NonNull BooleanInterestObserver stateObserver,
      @NonNull BooleanInterestObserver wearManageObserver,
      @NonNull BooleanInterestObserver wearStateObserver,
      @NonNull PermissionObserver rootPermissionObserver) {
    super(queuer, preferences, manageObserver, stateObserver, wearManageObserver, wearStateObserver);
    this.rootPermissionObserver = rootPermissionObserver;
  }

  @NonNull @Override public Observable<Boolean> isManaged() {
    return super.isManaged()
        .zipWith(rootPermissionObserver.hasPermission(),
            (managed, hasPermission) -> managed && hasPermission);
  }

  @Override protected long getDelayTime() {
    return preferences.getAirplaneDelay();
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
    return AIRPLANE_JOB_TAG;
  }

  @NonNull @Override public Observable<Boolean> isEnabled() {
    Timber.d("Invert isEnabled for Airplane");
    return super.isEnabled().map(aBoolean -> !aBoolean);
  }

  @NonNull @Override public FuncNone<Boolean> isIgnoreWhileCharging() {
    return () -> getPreferences().isIgnoreChargingAirplane();
  }
}
