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

package com.pyamsoft.powermanager.job;

import android.support.annotation.NonNull;
import com.evernote.android.job.Job;
import com.evernote.android.job.JobCreator;
import com.evernote.android.job.JobManager;
import dagger.Module;
import dagger.Provides;

@Module public class JobModule {

  @Provides JobQueuer provideJobQueuer(@NonNull JobManager jobManager) {
    return new JobQueuerImpl(jobManager);
  }

  @Provides JobManager provideJobManager(@NonNull JobCreator jobCreator) {
    JobManager.instance().addJobCreator(jobCreator);
    return JobManager.instance();
  }

  @Provides JobCreator provideJobCreator() {
    return tag -> {
      final Job job;
      switch (tag) {
        case JobQueuer.WIFI_JOB_TAG:
          job = new WifiJob.ManagedJob();
          break;
        case JobQueuer.DATA_JOB_TAG:
          job = new DataJob.ManagedJob();
          break;
        case JobQueuer.BLUETOOTH_JOB_TAG:
          job = new BluetoothJob.ManagedJob();
          break;
        case JobQueuer.SYNC_JOB_TAG:
          job = new SyncJob.ManagedJob();
          break;
        case JobQueuer.AIRPLANE_JOB_TAG:
          job = new AirplaneJob.ManagedJob();
          break;
        case JobQueuer.DOZE_JOB_TAG:
          job = new DozeJob.ManagedJob();
          break;
        case JobQueuer.TRIGGER_JOB_TAG:
          job = new TriggerJob();
          break;
        default:
          job = null;
      }

      return job;
    };
  }
}
