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

package com.pyamsoft.powermanager.app.manager;

import android.support.annotation.NonNull;
import android.support.v4.util.Pair;
import com.pyamsoft.powermanager.dagger.manager.backend.WearableManagerInteractor;
import javax.inject.Inject;
import javax.inject.Named;
import rx.Observable;
import rx.Scheduler;
import rx.Subscription;
import rx.subscriptions.Subscriptions;
import timber.log.Timber;

public final class BluetoothPresenter extends WearablePresenter<BluetoothView> {

  @NonNull private final WearableManagerInteractor interactor;
  @NonNull private Subscription isEnabledSubscription = Subscriptions.empty();
  @NonNull private Subscription isManagedSubscription = Subscriptions.empty();
  @NonNull private Subscription initialSubscription = Subscriptions.empty();

  @Inject public BluetoothPresenter(@NonNull @Named("bluetooth") WearableManagerInteractor interactor,
      @NonNull @Named("main") Scheduler mainScheduler, @NonNull @Named("io") Scheduler ioScheduler) {
    super(interactor, ioScheduler, mainScheduler);
    Timber.d("new ManagerBluetooth");
    this.interactor = interactor;
  }

  @Override protected void onUnbind() {
    super.onUnbind();
    unsubIsEnabled();
    unsubIsManaged();
    unsubInitial();
  }

  private void unsubInitial() {
    if (!initialSubscription.isUnsubscribed()) {
      initialSubscription.unsubscribe();
    }
  }

  @Override public void getCurrentState() {
    unsubInitial();
    initialSubscription = Observable.zip(interactor.isEnabled(), interactor.isManaged(), Pair::new)
        .subscribeOn(getSubscribeScheduler())
        .observeOn(getObserveScheduler())
        .subscribe(pair -> {
          getView().bluetoothInitialState(pair.first, pair.second);
        }, throwable -> {
          Timber.e(throwable, "onError");
          // TODO error
        });
  }

  public final void isEnabled() {
    unsubIsEnabled();
    isEnabledSubscription = interactor.isEnabled()
        .subscribeOn(getSubscribeScheduler())
        .observeOn(getObserveScheduler())
        .subscribe(enabled -> {
          if (enabled) {
            getView().bluetoothStateEnabled();
          } else {
            getView().bluetoothStateDisabled();
          }
        }, throwable -> {
          Timber.e(throwable, "onError");
          // TODO error
        });
  }

  void unsubIsEnabled() {
    if (!isEnabledSubscription.isUnsubscribed()) {
      isEnabledSubscription.unsubscribe();
    }
  }

  public final void isManaged() {
    unsubIsManaged();
    isManagedSubscription = interactor.isManaged()
        .subscribeOn(getSubscribeScheduler())
        .observeOn(getObserveScheduler())
        .subscribe(managed -> {
          if (managed) {
            getView().bluetoothStartManaging();
          } else {
            getView().bluetoothStopManaging();
          }
        }, throwable -> {
          Timber.e(throwable, "onError");
          // TODO error
        });
  }

  void unsubIsManaged() {
    if (!isManagedSubscription.isUnsubscribed()) {
      isManagedSubscription.unsubscribe();
    }
  }
}
