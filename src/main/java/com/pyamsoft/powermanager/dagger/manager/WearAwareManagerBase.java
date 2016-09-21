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
import rx.Observable;
import rx.Scheduler;
import rx.functions.Func1;
import timber.log.Timber;

abstract class WearAwareManagerBase extends ManagerBase {

  @SuppressWarnings("WeakerAccess") @NonNull final WearAwareManagerInteractor
      wearAwareManagerInteractor;

  WearAwareManagerBase(@NonNull WearAwareManagerInteractor interactor,
      @NonNull Scheduler observerScheduler, @NonNull Scheduler subscribeScheduler) {
    super(interactor, observerScheduler, subscribeScheduler);
    this.wearAwareManagerInteractor = interactor;
  }

  @NonNull @Override
  protected Func1<Boolean, Observable<Boolean>> accountForWearableBeforeDisable() {
    return ignore -> Observable.just(ignore).flatMap(new Func1<Boolean, Observable<Boolean>>() {
      @Override public Observable<Boolean> call(Boolean ignore) {
        if (!ignore) {
          Timber.d("Do not ignore charging, is wearable managed?");
          return wearAwareManagerInteractor.isWearManaged();
        } else {
          Timber.d("Ignore charging, return empty");
          return Observable.empty();
        }
      }
    }).flatMap(wearManaged -> {
      if (wearManaged) {
        Timber.d("Is wearable enabled?");
        return wearAwareManagerInteractor.isWearEnabled();
      } else {
        Timber.d("Wearable is not managed, but radio is managed, continue stream");
        return Observable.just(false);
      }
    });
  }

  @Override public void cleanup() {
    super.cleanup();
    wearAwareManagerInteractor.cleanup();
  }
}
