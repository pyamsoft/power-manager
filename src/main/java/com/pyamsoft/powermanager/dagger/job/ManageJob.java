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
import com.pyamsoft.powermanager.Singleton;
import timber.log.Timber;

public abstract class ManageJob extends BaseJob implements Runnable {

  static final long MINIMUM_PERIOD_SECONDS = 60L;
  static final int JOB_PRIORITY = 1;

  @NonNull final JobType jobType;
  final boolean periodic;
  final long periodicEnableInSeconds;
  final long periodicDisableInSeconds;

  protected ManageJob(@NonNull String tag, @NonNull JobType jobType, long delayInMilliseconds,
      boolean periodic, long periodicEnableInSeconds, long periodicDisableInSeconds) {
    super(new Params(JOB_PRIORITY).addTags(tag).setDelayMs(delayInMilliseconds));
    this.jobType = jobType;
    this.periodic = periodic;
    this.periodicEnableInSeconds = periodicEnableInSeconds;
    this.periodicDisableInSeconds = periodicDisableInSeconds;
  }

  @CheckResult private boolean isPeriodic() {
    return periodic;
  }

  @CheckResult private long getPeriodicDisableInSeconds() {
    return periodicDisableInSeconds;
  }

  @CheckResult private long getPeriodicEnableInSeconds() {
    return periodicEnableInSeconds;
  }

  @Override public final void onRun() throws Throwable {
    run();
    Timber.d("Run job type: %s", jobType.name());
    switch (jobType) {
      case ENABLE:
        internalEnable();
        break;
      case DISABLE:
        internalDisable();
        break;
      default:
        throw new RuntimeException("Invalid job type: " + jobType.name());
    }
  }

  @CheckResult private boolean hasValidPeriodicInterval() {
    return getPeriodicDisableInSeconds() >= MINIMUM_PERIOD_SECONDS
        && getPeriodicEnableInSeconds() >= MINIMUM_PERIOD_SECONDS;
  }

  private void internalEnable() {
    if (isPeriodic()) {
      if (!hasValidPeriodicInterval()) {
        Timber.e("Not queuing period disable job with interval less than 1 minute (%s, %s)",
            getPeriodicEnableInSeconds(), getPeriodicDisableInSeconds());
      } else {
        Timber.d("Queue periodic disable job for: %d", getPeriodicDisableInSeconds());
        Singleton.Jobs.with(getApplicationContext())
            .addJobInBackground(createPeriodicDisableJob(getPeriodicEnableInSeconds(),
                getPeriodicDisableInSeconds()));
      }
    }
  }

  private void internalDisable() {
    if (isPeriodic()) {
      if (!hasValidPeriodicInterval()) {
        Timber.e("Not queuing period disable job with interval less than 1 minute (%s, %s)",
            getPeriodicEnableInSeconds(), getPeriodicDisableInSeconds());
      } else {
        Timber.d("Queue periodic enable job for: %d", getPeriodicDisableInSeconds());
        Singleton.Jobs.with(getApplicationContext())
            .addJobInBackground(createPeriodicEnableJob(getPeriodicEnableInSeconds(),
                getPeriodicDisableInSeconds()));
      }
    }
  }

  @CheckResult @NonNull
  protected abstract Job createPeriodicDisableJob(long periodicEnableInSeconds,
      long periodicDisableInSeconds);

  @CheckResult @NonNull protected abstract Job createPeriodicEnableJob(long periodicEnableInSeconds,
      long periodicDisableInSeconds);

  protected enum JobType {
    ENABLE, DISABLE
  }
}
