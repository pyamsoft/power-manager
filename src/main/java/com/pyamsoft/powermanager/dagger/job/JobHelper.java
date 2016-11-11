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
import com.birbit.android.jobqueue.TagConstraint;
import com.pyamsoft.powermanager.PowerManagerPreferences;
import com.pyamsoft.powermanager.app.logger.Logger;
import com.pyamsoft.powermanager.app.modifier.BooleanInterestModifier;
import com.pyamsoft.powermanager.app.observer.BooleanInterestObserver;
import com.pyamsoft.powermanager.app.wrapper.JobSchedulerCompat;
import com.pyamsoft.powermanager.app.wrapper.PowerTriggerDB;
import timber.log.Timber;

public final class JobHelper {

  private JobHelper() {
    throw new RuntimeException("No instances");
  }

  @CheckResult @NonNull public static BaseJob createEnableJob(@NonNull JobType jobType,
      @NonNull JobSchedulerCompat jobSchedulerCompat, @NonNull String tag,
      @NonNull BooleanInterestObserver observer, @NonNull BooleanInterestModifier modifier,
      @NonNull Logger logger

  ) {
    switch (jobType) {
      case ENABLE:
      case DISABLE:
        Timber.d("Create Enable Manage Job");
        return createEnableManageJob(jobSchedulerCompat, tag, 100L, false, 0, 0, observer, modifier,
            logger);
      case TOGGLE_ENABLE:
      case TOGGLE_DISABLE:
        Timber.d("Create Enable Toggle Job");
        return createEnableToggleJob(jobSchedulerCompat, tag, 100L, false, 0, 0, observer, modifier,
            logger);
      default:
        throw new RuntimeException("Invalid enable job type: " + jobType);
    }
  }

  @CheckResult @NonNull private static EnableManageJob createEnableManageJob(
      @NonNull JobSchedulerCompat jobSchedulerCompat, @NonNull String tag, long delayTimeMillis,
      boolean periodic, long periodicEnableSeconds, long periodicDisableSeconds,
      @NonNull BooleanInterestObserver observer, @NonNull BooleanInterestModifier modifier,

      @NonNull Logger logger) {
    return new EnableManageJob(jobSchedulerCompat, tag, delayTimeMillis, periodic,
        periodicEnableSeconds, periodicDisableSeconds, observer, modifier, logger);
  }

  @CheckResult @NonNull private static EnableToggleJob createEnableToggleJob(
      @NonNull JobSchedulerCompat jobSchedulerCompat, @NonNull String tag, long delayTimeMillis,
      boolean periodic, long periodicEnableSeconds, long periodicDisableSeconds,
      @NonNull BooleanInterestObserver observer, @NonNull BooleanInterestModifier modifier,

      @NonNull Logger logger) {
    return new EnableToggleJob(jobSchedulerCompat, tag, delayTimeMillis, periodic,
        periodicEnableSeconds, periodicDisableSeconds, observer, modifier, logger);
  }

  @CheckResult @NonNull public static BaseJob createDisableJob(@NonNull JobType jobType,
      @NonNull JobSchedulerCompat jobSchedulerCompat, @NonNull String tag, long delayTimeInMillis,
      boolean periodic, long periodicEnableInSeconds, long periodicDisableInSeconds,
      @NonNull BooleanInterestObserver observer, @NonNull BooleanInterestModifier modifier,
      @NonNull Logger logger) {
    switch (jobType) {
      case ENABLE:
      case DISABLE:
        Timber.d("Create Disable Manage Job");
        return createDisableManageJob(jobSchedulerCompat, tag, delayTimeInMillis, periodic,
            periodicEnableInSeconds, periodicDisableInSeconds, observer, modifier, logger);
      case TOGGLE_ENABLE:
      case TOGGLE_DISABLE:
        Timber.d("Create Disable Toggle Job");
        return createDisableToggleJob(jobSchedulerCompat, tag, delayTimeInMillis, periodic,
            periodicEnableInSeconds, periodicDisableInSeconds, observer, modifier, logger);
      default:
        throw new RuntimeException("Invalid disable job type: " + jobType);
    }
  }

  @CheckResult @NonNull private static DisableManageJob createDisableManageJob(
      @NonNull JobSchedulerCompat jobSchedulerCompat, @NonNull String tag, long delayTimeInMillis,
      boolean periodic, long periodicEnableInSeconds, long periodicDisableInSeconds,
      @NonNull BooleanInterestObserver observer, @NonNull BooleanInterestModifier modifier,

      @NonNull Logger logger) {
    return new DisableManageJob(jobSchedulerCompat, tag, delayTimeInMillis, periodic,
        periodicEnableInSeconds, periodicDisableInSeconds, observer, modifier, logger);
  }

  @CheckResult @NonNull private static DisableToggleJob createDisableToggleJob(
      @NonNull JobSchedulerCompat jobSchedulerCompat, @NonNull String tag, long delayTimeInMillis,
      boolean periodic, long periodicEnableInSeconds, long periodicDisableInSeconds,
      @NonNull BooleanInterestObserver observer, @NonNull BooleanInterestModifier modifier,
      @NonNull Logger logger) {
    return new DisableToggleJob(jobSchedulerCompat, tag, delayTimeInMillis, periodic,
        periodicEnableInSeconds, periodicDisableInSeconds, observer, modifier, logger);
  }

  public static void queueTriggerJob(@NonNull JobSchedulerCompat jobSchedulerCompat,
      @NonNull BooleanInterestObserver wifiObserver, @NonNull BooleanInterestObserver dataObserver,
      @NonNull BooleanInterestObserver bluetoothObserver,
      @NonNull BooleanInterestObserver syncObserver, @NonNull BooleanInterestModifier wifiModifier,
      @NonNull BooleanInterestModifier dataModifier,
      @NonNull BooleanInterestModifier bluetoothModifier,
      @NonNull BooleanInterestModifier syncModifier, @NonNull PowerTriggerDB powerTriggerDB,
      @NonNull PowerManagerPreferences preferences, @NonNull Logger logger) {
    Timber.d("Cancel any old trigger jobs");
    jobSchedulerCompat.cancelJobsInBackground(TagConstraint.ANY, TriggerJob.TRIGGER_TAG);

    // TODO Get delay from preferences
    final long delayTime = 5 * 60 * 1000;

    final TriggerJob triggerJob =
        new TriggerJob(delayTime, wifiObserver, dataObserver, bluetoothObserver, syncObserver,
            wifiModifier, dataModifier, bluetoothModifier, syncModifier, jobSchedulerCompat,
            powerTriggerDB, preferences, logger);

    Timber.d("Add new trigger job");
    jobSchedulerCompat.addJobInBackground(triggerJob);
  }
}
