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

package com.pyamsoft.powermanager.job

import android.content.Context
import com.evernote.android.job.JobManager
import com.evernote.android.job.util.JobCat
import dagger.Module
import dagger.Provides
import timber.log.Timber
import javax.inject.Named
import javax.inject.Singleton

@Module class JobModule {
  @Singleton @Provides @Named("delay") fun provideDelayedJobQueuer(
      jobManager: JobManager): JobQueuer {
    return DelayedJobQueuerImpl(jobManager)
  }

  @Singleton @Provides @Named("instant") fun provideInstantJobQueuer(jobManager: JobManager,
      jobHandler: JobHandler): JobQueuer {
    return InstantJobQueuerImpl(jobManager, jobHandler)
  }

  @Singleton @Provides fun provideJobManager(context: Context): JobManager {
    // Job logs via Timber in debug mode
    JobCat.addLogPrinter { priority, tag, message, t -> Timber.tag(tag).log(priority, t, message) }
    JobCat.setLogcatEnabled(false)

    JobManager.create(context.applicationContext)
    return JobManager.instance()
  }
}
