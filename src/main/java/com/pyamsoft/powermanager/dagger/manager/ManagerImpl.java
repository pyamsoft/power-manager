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

import android.support.annotation.CallSuper;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;
import com.pyamsoft.powermanager.app.manager.Manager;
import com.pyamsoft.pydroidrx.SchedulerHelper;
import com.pyamsoft.pydroidrx.SubscriptionHelper;
import rx.Observable;
import rx.Scheduler;
import rx.Subscription;
import rx.functions.Func1;
import rx.subscriptions.Subscriptions;
import timber.log.Timber;

abstract class ManagerImpl implements Manager {

  @SuppressWarnings("WeakerAccess") @NonNull final ManagerInteractor interactor;
  @NonNull private final Scheduler subscribeScheduler;
  @NonNull private final Scheduler observerScheduler;
  @SuppressWarnings("WeakerAccess") @NonNull Subscription subscription = Subscriptions.empty();

  ManagerImpl(@NonNull ManagerInteractor interactor, @NonNull Scheduler observerScheduler,
      @NonNull Scheduler subscribeScheduler) {
    this.interactor = interactor;
    this.observerScheduler = observerScheduler;
    this.subscribeScheduler = subscribeScheduler;

    SchedulerHelper.enforceObserveScheduler(observerScheduler);
    SchedulerHelper.enforceSubscribeScheduler(subscribeScheduler);
  }

  @NonNull @CheckResult Scheduler getSubscribeScheduler() {
    return subscribeScheduler;
  }

  @NonNull @CheckResult Scheduler getObserverScheduler() {
    return observerScheduler;
  }

  @VisibleForTesting @SuppressWarnings("WeakerAccess") @CheckResult @NonNull
  Observable<Boolean> baseObservable() {
    return interactor.cancelJobs().flatMap(cancelled -> {
      if (cancelled) {
        Timber.d("Is Managed?");
        return interactor.isManaged();
      } else {
        Timber.w("Cancel jobs failed, return empty");
        return Observable.empty();
      }
    });
  }

  @Override public void queueSet() {
    SubscriptionHelper.unsubscribe(subscription);
    subscription = baseObservable().flatMap(managed -> {
      if (managed) {
        Timber.d("Is original state enabled?");
        return interactor.isOriginalStateEnabled();
      } else {
        Timber.w("Is not managed, return empty");
        return Observable.empty();
      }
    }).map(originalState -> {
      if (originalState) {
        interactor.queueEnableJob();
      }
      return originalState;
    }).subscribeOn(subscribeScheduler).observeOn(observerScheduler).subscribe(originalState -> {
          // Technically can ignore this as if we are here we are non-empty
          // If we are non empty it means we pass the test
          if (originalState) {
            Timber.d("Queued up a new enable job");
          }
        }, throwable -> Timber.e(throwable, "onError queueSet"),
        () -> SubscriptionHelper.unsubscribe(subscription));
  }

  @Override public void queueUnset(boolean deviceCharging) {
    SubscriptionHelper.unsubscribe(subscription);
    subscription = baseObservable().flatMap(managed -> {
      if (managed) {
        Timber.d("Is original state enabled?");
        return interactor.isEnabled();
      } else {
        Timber.w("Is not managed, return empty");
        return Observable.empty();
      }
    }).map(enabled -> {
      Timber.d("Set original state enabled: %s", enabled);
      interactor.setOriginalStateEnabled(enabled);
      return enabled;
    }).flatMap(enabled -> {
      if (enabled) {
        Timber.d("Is ignore while charging?");
        return interactor.isIgnoreWhileCharging();
      } else {
        Timber.w("Is not managed, return empty");
        return Observable.empty();
      }
    }).map(ignoreWhileCharging -> {
      Timber.d("Is device currently charging?");
      return ignoreWhileCharging && deviceCharging;
    }).flatMap(accountForWearableBeforeDisable()).map(ignore -> {
      if (!ignore) {
        interactor.queueDisableJob();
      }
      return ignore;
    }).subscribeOn(subscribeScheduler).observeOn(observerScheduler).subscribe(ignore -> {
          // Only queue a disable job if the radio is not ignored
          if (!ignore) {
            Timber.d("Queued up a new disable job");
          }
        }, throwable -> Timber.e(throwable, "onError queueUnset"),
        () -> SubscriptionHelper.unsubscribe(subscription));
  }

  @CheckResult @NonNull
  protected abstract Func1<Boolean, Observable<Boolean>> accountForWearableBeforeDisable();

  @CallSuper @Override public void cleanup() {
    interactor.destroy();
    SubscriptionHelper.unsubscribe(subscription);
  }
}
