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
import com.pyamsoft.powermanager.app.service.ForegroundPresenter;
import com.pyamsoft.pydroid.presenter.SchedulerPresenter;
import javax.inject.Inject;
import javax.inject.Named;
import rx.Scheduler;
import rx.Subscription;
import rx.subscriptions.Subscriptions;
import timber.log.Timber;

class ForegroundPresenterImpl extends SchedulerPresenter<ForegroundPresenter.ForegroundProvider>
    implements ForegroundPresenter {

  @NonNull private final ForegroundInteractor interactor;

  @NonNull private Subscription notificationSubscription = Subscriptions.empty();

  @Inject ForegroundPresenterImpl(@NonNull ForegroundInteractor interactor,
      @NonNull @Named("obs") Scheduler obsScheduler,
      @NonNull @Named("io") Scheduler subScheduler) {
    super(obsScheduler, subScheduler);
    this.interactor = interactor;
  }

  @Override protected void onBind() {
    super.onBind();
    interactor.create();
  }

  @Override protected void onUnbind() {
    super.onUnbind();
    interactor.destroy();
    unsubNotification();
  }

  @SuppressWarnings("WeakerAccess") void unsubNotification() {
    if (!notificationSubscription.isUnsubscribed()) {
      notificationSubscription.unsubscribe();
    }
  }

  @Override public void onStartNotification() {
    unsubNotification();
    notificationSubscription = interactor.createNotification()
        .subscribeOn(getSubscribeScheduler())
        .observeOn(getObserveScheduler())
        .subscribe(notification -> {
          getView(
              foregroundProvider -> foregroundProvider.startNotificationInForeground(notification));
        }, throwable -> {
          Timber.e(throwable, "onError");
          // TODO handle error
        });
  }
}
