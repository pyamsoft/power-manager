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

package com.pyamsoft.powermanagerpresenter.manager;

import android.support.annotation.NonNull;
import rx.Observable;
import rx.Scheduler;
import rx.functions.Func1;
import timber.log.Timber;

abstract class WearUnawareManagerImpl extends ManagerImpl {

  WearUnawareManagerImpl(@NonNull ManagerInteractor interactor,
      @NonNull Scheduler observerScheduler, @NonNull Scheduler subscribeScheduler) {
    super(interactor, observerScheduler, subscribeScheduler);
  }

  @NonNull @Override
  protected Func1<Boolean, Observable<Boolean>> accountForWearableBeforeDisable() {
    return originalStateEnabled -> {
      Timber.d("%s: Unaware of wearables,just pass through", getJobTag());
      return Observable.just(originalStateEnabled);
    };
  }
}
