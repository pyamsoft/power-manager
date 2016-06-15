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

import android.content.ContentResolver;
import android.content.Context;
import android.support.annotation.NonNull;
import com.birbit.android.jobqueue.Params;
import com.pyamsoft.powermanager.PowerManagerPreferences;
import javax.inject.Inject;
import timber.log.Timber;

final class ManagerInteractorSync extends ManagerInteractorBase {

  @NonNull private static final String TAG = "sync_manager_job";
  @NonNull private final PowerManagerPreferences preferences;
  @NonNull private final Context appContext;

  @Inject ManagerInteractorSync(@NonNull PowerManagerPreferences preferences,
      @NonNull Context context) {
    this.appContext = context.getApplicationContext();
    this.preferences = preferences;
  }

  @Override public void cancelJobs() {
    cancelJobs(TAG);
  }

  @Override public boolean isEnabled() {
    return ContentResolver.getMasterSyncAutomatically();
  }

  @Override public boolean isManaged() {
    return preferences.isSyncManaged();
  }

  @Override public long getDelayTime() {
    return preferences.getMasterSyncDelay();
  }

  @NonNull @Override public DeviceJob createEnableJob(long delayTime) {
    return new EnableJob(appContext, delayTime);
  }

  @NonNull @Override public DeviceJob createDisableJob(long delayTime) {
    return new DisableJob(appContext, delayTime);
  }

  static final class EnableJob extends Job {

    protected EnableJob(@NonNull Context context, long delayTime) {
      super(context, new Params(PRIORITY).setGroupId(ManagerInteractorSync.TAG)
          .setDelayMs(delayTime)
          .setRequiresNetwork(false)
          .setSingleId(ManagerInteractorSync.TAG)
          .singleInstanceBy(ManagerInteractorSync.TAG), JOB_TYPE_ENABLE);
    }
  }

  static final class DisableJob extends Job {

    protected DisableJob(@NonNull Context context, long delayTime) {
      super(context, new Params(PRIORITY).setGroupId(ManagerInteractorSync.TAG)
          .setDelayMs(delayTime)
          .setRequiresNetwork(false)
          .setSingleId(ManagerInteractorSync.TAG)
          .singleInstanceBy(ManagerInteractorSync.TAG), JOB_TYPE_DISABLE);
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