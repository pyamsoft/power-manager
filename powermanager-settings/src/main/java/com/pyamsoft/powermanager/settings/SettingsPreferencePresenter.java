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
import com.pyamsoft.pydroid.helper.SubscriptionHelper;
import com.pyamsoft.pydroid.presenter.Presenter;
import com.pyamsoft.pydroid.presenter.SchedulerPresenter;
import javax.inject.Inject;
import rx.Scheduler;
import rx.Subscription;
import rx.subscriptions.Subscriptions;
import timber.log.Timber;

class SettingsPreferencePresenter extends SchedulerPresenter<Presenter.Empty> {

  @SuppressWarnings("WeakerAccess") static final int CONFIRM_DATABASE = 0;
  @SuppressWarnings("WeakerAccess") static final int CONFIRM_ALL = 1;
  @NonNull private final SettingsPreferenceInteractor interactor;
  @SuppressWarnings("WeakerAccess") @NonNull Subscription confirmedSubscription =
      Subscriptions.empty();
  @SuppressWarnings("WeakerAccess") @NonNull Subscription rootSubscription = Subscriptions.empty();
  @SuppressWarnings("WeakerAccess") @NonNull Subscription bindCheckRootSubscription =
      Subscriptions.empty();

  @Inject SettingsPreferencePresenter(@NonNull SettingsPreferenceInteractor interactor,
      @NonNull Scheduler obsScheduler, @NonNull Scheduler subScheduler) {
    super(obsScheduler, subScheduler);
    this.interactor = interactor;
  }

  @Override protected void onUnbind() {
    super.onUnbind();
    SubscriptionHelper.unsubscribe(confirmedSubscription, rootSubscription,
        bindCheckRootSubscription);
  }

  public void checkRootEnabled(@NonNull RootCallback callback) {
    SubscriptionHelper.unsubscribe(bindCheckRootSubscription);
    bindCheckRootSubscription = interactor.isRootEnabled()
        .subscribeOn(getSubscribeScheduler())
        .observeOn(getObserveScheduler())
        .subscribe(rootEnabled -> checkRoot(false, rootEnabled, callback),
            throwable -> Timber.e(throwable, "onError bindCheckRoot"),
            () -> SubscriptionHelper.unsubscribe(bindCheckRootSubscription));
  }

  public void requestClearDatabase(@NonNull ConfirmDialogCallback callback) {
    callback.showConfirmDialog(CONFIRM_DATABASE);
  }

  public void requestClearAll(@NonNull ConfirmDialogCallback callback) {
    callback.showConfirmDialog(CONFIRM_ALL);
  }

  public void checkRoot(boolean causedByUser, boolean rootEnable, @NonNull RootCallback callback) {
    SubscriptionHelper.unsubscribe(rootSubscription);
    rootSubscription = interactor.checkRoot(rootEnable)
        .subscribeOn(getSubscribeScheduler())
        .observeOn(getObserveScheduler())
        .subscribe(hasRoot -> callback.onRootCallback(causedByUser, hasRoot, rootEnable),
            throwable -> {
              Timber.e(throwable, "onError checking root");
              callback.onRootCallback(causedByUser, false, rootEnable);
            }, () -> SubscriptionHelper.unsubscribe(rootSubscription));
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
    SubscriptionHelper.unsubscribe(confirmedSubscription);
    confirmedSubscription = interactor.clearAll()
        .subscribeOn(getSubscribeScheduler())
        .observeOn(getObserveScheduler())
        .subscribe(aBoolean -> callback.onClearAll(), throwable -> Timber.e(throwable, "onError"),
            () -> SubscriptionHelper.unsubscribe(confirmedSubscription));
  }

  private void clearDatabase(ClearRequestCallback callback) {
    SubscriptionHelper.unsubscribe(confirmedSubscription);
    confirmedSubscription = interactor.clearDatabase()
        .subscribeOn(getSubscribeScheduler())
        .observeOn(getObserveScheduler())
        .subscribe(aBoolean -> callback.onClearDatabase(),
            throwable -> Timber.e(throwable, "onError"),
            () -> SubscriptionHelper.unsubscribe(confirmedSubscription));
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
