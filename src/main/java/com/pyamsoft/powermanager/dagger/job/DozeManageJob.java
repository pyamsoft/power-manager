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

import android.support.annotation.NonNull;
import com.birbit.android.jobqueue.Job;
import com.pyamsoft.powermanager.Singleton;
import com.pyamsoft.powermanager.app.modifier.BooleanInterestModifier;
import javax.inject.Inject;
import javax.inject.Named;

public abstract class DozeManageJob extends ManageJob {

  @NonNull public static final String JOB_TAG = "doze_job";

  DozeManageJob(@NonNull JobType jobType, long delayInSeconds, boolean periodic,
      long periodicEnableInSeconds, long periodicDisableInSeconds) {
    super(JOB_TAG, jobType, delayInSeconds, periodic, periodicEnableInSeconds,
        periodicDisableInSeconds);
  }

  @NonNull @Override protected Job createPeriodicDisableJob(long periodicEnableInSeconds,
      long periodicDisableInSeconds) {
    return new DisableJob(periodicDisableInSeconds * 1000L, true, periodicEnableInSeconds,
        periodicDisableInSeconds);
  }

  @NonNull @Override protected Job createPeriodicEnableJob(long periodicEnableInSeconds,
      long periodicDisableInSeconds) {
    return new EnableJob(periodicEnableInSeconds * 1000L, true, periodicEnableInSeconds,
        periodicDisableInSeconds);
  }

  public static final class EnableJob extends DozeManageJob {

    @Inject @Named("mod_doze_state") BooleanInterestModifier interestModifier;

    public EnableJob(long delayTimeInMillis, boolean periodic, long periodicEnableInSeconds,
        long periodicDisableInSeconds) {
      super(JobType.ENABLE, delayTimeInMillis, periodic, periodicEnableInSeconds,
          periodicDisableInSeconds);
    }

    @Override public void onAdded() {
      super.onAdded();
      Singleton.Dagger.with(getApplicationContext()).plusJobComponent().inject(this);
    }

    @Override public void run() {
      // Doze job is a bit backwards since Doze is thought of differently
      // Doze being 'enabled' actually means to turn it off
      interestModifier.unset();
    }
  }

  public static final class DisableJob extends DozeManageJob {

    @Inject @Named("mod_doze_state") BooleanInterestModifier interestModifier;

    public DisableJob(long delayTimeInMillis, boolean periodic, long periodicEnableInSeconds,
        long periodicDisableInSeconds) {
      super(JobType.DISABLE, delayTimeInMillis, periodic, periodicEnableInSeconds,
          periodicDisableInSeconds);
    }

    @Override public void onAdded() {
      super.onAdded();
      Singleton.Dagger.with(getApplicationContext()).plusJobComponent().inject(this);
    }

    @Override public void run() {
      // Doze job is a bit backwards since Doze is thought of differently
      // Doze being 'disabled' actually means to turn it on
      interestModifier.set();
    }
  }
}
