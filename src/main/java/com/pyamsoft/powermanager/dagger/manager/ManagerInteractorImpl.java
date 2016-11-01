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

import android.support.annotation.CallSuper;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import com.birbit.android.jobqueue.Job;
import com.birbit.android.jobqueue.TagConstraint;
import com.pyamsoft.powermanager.PowerManagerPreferences;
import com.pyamsoft.powermanager.app.modifier.BooleanInterestModifier;
import com.pyamsoft.powermanager.app.observer.BooleanInterestObserver;
import com.pyamsoft.powermanager.app.wrapper.JobSchedulerCompat;
import com.pyamsoft.powermanager.dagger.job.JobHelper;
import rx.Observable;
import timber.log.Timber;

abstract class ManagerInteractorImpl implements ManagerInteractor {

  @SuppressWarnings("WeakerAccess") @NonNull final PowerManagerPreferences preferences;
  @SuppressWarnings("WeakerAccess") @NonNull final BooleanInterestObserver manageObserver;
  @SuppressWarnings("WeakerAccess") @NonNull final BooleanInterestObserver stateObserver;
  @SuppressWarnings("WeakerAccess") @NonNull final BooleanInterestModifier stateModifier;
  @SuppressWarnings("WeakerAccess") @NonNull final JobSchedulerCompat jobManager;
  @SuppressWarnings("WeakerAccess") boolean originalStateEnabled;

  ManagerInteractorImpl(@NonNull JobSchedulerCompat jobManager,
      @NonNull PowerManagerPreferences preferences, @NonNull BooleanInterestObserver manageObserver,
      @NonNull BooleanInterestModifier stateModifier,
      @NonNull BooleanInterestObserver stateObserver) {
    this.jobManager = jobManager;
    this.manageObserver = manageObserver;
    this.stateModifier = stateModifier;
    this.stateObserver = stateObserver;
    originalStateEnabled = false;
    this.preferences = preferences;
  }

  @Override public void destroy() {
    final String jobTag = getJobTag();
    Timber.d("Cancel jobs in background with tag: %s", jobTag);
    jobManager.cancelJobsInBackground(TagConstraint.ANY, jobTag);
  }

  @Override @NonNull @CheckResult public Observable<Boolean> cancelJobs() {
    return Observable.defer(() -> {
      final String jobTag = getJobTag();
      Timber.d("Cancel jobs in with tag: %s", jobTag);
      jobManager.cancelJobs(TagConstraint.ANY, jobTag);
      return Observable.just(true);
    });
  }

  @CallSuper @NonNull @Override public Observable<Boolean> isOriginalStateEnabled() {
    return Observable.defer(() -> {
      Timber.d("Original state: %s", originalStateEnabled);
      return Observable.just(originalStateEnabled);
    });
  }

  @CallSuper @Override public void setOriginalStateEnabled(boolean enabled) {
    Timber.d("Set original state: %s", enabled);
    originalStateEnabled = enabled;
  }

  @CallSuper @Override public void queueEnableJob() {
    Timber.d("Queue new enable job");
    final Job job =
        JobHelper.createManagerEnableJob(jobManager, getJobTag(), stateObserver, stateModifier);
    jobManager.addJobInBackground(job);
  }

  @CallSuper @Override public void queueDisableJob() {
    Timber.d("Queue new disable job");
    final Job job =
        JobHelper.createManagerDisableJob(jobManager, getJobTag(), getDelayTime() * 1000L,
            isPeriodic(), getPeriodicEnableTime(), getPeriodicDisableTime(), stateObserver,
            stateModifier);
    jobManager.addJobInBackground(job);
  }

  @CallSuper @CheckResult @NonNull PowerManagerPreferences getPreferences() {
    return preferences;
  }

  @CallSuper @NonNull @CheckResult JobSchedulerCompat getJobManager() {
    return jobManager;
  }

  @CallSuper @NonNull @Override public Observable<Boolean> isManaged() {
    return Observable.defer(() -> Observable.just(manageObserver.is()));
  }

  @CallSuper @NonNull @Override public Observable<Boolean> isEnabled() {
    return Observable.defer(() -> Observable.just(stateObserver.is()));
  }

  @CheckResult protected abstract long getDelayTime();

  @CheckResult protected abstract boolean isPeriodic();

  @CheckResult protected abstract long getPeriodicEnableTime();

  @CheckResult protected abstract long getPeriodicDisableTime();

  @CheckResult @NonNull protected abstract String getJobTag();
}
