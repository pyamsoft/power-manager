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

package com.pyamsoft.powermanager.dagger.modifier.preference.manage;

import android.support.annotation.NonNull;
import com.pyamsoft.powermanager.PowerManagerPreferences;
import com.pyamsoft.powermanager.app.observer.PermissionObserver;
import com.pyamsoft.powermanager.dagger.modifier.preference.BooleanPreferenceModifier;
import javax.inject.Inject;
import rx.Scheduler;
import rx.Subscription;
import rx.subscriptions.Subscriptions;
import timber.log.Timber;

class DozeManageModifier extends BooleanPreferenceModifier {

  @NonNull private final PermissionObserver observer;
  @NonNull private Subscription subscription = Subscriptions.empty();

  @Inject DozeManageModifier(@NonNull PowerManagerPreferences preferences,
      @NonNull Scheduler subscribeScheduler, @NonNull Scheduler observeScheduler,
      @NonNull PermissionObserver observer) {
    super(preferences, subscribeScheduler, observeScheduler);
    this.observer = observer;
  }

  @SuppressWarnings("WeakerAccess") void unsub() {
    if (!subscription.isUnsubscribed()) {
      subscription.unsubscribe();
    }
  }

  @Override protected void set(@NonNull PowerManagerPreferences preferences) {
    unsub();
    subscription = observer.hasPermission().subscribe(preferences::setDozeManaged, throwable -> {
      Timber.e(throwable, "onError DozeManageModifier set");
      unsub();
    }, this::unsub);
  }

  @Override protected void unset(@NonNull PowerManagerPreferences preferences) {
    preferences.setDozeManaged(false);
  }
}
