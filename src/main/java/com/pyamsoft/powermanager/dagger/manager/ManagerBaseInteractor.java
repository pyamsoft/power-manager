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

import android.content.Context;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import com.birbit.android.jobqueue.Job;
import com.birbit.android.jobqueue.TagConstraint;
import com.pyamsoft.powermanager.PowerManagerPreferences;
import com.pyamsoft.powermanager.Singleton;
import rx.Observable;
import timber.log.Timber;

abstract class ManagerBaseInteractor implements ManagerInteractor {

  @NonNull private final Context appContext;
  @NonNull private final PowerManagerPreferences preferences;
  private boolean originalStateEnabled;

  ManagerBaseInteractor(@NonNull Context context, @NonNull PowerManagerPreferences preferences) {
    this.appContext = context.getApplicationContext();
    originalStateEnabled = false;
    this.preferences = preferences;
  }

  void destroy(@NonNull String jobTag) {
    Timber.d("Cancel jobs in background with tag: %s", jobTag);
    Singleton.Jobs.with(appContext).cancelJobsInBackground(null, TagConstraint.ANY, jobTag);
  }

  @NonNull @CheckResult Observable<Boolean> cancelJobs(@NonNull String jobTag) {
    return Observable.defer(() -> {
      Timber.d("Cancel jobs in with tag: %s", jobTag);
      return Observable.just(Singleton.Jobs.with(appContext)
          .cancelJobs(TagConstraint.ANY, jobTag)
          .getFailedToCancel()
          .isEmpty());
    });
  }

  @NonNull @Override public Observable<Boolean> isOriginalStateEnabled() {
    return Observable.defer(() -> {
      Timber.d("Original state: %s", originalStateEnabled);
      return Observable.just(originalStateEnabled);
    });
  }

  @Override public void setOriginalStateEnabled(boolean enabled) {
    Timber.d("Set original state: %s", enabled);
    originalStateEnabled = enabled;
  }

  @Override public void queueEnableJob() {
    Timber.d("Queue new enable job");
    Singleton.Jobs.with(appContext).addJobInBackground(createEnableJob());
  }

  @Override public void queueDisableJob() {
    Timber.d("Queue new disable job");
    Singleton.Jobs.with(appContext).addJobInBackground(createDisableJob());
  }

  @CheckResult @NonNull Context getAppContext() {
    return appContext.getApplicationContext();
  }

  @CheckResult @NonNull PowerManagerPreferences getPreferences() {
    return preferences;
  }

  @CheckResult @NonNull protected abstract Job createEnableJob();

  @CheckResult @NonNull protected abstract Job createDisableJob();
}
