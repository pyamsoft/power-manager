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

import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import com.evernote.android.job.JobManager;
import com.evernote.android.job.JobRequest;
import com.evernote.android.job.util.support.PersistableBundleCompat;
import java.util.concurrent.TimeUnit;
import timber.log.Timber;

abstract class BaseJobQueuer implements JobQueuer {

  @NonNull final static String KEY_ON_WINDOW = "extra_key__on_window";
  @NonNull final static String KEY_OFF_WINDOW = "extra_key__off_window";
  @NonNull final static String KEY_SCREEN = "extra_key__screen";
  @NonNull final static String KEY_ONESHOT = "extra_key__once";
  @NonNull final static String KEY_FIRST_RUN = "extra_key__first";
  private static final long WINDOW_SMALL = TimeUnit.SECONDS.toMillis(15);
  private static final long FIVE_SECONDS = TimeUnit.SECONDS.toMillis(5);
  @NonNull private final JobManager jobManager;

  BaseJobQueuer(@NonNull JobManager jobManager) {
    this.jobManager = jobManager;
  }

  @Override public final void cancel(@NonNull String tag) {
    Timber.w("Cancel all jobs for tag: %s", tag);
    jobManager.cancelAllForTag(tag);
  }

  @CheckResult @NonNull private PersistableBundleCompat createExtras(JobQueuerEntry entry) {
    final PersistableBundleCompat extras = new PersistableBundleCompat();
    extras.putBoolean(KEY_SCREEN, entry.screenOn());
    extras.putLong(KEY_ON_WINDOW, entry.repeatingOnWindow());
    extras.putLong(KEY_OFF_WINDOW, entry.repeatingOffWindow());
    extras.putBoolean(KEY_ONESHOT, entry.oneshot());
    extras.putBoolean(KEY_FIRST_RUN, entry.firstRun());
    return extras;
  }

  @Override public final void queue(@NonNull JobQueuerEntry entry) {
    final PersistableBundleCompat extras = createExtras(entry);
    if (entry.delay() == 0) {
      runInstantJob(entry.tag(), extras);
    } else {
      scheduleJob(entry, extras);
    }
  }

  private void scheduleJob(@NonNull JobQueuerEntry entry, @NonNull PersistableBundleCompat extras) {
    long startTime = TimeUnit.SECONDS.toMillis(entry.delay());
    final long window;

    // If the delay is less than 15 seconds, set the window to +5 seconds
    if (startTime < WINDOW_SMALL) {
      window = FIVE_SECONDS;
    } else {
      window = FIVE_SECONDS << 1;
    }
    new JobRequest.Builder(entry.tag()).setExecutionWindow(startTime, startTime + window)
        .setPersisted(false)
        .setExtras(extras)
        .setRequiresCharging(false)
        .setRequiresDeviceIdle(false)
        .build()
        .schedule();
  }

  @Override public final void queueRepeating(@NonNull JobQueuerEntry entry) {
    final PersistableBundleCompat extras = createExtras(entry);
    new JobRequest.Builder(entry.tag()).setPeriodic(TimeUnit.SECONDS.toMillis(entry.delay()))
        .setPersisted(false)
        .setExtras(extras)
        .setRequiresCharging(false)
        .setRequiresDeviceIdle(false)
        .build()
        .schedule();
  }

  abstract void runInstantJob(@NonNull String tag, @NonNull PersistableBundleCompat extras);
}
