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

package com.pyamsoft.powermanager.dagger.settings;

import android.support.annotation.NonNull;
import com.pyamsoft.powermanager.app.settings.SettingsPreferencePresenter;
import com.pyamsoft.pydroidrx.SchedulerPresenter;
import com.pyamsoft.pydroidrx.SubscriptionHelper;
import javax.inject.Inject;
import rx.Scheduler;
import rx.Subscription;
import rx.subscriptions.Subscriptions;
import timber.log.Timber;

class SettingsPreferencePresenterImpl
    extends SchedulerPresenter<SettingsPreferencePresenter.SettingsPreferenceView>
    implements SettingsPreferencePresenter {

  @SuppressWarnings("WeakerAccess") static final int CONFIRM_DATABASE = 0;
  @SuppressWarnings("WeakerAccess") static final int CONFIRM_ALL = 1;
  @NonNull private final SettingsPreferenceInteractor interactor;
  @SuppressWarnings("WeakerAccess") @NonNull Subscription confirmedSubscription =
      Subscriptions.empty();
  @SuppressWarnings("WeakerAccess") @NonNull Subscription rootSubscription = Subscriptions.empty();
  @SuppressWarnings("WeakerAccess") @NonNull Subscription bindCheckRootSubscription =
      Subscriptions.empty();

  @Inject SettingsPreferencePresenterImpl(@NonNull SettingsPreferenceInteractor interactor,
      @NonNull Scheduler obsScheduler, @NonNull Scheduler subScheduler) {
    super(obsScheduler, subScheduler);
    this.interactor = interactor;
  }

  @Override protected void onBind() {
    super.onBind();

    SubscriptionHelper.unsubscribe(bindCheckRootSubscription);
    bindCheckRootSubscription = interactor.isRootEnabled()
        .subscribeOn(getSubscribeScheduler())
        .observeOn(getObserveScheduler())
        .subscribe(rootEnabled -> checkRoot(false, rootEnabled),
            throwable -> Timber.e(throwable, "onError bindCheckRoot"),
            () -> SubscriptionHelper.unsubscribe(bindCheckRootSubscription));
  }

  @Override protected void onUnbind() {
    super.onUnbind();
    SubscriptionHelper.unsubscribe(confirmedSubscription, rootSubscription,
        bindCheckRootSubscription);
  }

  @Override public void requestClearAll() {
    getView(settingsPreferenceView -> settingsPreferenceView.showConfirmDialog(CONFIRM_ALL));
  }

  @Override public void requestClearDatabase() {
    getView(settingsPreferenceView -> settingsPreferenceView.showConfirmDialog(CONFIRM_DATABASE));
  }

  @Override public void checkRoot(boolean causedByUser, boolean rootEnable) {
    SubscriptionHelper.unsubscribe(rootSubscription);
    rootSubscription = interactor.checkRoot(rootEnable)
        .subscribeOn(getSubscribeScheduler())
        .observeOn(getObserveScheduler())
        .subscribe(
            hasPermission -> getView(view -> view.onRootCallback(causedByUser, hasPermission, rootEnable)),
            throwable -> {
              Timber.e(throwable, "onError checking root");
              getView(view -> view.onRootCallback(causedByUser, false, rootEnable));
            }, () -> SubscriptionHelper.unsubscribe(rootSubscription));
  }

  @Override public void processClearRequest(int type) {
    switch (type) {
      case CONFIRM_DATABASE:
        clearDatabase();
        break;
      case CONFIRM_ALL:
        clearAll();
        break;
      default:
        throw new IllegalStateException("Received invalid confirmation event type: " + type);
    }
  }

  @SuppressWarnings("WeakerAccess") void clearAll() {
    SubscriptionHelper.unsubscribe(confirmedSubscription);
    confirmedSubscription = interactor.clearAll()
        .subscribeOn(getSubscribeScheduler())
        .observeOn(getObserveScheduler())
        .subscribe(aBoolean -> getView(SettingsPreferenceView::onClearAll),
            throwable -> Timber.e(throwable, "onError"),
            () -> SubscriptionHelper.unsubscribe(confirmedSubscription));
  }

  @SuppressWarnings("WeakerAccess") void clearDatabase() {
    SubscriptionHelper.unsubscribe(confirmedSubscription);
    confirmedSubscription = interactor.clearDatabase()
        .subscribeOn(getSubscribeScheduler())
        .observeOn(getObserveScheduler())
        .subscribe(aBoolean -> getView(SettingsPreferenceView::onClearDatabase),
            throwable -> Timber.e(throwable, "onError"),
            () -> SubscriptionHelper.unsubscribe(confirmedSubscription));
  }
}
