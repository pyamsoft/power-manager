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

package com.pyamsoft.powermanager.dagger.main;

import android.support.annotation.NonNull;
import com.pyamsoft.powermanager.app.main.MainPresenter;
import com.pyamsoft.powermanager.app.observer.PermissionObserver;
import com.pyamsoft.pydroidrx.SchedulerPresenter;
import com.pyamsoft.pydroidrx.SubscriptionHelper;
import javax.inject.Inject;
import rx.Scheduler;
import rx.Subscription;
import rx.subscriptions.Subscriptions;
import timber.log.Timber;

class MainPresenterImpl extends SchedulerPresenter<MainPresenter.View> implements MainPresenter {

  @NonNull private final MainInteractor interactor;
  @NonNull private final PermissionObserver rootPermissionObserver;
  @SuppressWarnings("WeakerAccess") @NonNull Subscription subscription = Subscriptions.empty();
  @SuppressWarnings("WeakerAccess") @NonNull Subscription rootSubscription = Subscriptions.empty();

  @Inject MainPresenterImpl(@NonNull MainInteractor interactor, @NonNull Scheduler observeScheduler,
      @NonNull Scheduler subscribeScheduler, @NonNull PermissionObserver rootPermissionObserver) {
    super(observeScheduler, subscribeScheduler);
    this.interactor = interactor;
    this.rootPermissionObserver = rootPermissionObserver;
  }

  @Override protected void onBind() {
    super.onBind();
    SubscriptionHelper.unsubscribe(subscription);
    subscription = interactor.isStartWhenOpen()
        .subscribeOn(getSubscribeScheduler())
        .observeOn(getObserveScheduler())
        .subscribe(start -> {
              if (start) {
                getView(View::onServiceEnabledWhenOpen);
              }
            }, throwable -> Timber.e(throwable, "onError isStartWhenOpen"),
            () -> SubscriptionHelper.unsubscribe(subscription));

    SubscriptionHelper.unsubscribe(rootSubscription);
    rootSubscription = rootPermissionObserver.hasPermission()
        .subscribeOn(getSubscribeScheduler())
        .observeOn(getObserveScheduler())
        .subscribe(hasPermission -> {
              if (!hasPermission) {
                getView(View::explainRootRequirement);
              }
            }, throwable -> Timber.e(throwable, "onError checking root permission"),
            () -> SubscriptionHelper.unsubscribe(rootSubscription));
  }

  @Override protected void onUnbind() {
    super.onUnbind();
    SubscriptionHelper.unsubscribe(subscription, rootSubscription);
  }
}
