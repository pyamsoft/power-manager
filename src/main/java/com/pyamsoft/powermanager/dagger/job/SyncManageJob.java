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

package com.pyamsoft.powermanager.dagger.job;

import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import com.birbit.android.jobqueue.Job;
import com.pyamsoft.powermanager.PowerManagerSingleInitProvider;
import com.pyamsoft.powermanager.app.modifier.BooleanInterestModifier;
import com.pyamsoft.powermanager.app.observer.BooleanInterestObserver;
import com.pyamsoft.powermanager.dagger.wrapper.JobSchedulerCompat;
import javax.inject.Inject;
import javax.inject.Named;

public abstract class SyncManageJob extends ManageJob {

  @NonNull public static final String JOB_TAG = "sync_job";

  @SuppressWarnings("WeakerAccess") SyncManageJob(@NonNull JobSchedulerCompat jobSchedulerCompat,
      @NonNull JobType jobType, long delayInSeconds, boolean periodic, long periodicEnableInSeconds,
      long periodicDisableInSeconds) {
    super(jobSchedulerCompat, JOB_TAG, jobType, delayInSeconds, periodic, periodicEnableInSeconds,
        periodicDisableInSeconds);
  }

  @NonNull @Override protected Job createPeriodicDisableJob(long periodicEnableInSeconds,
      long periodicDisableInSeconds) {
    return new DisableJob(getJobSchedulerCompat(), periodicDisableInSeconds * 1000L, true,
        periodicEnableInSeconds, periodicDisableInSeconds);
  }

  @NonNull @Override protected Job createPeriodicEnableJob(long periodicEnableInSeconds,
      long periodicDisableInSeconds) {
    return new EnableJob(getJobSchedulerCompat(), periodicEnableInSeconds * 1000L, true,
        periodicEnableInSeconds, periodicDisableInSeconds);
  }

  public static final class EnableJob extends SyncManageJob {

    @Inject @Named("mod_sync_state") BooleanInterestModifier interestModifier;
    @Inject @Named("obs_sync_state") BooleanInterestObserver interestObserver;

    EnableJob(@NonNull JobSchedulerCompat jobSchedulerCompat, long delayTimeInMillis,
        boolean periodic, long periodicEnableInSeconds, long periodicDisableInSeconds) {
      super(jobSchedulerCompat, JobType.ENABLE, delayTimeInMillis, periodic,
          periodicEnableInSeconds, periodicDisableInSeconds);
    }

    @CheckResult @NonNull
    public static EnableJob createManagerEnableJob(@NonNull JobSchedulerCompat jobSchedulerCompat) {
      return new EnableJob(jobSchedulerCompat, 100L, false, 0, 0);
    }

    @Override public void onAdded() {
      super.onAdded();
      PowerManagerSingleInitProvider.get().provideComponent().plusJobComponent().inject(this);
    }

    @Override public void run() {
      if (!interestObserver.is()) {
        interestModifier.set();
      }
    }
  }

  public static final class DisableJob extends SyncManageJob {

    @Inject @Named("mod_sync_state") BooleanInterestModifier interestModifier;
    @Inject @Named("obs_sync_state") BooleanInterestObserver interestObserver;

    DisableJob(@NonNull JobSchedulerCompat jobSchedulerCompat, long delayTimeInMillis,
        boolean periodic, long periodicEnableInSeconds, long periodicDisableInSeconds) {
      super(jobSchedulerCompat, JobType.DISABLE, delayTimeInMillis, periodic,
          periodicEnableInSeconds, periodicDisableInSeconds);
    }

    @CheckResult @NonNull
    public static DisableJob createManagerDisableJob(@NonNull JobSchedulerCompat jobSchedulerCompat,
        long delayTimeInMillis, boolean periodic, long periodicEnableInSeconds,
        long periodicDisableInSeconds) {
      return new DisableJob(jobSchedulerCompat, delayTimeInMillis, periodic,
          periodicEnableInSeconds, periodicDisableInSeconds);
    }

    @Override public void onAdded() {
      super.onAdded();
      PowerManagerSingleInitProvider.get().provideComponent().plusJobComponent().inject(this);
    }

    @Override public void run() {
      if (interestObserver.is()) {
        interestModifier.unset();
      }
    }
  }
}
