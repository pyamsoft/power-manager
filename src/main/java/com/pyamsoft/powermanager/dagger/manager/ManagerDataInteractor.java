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
import com.birbit.android.jobqueue.Job;
import com.pyamsoft.powermanager.PowerManagerPreferences;
import com.pyamsoft.powermanager.app.job.DataManageJob;
import com.pyamsoft.powermanager.app.observer.BooleanInterestObserver;
import com.pyamsoft.powermanager.app.observer.PermissionObserver;
import com.pyamsoft.powermanager.app.wrapper.JobSchedulerCompat;
import javax.inject.Inject;
import rx.Observable;

class ManagerDataInteractor extends ManagerBaseInteractor {

  @NonNull private final PermissionObserver rootPermissionObserver;

  @Inject ManagerDataInteractor(@NonNull JobSchedulerCompat jobManager,
      @NonNull PowerManagerPreferences preferences, @NonNull BooleanInterestObserver manageObserver,
      @NonNull BooleanInterestObserver stateObserver,
      @NonNull PermissionObserver rootPermissionObserver) {
    super(jobManager, preferences, manageObserver, stateObserver);
    this.rootPermissionObserver = rootPermissionObserver;
  }

  @NonNull @Override public Observable<Boolean> isManaged() {
    return super.isManaged()
        .zipWith(rootPermissionObserver.hasPermission(),
            (managed, hasPermission) -> managed && hasPermission);
  }

  @CheckResult private long getDelayTime() {
    return getPreferences().getDataDelay();
  }

  @CheckResult private boolean isPeriodic() {
    return getPreferences().isPeriodicData();
  }

  @CheckResult private long getPeriodicEnableTime() {
    return getPreferences().getPeriodicEnableTimeData();
  }

  @CheckResult private long getPeriodicDisableTime() {
    return getPreferences().getPeriodicDisableTimeData();
  }

  @NonNull @Override protected Job createEnableJob() {
    return DataManageJob.EnableJob.createManagerEnableJob(getJobManager());
  }

  @NonNull @Override protected Job createDisableJob() {
    return DataManageJob.DisableJob.createManagerDisableJob(getJobManager(), getDelayTime() * 1000L,
        isPeriodic(), getPeriodicEnableTime(), getPeriodicDisableTime());
  }

  @Override public void destroy() {
    destroy(DataManageJob.JOB_TAG);
  }

  @NonNull @Override public Observable<Boolean> cancelJobs() {
    return cancelJobs(DataManageJob.JOB_TAG);
  }

  @NonNull @Override public Observable<Boolean> isIgnoreWhileCharging() {
    return Observable.defer(() -> Observable.just(getPreferences().isIgnoreChargingData()));
  }
}
