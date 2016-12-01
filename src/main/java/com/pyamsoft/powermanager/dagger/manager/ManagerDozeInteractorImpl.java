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

import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import com.pyamsoft.powermanager.PowerManagerPreferences;
import com.pyamsoft.powermanager.app.observer.BooleanInterestObserver;
import com.pyamsoft.powermanager.app.observer.PermissionObserver;
import com.pyamsoft.powermanager.dagger.queuer.Queuer;
import com.pyamsoft.pydroid.FuncNone;
import javax.inject.Inject;
import rx.Observable;
import timber.log.Timber;

class ManagerDozeInteractorImpl extends ManagerInteractorImpl
    implements ExclusiveWearUnawareManagerInteractor {

  @NonNull private final PermissionObserver dozePermissionObserver;

  @Inject ManagerDozeInteractorImpl(@NonNull Queuer queuer,
      @NonNull PowerManagerPreferences preferences, @NonNull BooleanInterestObserver manageObserver,
      @NonNull BooleanInterestObserver stateObserver,
      @NonNull PermissionObserver dozePermissionObserver) {
    super(queuer, preferences, manageObserver, stateObserver);
    this.dozePermissionObserver = dozePermissionObserver;
  }

  @NonNull @Override public Observable<Boolean> isManaged() {
    return super.isManaged()
        .zipWith(dozePermissionObserver.hasPermission(),
            (managed, hasPermission) -> managed && hasPermission);
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
    return DOZE_JOB_TAG;
  }

  @NonNull @Override public Observable<Boolean> isEnabled() {
    Timber.d("Invert isEnabled for Doze");
    return super.isEnabled().map(aBoolean -> !aBoolean);
  }

  @NonNull @Override public FuncNone<Boolean> isIgnoreWhileCharging() {
    return () -> getPreferences().isIgnoreChargingDoze();
  }

  @NonNull @Override public Observable<Boolean> isExclusive() {
    return Observable.defer(() -> {
      final boolean preference = getPreferences().isExclusiveDoze() && preferences.isDozeManaged();
      return Observable.just(preference);
    });
  }
}
