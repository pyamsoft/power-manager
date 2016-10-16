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

import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import com.pyamsoft.powermanager.PowerManagerPreferences;
import com.pyamsoft.pydroidrx.SchedulerUtil;
import rx.Observable;
import rx.Scheduler;
import rx.Subscription;
import rx.subscriptions.Subscriptions;
import timber.log.Timber;

abstract class PreferenceModifier {

  // KLUDGE Holds reference to application context
  @SuppressWarnings("WeakerAccess") @NonNull final PowerManagerPreferences preferences;
  @NonNull private final Scheduler subscribeScheduler;
  @NonNull private final Scheduler observeScheduler;
  @NonNull private Subscription subscription = Subscriptions.empty();

  PreferenceModifier(@NonNull PowerManagerPreferences preferences,
      @NonNull Scheduler subscribeScheduler, @NonNull Scheduler observeScheduler) {
    Timber.d("New PreferenceModifier");

    SchedulerUtil.enforceObserveScheduler(observeScheduler);
    SchedulerUtil.enforceSubscribeScheduler(subscribeScheduler);

    this.preferences = preferences;
    this.subscribeScheduler = subscribeScheduler;
    this.observeScheduler = observeScheduler;
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
    })
        .subscribeOn(subscribeScheduler)
        .observeOn(observeScheduler)
        .subscribe(aBoolean -> Timber.d("Modifier success"),
            throwable -> Timber.e(throwable, "onError wrapInSubscription"), this::unsub);
  }

  interface WrappedSubscription {

    void call(@NonNull PowerManagerPreferences preferences);
  }
}

