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

import android.os.Build;
import android.support.annotation.CheckResult;
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

class ManagerDataInteractorImpl extends ManagerInteractorImpl {

  @NonNull private final PermissionObserver rootPermissionObserver;

  @Inject ManagerDataInteractorImpl(@NonNull Queuer queuer,
      @NonNull PowerManagerPreferences preferences, @NonNull BooleanInterestObserver manageObserver,
      @NonNull BooleanInterestObserver stateObserver,
      @NonNull PermissionObserver rootPermissionObserver, @NonNull Logger logger) {
    super(queuer, preferences, manageObserver, stateObserver, logger);
    this.rootPermissionObserver = rootPermissionObserver;
  }

  @NonNull @Override public Observable<Boolean> isManaged() {
    final Observable<Boolean> superManaged = super.isManaged();
    if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
      Timber.d("isManaged: check for root on API > 19");
      return superManaged.zipWith(rootPermissionObserver.hasPermission(),
          (managed, hasPermission) -> managed && hasPermission);
    } else {
      return superManaged;
    }
  }

  @Override @CheckResult protected long getDelayTime() {
    return getPreferences().getDataDelay();
  }

  @Override @CheckResult protected boolean isPeriodic() {
    return getPreferences().isPeriodicData();
  }

  @Override @CheckResult protected long getPeriodicEnableTime() {
    return getPreferences().getPeriodicEnableTimeData();
  }

  @Override @CheckResult protected long getPeriodicDisableTime() {
    return getPreferences().getPeriodicDisableTimeData();
  }

  @NonNull @Override public String getJobTag() {
    return DATA_JOB_TAG;
  }

  @NonNull @Override public FuncNone<Boolean> isIgnoreWhileCharging() {
    return () -> getPreferences().isIgnoreChargingData();
  }
}
