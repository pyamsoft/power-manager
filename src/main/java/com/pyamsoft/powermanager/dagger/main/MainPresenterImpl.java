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
import javax.inject.Inject;
import rx.Scheduler;
import rx.Subscription;
import rx.subscriptions.Subscriptions;
import timber.log.Timber;

class MainPresenterImpl extends SchedulerPresenter<MainPresenter.View> implements MainPresenter {

  @NonNull private final MainInteractor interactor;
  @NonNull private final PermissionObserver rootPermissionObserver;
  @NonNull private Subscription subscription = Subscriptions.empty();
  @NonNull private Subscription rootSubscription = Subscriptions.empty();

  @Inject MainPresenterImpl(@NonNull MainInteractor interactor, @NonNull Scheduler observeScheduler,
      @NonNull Scheduler subscribeScheduler, @NonNull PermissionObserver rootPermissionObserver) {
    super(observeScheduler, subscribeScheduler);
    this.interactor = interactor;
    this.rootPermissionObserver = rootPermissionObserver;
  }

  @SuppressWarnings("WeakerAccess") void unsub() {
    if (!subscription.isUnsubscribed()) {
      subscription.unsubscribe();
    }
  }

  @SuppressWarnings("WeakerAccess") void unsubRoot() {
    if (!rootSubscription.isUnsubscribed()) {
      rootSubscription.unsubscribe();
    }
  }

  @Override protected void onBind() {
    super.onBind();
    unsub();
    subscription = interactor.isStartWhenOpen()
        .subscribeOn(getSubscribeScheduler())
        .observeOn(getObserveScheduler())
        .subscribe(start -> {
          if (start) {
            getView(View::onServiceEnabledWhenOpen);
          }
        }, throwable -> Timber.e(throwable, "onError isStartWhenOpen"), this::unsub);

    unsubRoot();
    rootSubscription = rootPermissionObserver.hasPermission()
        .subscribeOn(getSubscribeScheduler())
        .observeOn(getObserveScheduler())
        .subscribe(hasPermission -> {
          if (!hasPermission) {
            getView(View::explainRootRequirement);
          }
        }, throwable -> Timber.e(throwable, "onError checking root permission"), this::unsubRoot);
  }

  @Override protected void onUnbind() {
    super.onUnbind();
    unsub();
    unsubRoot();
  }
}
