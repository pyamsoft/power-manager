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

package com.pyamsoft.powermanager.app.settings;

import android.support.annotation.NonNull;
import com.pyamsoft.powermanager.dagger.settings.SettingsInteractor;
import com.pyamsoft.pydroid.base.Presenter;
import javax.inject.Inject;
import javax.inject.Named;
import rx.Scheduler;
import rx.Subscription;
import rx.subscriptions.Subscriptions;
import timber.log.Timber;

public final class SettingsPresenter extends Presenter<SettingsPresenter.MainSettingsView> {

  public static final int CONFIRM_DATABASE = 0;
  public static final int CONFIRM_ALL = 1;
  @NonNull private final SettingsInteractor interactor;
  @NonNull private final Scheduler ioScheduler;
  @NonNull private final Scheduler mainScheduler;
  @NonNull private Subscription confirmBusSubscription = Subscriptions.empty();
  @NonNull private Subscription confirmedSubscription = Subscriptions.empty();

  @Inject public SettingsPresenter(@NonNull SettingsInteractor interactor,
      @NonNull @Named("io") Scheduler ioScheduler,
      @NonNull @Named("main") Scheduler mainScheduler) {
    this.interactor = interactor;
    this.ioScheduler = ioScheduler;
    this.mainScheduler = mainScheduler;
  }

  @Override protected void onResume(@NonNull MainSettingsView view) {
    super.onResume(view);
    registerOnConfirmEventBus();
  }

  @Override protected void onPause(@NonNull MainSettingsView view) {
    super.onPause(view);
    unregisterFromConfirmEventBus();
  }

  @Override protected void onUnbind(@NonNull MainSettingsView view) {
    super.onUnbind(view);
    unsubscribeConfirm();
  }

  public final void clearAll() {
    getView().showConfirmDialog(CONFIRM_ALL);
  }

  public final void clearDatabase() {
    getView().showConfirmDialog(CONFIRM_DATABASE);
  }

  void unsubscribeConfirm() {
    if (!confirmedSubscription.isUnsubscribed()) {
      confirmedSubscription.unsubscribe();
    }
  }

  void unregisterFromConfirmEventBus() {
    if (!confirmBusSubscription.isUnsubscribed()) {
      confirmBusSubscription.unsubscribe();
    }
  }

  void registerOnConfirmEventBus() {
    unregisterFromConfirmEventBus();
    confirmBusSubscription =
        ConfirmationDialog.Bus.get().register().subscribe(confirmationEvent -> {
          switch (confirmationEvent.type()) {
            case CONFIRM_DATABASE:
              unsubscribeConfirm();
              confirmedSubscription = interactor.clearDatabase()
                  .subscribeOn(ioScheduler)
                  .observeOn(mainScheduler)
                  .subscribe(aBoolean -> {
                    getView().onClearDatabase();
                  }, throwable -> {
                    Timber.e(throwable, "onError");
                  });
              break;
            case CONFIRM_ALL:
              unsubscribeConfirm();
              confirmedSubscription = interactor.clearAll()
                  .subscribeOn(ioScheduler)
                  .observeOn(mainScheduler)
                  .subscribe(aBoolean -> {
                    getView().onClearAll();
                  }, throwable -> {
                    Timber.e(throwable, "onError");
                  });
              break;
            default:
              throw new IllegalStateException(
                  "Received invalid confirmation event type: " + confirmationEvent.type());
          }
        }, throwable -> {
          Timber.e(throwable, "onError");
        });
  }

  public interface MainSettingsView {

    void showConfirmDialog(int type);

    void onClearAll();

    void onClearDatabase();
  }
}
