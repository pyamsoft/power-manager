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

package com.pyamsoft.powermanager.app.manager.backend;

import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import com.pyamsoft.powermanager.dagger.manager.backend.ManagerInteractor;
import com.pyamsoft.powermanager.dagger.manager.backend.WearableManagerInteractor;
import javax.inject.Named;
import rx.Observable;
import rx.Scheduler;
import timber.log.Timber;

abstract class WearableManager extends Manager {

  @NonNull private final WearableManagerInteractor interactor;

  WearableManager(@NonNull WearableManagerInteractor interactor,
      @NonNull @Named("io") Scheduler ioScheduler,
      @NonNull @Named("main") Scheduler mainScheduler) {
    super(interactor, mainScheduler, ioScheduler);
    this.interactor = interactor;
  }

  @CheckResult @NonNull private Observable<ManagerInteractor> zipWithWearableManagedState(
      @NonNull Observable<ManagerInteractor> observable) {
    final Observable<Boolean> connectedObservable =
        interactor.isWearableManaged().flatMap(managed -> {
          if (managed) {
            return interactor.isWearableConnected();
          } else {
            return Observable.just(managed);
          }
        });

    return Observable.zip(observable, connectedObservable, (managerInteractor, shouldPass) -> {
      if (shouldPass) {
        Timber.d("Wearable is managed and connected, return NULL");
        return null;
      } else {
        Timber.d("Wearable is not managed or not connected, return NORMAL");
        return managerInteractor;
      }
    });
  }

  @Override public void disable(boolean charging) {
    disable(zipWithWearableManagedState(baseDisableObservable(charging)));
  }
}
