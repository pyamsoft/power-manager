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
import com.evernote.android.job.JobManager;
import com.evernote.android.job.JobRequest;
import com.evernote.android.job.util.support.PersistableBundleCompat;
import com.pyamsoft.powermanager.model.JobQueuerEntry;
import javax.inject.Inject;
import timber.log.Timber;

class JobQueuerImpl implements JobQueuer {

  @NonNull final static String KEY_IGNORE_CHARGING = "extra_key__ignore_charging";
  @NonNull final static String KEY_PERIODIC = "extra_key__periodic";
  @NonNull final static String KEY_ON_WINDOW = "extra_key__on_window";
  @NonNull final static String KEY_OFF_WINDOW = "extra_key__off_window";
  @NonNull final static String KEY_QUEUE_TYPE = "extra_key__type";

  @NonNull private final JobManager jobManager;

  @Inject JobQueuerImpl(@NonNull JobManager jobManager) {
    this.jobManager = jobManager;
  }

  @Override public void cancel(@NonNull String tag) {
    Timber.w("Cancel all jobs for tag: %s", tag);
    jobManager.cancelAllForTag(tag);
  }

  @Override public void queue(@NonNull JobQueuerEntry entry) {
    final PersistableBundleCompat extras = new PersistableBundleCompat();
    extras.putString(KEY_QUEUE_TYPE, entry.type().name());
    extras.putLong(KEY_ON_WINDOW, entry.repeatingOnWindow());
    extras.putLong(KEY_OFF_WINDOW, entry.repeatingOffWindow());
    extras.putBoolean(KEY_PERIODIC, entry.repeating());
    extras.putBoolean(KEY_IGNORE_CHARGING, entry.ignoreIfCharging());
    if (entry.delay() == 0) {
      runDirectJob(entry.tag(), extras);
    } else {
      new JobRequest.Builder(entry.tag()).setExact(entry.delay())
          .setPersisted(false)
          .setExtras(extras)
          .setRequiresCharging(false)
          .setRequiresDeviceIdle(false)
          .build()
          .schedule();
    }
  }

  private void runDirectJob(@NonNull String tag, @NonNull PersistableBundleCompat extras) {
    final BaseJob job;
    switch (tag) {
      case JobQueuer.WIFI_JOB_TAG:
        job = new WifiJob();
        break;
      case JobQueuer.DATA_JOB_TAG:
        job = new DataJob();
        break;
      case JobQueuer.BLUETOOTH_JOB_TAG:
        job = new BluetoothJob();
        break;
      case JobQueuer.SYNC_JOB_TAG:
        job = new SyncJob();
        break;
      case JobQueuer.AIRPLANE_JOB_TAG:
        job = new AirplaneJob();
        break;
      case JobQueuer.DOZE_JOB_TAG:
        job = new DozeJob();
        break;
      default:
        job = null;
    }

    if (job != null) {
      job.run(tag, extras);
    }
  }
}
