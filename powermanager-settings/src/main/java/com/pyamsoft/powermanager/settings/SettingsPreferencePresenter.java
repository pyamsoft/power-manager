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
import com.pyamsoft.pydroid.bus.EventBus;
import com.pyamsoft.pydroid.helper.Checker;
import com.pyamsoft.pydroid.helper.DisposableHelper;
import com.pyamsoft.pydroid.presenter.SchedulerPresenter;
import io.reactivex.Scheduler;
import io.reactivex.disposables.Disposable;
import io.reactivex.disposables.Disposables;
import javax.inject.Inject;
import javax.inject.Named;
import timber.log.Timber;

class SettingsPreferencePresenter extends SchedulerPresenter {

  @NonNull private final SettingsPreferenceInteractor interactor;
  @NonNull private Disposable confirmedDisposable = Disposables.empty();
  @NonNull private Disposable rootDisposable = Disposables.empty();
  @NonNull private Disposable bindCheckRootDisposable = Disposables.empty();
  @NonNull private Disposable bus = Disposables.empty();

  @Inject SettingsPreferencePresenter(@NonNull SettingsPreferenceInteractor interactor,
      @Named("obs") Scheduler obsScheduler, @Named("sub") Scheduler subScheduler) {
    super(obsScheduler, subScheduler);
    this.interactor = interactor;
  }

  @Override protected void onStop() {
    super.onStop();
    confirmedDisposable = DisposableHelper.dispose(confirmedDisposable);
    rootDisposable = DisposableHelper.dispose(rootDisposable);
    bindCheckRootDisposable = DisposableHelper.dispose(bindCheckRootDisposable);
    bus = DisposableHelper.dispose(bus);
  }

  /**
   * public
   *
   * Gets confirm events from ConfirmationDialog
   */
  void registerOnBus(@NonNull BusCallback callback) {
    BusCallback busCallback = Checker.checkNonNull(callback);
    bus = DisposableHelper.dispose(bus);
    bus = EventBus.get()
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
        }, throwable -> Timber.e(throwable, "confirm bus error"));
  }

  @SuppressWarnings("WeakerAccess") void clearAll(ClearRequestCallback callback) {
    confirmedDisposable = DisposableHelper.dispose(confirmedDisposable);
    confirmedDisposable = interactor.clearAll()
        .subscribeOn(getSubscribeScheduler())
        .observeOn(getObserveScheduler())
        .subscribe(aBoolean -> callback.onClearAll(), throwable -> Timber.e(throwable, "onError"));
  }

  @SuppressWarnings("WeakerAccess") void clearDatabase(ClearRequestCallback callback) {
    confirmedDisposable = DisposableHelper.dispose(confirmedDisposable);
    confirmedDisposable = interactor.clearDatabase()
        .subscribeOn(getSubscribeScheduler())
        .observeOn(getObserveScheduler())
        .subscribe(aBoolean -> callback.onClearDatabase(),
            throwable -> Timber.e(throwable, "onError"));
  }

  /**
   * public
   */
  void checkRootEnabled(@NonNull RootCallback callback) {
    callback.onBegin();
    bindCheckRootDisposable = DisposableHelper.dispose(bindCheckRootDisposable);
    bindCheckRootDisposable = interactor.isRootEnabled()
        .subscribeOn(getSubscribeScheduler())
        .observeOn(getObserveScheduler())
        .doAfterTerminate(callback::onComplete)
        .subscribe(rootEnabled -> checkRoot(false, rootEnabled, callback),
            throwable -> Timber.e(throwable, "onError bindCheckRoot"));
  }

  /**
   * public
   */
  void checkRoot(boolean causedByUser, boolean rootEnable, @NonNull RootCallback callback) {
    callback.onBegin();
    rootDisposable = DisposableHelper.dispose(rootDisposable);
    rootDisposable = interactor.checkRoot(rootEnable)
        .subscribeOn(getSubscribeScheduler())
        .observeOn(getObserveScheduler())
        .doAfterTerminate(callback::onComplete)
        .subscribe(hasRoot -> callback.onRootCallback(causedByUser, hasRoot, rootEnable),
            throwable -> {
              Timber.e(throwable, "onError checking root");
              callback.onRootCallback(causedByUser, false, rootEnable);
            });
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
