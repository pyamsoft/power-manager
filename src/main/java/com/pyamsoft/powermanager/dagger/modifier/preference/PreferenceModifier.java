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
import android.support.annotation.NonNull;
import com.pyamsoft.powermanager.PowerManagerPreferences;
import com.pyamsoft.powermanager.app.service.ForegroundService;
import rx.Observable;
import rx.Scheduler;
import rx.Subscription;
import rx.subscriptions.Subscriptions;
import timber.log.Timber;

public abstract class PreferenceModifier {

  @NonNull final Context appContext;
  @NonNull final Intent service;
  @NonNull final PowerManagerPreferences preferences;
  @NonNull final Scheduler subscribeScheduler;
  @NonNull final Scheduler observeScheduler;
  @NonNull Subscription subscription = Subscriptions.empty();

  protected PreferenceModifier(@NonNull Context context,
      @NonNull PowerManagerPreferences preferences, @NonNull Scheduler subscribeScheduler,
      @NonNull Scheduler observeScheduler) {
    Timber.d("New PreferenceModifier");
    this.appContext = context.getApplicationContext();
    this.preferences = preferences;
    this.service = new Intent(appContext, ForegroundService.class);
    this.subscribeScheduler = subscribeScheduler;
    this.observeScheduler = observeScheduler;
  }

  void unsub() {
    if (!subscription.isUnsubscribed()) {
      subscription.unsubscribe();
    }
  }

  protected void wrapInSubscription(@NonNull WrappedSubscription wrappedSubscription) {
    unsub();
    subscription = Observable.defer(() -> {
      Timber.d("Run modifier on subscription thread");
      wrappedSubscription.call(appContext, preferences);
      return Observable.just(true);
    }).subscribeOn(subscribeScheduler).observeOn(observeScheduler).subscribe(aBoolean -> {
      Timber.d("Modifier success");
      appContext.startService(service);
    }, throwable -> Timber.e(throwable, "onError wrapInSubscription"), this::unsub);
  }

  protected interface WrappedSubscription {

    void call(@NonNull Context context, @NonNull PowerManagerPreferences preferences);
  }
}
