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

package com.pyamsoft.powermanager.dagger.service;

import android.support.annotation.NonNull;
import com.pyamsoft.powermanager.app.bus.FullNotificationBus;
import com.pyamsoft.powermanager.app.service.FullNotificationPresenter;
import com.pyamsoft.pydroid.base.presenter.SchedulerPresenter;
import javax.inject.Inject;
import javax.inject.Named;
import rx.Scheduler;
import rx.Subscription;
import rx.subscriptions.Subscriptions;
import timber.log.Timber;

class FullNotificationPresenterImpl
    extends SchedulerPresenter<FullNotificationPresenter.FullNotificationView>
    implements FullNotificationPresenter {

  @NonNull Subscription dismissSubscription = Subscriptions.empty();

  @Inject FullNotificationPresenterImpl(@NonNull @Named("main") Scheduler observeScheduler,
      @NonNull @Named("io") Scheduler subscribeScheduler) {
    super(observeScheduler, subscribeScheduler);
  }

  @Override protected void onBind(@NonNull FullNotificationView view) {
    super.onBind(view);
    registerOnDismissBus(view);
  }

  @Override protected void onUnbind(@NonNull FullNotificationView view) {
    super.onUnbind(view);
    unregisterFromDismissBus();
  }

  void registerOnDismissBus(@NonNull FullNotificationView view) {
    unregisterFromDismissBus();
    dismissSubscription = FullNotificationBus.get()
        .register()
        .subscribeOn(getSubscribeScheduler())
        .observeOn(getObserveScheduler())
        .subscribe(dismissEvent -> {
          view.onDismissEvent();
        }, throwable -> {
          // TODO
          Timber.e(throwable, "onError");
        });
  }

  void unregisterFromDismissBus() {
    if (!dismissSubscription.isUnsubscribed()) {
      dismissSubscription.unsubscribe();
    }
  }
}