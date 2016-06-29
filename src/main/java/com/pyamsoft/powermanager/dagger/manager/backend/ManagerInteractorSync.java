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
import rx.Observable;
import timber.log.Timber;

public final class ManagerInteractorSync extends ManagerInteractorBase {

  @NonNull private static final String TAG = "sync_manager_job";
  @NonNull private final Context appContext;

  @Inject ManagerInteractorSync(@NonNull PowerManagerPreferences preferences,
      @NonNull Context context) {
    super(preferences);
    this.appContext = context.getApplicationContext();
  }

  @Override public void cancelJobs() {
    cancelJobs(TAG);
  }

  @NonNull @Override public Observable<Boolean> isEnabled() {
    return Observable.defer(() -> Observable.just(ContentResolver.getMasterSyncAutomatically()));
  }

  @NonNull @Override public Observable<Boolean> isManaged() {
    return Observable.defer(() -> Observable.just(getPreferences().isSyncManaged()));
  }

  @NonNull @Override public Observable<Boolean> isPeriodic() {
    return Observable.defer(() -> Observable.just(getPreferences().isPeriodicSync()));
  }

  @Override @NonNull public Observable<Long> getPeriodicEnableTime() {
    return Observable.defer(() -> Observable.just(getPreferences().getPeriodicEnableTimeSync()));
  }

  @Override @NonNull public Observable<Long> getPeriodicDisableTime() {
    return Observable.defer(() -> Observable.just(getPreferences().getPeriodicDisableTimeSync()));
  }

  @NonNull @Override public Observable<Long> getDelayTime() {
    return Observable.defer(() -> Observable.just(getPreferences().getMasterSyncDelay()));
  }

  @NonNull @Override public Observable<Boolean> isChargingIgnore() {
    return Observable.defer(() -> Observable.just(getPreferences().isIgnoreChargingSync()));
  }

  @NonNull @Override
  public Observable<DeviceJob> createEnableJob(long delayTime, boolean periodic) {
    return Observable.zip(getPeriodicDisableTime(), getPeriodicEnableTime(),
        isOriginalStateEnabled(),
        (disable, enable, original) -> new EnableJob(appContext, delayTime, original, periodic,
            disable, enable));
  }

  @NonNull @Override
  public Observable<DeviceJob> createDisableJob(long delayTime, boolean periodic) {
    return Observable.zip(getPeriodicDisableTime(), getPeriodicEnableTime(),
        isOriginalStateEnabled(),
        (disable, enable, original) -> new DisableJob(appContext, delayTime, original, periodic,
            disable, enable));
  }

  static final class EnableJob extends Job {

    protected EnableJob(@NonNull Context context, long delayTime, boolean originalState,
        boolean periodic, long periodicDisableTime, long periodicEnableTime) {
      super(context, new Params(PRIORITY).setDelayMs(delayTime), JOB_TYPE_ENABLE, originalState,
          periodic, periodicDisableTime, periodicEnableTime);
    }
  }

  static final class DisableJob extends Job {

    protected DisableJob(@NonNull Context context, long delayTime, boolean originalState,
        boolean periodic, long periodicDisableTime, long periodicEnableTime) {
      super(context, new Params(PRIORITY).setDelayMs(delayTime), JOB_TYPE_DISABLE, originalState,
          periodic, periodicDisableTime, periodicEnableTime);
    }
  }

  static abstract class Job extends DeviceJob {

    protected Job(@NonNull Context context, @NonNull Params params, int jobType,
        boolean originalState, boolean periodic, long periodicDisableTime,
        long periodicEnableTime) {
      super(context, params.addTags(ManagerInteractorSync.TAG), jobType, originalState, periodic,
          periodicDisableTime, periodicEnableTime);
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
      return new DisableJob(getContext(), getPeriodicDisableTime() * 1000, isOriginalState(), true,
          getPeriodicDisableTime(), getPeriodicEnableTime());
    }

    @Override protected DeviceJob periodicEnableJob() {
      Timber.d("Periodic sync enable job");
      return new EnableJob(getContext(), getPeriodicEnableTime() * 1000, isOriginalState(), true,
          getPeriodicDisableTime(), getPeriodicEnableTime());
    }
  }
}
