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

import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import com.pyamsoft.powermanager.app.manager.WearableManager;
import javax.inject.Named;
import rx.Observable;
import rx.Scheduler;
import timber.log.Timber;

abstract class WearableManagerImpl extends ManagerBaseImpl implements WearableManager {

  @NonNull private final WearableManagerInteractor interactor;

  WearableManagerImpl(@NonNull WearableManagerInteractor interactor,
      @NonNull @Named("io") Scheduler ioScheduler,
      @NonNull @Named("main") Scheduler mainScheduler) {
    super(interactor, ioScheduler, mainScheduler);
    this.interactor = interactor;
  }

  @Override public boolean isWearableManaged() {
    return interactor.isWearableManaged();
  }

  @CheckResult @NonNull final Observable<ManagerInteractor> zipWithWearableManagedState(
      @NonNull Observable<ManagerInteractor> observable) {
    if (interactor.isManaged() && interactor.isWearableManaged()) {
      observable = observable.zipWith(Observable.defer(interactor::isWearableConnected),
          (managerInteractor, isConnected) -> {
            if (interactor.isWearableManaged()) {
              if (isConnected) {
                Timber.d("Wearable is managed and connected, return NULL");
                return null;
              } else {
                Timber.d("Wearable is managed but not connected");
                return managerInteractor;
              }
            } else {
              Timber.d("Wearable is not managed");
              return managerInteractor;
            }
          });
    }
    return observable;
  }
}
