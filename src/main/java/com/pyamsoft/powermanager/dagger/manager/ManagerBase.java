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

import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import com.pyamsoft.powermanager.app.manager.Manager;
import rx.Observable;
import rx.Scheduler;
import rx.Subscription;
import rx.subscriptions.Subscriptions;
import timber.log.Timber;

abstract class ManagerBase implements Manager {

  @SuppressWarnings("WeakerAccess") @NonNull final ManagerInteractor interactor;
  @NonNull private final Scheduler subscribeScheduler;
  @NonNull private final Scheduler observerScheduler;
  @NonNull private Subscription setSubscription = Subscriptions.empty();
  @NonNull private Subscription unsetSubscription = Subscriptions.empty();

  ManagerBase(@NonNull ManagerInteractor interactor, @NonNull Scheduler subscribeScheduler,
      @NonNull Scheduler observerScheduler) {
    this.interactor = interactor;
    this.subscribeScheduler = subscribeScheduler;
    this.observerScheduler = observerScheduler;
  }

  @NonNull @CheckResult Scheduler getSubscribeScheduler() {
    return subscribeScheduler;
  }

  @NonNull @CheckResult Scheduler getObserverScheduler() {
    return observerScheduler;
  }

  @SuppressWarnings("WeakerAccess") void unsubSet() {
    if (!setSubscription.isUnsubscribed()) {
      setSubscription.unsubscribe();
    }
  }

  @SuppressWarnings("WeakerAccess") void unsubUnset() {
    if (!unsetSubscription.isUnsubscribed()) {
      unsetSubscription.unsubscribe();
    }
  }

  @CheckResult @NonNull private Observable<Boolean> baseObservable() {
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
    unsubSet();
    setSubscription = baseObservable().flatMap(managed -> {
      if (managed) {
        Timber.d("Is original state enabled?");
        return interactor.isOriginalStateEnabled();
      } else {
        Timber.w("Is not managed, return empty");
        return Observable.empty();
      }
    }).subscribeOn(subscribeScheduler).observeOn(observerScheduler).subscribe(originalState -> {
      // Technically can ignore this as if we are here we are non-empty
      // If we are non empty it means we pass the test
      if (originalState) {
        interactor.queueEnableJob();
      }
    }, throwable -> Timber.e(throwable, "onError queueSet"), this::unsubSet);
  }

  @Override public void queueUnset(boolean deviceCharging) {
    unsubUnset();
    unsetSubscription = baseObservable().flatMap(managed -> {
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
    }).subscribeOn(subscribeScheduler).observeOn(observerScheduler).subscribe(ignore -> {
      // Only queue a disable job if the radio is not ignored
      if (!ignore) {
        interactor.queueDisableJob();
      }
    }, throwable -> Timber.e(throwable, "onError queueUnset"), this::unsubUnset);
  }

  @Override public void cleanup() {
    interactor.destroy();
    unsubSet();
    unsubUnset();
  }
}
