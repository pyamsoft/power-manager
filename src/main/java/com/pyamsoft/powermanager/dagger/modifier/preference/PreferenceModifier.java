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

package com.pyamsoft.powermanager.dagger.modifier.preference;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import com.pyamsoft.powermanager.PowerManagerPreferences;
import com.pyamsoft.powermanager.app.service.ForegroundService;
import rx.Observable;
import rx.Scheduler;
import rx.Subscription;
import rx.schedulers.Schedulers;
import rx.subscriptions.Subscriptions;
import timber.log.Timber;

abstract class PreferenceModifier {

  // KLUDGE Holds reference to application context
  @SuppressWarnings("WeakerAccess") @NonNull final Context appContext;
  @SuppressWarnings("WeakerAccess") @NonNull final Intent service;
  @SuppressWarnings("WeakerAccess") @NonNull final PowerManagerPreferences preferences;
  @NonNull private final Scheduler subscribeScheduler;
  @NonNull private final Scheduler observeScheduler;
  @NonNull private Subscription subscription = Subscriptions.empty();

  PreferenceModifier(@NonNull Context context, @NonNull PowerManagerPreferences preferences,
      @NonNull Scheduler subscribeScheduler, @NonNull Scheduler observeScheduler) {
    Timber.d("New PreferenceModifier");

    if (isBackgroundScheduler(observeScheduler)) {
      throw new RuntimeException("Cannot observe on a background scheduler");
    }

    if (!isBackgroundScheduler(subscribeScheduler)
        && subscribeScheduler != Schedulers.immediate()) {
      throw new RuntimeException("Cannot subscribe on a foreground scheduler");
    }

    this.appContext = context.getApplicationContext();
    this.preferences = preferences;
    this.service = new Intent(appContext, ForegroundService.class);
    this.subscribeScheduler = subscribeScheduler;
    this.observeScheduler = observeScheduler;
  }

  /**
   * Returns whether the given scheduler is one that runs operations in a background thread
   */
  @CheckResult private boolean isBackgroundScheduler(@NonNull Scheduler scheduler) {
    return scheduler == Schedulers.computation()
        || scheduler == Schedulers.io()
        || scheduler == Schedulers.newThread();
  }

  @NonNull @CheckResult protected Scheduler getObserveScheduler() {
    return observeScheduler;
  }

  @NonNull @CheckResult protected Scheduler getSubscribeScheduler() {
    return subscribeScheduler;
  }

  @SuppressWarnings("WeakerAccess") void unsub() {
    if (!subscription.isUnsubscribed()) {
      subscription.unsubscribe();
    }
  }

  void wrapInSubscription(@NonNull WrappedSubscription wrappedSubscription) {
    unsub();
    subscription = Observable.defer(() -> {
      Timber.d("Run modifier on subscription thread");
      wrappedSubscription.call(preferences);
      return Observable.just(true);
    }).subscribeOn(subscribeScheduler).observeOn(observeScheduler).subscribe(aBoolean -> {
      Timber.d("Modifier success");
      appContext.startService(service);
    }, throwable -> Timber.e(throwable, "onError wrapInSubscription"), this::unsub);
  }

  interface WrappedSubscription {

    void call(@NonNull PowerManagerPreferences preferences);
  }
}

