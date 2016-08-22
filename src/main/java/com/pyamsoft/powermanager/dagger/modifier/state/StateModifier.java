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

package com.pyamsoft.powermanager.dagger.modifier.state;

import android.content.Context;
import android.support.annotation.NonNull;
import com.pyamsoft.powermanager.PowerManagerPreferences;
import com.pyamsoft.powermanager.app.modifier.BooleanInterestModifier;
import rx.Observable;
import rx.Scheduler;
import rx.Subscription;
import rx.subscriptions.Subscriptions;
import timber.log.Timber;

abstract class StateModifier implements BooleanInterestModifier {

  @NonNull private final Context appContext;
  @NonNull private final PowerManagerPreferences preferences;
  @NonNull private final Scheduler subscribeScheduler;
  @NonNull private final Scheduler observeScheduler;
  @NonNull private Subscription subscription = Subscriptions.empty();

  StateModifier(@NonNull Context context, @NonNull PowerManagerPreferences preferences,
      @NonNull Scheduler subscribeScheduler, @NonNull Scheduler observeScheduler) {
    Timber.d("New StateModifier");
    this.appContext = context.getApplicationContext();
    this.preferences = preferences;
    this.subscribeScheduler = subscribeScheduler;
    this.observeScheduler = observeScheduler;
  }

  void unsub() {
    if (!subscription.isUnsubscribed()) {
      subscription.unsubscribe();
    }
  }

  @Override public final void set() {
    unsub();
    subscription = Observable.defer(() -> {
      Timber.d("Set on IO thread");
      set(appContext, preferences);
      return Observable.just(true);
    })
        .subscribeOn(subscribeScheduler)
        .observeOn(observeScheduler)
        .subscribe(aBoolean -> Timber.d("Set success"),
            throwable -> Timber.e(throwable, "onError set"), this::unsub);
  }

  @Override public final void unset() {
    unsub();
    subscription = Observable.defer(() -> {
      Timber.d("Unset on IO thread");
      unset(appContext, preferences);
      return Observable.just(true);
    })
        .subscribeOn(subscribeScheduler)
        .observeOn(observeScheduler)
        .subscribe(aBoolean -> Timber.d("Unset success"),
            throwable -> Timber.e(throwable, "onError unset"), this::unsub);
  }

  abstract void set(@NonNull Context context, @NonNull PowerManagerPreferences preferences);

  abstract void unset(@NonNull Context context, @NonNull PowerManagerPreferences preferences);
}
