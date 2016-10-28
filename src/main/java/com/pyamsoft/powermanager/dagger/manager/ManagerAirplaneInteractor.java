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
import com.pyamsoft.powermanager.app.modifier.BooleanInterestModifier;
import com.pyamsoft.powermanager.app.observer.BooleanInterestObserver;
import com.pyamsoft.powermanager.app.observer.PermissionObserver;
import com.pyamsoft.powermanager.app.wrapper.JobSchedulerCompat;
import javax.inject.Inject;
import rx.Observable;

class ManagerAirplaneInteractor extends WearAwareManagerBaseInteractor {

  @NonNull private final PermissionObserver rootPermissionObserver;

  @Inject ManagerAirplaneInteractor(@NonNull JobSchedulerCompat jobManager,
      @NonNull PowerManagerPreferences preferences, @NonNull BooleanInterestObserver manageObserver,
      @NonNull BooleanInterestObserver stateObserver,
      @NonNull BooleanInterestModifier stateModifier,
      @NonNull BooleanInterestObserver wearManageObserver,
      @NonNull BooleanInterestObserver wearStateObserver,
      @NonNull PermissionObserver rootPermissionObserver) {
    super(jobManager, preferences, manageObserver, stateObserver, stateModifier, wearManageObserver,
        wearStateObserver);
    this.rootPermissionObserver = rootPermissionObserver;
  }

  @NonNull @Override public Observable<Boolean> isManaged() {
    return super.isManaged()
        .zipWith(rootPermissionObserver.hasPermission(),
            (managed, hasPermission) -> managed && hasPermission);
  }

  @Override protected long getDelayTime() {
    return 0;
  }

  @Override protected boolean isPeriodic() {
    return false;
  }

  @Override protected long getPeriodicEnableTime() {
    return 0;
  }

  @Override protected long getPeriodicDisableTime() {
    return 0;
  }

  @NonNull @Override protected String getJobTag() {
    return "airplane_job";
  }

  @NonNull @Override public Observable<Boolean> isIgnoreWhileCharging() {
    // TODO
    return Observable.just(true);
  }
}
