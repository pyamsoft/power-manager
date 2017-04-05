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

import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import com.evernote.android.job.JobManager;
import com.evernote.android.job.JobRequest;
import com.evernote.android.job.util.support.PersistableBundleCompat;
import javax.inject.Inject;
import timber.log.Timber;

class JobQueuerImpl implements JobQueuer {

  @NonNull final static String KEY_IGNORE_CHARGING = "extra_key__ignore_charging";
  @NonNull final static String KEY_PERIODIC = "extra_key__periodic";
  @NonNull final static String KEY_ON_WINDOW = "extra_key__on_window";
  @NonNull final static String KEY_OFF_WINDOW = "extra_key__off_window";
  @NonNull final static String KEY_QUEUE_TYPE = "extra_key__type";
  @NonNull private final JobManager jobManager;
  @NonNull private final BaseJobCreator jobCreator;

  @Inject JobQueuerImpl(@NonNull JobManager jobManager, @NonNull BaseJobCreator creator) {
    this.jobManager = jobManager;
    this.jobCreator = creator;
  }

  @Override public void cancel(@NonNull String tag) {
    Timber.w("Cancel all jobs for tag: %s", tag);
    jobManager.cancelAllForTag(tag);
  }

  @CheckResult @NonNull private PersistableBundleCompat createExtras(JobQueuerEntry entry) {
    final PersistableBundleCompat extras = new PersistableBundleCompat();
    extras.putString(KEY_QUEUE_TYPE, entry.type().name());
    extras.putLong(KEY_ON_WINDOW, entry.repeatingOnWindow());
    extras.putLong(KEY_OFF_WINDOW, entry.repeatingOffWindow());
    extras.putBoolean(KEY_PERIODIC, entry.repeating());
    extras.putBoolean(KEY_IGNORE_CHARGING, entry.ignoreIfCharging());
    return extras;
  }

  @Override public void queue(@NonNull JobQueuerEntry entry) {
    final PersistableBundleCompat extras = createExtras(entry);
    if (entry.delay() == 0) {
      jobCreator.create(entry.tag()).run(entry.tag(), extras);
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

  @Override public void queueRepeating(@NonNull JobQueuerEntry entry) {
    final PersistableBundleCompat extras = createExtras(entry);
    new JobRequest.Builder(entry.tag()).setPeriodic(entry.delay())
        .setPersisted(false)
        .setExtras(extras)
        .setRequiresCharging(false)
        .setRequiresDeviceIdle(false)
        .build()
        .schedule();
  }
}
