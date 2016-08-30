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
import com.pyamsoft.pydroid.base.presenter.SchedulerPresenter;
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
      @NonNull @Named("main") Scheduler mainScheduler,
      @NonNull @Named("io") Scheduler ioScheduler) {
    super(mainScheduler, ioScheduler);
    this.interactor = interactor;
  }

  @Override protected void onBind(@NonNull ForegroundProvider view) {
    super.onBind(view);
    interactor.create();
  }

  @Override protected void onUnbind(@NonNull ForegroundProvider view) {
    super.onUnbind(view);
    interactor.destroy();
    unsubNotification();
  }

  @SuppressWarnings("WeakerAccess") void unsubNotification() {
    if (!notificationSubscription.isUnsubscribed()) {
      notificationSubscription.unsubscribe();
    }
  }

  @Override public void onStartNotification(boolean explicit) {
    unsubNotification();
    notificationSubscription = interactor.createNotification(explicit)
        .subscribeOn(getSubscribeScheduler())
        .observeOn(getObserveScheduler())
        .subscribe(notification -> {
          getView().startNotificationInForeground(notification);
        }, throwable -> {
          Timber.e(throwable, "onError");
          // TODO handle error
        });
  }

  @Override public void updateWearableAction() {
    interactor.updateWearablePreferenceStatus();
  }

  @Override public void updateWifiAction() {
    interactor.updateWifiPreferenceStatus();
  }

  @Override public void updateDataAction() {
    interactor.updateDataPreferenceStatus();
  }

  @Override public void updateBluetoothAction() {
    interactor.updateBluetoothPreferenceStatus();
  }

  @Override public void updateSyncAction() {
    interactor.updateSyncPreferenceStatus();
  }

  @Override public void updateDozeAction() {
    interactor.updateDozePreferenceStatus();
  }
}
