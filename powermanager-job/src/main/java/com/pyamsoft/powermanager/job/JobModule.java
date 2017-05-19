/*
 * Copyright 2017 Peter Kenji Yamanaka
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

package com.pyamsoft.powermanager.job;

import android.content.Context;
import android.support.annotation.NonNull;
import com.evernote.android.job.JobManager;
import com.evernote.android.job.util.JobCat;
import dagger.Module;
import dagger.Provides;
import javax.inject.Named;
import javax.inject.Singleton;
import timber.log.Timber;

@Module public class JobModule {

  @Singleton @Provides @Named("delay") JobQueuer provideDelayedJobQueuer(
      @NonNull JobManager jobManager) {
    return new DelayedJobQueuerImpl(jobManager);
  }

  @Singleton @Provides @Named("instant") JobQueuer provideInstantJobQueuer(
      @NonNull JobManager jobManager, @NonNull JobHandler jobHandler) {
    return new InstantJobQueuerImpl(jobManager, jobHandler);
  }

  @Singleton @Provides JobManager provideJobManager(@NonNull Context context) {
    // Job logs via Timber in debug mode
    JobCat.addLogPrinter((priority, tag, message, t) -> Timber.tag(tag).log(priority, t, message));
    JobCat.setLogcatEnabled(false);

    JobManager.create(context.getApplicationContext());
    return JobManager.instance();
  }
}
