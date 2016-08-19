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

import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import com.pyamsoft.pydroid.base.presenter.SchedulerPresenter;
import com.pyamsoft.pydroid.tool.RxBus;
import javax.inject.Inject;
import javax.inject.Named;
import rx.Scheduler;
import rx.Subscription;
import rx.subscriptions.Subscriptions;
import timber.log.Timber;

public class FullNotificationPresenter
    extends SchedulerPresenter<FullNotificationPresenter.FullNotificationView> {

  @NonNull private Subscription dismissSubscription = Subscriptions.empty();

  @Inject FullNotificationPresenter(@NonNull @Named("main") Scheduler observeScheduler,
      @NonNull @Named("io") Scheduler subscribeScheduler) {
    super(observeScheduler, subscribeScheduler);
  }

  @Override protected void onResume(@NonNull FullNotificationView view) {
    super.onResume(view);
    registerOnDismissBus();
  }

  @Override protected void onPause(@NonNull FullNotificationView view) {
    super.onPause(view);
    unregisterFromDismissBus();
  }

  void registerOnDismissBus() {
    dismissSubscription = Bus.get()
        .register()
        .subscribeOn(getSubscribeScheduler())
        .observeOn(getObserveScheduler())
        .subscribe(dismissEvent -> {
          getView().onDismissEvent();
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

  public interface FullNotificationView {

    void onDismissEvent();
  }

  public static final class DismissEvent {

  }

  public static final class Bus extends RxBus<DismissEvent> {

    @NonNull private static final RxBus<DismissEvent> instance = new Bus();

    @CheckResult @NonNull public static RxBus<DismissEvent> get() {
      return instance;
    }
  }
}
