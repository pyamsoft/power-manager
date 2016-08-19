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

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import com.birbit.android.jobqueue.TagConstraint;
import com.pyamsoft.powermanager.PowerManagerPreferences;
import com.pyamsoft.powermanager.Singleton;
import com.pyamsoft.powermanager.dagger.base.BaseJob;
import rx.Observable;
import timber.log.Timber;

abstract class ManagerInteractorBase implements ManagerInteractor {

  @NonNull private final PowerManagerPreferences preferences;
  @NonNull private final Context appContext;
  private boolean originalState = false;

  ManagerInteractorBase(@NonNull Context context, @NonNull PowerManagerPreferences preferences) {
    this.preferences = preferences;
    this.appContext = context.getApplicationContext();
  }

  @Override public final void setOriginalState(boolean originalState) {
    this.originalState = originalState;
  }

  @NonNull @Override public Observable<Boolean> isOriginalState() {
    return Observable.defer(() -> Observable.just(originalState));
  }

  @CheckResult @NonNull abstract Observable<Long> getPeriodicEnableTime();

  @CheckResult @NonNull abstract Observable<Long> getPeriodicDisableTime();

  @Override public void queueJob(@NonNull BaseJob job) {
    Singleton.Jobs.with(getAppContext()).addJobInBackground(job);
  }

  @NonNull @CheckResult final Observable<ManagerInteractor> cancelJobs(@NonNull String tag) {
    return Observable.defer(() -> {
      Timber.d("Attempt job cancel %s", tag);
      Singleton.Jobs.with(getAppContext()).cancelJobs(TagConstraint.ANY, tag);
      return Observable.just(this);
    });
  }

  @NonNull @CheckResult protected final Observable<Boolean> hasDumpSysPermission() {
    return Observable.defer(() -> Observable.just(
        appContext.getApplicationContext().checkCallingOrSelfPermission(Manifest.permission.DUMP)
            == PackageManager.PERMISSION_GRANTED));
  }

  @CheckResult @NonNull final Context getAppContext() {
    return appContext;
  }

  @NonNull @CheckResult final PowerManagerPreferences getPreferences() {
    return preferences;
  }

  @Override @NonNull public Observable<Boolean> isDozeAvailable() {
    return Observable.defer(() -> Observable.just(Build.VERSION.SDK_INT == Build.VERSION_CODES.M));
  }

  @NonNull @Override public Observable<Boolean> isDozeEnabled() {
    return isDozeAvailable().flatMap(
        available -> hasDumpSysPermission().map(hasPermission -> hasPermission && available))
        .map(available -> available && preferences.isDozeEnabled());
  }

  @NonNull @Override public Observable<Boolean> isDozeExclusive() {
    // TODO replace with setting
    return Observable.defer(() -> Observable.just(false));
  }

  @NonNull @Override public Observable<Boolean> isDozeIgnoreCharging() {
    return Observable.defer(() -> Observable.just(getPreferences().isIgnoreChargingDoze()));
  }
}
