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

import android.app.Service;
import android.content.Context;
import android.os.Build;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import com.birbit.android.jobqueue.Job;
import com.birbit.android.jobqueue.JobManager;
import com.birbit.android.jobqueue.TagConstraint;
import com.birbit.android.jobqueue.config.Configuration;
import com.birbit.android.jobqueue.scheduling.FrameworkJobSchedulerService;
import com.birbit.android.jobqueue.scheduling.GcmJobSchedulerService;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.pyamsoft.powermanager.app.service.job.PowerManagerFrameworkJobSchedulerService;
import com.pyamsoft.powermanager.app.service.job.PowerManagerGCMJobSchedulerService;
import com.pyamsoft.powermanager.app.wrapper.JobSchedulerCompat;
import javax.inject.Inject;
import timber.log.Timber;

class JobSchedulerCompatImpl implements JobSchedulerCompat {

  @NonNull private final JobManager jobManager;

  @Inject JobSchedulerCompatImpl(@NonNull Context context) {
    jobManager = createJobManager(context.getApplicationContext());
  }

  @CheckResult @NonNull JobManager createJobManager(@NonNull Context context) {
    final Context appContext = context.getApplicationContext();
    final Configuration.Builder builder = new Configuration.Builder(appContext);

    Timber.d("Create scheduler");
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      Timber.i("Create scheduler using JobScheduler framework");
      builder.scheduler(FrameworkJobSchedulerService.createSchedulerFor(appContext,
          PowerManagerFrameworkJobSchedulerService.class));
    } else {
      Timber.d("Check Google play availability");
      final int googleAvailable =
          GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(appContext);
      if (googleAvailable == ConnectionResult.SUCCESS) {
        Timber.i("Create scheduler using Google play services");

        // Batch by default
        builder.scheduler(GcmJobSchedulerService.createSchedulerFor(appContext,
            PowerManagerGCMJobSchedulerService.class));
      } else {
        Timber.e("Could not create a scheduler to use with the JobScheduler");
      }
    }

    Timber.d("Create a new JobManager");
    return new JobManager(builder.build());
  }

  @CheckResult @NonNull @Override
  public JobManager provideManagerToService(@NonNull Service service) {
    return jobManager;
  }

  @Override
  public void cancelJobsInBackground(@NonNull TagConstraint constraint, @NonNull String... tags) {
    jobManager.cancelJobsInBackground(null, constraint, tags);
  }

  @Override public void addJobInBackground(@NonNull Job job) {
    jobManager.addJobInBackground(job);
  }

  @Override public void cancelJobs(@NonNull TagConstraint constraint, @NonNull String... tags) {
    jobManager.cancelJobs(constraint, tags);
  }

  @Override public void addJob(@NonNull Job job) {
    jobManager.addJob(job);
  }
}
