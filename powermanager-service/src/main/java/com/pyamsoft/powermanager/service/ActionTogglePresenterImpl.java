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

package com.pyamsoft.powermanager.service;

import android.support.annotation.NonNull;
import com.pyamsoft.pydroid.presenter.Presenter;
import com.pyamsoft.pydroid.rx.SchedulerPresenter;
import com.pyamsoft.pydroid.rx.SubscriptionHelper;
import javax.inject.Inject;
import rx.Scheduler;
import rx.Subscription;
import rx.subscriptions.Subscriptions;
import timber.log.Timber;

class ActionTogglePresenterImpl extends SchedulerPresenter<Presenter.Empty>
    implements ActionTogglePresenter {

  @SuppressWarnings("WeakerAccess") @NonNull final ActionToggleInteractor interactor;
  @SuppressWarnings("WeakerAccess") @NonNull Subscription subscription = Subscriptions.empty();

  @Inject ActionTogglePresenterImpl(@NonNull ActionToggleInteractor interactor,
      @NonNull Scheduler observeScheduler, @NonNull Scheduler subscribeScheduler) {
    super(observeScheduler, subscribeScheduler);
    this.interactor = interactor;
  }

  @Override protected void onUnbind() {
    super.onUnbind();
    SubscriptionHelper.unsubscribe(subscription);
  }

  @Override public void toggleForegroundState(@NonNull ForegroundStateCallback callback) {
    SubscriptionHelper.unsubscribe(subscription);
    subscription = interactor.isServiceEnabled()
        .map(enabled -> {
          final boolean newState = !enabled;
          interactor.setServiceEnabled(newState);
          return newState;
        })
        .subscribeOn(getSubscribeScheduler())
        .observeOn(getObserveScheduler())
        .subscribe(callback::onForegroundStateToggled,
            throwable -> Timber.e(throwable, "onError toggleForegroundState"),
            () -> SubscriptionHelper.unsubscribe(subscription));
  }
}
