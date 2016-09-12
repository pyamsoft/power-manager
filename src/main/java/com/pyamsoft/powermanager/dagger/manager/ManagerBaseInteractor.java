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
import com.birbit.android.jobqueue.JobManager;
import com.birbit.android.jobqueue.TagConstraint;
import com.pyamsoft.powermanager.PowerManagerPreferences;
import com.pyamsoft.powermanager.app.observer.BooleanInterestObserver;
import com.pyamsoft.powermanager.dagger.wrapper.JobSchedulerCompat;
import rx.Observable;
import timber.log.Timber;

abstract class ManagerBaseInteractor implements ManagerInteractor {

  @SuppressWarnings("WeakerAccess") @NonNull final PowerManagerPreferences preferences;
  @SuppressWarnings("WeakerAccess") @NonNull final BooleanInterestObserver manageObserver;
  @SuppressWarnings("WeakerAccess") @NonNull final BooleanInterestObserver stateObserver;
  @NonNull final JobSchedulerCompat jobManager;
  @NonNull private final Context appContext;
  @SuppressWarnings("WeakerAccess") boolean originalStateEnabled;

  ManagerBaseInteractor(@NonNull JobSchedulerCompat jobManager, @NonNull Context context,
      @NonNull PowerManagerPreferences preferences, @NonNull BooleanInterestObserver manageObserver,
      @NonNull BooleanInterestObserver stateObserver) {
    this.jobManager = jobManager;
    this.manageObserver = manageObserver;
    this.stateObserver = stateObserver;
    this.appContext = context.getApplicationContext();
    originalStateEnabled = false;
    this.preferences = preferences;
  }

  void destroy(@NonNull String jobTag) {
    Timber.d("Cancel jobs in background with tag: %s", jobTag);
    jobManager.cancelJobsInBackground(TagConstraint.ANY, jobTag);
  }

  @NonNull @CheckResult Observable<Boolean> cancelJobs(@NonNull String jobTag) {
    return Observable.defer(() -> {
      Timber.d("Cancel jobs in with tag: %s", jobTag);
      jobManager.cancelJobs(TagConstraint.ANY, jobTag);
      return Observable.just(true);
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
    jobManager.addJobInBackground(createEnableJob());
  }

  @Override public void queueDisableJob() {
    Timber.d("Queue new disable job");
    jobManager.addJobInBackground(createDisableJob());
  }

  @SuppressWarnings("WeakerAccess") @CheckResult @NonNull Context getAppContext() {
    return appContext.getApplicationContext();
  }

  @CheckResult @NonNull PowerManagerPreferences getPreferences() {
    return preferences;
  }

  @NonNull @CheckResult JobSchedulerCompat getJobManager() {
    return jobManager;
  }

  @NonNull @Override public Observable<Boolean> isManaged() {
    return Observable.defer(() -> Observable.just(manageObserver.is()));
  }

  @NonNull @Override public Observable<Boolean> isEnabled() {
    return Observable.defer(() -> Observable.just(stateObserver.is()));
  }

  @CheckResult @NonNull protected abstract Job createEnableJob();

  @CheckResult @NonNull protected abstract Job createDisableJob();
}
