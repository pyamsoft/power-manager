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

package com.pyamsoft.powermanager.manager;

import android.support.annotation.CallSuper;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.pyamsoft.pydroid.helper.DisposableHelper;
import com.pyamsoft.pydroid.helper.SchedulerHelper;
import io.reactivex.Scheduler;
import io.reactivex.disposables.Disposable;
import io.reactivex.disposables.Disposables;
import timber.log.Timber;

public class Manager {

  @SuppressWarnings("WeakerAccess") @NonNull final ManagerInteractor interactor;
  @NonNull private final Scheduler scheduler;
  @NonNull private Disposable cancelDisposable = Disposables.empty();
  @NonNull private Disposable setDisposable = Disposables.empty();
  @NonNull private Disposable unsetDisposable = Disposables.empty();

  Manager(@NonNull ManagerInteractor interactor, @NonNull Scheduler scheduler) {
    this.interactor = interactor;
    this.scheduler = scheduler;
    SchedulerHelper.enforceSubscribeScheduler(scheduler);
  }

  @NonNull @CheckResult Scheduler getScheduler() {
    return scheduler;
  }

  public void cancel(@NonNull Runnable onCancel) {
    cancelDisposable = DisposableHelper.dispose(cancelDisposable);
    cancelDisposable = interactor.cancelJobs()
        .subscribeOn(getScheduler())
        .observeOn(getScheduler())
        .subscribe(() -> {
          Timber.d("Job cancelled: %s", interactor.getJobTag());
          onCancel.run();
        }, throwable -> Timber.e(throwable, "onError cancelling manager"));
  }

  public void queueSet(@Nullable Runnable onSet) {
    setDisposable = DisposableHelper.dispose(setDisposable);
    setDisposable =
        interactor.queueSet().subscribeOn(scheduler).observeOn(scheduler).subscribe(() -> {
          // Technically can ignore this as if we are here we are non-empty
          // If we are non empty it means we pass the test
          Timber.d("%s: Queued up a new enable job", interactor.getJobTag());
          if (onSet != null) {
            onSet.run();
          }
        }, throwable -> Timber.e(throwable, "%s: onError queueSet", interactor.getJobTag()));
  }

  public void queueUnset(@Nullable Runnable onUnset) {
    unsetDisposable = DisposableHelper.dispose(unsetDisposable);
    unsetDisposable = interactor.queueUnset().
        subscribeOn(scheduler).observeOn(scheduler).subscribe(() -> {
      // Only queue a disable job if the radio is not ignored
      Timber.d("%s: Queued up a new disable job", interactor.getJobTag());
      if (onUnset != null) {
        onUnset.run();
      }
    }, throwable -> Timber.e(throwable, "%s: onError queueUnset", interactor.getJobTag()));
  }

  @CallSuper public void cleanup() {
    interactor.destroy();
    cancelDisposable = DisposableHelper.dispose(cancelDisposable);
    setDisposable = DisposableHelper.dispose(setDisposable);
    unsetDisposable = DisposableHelper.dispose(unsetDisposable);

    // Reset the device back to its original state when the Service is cleaned up
    queueSet(null);
  }
}
