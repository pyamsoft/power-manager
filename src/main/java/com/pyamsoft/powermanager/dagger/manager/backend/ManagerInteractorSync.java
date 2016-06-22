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

package com.pyamsoft.powermanager.dagger.manager.backend;

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

  @NonNull @Override public DeviceJob createEnableJob(long delayTime, boolean periodic) {
    return new EnableJob(appContext, delayTime, isOriginalStateEnabled(), periodic);
  }

  @NonNull @Override public DeviceJob createDisableJob(long delayTime, boolean periodic) {
    return new DisableJob(appContext, delayTime, isOriginalStateEnabled(), periodic);
  }

  static final class EnableJob extends Job {

    protected EnableJob(@NonNull Context context, long delayTime, boolean originalState,
        boolean periodic) {
      super(context, new Params(PRIORITY).setDelayMs(delayTime), JOB_TYPE_ENABLE, originalState,
          periodic);
    }
  }

  static final class DisableJob extends Job {

    protected DisableJob(@NonNull Context context, long delayTime, boolean originalState,
        boolean periodic) {
      super(context, new Params(PRIORITY).setDelayMs(delayTime), JOB_TYPE_DISABLE, originalState,
          periodic);
    }
  }

  static abstract class Job extends DeviceJob {

    protected Job(@NonNull Context context, @NonNull Params params, int jobType,
        boolean originalState, boolean periodic) {
      super(context, params.addTags(ManagerInteractorSync.TAG), jobType, originalState, periodic);
    }

    @Override protected void callEnable() {
      Timber.d("Enable sync");
      ContentResolver.setMasterSyncAutomatically(true);
    }

    @Override protected void callDisable() {
      Timber.d("Disable sync");
      ContentResolver.setMasterSyncAutomatically(false);
    }

    @Override protected boolean isEnabled() {
      Timber.d("isSyncEnabled");
      return ContentResolver.getMasterSyncAutomatically();
    }

    @Override protected DeviceJob periodicDisableJob() {
      Timber.d("Periodic sync disable job");
      return new DisableJob(getContext(), 10 * 1000, isOriginalState(), true);
    }

    @Override protected DeviceJob periodicEnableJob() {
      Timber.d("Periodic sync enable job");
      return new EnableJob(getContext(), 10 * 1000, isOriginalState(), true);
    }
  }
}
