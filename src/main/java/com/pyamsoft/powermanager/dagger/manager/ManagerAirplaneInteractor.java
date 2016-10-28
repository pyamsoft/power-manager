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
import com.birbit.android.jobqueue.Job;
import com.pyamsoft.powermanager.PowerManagerPreferences;
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
      @NonNull BooleanInterestObserver wearManageObserver,
      @NonNull BooleanInterestObserver wearStateObserver,
      @NonNull PermissionObserver rootPermissionObserver) {
    super(jobManager, preferences, manageObserver, stateObserver, wearManageObserver,
        wearStateObserver);
    this.rootPermissionObserver = rootPermissionObserver;
  }

  @NonNull @Override public Observable<Boolean> isManaged() {
    return super.isManaged()
        .zipWith(rootPermissionObserver.hasPermission(),
            (managed, hasPermission) -> managed && hasPermission);
  }

  @NonNull @Override protected Job createEnableJob() {
    return null;
  }

  @NonNull @Override protected Job createDisableJob() {
    return null;
  }

  @Override public void destroy() {

  }

  @NonNull @Override public Observable<Boolean> cancelJobs() {
    return null;
  }

  @NonNull @Override public Observable<Boolean> isIgnoreWhileCharging() {
    return null;
  }
}
