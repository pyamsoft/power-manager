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

import android.app.Notification;
import android.support.annotation.NonNull;
import com.pyamsoft.powermanager.dagger.base.SchedulerPresenter;
import javax.inject.Inject;
import javax.inject.Named;
import rx.Scheduler;
import rx.Subscription;
import rx.subscriptions.Subscriptions;
import timber.log.Timber;

public final class ForegroundPresenter
    extends SchedulerPresenter<ForegroundPresenter.ForegroundProvider> {

  @NonNull private final ForegroundInteractor interactor;

  @NonNull private Subscription notificationSubscription = Subscriptions.empty();

  @Inject ForegroundPresenter(@NonNull ForegroundInteractor interactor,
      @NonNull @Named("main") Scheduler mainScheduler,
      @NonNull @Named("io") Scheduler ioScheduler) {
    super(mainScheduler, ioScheduler);
    this.interactor = interactor;
  }

  @Override protected void onUnbind(@NonNull ForegroundProvider view) {
    super.onUnbind(view);
    unsubNotification();
  }

  void unsubNotification() {
    if (!notificationSubscription.isUnsubscribed()) {
      notificationSubscription.unsubscribe();
    }
  }

  public void onStartNotification(boolean explicit) {
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

  public void updateWearableAction() {
    interactor.updateWearablePreferenceStatus();
  }

  public void updateWifiAction() {
    interactor.updateWifiPreferenceStatus();
  }

  public void updateDataAction() {
    interactor.updateDataPreferenceStatus();
  }

  public void updateBluetoothAction() {
    interactor.updateBluetoothPreferenceStatus();
  }

  public void updateSyncAction() {
    interactor.updateSyncPreferenceStatus();
  }

  public interface ForegroundProvider {

    void startNotificationInForeground(@NonNull Notification notification);
  }
}