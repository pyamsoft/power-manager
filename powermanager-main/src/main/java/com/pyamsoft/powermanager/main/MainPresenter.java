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
import com.pyamsoft.powermanager.model.PermissionObserver;
import com.pyamsoft.pydroid.helper.SubscriptionHelper;
import com.pyamsoft.pydroid.presenter.Presenter;
import com.pyamsoft.pydroid.presenter.SchedulerPresenter;
import javax.inject.Inject;
import rx.Observable;
import rx.Scheduler;
import rx.Subscription;
import rx.subscriptions.Subscriptions;
import timber.log.Timber;

class MainPresenter extends SchedulerPresenter<Presenter.Empty> {

  @SuppressWarnings("WeakerAccess") @NonNull final MainInteractor interactor;
  @SuppressWarnings("WeakerAccess") @NonNull final PermissionObserver rootPermissionObserver;
  @NonNull private Subscription subscription = Subscriptions.empty();
  @NonNull private Subscription rootSubscription = Subscriptions.empty();

  @Inject MainPresenter(@NonNull MainInteractor interactor, @NonNull Scheduler observeScheduler,
      @NonNull Scheduler subscribeScheduler, @NonNull PermissionObserver rootPermissionObserver) {
    super(observeScheduler, subscribeScheduler);
    this.interactor = interactor;
    this.rootPermissionObserver = rootPermissionObserver;
  }

  @Override protected void onUnbind() {
    super.onUnbind();
    subscription = SubscriptionHelper.unsubscribe(subscription);
    rootSubscription = SubscriptionHelper.unsubscribe(rootSubscription);
  }

  public void runStartupHooks(@NonNull StartupCallback callback) {
    startServiceWhenOpen(callback);
    checkForRoot(callback);
  }

  private void startServiceWhenOpen(@NonNull StartupCallback callback) {
    subscription = SubscriptionHelper.unsubscribe(subscription);
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
    rootSubscription = SubscriptionHelper.unsubscribe(rootSubscription);
    rootSubscription = Observable.fromCallable(rootPermissionObserver::hasPermission)
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
