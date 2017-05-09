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
import com.evernote.android.job.JobCreator;
import com.evernote.android.job.JobManager;
import dagger.Module;
import dagger.Provides;
import javax.inject.Singleton;
import timber.log.Timber;

@Module public class JobModule {

  @Singleton @Provides JobCreator provideJobCreator(@NonNull JobHandler jobHandler) {
    return s -> {
      if (JobQueuer.MANAGED_TAG.equals(s)) {
        return new ManagedJob(jobHandler);
      } else {
        Timber.e("Invalid job tag: %s", s);
        return null;
      }
    };
  }

  @Singleton @Provides JobQueuer provideJobQueuer(@NonNull JobManager jobManager,
      @NonNull JobHandler jobHandler) {
    return new JobQueuerImpl(jobManager, jobHandler);
  }

  @Singleton @Provides JobManager provideJobManager(@NonNull Context context,
      @NonNull JobCreator jobCreator) {
    JobManager.create(context.getApplicationContext());
    JobManager.instance().removeJobCreator(jobCreator);
    JobManager.instance().addJobCreator(jobCreator);
    return JobManager.instance();
  }
}
