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
import com.birbit.android.jobqueue.Params;
import com.pyamsoft.powermanager.app.modifier.BooleanInterestModifier;
import com.pyamsoft.powermanager.app.observer.BooleanInterestObserver;
import com.pyamsoft.powermanager.app.wrapper.JobSchedulerCompat;
import timber.log.Timber;

abstract class ManageJobImpl extends BaseJob {

  private static final long MINIMUM_PERIOD_SECONDS = 60L;
  private static final int JOB_PRIORITY = 1;

  @NonNull private final JobSchedulerCompat jobSchedulerCompat;
  @NonNull private final JobType jobType;
  private final boolean periodic;
  private final long periodicEnableInSeconds;
  private final long periodicDisableInSeconds;
  @NonNull private final BooleanInterestObserver interestObserver;
  @NonNull private final BooleanInterestModifier interestModifier;
  @NonNull private final String jobTag;

  ManageJobImpl(@NonNull JobSchedulerCompat jobSchedulerCompat, @NonNull String tag,
      @NonNull JobType jobType, long delayInMilliseconds, boolean periodic,
      long periodicEnableInSeconds, long periodicDisableInSeconds,
      @NonNull BooleanInterestObserver interestObserver,
      @NonNull BooleanInterestModifier interestModifier) {
    super(new Params(JOB_PRIORITY).addTags(tag).setDelayMs(delayInMilliseconds));
    this.jobSchedulerCompat = jobSchedulerCompat;
    this.jobType = jobType;
    this.periodic = periodic;
    this.periodicEnableInSeconds = periodicEnableInSeconds;
    this.periodicDisableInSeconds = periodicDisableInSeconds;
    this.interestObserver = interestObserver;
    this.interestModifier = interestModifier;
    this.jobTag = tag;
  }

  @CheckResult @NonNull final JobSchedulerCompat getJobSchedulerCompat() {
    return jobSchedulerCompat;
  }

  @Override public final void onRun() throws Throwable {
    Timber.d("Run job type: %s", jobType.name());
    switch (jobType) {
      case ENABLE:
        enable();
        break;
      case TOGGLE_ENABLE:
        enable();
        break;
      case DISABLE:
        disable();
        break;
      case TOGGLE_DISABLE:
        disable();
        break;
      default:
        throw new RuntimeException("Invalid job type: " + jobType.name());
    }
  }

  @CheckResult private boolean hasValidPeriodicInterval() {
    return periodicDisableInSeconds >= MINIMUM_PERIOD_SECONDS
        && periodicEnableInSeconds >= MINIMUM_PERIOD_SECONDS;
  }

  void internalEnable() {
    if (!interestObserver.is()) {
      interestModifier.set();
    }
  }

  private void enable() {
    internalEnable();
    if (periodic) {
      if (!hasValidPeriodicInterval()) {
        Timber.e("Not queuing period disable job with interval less than 1 minute (%s, %s)",
            periodicEnableInSeconds, periodicDisableInSeconds);
      } else {
        Timber.d("Queue periodic disable job for: %d", periodicDisableInSeconds);
        jobSchedulerCompat.addJob(
            createPeriodicDisableJob(periodicEnableInSeconds, periodicDisableInSeconds));
      }
    }
  }

  @CheckResult @NonNull private Job createPeriodicDisableJob(long periodicEnableInSeconds,
      long periodicDisableInSeconds) {
    return JobHelper.createPeriodicDisableJob(jobType, getJobSchedulerCompat(), jobTag,
        periodicEnableInSeconds, periodicDisableInSeconds, interestObserver, interestModifier);
  }

  void internalDisable() {
    if (interestObserver.is()) {
      interestModifier.unset();
    }
  }

  private void disable() {
    internalDisable();
    if (periodic) {
      if (!hasValidPeriodicInterval()) {
        Timber.e("Not queuing period disable job with interval less than 1 minute (%s, %s)",
            periodicEnableInSeconds, periodicDisableInSeconds);
      } else {
        Timber.d("Queue periodic enable job for: %d", periodicEnableInSeconds);
        jobSchedulerCompat.addJob(
            createPeriodicEnableJob(periodicEnableInSeconds, periodicDisableInSeconds));
      }
    }
  }

  @CheckResult @NonNull
  private Job createPeriodicEnableJob(long periodicEnableInSeconds, long periodicDisableInSeconds) {
    return JobHelper.createPeriodicEnableJob(jobType, getJobSchedulerCompat(), jobTag,
        periodicEnableInSeconds, periodicDisableInSeconds, interestObserver, interestModifier);
  }
}
