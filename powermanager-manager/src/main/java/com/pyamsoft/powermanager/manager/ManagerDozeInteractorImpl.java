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
import com.pyamsoft.powermanager.model.BooleanInterestObserver;
import com.pyamsoft.powermanager.manager.queuer.Queuer;
import com.pyamsoft.pydroid.FuncNone;
import javax.inject.Inject;
import rx.Observable;
import timber.log.Timber;

class ManagerDozeInteractorImpl extends ManagerInteractorImpl
    implements ExclusiveWearUnawareManagerInteractor {

  @Inject ManagerDozeInteractorImpl(@NonNull Queuer queuer,
      @NonNull PowerManagerPreferences preferences, @NonNull BooleanInterestObserver manageObserver,
      @NonNull BooleanInterestObserver stateObserver) {
    super(queuer, preferences, manageObserver, stateObserver);
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

  @Override public void setOriginalStateEnabled(boolean enabled) {
    getPreferences().setOriginalDoze(enabled);
  }

  @NonNull @Override public FuncNone<Boolean> isIgnoreWhileCharging() {
    return () -> getPreferences().isIgnoreChargingDoze();
  }

  @NonNull @Override public Observable<Boolean> isOriginalStateEnabled() {
    return Observable.defer(() -> Observable.just(getPreferences().isOriginalDoze()));
  }

  @NonNull @Override public Observable<Boolean> isExclusive() {
    return Observable.defer(() -> {
      final boolean preference =
          getPreferences().isExclusiveDoze() && getPreferences().isDozeManaged();
      return Observable.just(preference);
    });
  }
}