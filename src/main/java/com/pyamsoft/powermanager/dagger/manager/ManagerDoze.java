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

package com.pyamsoft.powermanager.dagger.manager;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.pyamsoft.powermanager.app.manager.ExclusiveManager;
import javax.inject.Inject;
import rx.Scheduler;
import rx.Subscription;
import rx.subscriptions.Subscriptions;
import timber.log.Timber;

class ManagerDoze extends ManagerBase implements ExclusiveManager {

  @NonNull private final ExclusiveManagerInteractor interactor;
  @NonNull private Subscription dozeSetSubscription = Subscriptions.empty();
  @NonNull private Subscription dozeUnsetSubscription = Subscriptions.empty();

  @Inject ManagerDoze(@NonNull ExclusiveManagerInteractor interactor,
      @NonNull Scheduler subscribeScheduler, @NonNull Scheduler observerScheduler) {
    super(interactor, subscribeScheduler, observerScheduler);
    this.interactor = interactor;
  }

  @Override public void queueExclusiveSet(@Nullable NonExclusiveCallback callback) {
    queueSet();

    unsubDozeSet();
    dozeSetSubscription = interactor.isExclusive()
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
        }, throwable -> Timber.e(throwable, "onError queueExclusiveSet"), this::unsubDozeSet);
  }

  @SuppressWarnings("WeakerAccess") void unsubDozeSet() {
    if (!dozeSetSubscription.isUnsubscribed()) {
      dozeSetSubscription.unsubscribe();
    }
  }

  @Override
  public void queueExclusiveUnset(boolean deviceCharging, @Nullable NonExclusiveCallback callback) {
    queueUnset(deviceCharging);

    unsubDozeUnset();
    dozeUnsetSubscription = interactor.isExclusive()
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
        }, throwable -> Timber.e(throwable, "onError queueExclusiveSet"), this::unsubDozeUnset);
  }

  @SuppressWarnings("WeakerAccess") void unsubDozeUnset() {
    if (!dozeUnsetSubscription.isUnsubscribed()) {
      dozeUnsetSubscription.unsubscribe();
    }
  }

  @Override public void cleanup() {
    super.cleanup();
    unsubDozeSet();
    unsubDozeUnset();
  }
}
