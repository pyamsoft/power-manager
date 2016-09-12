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

package com.pyamsoft.powermanager.dagger.wrapper;

import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import com.birbit.android.jobqueue.Job;
import com.birbit.android.jobqueue.JobManager;
import com.birbit.android.jobqueue.TagConstraint;
import com.birbit.android.jobqueue.scheduling.FrameworkJobSchedulerService;
import com.birbit.android.jobqueue.scheduling.GcmJobSchedulerService;

public interface JobSchedulerCompat {

  @CheckResult @NonNull JobManager provideManagerToService(
      @NonNull FrameworkJobSchedulerService service);

  @CheckResult @NonNull JobManager provideManagerToService(@NonNull GcmJobSchedulerService service);

  void cancelJobsInBackground(@NonNull TagConstraint constraint, @NonNull String... tags);

  void addJobInBackground(@NonNull Job job);

  void cancelJobs(@NonNull TagConstraint constraint, @NonNull String... tags);
}
