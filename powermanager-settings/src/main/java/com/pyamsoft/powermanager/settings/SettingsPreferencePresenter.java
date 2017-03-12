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
import com.pyamsoft.pydroid.helper.DisposableHelper;
import com.pyamsoft.pydroid.presenter.Presenter;
import com.pyamsoft.pydroid.presenter.SchedulerPresenter;
import io.reactivex.Scheduler;
import io.reactivex.disposables.Disposable;
import io.reactivex.disposables.Disposables;
import javax.inject.Inject;
import javax.inject.Named;
import timber.log.Timber;

class SettingsPreferencePresenter extends SchedulerPresenter<Presenter.Empty> {

  private static final int CONFIRM_DATABASE = 0;
  private static final int CONFIRM_ALL = 1;
  @NonNull private final SettingsPreferenceInteractor interactor;
  @NonNull private Disposable confirmedDisposable = Disposables.empty();
  @NonNull private Disposable rootDisposable = Disposables.empty();
  @NonNull private Disposable bindCheckRootDisposable = Disposables.empty();

  @Inject SettingsPreferencePresenter(@NonNull SettingsPreferenceInteractor interactor,
      @Named("obs") Scheduler obsScheduler, @Named("sub") Scheduler subScheduler) {
    super(obsScheduler, subScheduler);
    this.interactor = interactor;
  }

  @Override protected void onUnbind() {
    super.onUnbind();
    confirmedDisposable = DisposableHelper.unsubscribe(confirmedDisposable);
    rootDisposable = DisposableHelper.unsubscribe(rootDisposable);
    bindCheckRootDisposable = DisposableHelper.unsubscribe(bindCheckRootDisposable);
  }

  public void checkRootEnabled(@NonNull RootCallback callback) {
    bindCheckRootDisposable = DisposableHelper.unsubscribe(bindCheckRootDisposable);
    bindCheckRootDisposable = interactor.isRootEnabled()
        .subscribeOn(getSubscribeScheduler())
        .observeOn(getObserveScheduler())
        .subscribe(rootEnabled -> checkRoot(false, rootEnabled, callback),
            throwable -> Timber.e(throwable, "onError bindCheckRoot"));
  }

  public void requestClearDatabase(@NonNull ConfirmDialogCallback callback) {
    callback.showConfirmDialog(CONFIRM_DATABASE);
  }

  public void requestClearAll(@NonNull ConfirmDialogCallback callback) {
    callback.showConfirmDialog(CONFIRM_ALL);
  }

  public void checkRoot(boolean causedByUser, boolean rootEnable, @NonNull RootCallback callback) {
    rootDisposable = DisposableHelper.unsubscribe(rootDisposable);
    rootDisposable = interactor.checkRoot(rootEnable)
        .subscribeOn(getSubscribeScheduler())
        .observeOn(getObserveScheduler())
        .subscribe(hasRoot -> callback.onRootCallback(causedByUser, hasRoot, rootEnable),
            throwable -> {
              Timber.e(throwable, "onError checking root");
              callback.onRootCallback(causedByUser, false, rootEnable);
            });
  }

  public void processClearRequest(int type, @NonNull ClearRequestCallback callback) {
    switch (type) {
      case CONFIRM_DATABASE:
        clearDatabase(callback);
        break;
      case CONFIRM_ALL:
        clearAll(callback);
        break;
      default:
        throw new IllegalStateException("Received invalid confirmation event type: " + type);
    }
  }

  private void clearAll(ClearRequestCallback callback) {
    confirmedDisposable = DisposableHelper.unsubscribe(confirmedDisposable);
    confirmedDisposable = interactor.clearAll()
        .subscribeOn(getSubscribeScheduler())
        .observeOn(getObserveScheduler())
        .subscribe(aBoolean -> callback.onClearAll(), throwable -> Timber.e(throwable, "onError"));
  }

  private void clearDatabase(ClearRequestCallback callback) {
    confirmedDisposable = DisposableHelper.unsubscribe(confirmedDisposable);
    confirmedDisposable = interactor.clearDatabase()
        .subscribeOn(getSubscribeScheduler())
        .observeOn(getObserveScheduler())
        .subscribe(aBoolean -> callback.onClearDatabase(),
            throwable -> Timber.e(throwable, "onError"));
  }

  interface RootCallback {

    void onRootCallback(boolean causedByUser, boolean hasPermission, boolean rootEnable);
  }

  interface ConfirmDialogCallback {

    void showConfirmDialog(int type);
  }

  interface ClearRequestCallback {

    void onClearAll();

    void onClearDatabase();
  }
}
