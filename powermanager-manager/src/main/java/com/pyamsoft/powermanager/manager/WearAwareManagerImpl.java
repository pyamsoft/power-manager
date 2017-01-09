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

import android.support.annotation.NonNull;
import javax.inject.Inject;
import rx.Observable;
import rx.Scheduler;
import rx.functions.Func1;
import timber.log.Timber;

class WearAwareManagerImpl extends ManagerImpl {

  @SuppressWarnings("WeakerAccess") @NonNull final WearAwareManagerInteractor
      wearAwareManagerInteractor;

  @Inject WearAwareManagerImpl(@NonNull WearAwareManagerInteractor interactor,
      @NonNull Scheduler scheduler) {
    super(interactor, scheduler);
    this.wearAwareManagerInteractor = interactor;
  }

  @NonNull @Override
  protected Func1<Boolean, Observable<Boolean>> accountForWearableBeforeDisable() {
    return originalState -> Observable.fromCallable(() -> originalState)
        .flatMap(originalStateEnabled -> {
          if (originalStateEnabled) {
            Timber.d("%s: Original state is enabled, is wearable managed?", interactor.getJobTag());
            return wearAwareManagerInteractor.isWearManaged();
          } else {
            Timber.w("%s: Original state not enabled, return empty", interactor.getJobTag());
            return Observable.empty();
          }
        })
        .flatMap(wearManaged -> {
          if (wearManaged) {
            Timber.d("%s: Is wearable not enabled?", interactor.getJobTag());
            // Invert the result
            return wearAwareManagerInteractor.isWearEnabled().map(wearEnabled -> !wearEnabled);
          } else {
            Timber.d("%s: Wearable is not managed, but radio is managed, continue stream",
                interactor.getJobTag());
            return Observable.just(Boolean.TRUE);
          }
        });
  }
}
