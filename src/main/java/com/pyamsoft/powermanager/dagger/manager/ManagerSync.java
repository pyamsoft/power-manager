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

package com.pyamsoft.powermanager.dagger.manager;

import android.app.Application;
import android.content.ContentResolver;
import android.content.Context;
import android.support.annotation.NonNull;
import com.birbit.android.jobqueue.Params;
import com.pyamsoft.powermanager.PowerManager;
import com.pyamsoft.powermanager.PowerManagerPreferences;
import javax.inject.Inject;
import timber.log.Timber;

final class ManagerSync extends ManagerBase {

  @NonNull private static final String TAG = "sync_manager_job";
  @NonNull private final PowerManagerPreferences preferences;

  @Inject ManagerSync(@NonNull PowerManagerPreferences preferences) {
    Timber.d("new ManagerSync");
    this.preferences = preferences;
  }

  @Override public void enable(@NonNull Application application) {
    enable(application, 0);
  }

  @Override public void enable(@NonNull Application application, long time) {
    if (preferences.isSyncManaged()) {
      Timber.d("Queue Sync enable");
      PowerManager.getJobManager(application).addJobInBackground(new EnableJob(application, time));
    } else {
      Timber.w("Sync is not managed");
    }
  }

  @Override public void disable(@NonNull Application application) {
    disable(application, preferences.getMasterSyncDelay() * 1000L);
  }

  @Override public void disable(@NonNull Application application, long time) {
    if (preferences.isSyncManaged()) {
      Timber.d("Queue Sync disable");
      PowerManager.getJobManager(application).addJobInBackground(new DisableJob(application, time));
    } else {
      Timber.w("Sync is not managed");
    }
  }

  @Override public boolean isEnabled() {
    return ContentResolver.getMasterSyncAutomatically();
  }

  static final class EnableJob extends Job {

    protected EnableJob(@NonNull Context context, long delayTime) {
      super(context, new Params(PRIORITY).setGroupId(ManagerSync.TAG)
          .setDelayMs(delayTime)
          .setRequiresNetwork(false)
          .setSingleId(ManagerSync.TAG)
          .singleInstanceBy(ManagerSync.TAG), JOB_TYPE_ENABLE);
    }
  }

  static final class DisableJob extends Job {

    protected DisableJob(@NonNull Context context, long delayTime) {
      super(context, new Params(PRIORITY).setGroupId(ManagerSync.TAG)
          .setDelayMs(delayTime)
          .setRequiresNetwork(false)
          .setSingleId(ManagerSync.TAG)
          .singleInstanceBy(ManagerSync.TAG), JOB_TYPE_DISABLE);
    }
  }

  static abstract class Job extends DeviceJob {

    protected Job(@NonNull Context context, @NonNull Params params, int jobType) {
      super(context, params, jobType);
    }

    @Override protected void enable() {
      Timber.d("Sync job enable");

      if (!ContentResolver.getMasterSyncAutomatically()) {
        Timber.d("Turn on Master Sync");
        ContentResolver.setMasterSyncAutomatically(true);
      } else {
        Timber.e("Master Sync is already off");
      }
    }

    @Override protected void disable() {
      Timber.d("Sync job disable");

      if (ContentResolver.getMasterSyncAutomatically()) {
        Timber.d("Turn off Master Sync");
        ContentResolver.setMasterSyncAutomatically(false);
      } else {
        Timber.e("Master Sync is already off");
      }
    }
  }
}
