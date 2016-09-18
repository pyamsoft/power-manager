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

package com.pyamsoft.powermanager.dagger.service.notification;

import android.support.annotation.NonNull;
import com.pyamsoft.powermanager.app.modifier.BooleanInterestModifier;
import com.pyamsoft.powermanager.app.observer.BooleanInterestObserver;
import com.pyamsoft.powermanager.app.service.notification.NotificationDialogPresenter;
import com.pyamsoft.pydroid.dagger.presenter.SchedulerPresenter;
import javax.inject.Inject;
import rx.Observable;
import rx.Scheduler;
import rx.Subscription;
import rx.subscriptions.Subscriptions;
import timber.log.Timber;

class NotificationDialogPresenterImpl extends SchedulerPresenter<NotificationDialogPresenter.View>
    implements NotificationDialogPresenter {

  @NonNull final BooleanInterestModifier wifiStateModifier;
  @NonNull final BooleanInterestModifier dataStateModifier;
  @NonNull final BooleanInterestModifier bluetoothStateModifier;
  @NonNull final BooleanInterestModifier syncStateModifier;
  @NonNull final BooleanInterestModifier wifiManageModifier;
  @NonNull final BooleanInterestModifier dataManageModifier;
  @NonNull final BooleanInterestModifier bluetoothManageModifier;
  @NonNull final BooleanInterestModifier syncManageModifier;
  @NonNull final BooleanInterestObserver wifiStateObserver;
  @NonNull final BooleanInterestObserver dataStateObserver;
  @NonNull final BooleanInterestObserver bluetoothStateObserver;
  @NonNull final BooleanInterestObserver syncStateObserver;
  @NonNull final BooleanInterestObserver wifiManageObserver;
  @NonNull final BooleanInterestObserver dataManageObserver;
  @NonNull final BooleanInterestObserver bluetoothManageObserver;
  @NonNull final BooleanInterestObserver syncManageObserver;

  @NonNull private Subscription wifiStateSubscription = Subscriptions.empty();
  @NonNull private Subscription dataStateSubscription = Subscriptions.empty();
  @NonNull private Subscription bluetoothStateSubscription = Subscriptions.empty();
  @NonNull private Subscription syncStateSubscription = Subscriptions.empty();

  @NonNull private Subscription wifiManageSubscription = Subscriptions.empty();
  @NonNull private Subscription dataManageSubscription = Subscriptions.empty();
  @NonNull private Subscription bluetoothManageSubscription = Subscriptions.empty();
  @NonNull private Subscription syncManageSubscription = Subscriptions.empty();

  @Inject NotificationDialogPresenterImpl(@NonNull Scheduler observeScheduler,
      @NonNull Scheduler subscribeScheduler, @NonNull BooleanInterestObserver wifiStateObserver,
      @NonNull BooleanInterestObserver dataStateObserver,
      @NonNull BooleanInterestObserver bluetoothStateObserver,
      @NonNull BooleanInterestObserver syncStateObserver,
      @NonNull BooleanInterestObserver wifiManageObserver,
      @NonNull BooleanInterestObserver dataManageObserver,
      @NonNull BooleanInterestObserver bluetoothManageObserver,
      @NonNull BooleanInterestObserver syncManageObserver,
      @NonNull BooleanInterestModifier wifiStateModifier,
      @NonNull BooleanInterestModifier dataStateModifier,
      @NonNull BooleanInterestModifier bluetoothStateModifier,
      @NonNull BooleanInterestModifier syncStateModifier,
      @NonNull BooleanInterestModifier wifiManageModifier,
      @NonNull BooleanInterestModifier dataManageModifier,
      @NonNull BooleanInterestModifier bluetoothManageModifier,
      @NonNull BooleanInterestModifier syncManageModifier) {
    super(observeScheduler, subscribeScheduler);
    this.wifiStateObserver = wifiStateObserver;
    this.dataStateObserver = dataStateObserver;
    this.bluetoothStateObserver = bluetoothStateObserver;
    this.syncStateObserver = syncStateObserver;
    this.wifiManageObserver = wifiManageObserver;
    this.dataManageObserver = dataManageObserver;
    this.bluetoothManageObserver = bluetoothManageObserver;
    this.syncManageObserver = syncManageObserver;
    this.wifiStateModifier = wifiStateModifier;
    this.dataStateModifier = dataStateModifier;
    this.bluetoothStateModifier = bluetoothStateModifier;
    this.syncStateModifier = syncStateModifier;
    this.wifiManageModifier = wifiManageModifier;
    this.dataManageModifier = dataManageModifier;
    this.bluetoothManageModifier = bluetoothManageModifier;
    this.syncManageModifier = syncManageModifier;
  }

  @Override protected void onBind() {
    super.onBind();
    getView(view -> {
      view.setWifiToggleState(wifiStateObserver.is());
      view.setDataToggleState(dataStateObserver.is());
      view.setBluetoothToggleState(bluetoothStateObserver.is());
      view.setSyncToggleState(syncStateObserver.is());

      view.setWifiManageState(wifiManageObserver.is());
      view.setDataManageState(dataManageObserver.is());
      view.setBluetoothManageState(bluetoothManageObserver.is());
      view.setSyncManageState(syncManageObserver.is());

      wifiStateObserver.register("wifi", () -> view.setWifiToggleState(true),
          () -> view.setWifiToggleState(false));
      dataStateObserver.register("data", () -> view.setDataToggleState(true),
          () -> view.setDataToggleState(false));
      bluetoothStateObserver.register("bluetooth", () -> view.setBluetoothToggleState(true),
          () -> view.setBluetoothToggleState(false));
      syncStateObserver.register("sync", () -> view.setSyncToggleState(true),
          () -> view.setSyncToggleState(false));

      wifiManageObserver.register("wifi", () -> view.setWifiManageState(true),
          () -> view.setWifiManageState(false));
      dataManageObserver.register("data", () -> view.setDataManageState(true),
          () -> view.setDataManageState(false));
      bluetoothManageObserver.register("bluetooth", () -> view.setBluetoothManageState(true),
          () -> view.setBluetoothManageState(false));
      syncManageObserver.register("sync", () -> view.setSyncManageState(true),
          () -> view.setSyncManageState(false));
    });
  }

  @Override protected void onUnbind() {
    super.onUnbind();
    wifiStateObserver.unregister("wifi");
    dataStateObserver.unregister("data");
    bluetoothStateObserver.unregister("bluetooth");
    syncStateObserver.unregister("sync");

    wifiManageObserver.unregister("wifi");
    dataManageObserver.unregister("data");
    bluetoothManageObserver.unregister("bluetooth");
    syncManageObserver.unregister("sync");

    unsubWifiState();
    unsubDataState();
    unsubBluetoothState();
    unsubSyncState();

    unsubWifiManage();
    unsubDataManage();
    unsubBluetoothManage();
    unsubSyncManage();
  }

  void unsubWifiState() {
    if (!wifiStateSubscription.isUnsubscribed()) {
      wifiStateSubscription.unsubscribe();
    }
  }

  void unsubDataState() {
    if (!dataStateSubscription.isUnsubscribed()) {
      dataStateSubscription.unsubscribe();
    }
  }

  void unsubBluetoothState() {
    if (!bluetoothStateSubscription.isUnsubscribed()) {
      bluetoothStateSubscription.unsubscribe();
    }
  }

  void unsubSyncState() {
    if (!syncStateSubscription.isUnsubscribed()) {
      syncStateSubscription.unsubscribe();
    }
  }

  void unsubWifiManage() {
    if (!wifiManageSubscription.isUnsubscribed()) {
      wifiManageSubscription.unsubscribe();
    }
  }

  void unsubDataManage() {
    if (!dataManageSubscription.isUnsubscribed()) {
      dataManageSubscription.unsubscribe();
    }
  }

  void unsubBluetoothManage() {
    if (!bluetoothManageSubscription.isUnsubscribed()) {
      bluetoothManageSubscription.unsubscribe();
    }
  }

  void unsubSyncManage() {
    if (!syncManageSubscription.isUnsubscribed()) {
      syncManageSubscription.unsubscribe();
    }
  }

  @Override public void wifiToggleClicked() {
    unsubWifiState();
    wifiStateSubscription = Observable.defer(() -> {
      if (wifiStateObserver.is()) {
        wifiStateModifier.unset();
      } else {
        wifiStateModifier.set();
      }
      return Observable.just(true);
    })
        .subscribeOn(getSubscribeScheduler())
        .observeOn(getObserveScheduler())
        .subscribe(aBoolean -> Timber.d("Wifi toggle clicked"),
            throwable -> Timber.e(throwable, "onError wifiToggleClicked"), this::unsubWifiState);
  }

  @Override public void dataToggleClicked() {
    unsubDataState();
    dataStateSubscription = Observable.defer(() -> {
      if (dataStateObserver.is()) {
        dataStateModifier.unset();
      } else {
        dataStateModifier.set();
      }
      return Observable.just(true);
    })
        .subscribeOn(getSubscribeScheduler())
        .observeOn(getObserveScheduler())
        .subscribe(aBoolean -> Timber.d("Data toggle clicked"),
            throwable -> Timber.e(throwable, "onError dataToggleClicked"), this::unsubDataState);
  }

  @Override public void bluetoothToggleClicked() {
    unsubBluetoothState();
    bluetoothStateSubscription = Observable.defer(() -> {
      if (bluetoothStateObserver.is()) {
        bluetoothStateModifier.unset();
      } else {
        bluetoothStateModifier.set();
      }
      return Observable.just(true);
    })
        .subscribeOn(getSubscribeScheduler())
        .observeOn(getObserveScheduler())
        .subscribe(aBoolean -> Timber.d("Bluetooth toggle clicked"),
            throwable -> Timber.e(throwable, "onError bluetoothToggleClicked"),
            this::unsubBluetoothState);
  }

  @Override public void syncToggleClicked() {
    unsubSyncState();
    syncStateSubscription = Observable.defer(() -> {
      if (syncStateObserver.is()) {
        syncStateModifier.unset();
      } else {
        syncStateModifier.set();
      }
      return Observable.just(true);
    })
        .subscribeOn(getSubscribeScheduler())
        .observeOn(getObserveScheduler())
        .subscribe(aBoolean -> Timber.d("Sync toggle clicked"),
            throwable -> Timber.e(throwable, "onError syncToggleClicked"), this::unsubSyncState);
  }

  @Override public void wifiManageClicked() {
    unsubWifiManage();
    wifiManageSubscription = Observable.defer(() -> {
      if (wifiManageObserver.is()) {
        wifiManageModifier.unset();
      } else {
        wifiManageModifier.set();
      }
      return Observable.just(true);
    })
        .subscribeOn(getSubscribeScheduler())
        .observeOn(getObserveScheduler())
        .subscribe(aBoolean -> Timber.d("Wifi manage clicked"),
            throwable -> Timber.e(throwable, "onError wifiManageClicked"), this::unsubWifiManage);
  }

  @Override public void dataManageClicked() {
    unsubDataManage();
    dataManageSubscription = Observable.defer(() -> {
      if (dataManageObserver.is()) {
        dataManageModifier.unset();
      } else {
        dataManageModifier.set();
      }
      return Observable.just(true);
    })
        .subscribeOn(getSubscribeScheduler())
        .observeOn(getObserveScheduler())
        .subscribe(aBoolean -> Timber.d("Data manage clicked"),
            throwable -> Timber.e(throwable, "onError dataManageClicked"), this::unsubDataManage);
  }

  @Override public void bluetoothManageClicked() {
    unsubBluetoothManage();
    bluetoothManageSubscription = Observable.defer(() -> {
      if (bluetoothManageObserver.is()) {
        bluetoothManageModifier.unset();
      } else {
        bluetoothManageModifier.set();
      }
      return Observable.just(true);
    })
        .subscribeOn(getSubscribeScheduler())
        .observeOn(getObserveScheduler())
        .subscribe(aBoolean -> Timber.d("Bluetooth manage clicked"),
            throwable -> Timber.e(throwable, "onError bluetoothManageClicked"),
            this::unsubBluetoothManage);
  }

  @Override public void syncManageClicked() {
    unsubSyncManage();
    syncManageSubscription = Observable.defer(() -> {
      if (syncManageObserver.is()) {
        syncManageModifier.unset();
      } else {
        syncManageModifier.set();
      }
      return Observable.just(true);
    })
        .subscribeOn(getSubscribeScheduler())
        .observeOn(getObserveScheduler())
        .subscribe(aBoolean -> Timber.d("Sync manage clicked"),
            throwable -> Timber.e(throwable, "onError syncManageClicked"), this::unsubSyncManage);
  }
}
