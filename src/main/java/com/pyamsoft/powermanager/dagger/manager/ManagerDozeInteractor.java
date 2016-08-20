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
import com.pyamsoft.powermanager.app.observer.InterestObserver;
import com.pyamsoft.powermanager.dagger.job.DozeManageJob;
import javax.inject.Inject;
import rx.Observable;

final class ManagerDozeInteractor extends ManagerBaseInteractor
    implements ExclusiveManagerInteractor {

  @NonNull private final InterestObserver dozeObserver;

  @Inject ManagerDozeInteractor(@NonNull Context context,
      @NonNull PowerManagerPreferences preferences, @NonNull InterestObserver observer) {
    super(context, preferences);
    this.dozeObserver = observer;
  }

  @CheckResult long getDelayTime() {
    return getPreferences().getDozeDelay();
  }

  // KLUDGE Should Doze be periodic too? The system already makes it so
  @CheckResult boolean isPeriodic() {
    return false;
  }

  // KLUDGE Should Doze be periodic too? The system already makes it so
  @CheckResult long getPeriodicEnableTime() {
    return 0;
  }

  // KLUDGE Should Doze be periodic too? The system already makes it so
  @CheckResult long getPeriodicDisableTime() {
    return 0;
  }

  @NonNull @Override protected Job createEnableJob() {
    return new DozeManageJob.EnableJob(100L, false, 0, 0);
  }

  @NonNull @Override protected Job createDisableJob() {
    return new DozeManageJob.DisableJob(getDelayTime() * 1000L, isPeriodic(),
        getPeriodicEnableTime(), getPeriodicDisableTime());
  }

  @Override public void destroy() {
    destroy(DozeManageJob.JOB_TAG);
  }

  @NonNull @Override public Observable<Boolean> cancelJobs() {
    return cancelJobs(DozeManageJob.JOB_TAG);
  }

  @NonNull @Override public Observable<Boolean> isManaged() {
    return Observable.defer(() -> Observable.just(getPreferences().isDozeManaged()));
  }

  @NonNull @Override public Observable<Boolean> isIgnoreWhileCharging() {
    return Observable.defer(() -> Observable.just(getPreferences().isIgnoreChargingDoze()));
  }

  @NonNull @Override public Observable<Boolean> isEnabled() {
    return Observable.defer(() -> Observable.just(dozeObserver.is()));
  }

  // TODO read from preference
  // TODO observable?
  @Override public boolean isExclusive() {
    return false;
  }
}