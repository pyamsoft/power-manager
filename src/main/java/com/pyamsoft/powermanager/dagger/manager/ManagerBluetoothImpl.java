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
import com.pyamsoft.powermanager.app.manager.ManagerBluetooth;
import javax.inject.Inject;
import javax.inject.Named;
import rx.Observable;
import rx.Scheduler;
import rx.Subscription;
import timber.log.Timber;

final class ManagerBluetoothImpl extends WearableManagerImpl implements ManagerBluetooth {

  @NonNull private final WearableManagerInteractor interactor;

  @Inject ManagerBluetoothImpl(@NonNull @Named("bluetooth") WearableManagerInteractor interactor,
      @NonNull @Named("io") Scheduler ioScheduler,
      @NonNull @Named("main") Scheduler mainScheduler) {
    super(interactor, ioScheduler, mainScheduler);
    Timber.d("new ManagerBluetooth");
    this.interactor = interactor;
  }

  @Override public void enable() {
    unsubscribe();
    final Subscription subscription =
        Observable.defer(() -> Observable.just(interactor))
            .filter(wearableManagerInteractor -> {
              Timber.d("Check that manager isManaged");
              return wearableManagerInteractor.isManaged();
            })
            .subscribeOn(getIoScheduler())
            .observeOn(getMainScheduler())
            .subscribe(wearableManagerInteractor -> {
              Timber.d("Queue Bluetooth enable");
              enable(0);
            }, throwable -> {
              Timber.e(throwable, "onError");
            });
    setSubscription(subscription);
  }

  @Override public void disable() {
    unsubscribe();
    final Subscription subscription =
        Observable.defer(() -> Observable.just(interactor))
            .filter(wearableManagerInteractor -> {
              Timber.d("Check that manager isManaged");
              return wearableManagerInteractor.isManaged();
            })
            .filter(wearableManagerInteractor -> {
              if (wearableManagerInteractor.isWearableManaged()) {
                Timber.d("Check that no wearable is currently connected");
                return wearableManagerInteractor.isWearableConnected();
              } else {
                return true;
              }
            })
            .subscribeOn(getIoScheduler())
            .observeOn(getMainScheduler())
            .subscribe(wearableManagerInteractor -> {
              Timber.d("Queue Bluetooth disable");
              disable(wearableManagerInteractor.getDelayTime() * 1000);
            }, throwable -> {
              Timber.e(throwable, "onError");
            });
    setSubscription(subscription);
  }

  @Override public boolean isEnabled() {
    return interactor.isEnabled();
  }

  @Override public boolean isManaged() {
    return interactor.isManaged();
  }
}
