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

package com.pyamsoft.powermanager.settings;

import android.support.annotation.NonNull;
import com.pyamsoft.powermanager.settings.bus.ConfirmEvent;
import com.pyamsoft.pydroid.bus.EventBus;
import com.pyamsoft.pydroid.helper.Checker;
import com.pyamsoft.pydroid.presenter.SchedulerPresenter;
import io.reactivex.Scheduler;
import javax.inject.Inject;
import javax.inject.Named;
import timber.log.Timber;

class SettingsPreferencePresenter extends SchedulerPresenter {

  @NonNull private final SettingsPreferenceInteractor interactor;

  @Inject SettingsPreferencePresenter(@NonNull SettingsPreferenceInteractor interactor,
      @Named("obs") Scheduler obsScheduler, @Named("sub") Scheduler subScheduler) {
    super(obsScheduler, subScheduler);
    this.interactor = interactor;
  }

  /**
   * public
   *
   * Gets confirm events from ConfirmationDialog
   */
  void registerOnBus(@NonNull BusCallback callback) {
    BusCallback busCallback = Checker.checkNonNull(callback);
    disposeOnStop(EventBus.get()
        .listen(ConfirmEvent.class)
        .subscribeOn(getSubscribeScheduler())
        .observeOn(getObserveScheduler())
        .subscribe(confirmEvent -> {
          switch (confirmEvent.type()) {
            case DATABASE:
              clearDatabase(busCallback);
              break;
            case ALL:
              clearAll(busCallback);
              break;
            default:
              throw new IllegalStateException(
                  "Received invalid confirmation event type: " + confirmEvent.type());
          }
        }, throwable -> Timber.e(throwable, "confirm bus error")));
  }

  @SuppressWarnings("WeakerAccess") void clearAll(ClearRequestCallback callback) {
    disposeOnStop(interactor.clearAll()
        .subscribeOn(getSubscribeScheduler())
        .observeOn(getObserveScheduler())
        .subscribe(aBoolean -> callback.onClearAll(), throwable -> Timber.e(throwable, "onError")));
  }

  @SuppressWarnings("WeakerAccess") void clearDatabase(ClearRequestCallback callback) {
    disposeOnStop(interactor.clearDatabase()
        .subscribeOn(getSubscribeScheduler())
        .observeOn(getObserveScheduler())
        .subscribe(aBoolean -> callback.onClearDatabase(),
            throwable -> Timber.e(throwable, "onError")));
  }

  /**
   * public
   */
  void checkRootEnabled(@NonNull RootCallback callback) {
    disposeOnStop(interactor.isRootEnabled()
        .subscribeOn(getSubscribeScheduler())
        .observeOn(getObserveScheduler())
        .doAfterTerminate(callback::onComplete)
        .doOnSubscribe(disposable -> callback.onBegin())
        .subscribe(rootEnabled -> checkRoot(false, rootEnabled, callback),
            throwable -> Timber.e(throwable, "onError bindCheckRoot")));
  }

  /**
   * public
   */
  void checkRoot(boolean causedByUser, boolean rootEnable, @NonNull RootCallback callback) {
    disposeOnStop(interactor.checkRoot(rootEnable)
        .subscribeOn(getSubscribeScheduler())
        .observeOn(getObserveScheduler())
        .doAfterTerminate(callback::onComplete)
        .doOnSubscribe(disposable -> callback.onBegin())
        .subscribe(hasRoot -> callback.onRootCallback(causedByUser, hasRoot, rootEnable),
            throwable -> {
              Timber.e(throwable, "onError checking root");
              callback.onRootCallback(causedByUser, false, rootEnable);
            }));
  }

  interface BusCallback extends ClearRequestCallback {
  }

  interface RootCallback {

    void onBegin();

    void onRootCallback(boolean causedByUser, boolean hasPermission, boolean rootEnable);

    void onComplete();
  }

  interface ClearRequestCallback {

    void onClearAll();

    void onClearDatabase();
  }
}
