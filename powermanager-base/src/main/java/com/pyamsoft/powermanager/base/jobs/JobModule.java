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

import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import com.birbit.android.jobqueue.JobManager;
import com.birbit.android.jobqueue.config.Configuration;
import com.birbit.android.jobqueue.scheduling.FrameworkJobSchedulerService;
import com.birbit.android.jobqueue.scheduling.GcmJobSchedulerService;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import dagger.Module;
import dagger.Provides;
import javax.inject.Singleton;

@Module public class JobModule {

  @Singleton @Provides JobQueuer provideJobQueuer(@NonNull JobManager jobManager) {
    return new JobQueuerImpl(jobManager);
  }

  @Singleton @Provides JobManager provideJobManager(@NonNull Context context) {
    final Context appContext = context.getApplicationContext();
    final Configuration.Builder builder = new Configuration.Builder(appContext);

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      builder.scheduler(
          FrameworkJobSchedulerService.createSchedulerFor(appContext, FrameworkJobService.class));
    } else if (GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(appContext)
        == ConnectionResult.SUCCESS) {
      builder.scheduler(GcmJobSchedulerService.createSchedulerFor(appContext, GCMJobService.class));
    }

    return new JobManager(builder.build());
  }
}
