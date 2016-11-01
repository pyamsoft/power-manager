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
import com.pyamsoft.powermanager.app.modifier.BooleanInterestModifier;
import com.pyamsoft.powermanager.app.observer.BooleanInterestObserver;
import com.pyamsoft.powermanager.app.wrapper.JobSchedulerCompat;
import javax.inject.Inject;
import rx.Observable;

class ManagerSyncInteractorImpl extends ManagerInteractorImpl {

  @Inject ManagerSyncInteractorImpl(@NonNull JobSchedulerCompat jobManager,
      @NonNull PowerManagerPreferences preferences, @NonNull BooleanInterestObserver manageObserver,
      @NonNull BooleanInterestObserver stateObserver,
      @NonNull BooleanInterestModifier stateModifier) {
    super(jobManager, preferences, manageObserver, stateModifier, stateObserver);
  }

  @Override @CheckResult protected long getDelayTime() {
    return getPreferences().getMasterSyncDelay();
  }

  @Override @CheckResult protected boolean isPeriodic() {
    return getPreferences().isPeriodicSync();
  }

  @Override @CheckResult protected long getPeriodicEnableTime() {
    return getPreferences().getPeriodicEnableTimeSync();
  }

  @Override @CheckResult protected long getPeriodicDisableTime() {
    return getPreferences().getPeriodicDisableTimeSync();
  }

  @NonNull @Override protected String getJobTag() {
    return "sync_jobs";
  }

  @NonNull @Override public Observable<Boolean> isIgnoreWhileCharging() {
    return Observable.defer(() -> Observable.just(getPreferences().isIgnoreChargingSync()));
  }
}