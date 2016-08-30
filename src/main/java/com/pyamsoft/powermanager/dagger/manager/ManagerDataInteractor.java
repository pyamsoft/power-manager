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

import android.content.Context;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import com.birbit.android.jobqueue.Job;
import com.pyamsoft.powermanager.PowerManagerPreferences;
import com.pyamsoft.powermanager.app.observer.BooleanInterestObserver;
import com.pyamsoft.powermanager.dagger.job.DataManageJob;
import javax.inject.Inject;
import rx.Observable;

class ManagerDataInteractor extends ManagerBaseInteractor {

  @Inject ManagerDataInteractor(@NonNull Context context,
      @NonNull PowerManagerPreferences preferences, @NonNull BooleanInterestObserver manageObserver,
      @NonNull BooleanInterestObserver stateObserver) {
    super(context, preferences, manageObserver, stateObserver);
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
    return new DataManageJob.EnableJob(100L, false, 0, 0);
  }

  @NonNull @Override protected Job createDisableJob() {
    return new DataManageJob.DisableJob(getDelayTime() * 1000L, isPeriodic(),
        getPeriodicEnableTime(), getPeriodicDisableTime());
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
