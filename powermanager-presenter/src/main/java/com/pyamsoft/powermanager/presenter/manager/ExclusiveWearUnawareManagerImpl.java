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

package com.pyamsoft.powermanager.presenter.manager;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.pyamsoft.powermanager.model.ExclusiveManager;
import com.pyamsoft.pydroid.rx.SubscriptionHelper;
import javax.inject.Inject;
import rx.Scheduler;
import rx.Subscription;
import rx.subscriptions.Subscriptions;
import timber.log.Timber;

class ExclusiveWearUnawareManagerImpl extends WearUnawareManagerImpl implements ExclusiveManager {

  @NonNull private final ExclusiveWearUnawareManagerInteractor interactor;
  @SuppressWarnings("WeakerAccess") @NonNull Subscription exclusiveSetSubscription =
      Subscriptions.empty();
  @SuppressWarnings("WeakerAccess") @NonNull Subscription exclusiveUnsetSubscription =
      Subscriptions.empty();

  @Inject ExclusiveWearUnawareManagerImpl(@NonNull ExclusiveWearUnawareManagerInteractor interactor,
      @NonNull Scheduler observerScheduler, @NonNull Scheduler subscribeScheduler) {
    super(interactor, observerScheduler, subscribeScheduler);
    this.interactor = interactor;
  }

  @Override
  public void queueExclusiveSet(@Nullable ExclusiveManager.NonExclusiveCallback callback) {
    queueSet();

    SubscriptionHelper.unsubscribe(exclusiveSetSubscription);
    exclusiveSetSubscription = interactor.isExclusive()
        .subscribeOn(getSubscribeScheduler())
        .observeOn(getObserverScheduler())
        .subscribe(exclusive -> {
              if (exclusive) {
                Timber.d("ManagerDoze is exclusive");
              } else {
                Timber.d("ManagerDoze is not exclusive");
                if (callback == null) {
                  Timber.e("Callback is null but ManagerDoze is not exclusive");
                } else {
                  callback.call();
                }
              }
            }, throwable -> Timber.e(throwable, "onError queueExclusiveSet"),
            () -> SubscriptionHelper.unsubscribe(exclusiveSetSubscription));
  }

  @Override public void queueExclusiveUnset(@Nullable NonExclusiveCallback callback) {
    queueUnset();

    SubscriptionHelper.unsubscribe(exclusiveUnsetSubscription);
    exclusiveUnsetSubscription = interactor.isExclusive()
        .subscribeOn(getSubscribeScheduler())
        .observeOn(getObserverScheduler())
        .subscribe(exclusive -> {
              if (exclusive) {
                Timber.d("ManagerDoze is exclusive");
              } else {
                Timber.d("ManagerDoze is not exclusive");
                if (callback == null) {
                  Timber.e("Callback is null but ManagerDoze is not exclusive");
                } else {
                  callback.call();
                }
              }
            }, throwable -> Timber.e(throwable, "onError queueExclusiveSet"),
            () -> SubscriptionHelper.unsubscribe(exclusiveUnsetSubscription));
  }

  @Override public void cleanup() {
    super.cleanup();
    SubscriptionHelper.unsubscribe(exclusiveSetSubscription, exclusiveUnsetSubscription);
  }
}
