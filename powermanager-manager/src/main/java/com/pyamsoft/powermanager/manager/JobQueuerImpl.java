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

package com.pyamsoft.powermanager.manager;

import android.os.Looper;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.birbit.android.jobqueue.Job;
import com.birbit.android.jobqueue.JobManager;
import com.birbit.android.jobqueue.Params;
import com.birbit.android.jobqueue.RetryConstraint;
import com.birbit.android.jobqueue.TagConstraint;
import com.pyamsoft.powermanager.model.BooleanInterestModifier;
import com.pyamsoft.powermanager.model.BooleanInterestObserver;
import com.pyamsoft.powermanager.model.JobQueuerEntry;
import com.pyamsoft.powermanager.model.QueuerType;
import javax.inject.Inject;

class JobQueuerImpl implements JobQueuer {

  @NonNull private final JobManager jobManager;

  @Inject JobQueuerImpl(@NonNull JobManager jobManager) {
    this.jobManager = jobManager;
  }

  @Override public void cancel(@NonNull String... tags) {
    if (Thread.currentThread() == Looper.getMainLooper().getThread()) {
      jobManager.cancelJobsInBackground(null, TagConstraint.ANY, tags);
    } else {
      jobManager.cancelJobs(TagConstraint.ANY, tags);
    }
  }

  @Override public void queue(@NonNull JobQueuerEntry entry) {
    final Job job = createJobForEntry(entry);
    if (Thread.currentThread() == Looper.getMainLooper().getThread()) {
      jobManager.addJobInBackground(job);
    } else {
      jobManager.addJob(job);
    }
  }

  @CheckResult @NonNull private Job createJobForEntry(JobQueuerEntry entry) {
    return null;
  }

  static class DeviceJob extends Job {

    @NonNull private final BooleanInterestObserver observer;
    @NonNull private final BooleanInterestModifier modifier;
    @NonNull private final QueuerType type;

    private DeviceJob(@NonNull Params params, @NonNull BooleanInterestObserver observer,
        @NonNull BooleanInterestModifier modifier, @NonNull QueuerType type) {
      super(params.addTags(ManagerInteractor.ALL_JOB_TAG)
          .setRequiresNetwork(false)
          .setRequiresUnmeteredNetwork(false));

      this.observer = observer;
      this.modifier = modifier;
      this.type = type;
    }

    @Override public void onAdded() {

    }

    @Override public void onRun() throws Throwable {

    }

    @Override protected void onCancel(int cancelReason, @Nullable Throwable throwable) {

    }

    @Override
    protected RetryConstraint shouldReRunOnThrowable(@NonNull Throwable throwable, int runCount,
        int maxRunCount) {
      return null;
    }
  }
}
