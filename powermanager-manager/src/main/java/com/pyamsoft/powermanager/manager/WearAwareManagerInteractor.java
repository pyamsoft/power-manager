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

package com.pyamsoft.powermanager.manager;

import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import com.pyamsoft.powermanager.base.preference.WearablePreferences;
import com.pyamsoft.powermanager.job.JobQueuer;
import com.pyamsoft.powermanager.model.StateObserver;
import io.reactivex.Maybe;
import io.reactivex.Single;
import timber.log.Timber;

abstract class WearAwareManagerInteractor extends ManagerInteractor {

  @SuppressWarnings("WeakerAccess") @NonNull final StateObserver wearStateObserver;
  @NonNull final WearablePreferences preferences;

  WearAwareManagerInteractor(@NonNull WearablePreferences preferences,
      @NonNull StateObserver stateObserver, @NonNull JobQueuer jobQueuer,
      @NonNull StateObserver wearStateObserver) {
    super(jobQueuer, stateObserver);
    this.wearStateObserver = wearStateObserver;
    this.preferences = preferences;
  }

  @SuppressWarnings("WeakerAccess") @NonNull @CheckResult Single<Boolean> isWearEnabled() {
    return Single.fromCallable(wearStateObserver::enabled);
  }

  @Override public void destroy() {
    super.destroy();
    Timber.d("Unregsiter wear state observer");
    // TODO clean up wear
  }

  @NonNull @Override
  protected Maybe<Boolean> accountForWearableBeforeDisable(boolean originalState) {
    return Maybe.fromCallable(() -> originalState).flatMap(originalStateEnabled -> {
      if (originalStateEnabled) {
        Timber.d("%s: Original state is enabled, is wearable managed?", getJobTag());
        return Maybe.just(isWearManaged());
      } else {
        Timber.w("%s: Original state not enabled, return empty", getJobTag());
        return Maybe.empty();
      }
    }).flatMap(wearManaged -> {
      if (wearManaged) {
        Timber.d("%s: Is wearable not enabled?", getJobTag());
        // Invert the result
        return isWearEnabled().map(wearEnabled -> !wearEnabled).toMaybe();
      } else {
        Timber.d("%s: Wearable is not managed, but radio is managed, continue stream", getJobTag());
        return Maybe.just(Boolean.TRUE);
      }
    });
  }

  @CheckResult abstract boolean isWearManaged();
}
