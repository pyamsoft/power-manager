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

package com.pyamsoft.powermanager.main;

import android.support.annotation.NonNull;
import com.pyamsoft.powermanager.model.states.PermissionObserver;
import com.pyamsoft.pydroid.helper.DisposableHelper;
import com.pyamsoft.pydroid.presenter.SchedulerPresenter;
import io.reactivex.Observable;
import io.reactivex.Scheduler;
import io.reactivex.disposables.Disposable;
import io.reactivex.disposables.Disposables;
import javax.inject.Inject;
import javax.inject.Named;
import timber.log.Timber;

class MainPresenter extends SchedulerPresenter {

  @SuppressWarnings("WeakerAccess") @NonNull final MainInteractor interactor;
  @SuppressWarnings("WeakerAccess") @NonNull final PermissionObserver rootPermissionObserver;
  @NonNull private Disposable subscription = Disposables.empty();
  @NonNull private Disposable rootDisposable = Disposables.empty();

  @Inject MainPresenter(@NonNull MainInteractor interactor,
      @NonNull @Named("obs") Scheduler obsScheduler, @NonNull @Named("sub") Scheduler subScheduler,
      @NonNull @Named("obs_root_permission") PermissionObserver rootPermissionObserver) {
    super(obsScheduler, subScheduler);
    this.interactor = interactor;
    this.rootPermissionObserver = rootPermissionObserver;
  }

  @Override protected void onStop() {
    super.onStop();
    subscription = DisposableHelper.dispose(subscription);
    rootDisposable = DisposableHelper.dispose(rootDisposable);
  }

  public void runStartupHooks(@NonNull StartupCallback callback) {
    startServiceWhenOpen(callback);
    checkForRoot(callback);
  }

  private void startServiceWhenOpen(@NonNull StartupCallback callback) {
    subscription = DisposableHelper.dispose(subscription);
    subscription = interactor.isStartWhenOpen()
        .subscribeOn(getSubscribeScheduler())
        .observeOn(getObserveScheduler())
        .subscribe(start -> {
          if (start) {
            callback.onServiceEnabledWhenOpen();
          }
        }, throwable -> Timber.e(throwable, "onError isStartWhenOpen"));
  }

  private void checkForRoot(@NonNull StartupCallback callback) {
    rootDisposable = DisposableHelper.dispose(rootDisposable);
    rootDisposable = Observable.fromCallable(rootPermissionObserver::hasPermission)
        .subscribeOn(getSubscribeScheduler())
        .observeOn(getObserveScheduler())
        .subscribe(hasPermission -> {
          if (!hasPermission) {
            interactor.missingRootPermission();
            callback.explainRootRequirement();
          }
        }, throwable -> Timber.e(throwable, "onError checking root permission"));
  }

  interface StartupCallback {

    void onServiceEnabledWhenOpen();

    void explainRootRequirement();
  }
}
