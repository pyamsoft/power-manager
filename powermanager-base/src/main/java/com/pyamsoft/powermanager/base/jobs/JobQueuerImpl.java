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

package com.pyamsoft.powermanager.base.jobs;

import android.os.Looper;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import com.birbit.android.jobqueue.Job;
import com.birbit.android.jobqueue.JobManager;
import com.birbit.android.jobqueue.TagConstraint;
import com.pyamsoft.powermanager.base.db.PowerTriggerDB;
import com.pyamsoft.powermanager.model.BooleanInterestModifier;
import com.pyamsoft.powermanager.model.BooleanInterestObserver;
import com.pyamsoft.powermanager.model.JobQueuerEntry;
import com.pyamsoft.powermanager.model.Logger;
import java.util.Arrays;
import javax.inject.Inject;
import timber.log.Timber;

class JobQueuerImpl implements JobQueuer {

  @NonNull private final JobManager jobManager;

  @Inject JobQueuerImpl(@NonNull JobManager jobManager) {
    this.jobManager = jobManager;
  }

  @Override public void cancel(@NonNull String... tags) {
    Timber.d("Cancel jobs with tag: %s", Arrays.toString(tags));
    jobManager.cancelJobs(TagConstraint.ANY, tags);
  }

  @Override public void destroy(@NonNull String... tags) {
    Timber.d("Destroy jobs with tag: %s", Arrays.toString(tags));
    if (Thread.currentThread() == Looper.getMainLooper().getThread()) {
      jobManager.cancelJobsInBackground(null, TagConstraint.ANY, tags);
    } else {
      jobManager.cancelJobs(TagConstraint.ANY, tags);
    }
  }

  @Override public void queue(@NonNull JobQueuerEntry entry) {
    Timber.d("Queue new job with tag: %s", entry.tag());
    jobManager.addJob(createJobForEntry(entry));
  }

  @Override public void queueTrigger(long delayTime, @NonNull Logger logger,
      @NonNull PowerTriggerDB powerTriggerDB, @NonNull BooleanInterestObserver wifiObserver,
      @NonNull BooleanInterestObserver dataObserver,
      @NonNull BooleanInterestObserver bluetoothObserver,
      @NonNull BooleanInterestObserver syncObserver, @NonNull BooleanInterestModifier wifiModifier,
      @NonNull BooleanInterestModifier dataModifier,
      @NonNull BooleanInterestModifier bluetoothModifier,
      @NonNull BooleanInterestModifier syncModifier,
      @NonNull BooleanInterestObserver chargingObserver) {
    final Job job =
        new TriggerJob(delayTime, this, logger, powerTriggerDB, wifiObserver, dataObserver,
            bluetoothObserver, syncObserver, wifiModifier, dataModifier, bluetoothModifier,
            syncModifier, chargingObserver);
    jobManager.addJob(job);
  }

  @CheckResult @NonNull private Job createJobForEntry(JobQueuerEntry entry) {
    final Job job;
    if (entry.tag().equals(JobQueuer.TRIGGER_JOB_TAG)) {
      throw new IllegalStateException("Should call queueTrigger with Trigger jobs");
    } else if (entry.repeating()) {
      job = Jobs.createRepeating(entry.tag(), entry.delay(), entry.ignoreIfCharging(),
          entry.observer(), entry.modifier(), entry.chargingObserver(), entry.type(),
          entry.repeatingOnWindow(), entry.repeatingOffWindow(), entry.logger(), this);
    } else {
      job = Jobs.createNonRepeating(entry.tag(), entry.delay(), entry.ignoreIfCharging(),
          entry.observer(), entry.modifier(), entry.chargingObserver(), entry.type(),
          entry.logger());
    }
    return job;
  }
}
