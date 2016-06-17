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

package com.pyamsoft.powermanager.dagger.manager.backend;

import android.support.annotation.NonNull;
import com.pyamsoft.powermanager.app.manager.backend.ManagerData;
import javax.inject.Inject;
import javax.inject.Named;
import rx.Scheduler;
import rx.Subscription;
import timber.log.Timber;

final class ManagerDataImpl extends ManagerBaseImpl implements ManagerData {

  @NonNull private final ManagerInteractor interactor;

  @Inject ManagerDataImpl(@NonNull @Named("data") ManagerInteractor interactor,
      @NonNull @Named("io") Scheduler ioScheduler,
      @NonNull @Named("main") Scheduler mainScheduler) {
    super(interactor, ioScheduler, mainScheduler);
    Timber.d("new ManagerData");
    this.interactor = interactor;
  }

  @Override public void enable() {
    unsubscribe();
    final Subscription subscription = baseEnableObservable().subscribeOn(getIoScheduler())
        .observeOn(getMainScheduler())
        .subscribe(managerInteractor -> {
          Timber.d("Queue Data enable");
          enable(0);
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
    final Subscription subscription = baseDisableObservable().subscribeOn(getIoScheduler())
        .observeOn(getMainScheduler())
        .subscribe(managerInteractor -> {
          Timber.d("Queue Data disable");
          disable(managerInteractor.getDelayTime() * 1000);
        }, throwable -> {
          Timber.e(throwable, "onError");
        }, () -> Timber.d("onComplete"));
    setSubscription(subscription);
  }

  @Override public boolean isEnabled() {
    return interactor.isEnabled();
  }

  @Override public boolean isManaged() {
    return interactor.isManaged();
  }
}
