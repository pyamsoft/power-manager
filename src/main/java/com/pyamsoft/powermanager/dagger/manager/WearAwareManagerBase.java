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
import timber.log.Timber;

abstract class WearAwareManagerBase extends WearUnawareManagerBase {

  @NonNull final WearAwareManagerInteractor wearAwareManagerInteractor;

  WearAwareManagerBase(@NonNull WearAwareManagerInteractor interactor,
      @NonNull Scheduler observerScheduler, @NonNull Scheduler subscribeScheduler) {
    super(interactor, observerScheduler, subscribeScheduler);
    this.wearAwareManagerInteractor = interactor;
  }

  @NonNull @Override Observable<Boolean> baseObservable() {
    return jobCancellingObservable().flatMap(managed -> {
      if (managed) {
        Timber.d("Normal managed, is wearable managed?");
        return wearAwareManagerInteractor.isWearManaged();
      } else {
        Timber.d("Wearable not managed, return empty");
        return Observable.empty();
      }
    }).flatMap(wearManaged -> {
      if (wearManaged) {
        Timber.d("Is wearable enabled?");
        return wearAwareManagerInteractor.isWearEnabled();
      } else {
        Timber.d("Wearable is not managed, but radio is managed, continue stream");
        return Observable.just(true);
      }
    }).map(shouldContinue -> {
      Timber.d("Should continue stream? %s", shouldContinue);
      return shouldContinue;
    });
  }

  @Override public void cleanup() {
    super.cleanup();
    wearAwareManagerInteractor.cleanup();
  }
}
