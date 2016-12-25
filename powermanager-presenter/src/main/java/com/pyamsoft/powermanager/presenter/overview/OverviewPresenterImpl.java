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

package com.pyamsoft.powermanager.presenter.overview;

import android.support.annotation.NonNull;
import android.view.View;
import com.pyamsoft.powermanagermodel.BooleanInterestObserver;
import com.pyamsoft.pydroidrx.SchedulerPresenter;
import com.pyamsoft.pydroidrx.SubscriptionHelper;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;
import rx.Scheduler;
import rx.Subscription;
import rx.subscriptions.Subscriptions;
import timber.log.Timber;

class OverviewPresenterImpl extends SchedulerPresenter<OverviewPresenter.Overview>
    implements OverviewPresenter {

  @SuppressWarnings("WeakerAccess") @NonNull final BooleanInterestObserver wifiObserver;
  @SuppressWarnings("WeakerAccess") @NonNull final BooleanInterestObserver dataObserver;
  @SuppressWarnings("WeakerAccess") @NonNull final BooleanInterestObserver bluetoothObserver;
  @SuppressWarnings("WeakerAccess") @NonNull final BooleanInterestObserver syncObserver;
  @SuppressWarnings("WeakerAccess") @NonNull final BooleanInterestObserver airplaneObserver;
  @SuppressWarnings("WeakerAccess") @NonNull final BooleanInterestObserver dozeObserver;
  @SuppressWarnings("WeakerAccess") @NonNull final BooleanInterestObserver wearObserver;
  @NonNull private final OverviewInteractor interactor;
  @SuppressWarnings("WeakerAccess") @NonNull Subscription onboardingSubscription =
      Subscriptions.empty();

  @Inject OverviewPresenterImpl(@NonNull OverviewInteractor interactor,
      @NonNull Scheduler observeScheduler, @NonNull Scheduler subscribeScheduler,
      @NonNull BooleanInterestObserver wifiObserver, @NonNull BooleanInterestObserver dataObserver,
      @NonNull BooleanInterestObserver bluetoothObserver,
      @NonNull BooleanInterestObserver syncObserver,
      @NonNull BooleanInterestObserver airplaneObserver,
      @NonNull BooleanInterestObserver dozeObserver,
      @NonNull BooleanInterestObserver wearObserver) {
    super(observeScheduler, subscribeScheduler);
    this.interactor = interactor;
    this.wifiObserver = wifiObserver;
    this.dataObserver = dataObserver;
    this.bluetoothObserver = bluetoothObserver;
    this.syncObserver = syncObserver;
    this.airplaneObserver = airplaneObserver;
    this.dozeObserver = dozeObserver;
    this.wearObserver = wearObserver;
  }

  @Override protected void onUnbind() {
    super.onUnbind();
    SubscriptionHelper.unsubscribe(onboardingSubscription);
  }

  @Override protected void onBind() {
    super.onBind();
    showOnBoarding();
  }

  @SuppressWarnings("WeakerAccess") void showOnBoarding() {
    SubscriptionHelper.unsubscribe(onboardingSubscription);
    onboardingSubscription = interactor.hasShownOnboarding()
        .delay(1, TimeUnit.SECONDS)
        .subscribeOn(getSubscribeScheduler())
        .observeOn(getObserveScheduler())
        .subscribe(onboard -> {
              if (!onboard) {
                getView(Overview::showOnBoarding);
              }
            }, throwable -> Timber.e(throwable, "onError showOnBoarding"),
            () -> SubscriptionHelper.unsubscribe(onboardingSubscription));
  }

  @Override public void setShownOnBoarding() {
    interactor.setShownOnboarding();
  }

  @Override public void getWifiObserver(@NonNull View view) {
    getView(overview -> overview.onWifiObserverRetrieved(view, wifiObserver));
  }

  @Override public void getDataObserver(@NonNull View view) {
    getView(overview -> overview.onDataObserverRetrieved(view, dataObserver));
  }

  @Override public void getBluetoothObserver(@NonNull View view) {
    getView(overview -> overview.onBluetoothObserverRetrieved(view, bluetoothObserver));
  }

  @Override public void getSyncObserver(@NonNull View view) {
    getView(overview -> overview.onSyncObserverRetrieved(view, syncObserver));
  }

  @Override public void getAirplaneObserver(@NonNull View view) {
    getView(overview -> overview.onAirplaneObserverRetrieved(view, airplaneObserver));
  }

  @Override public void getDozeObserver(@NonNull View view) {
    getView(overview -> overview.onDozeObserverRetrieved(view, dozeObserver));
  }

  @Override public void getWearObserver(@NonNull View view) {
    getView(overview -> overview.onWearObserverRetrieved(view, wearObserver));
  }
}
