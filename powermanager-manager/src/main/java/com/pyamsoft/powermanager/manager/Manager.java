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

package com.pyamsoft.powermanager.manager;

import android.support.annotation.CallSuper;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.pyamsoft.pydroid.helper.SchedulerHelper;
import io.reactivex.Scheduler;
import io.reactivex.disposables.CompositeDisposable;
import timber.log.Timber;

public class Manager {

  @SuppressWarnings("WeakerAccess") @NonNull final ManagerInteractor interactor;
  @NonNull private final CompositeDisposable compositeDisposable;
  @NonNull private final Scheduler scheduler;

  Manager(@NonNull ManagerInteractor interactor, @NonNull Scheduler scheduler) {
    this.interactor = interactor;
    this.scheduler = scheduler;
    compositeDisposable = new CompositeDisposable();
    SchedulerHelper.enforceSubscribeScheduler(scheduler);
  }

  @NonNull @CheckResult Scheduler getScheduler() {
    return scheduler;
  }

  public void cancel(@NonNull Runnable onCancel) {
    compositeDisposable.add(
        interactor.cancel().subscribeOn(getScheduler()).observeOn(getScheduler()).subscribe(tag -> {
          Timber.d("Job cancelled: %s", tag);
          onCancel.run();
        }, throwable -> Timber.e(throwable, "onError cancelling manager")));
  }

  public void enable(@Nullable Runnable onEnabled) {
    compositeDisposable.add(
        interactor.queueEnable().subscribeOn(scheduler).observeOn(scheduler).subscribe(tag -> {
          Timber.d("%s: Queued up a new enable job", tag);
          if (onEnabled != null) {
            onEnabled.run();
          }
        }, throwable -> Timber.e(throwable, "%s: onError enable")));
  }

  public void disable(@Nullable Runnable onDisabled) {
    compositeDisposable.add(interactor.queueDisable().
        subscribeOn(scheduler).observeOn(scheduler).subscribe(tag -> {
      Timber.d("%s: Queued up a new disable job", tag);
      if (onDisabled != null) {
        onDisabled.run();
      }
    }, throwable -> Timber.e(throwable, "%s: onError disable")));
  }

  @CallSuper public void cleanup() {
    compositeDisposable.clear();
    interactor.destroy();

    // Reset the device back to its original state when the Service is cleaned up
    enable(null);
  }
}
