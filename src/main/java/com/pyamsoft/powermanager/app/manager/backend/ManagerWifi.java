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

import android.support.annotation.NonNull;
import com.pyamsoft.powermanager.dagger.manager.backend.ManagerInteractor;
import com.pyamsoft.powermanager.dagger.manager.backend.WearableManagerInteractor;
import javax.inject.Inject;
import javax.inject.Named;
import rx.Observable;
import rx.Scheduler;
import rx.Subscription;
import timber.log.Timber;

public final class ManagerWifi extends WearableManager<WifiView> {

  @NonNull private final WearableManagerInteractor interactor;

  @Inject public ManagerWifi(@NonNull @Named("wifi") WearableManagerInteractor interactor,
      @NonNull @Named("io") Scheduler ioScheduler,
      @NonNull @Named("main") Scheduler mainScheduler) {
    super(interactor, ioScheduler, mainScheduler);
    Timber.d("new ManagerWifi");
    this.interactor = interactor;
  }

  @Override public void enable() {
    unsubscribe();
    final Subscription subscription = baseEnableObservable().subscribeOn(getIoScheduler())
        .observeOn(getMainScheduler())
        .subscribe(managerInteractor -> {
          Timber.d("Queue Wifi enable");
          enable(0, false);
        }, throwable -> {
          Timber.e(throwable, "onError");
        }, () -> {
          Timber.d("onComplete");
          interactor.setOriginalState(false);
        });
    setSubscription(subscription);
  }

  @Override public void disable() {
    unsubscribe();
    Observable<ManagerInteractor> observable = baseDisableObservable();
    observable = zipWithWearableManagedState(observable);

    final Subscription subscription =
        observable.filter(managerInteractor -> managerInteractor != null)
            .subscribeOn(getIoScheduler())
            .observeOn(getMainScheduler())
            .subscribe(managerInteractor -> {
              Timber.d("Queue Wifi disable");
              disable(managerInteractor.getDelayTime() * 1000, false);
            }, throwable -> {
              Timber.e(throwable, "onError");
            }, () -> {
              Timber.d("onComplete");
              interactor.disconnectGoogleApis();
            });
    setSubscription(subscription);
  }
}
