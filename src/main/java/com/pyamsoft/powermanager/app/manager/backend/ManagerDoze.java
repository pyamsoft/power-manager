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

package com.pyamsoft.powermanager.app.manager.backend;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import com.birbit.android.jobqueue.TagConstraint;
import com.pyamsoft.powermanager.PowerManager;
import com.pyamsoft.powermanager.app.base.SchedulerPresenter;
import com.pyamsoft.powermanager.dagger.manager.backend.DozeJob;
import com.pyamsoft.powermanager.dagger.manager.backend.ManagerDozeInteractor;
import javax.inject.Inject;
import javax.inject.Named;
import rx.Observable;
import rx.Scheduler;
import rx.Subscription;
import rx.subscriptions.Subscriptions;
import timber.log.Timber;

public class ManagerDoze extends SchedulerPresenter<ManagerDoze.DozeView> implements Manager {

  //@NonNull public static final String GRANT_PERMISSION_COMMAND =
  //    "adb -d shell pm grant com.pyamsoft.powermanager android.permission.DUMP";

  @NonNull private final ManagerDozeInteractor interactor;
  @NonNull private Subscription subscription = Subscriptions.empty();

  @Inject public ManagerDoze(@NonNull ManagerDozeInteractor interactor,
      @NonNull @Named("io") Scheduler ioScheduler,
      @NonNull @Named("main") Scheduler mainScheduler) {
    super(mainScheduler, ioScheduler);
    this.interactor = interactor;
  }

  @CheckResult public static boolean isDozeAvailable() {
    return Build.VERSION.SDK_INT == Build.VERSION_CODES.M;
  }

  @CheckResult public static boolean checkDumpsysPermission(@NonNull Context context) {
    return context.getApplicationContext().checkCallingOrSelfPermission(Manifest.permission.DUMP)
        == PackageManager.PERMISSION_GRANTED;
  }

  @Override public void enable() {
    unsubSubscription();
    subscription = Observable.defer(() -> {
      if (!isDozeAvailable()) {
        Timber.e("Doze is not available on this platform");
        return Observable.empty();
      }

      PowerManager.getInstance().getJobManager().cancelJobs(TagConstraint.ANY, DozeJob.DOZE_TAG);
      return Observable.just(0);
    }).subscribeOn(getSubscribeScheduler()).observeOn(getObserveScheduler()).subscribe(delay -> {
      PowerManager.getInstance().getJobManager().addJobInBackground(new DozeJob.EnableJob());
    }, throwable -> {
      Timber.e(throwable, "onError");
    }, this::unsubSubscription);
  }

  private void unsubSubscription() {
    if (!subscription.isUnsubscribed()) {
      subscription.unsubscribe();
    }
  }

  @Override public void disable(boolean charging) {
    unsubSubscription();
    subscription = Observable.defer(() -> {
      if (!isDozeAvailable()) {
        Timber.e("Doze is not available on this platform");
        return Observable.empty();
      }

      PowerManager.getInstance().getJobManager().cancelJobs(TagConstraint.ANY, DozeJob.DOZE_TAG);
      return interactor.getDozeDelay();
    }).subscribeOn(getSubscribeScheduler()).observeOn(getObserveScheduler()).subscribe(delay -> {
      PowerManager.getInstance().getJobManager().addJobInBackground(new DozeJob.DisableJob(delay));
    }, throwable -> {
      Timber.e(throwable, "onError");
    }, this::unsubSubscription);
  }

  @Override public void cleanup() {
    unsubSubscription();
  }

  public interface DozeView {

    void onDumpSysPermissionSuccess();

    void onDumpSysPermissionError();
  }
}
