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

package com.pyamsoft.powermanager.service;

import android.app.Notification;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.pyamsoft.pydroid.helper.SubscriptionHelper;
import com.pyamsoft.pydroid.presenter.Presenter;
import com.pyamsoft.pydroid.presenter.SchedulerPresenter;
import javax.inject.Inject;
import javax.inject.Named;
import rx.Observable;
import rx.Scheduler;
import rx.Subscription;
import rx.subscriptions.Subscriptions;
import timber.log.Timber;

class ForegroundPresenter extends SchedulerPresenter<Presenter.Empty> {

  @SuppressWarnings("WeakerAccess") @NonNull final ForegroundInteractor interactor;
  @NonNull private Subscription notificationSubscription = Subscriptions.empty();
  @NonNull private Subscription createSubscription = Subscriptions.empty();

  @Inject ForegroundPresenter(@NonNull ForegroundInteractor interactor,
      @Named("obs") Scheduler obsScheduler, @Named("sub") Scheduler subScheduler) {
    super(obsScheduler, subScheduler);
    this.interactor = interactor;
  }

  @Override protected void onBind(@Nullable Empty view) {
    super.onBind(view);
    createSubscription = SubscriptionHelper.unsubscribe(createSubscription);
    createSubscription = Observable.fromCallable(() -> {
      interactor.create();
      return Boolean.TRUE;
    })
        .subscribeOn(getSubscribeScheduler())
        .observeOn(getObserveScheduler())
        .subscribe(success -> Timber.d("Interactor was created"),
            throwable -> Timber.e(throwable, "Error creating interactor"));
  }

  @Override protected void onUnbind() {
    super.onUnbind();
    interactor.destroy();
    notificationSubscription = SubscriptionHelper.unsubscribe(notificationSubscription);
    createSubscription = SubscriptionHelper.unsubscribe(createSubscription);
  }

  public void startNotification(@NonNull NotificationCallback callback) {
    notificationSubscription = SubscriptionHelper.unsubscribe(notificationSubscription);
    notificationSubscription = interactor.createNotification()
        .subscribeOn(getSubscribeScheduler())
        .observeOn(getObserveScheduler())
        .subscribe(callback::onStartNotificationInForeground, throwable -> {
          Timber.e(throwable, "onError");
          // TODO handle error
        });
  }

  /**
   * Trigger interval is only read on interactor.create()
   *
   * Restart it by destroying and then re-creating the interactor
   */
  public void restartTriggerAlarm() {
    interactor.destroy();
    interactor.create();
  }

  public void setForegroundState(boolean enable) {
    interactor.setServiceEnabled(enable);
  }

  interface NotificationCallback {

    void onStartNotificationInForeground(@NonNull Notification notification);
  }
}
