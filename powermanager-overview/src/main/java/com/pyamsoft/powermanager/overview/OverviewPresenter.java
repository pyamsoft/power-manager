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

package com.pyamsoft.powermanager.overview;

import android.support.annotation.NonNull;
import com.pyamsoft.powermanager.model.BooleanInterestObserver;
import com.pyamsoft.pydroid.helper.SubscriptionHelper;
import com.pyamsoft.pydroid.presenter.Presenter;
import com.pyamsoft.pydroid.presenter.SchedulerPresenter;
import javax.inject.Inject;
import rx.Scheduler;
import rx.Subscription;
import rx.subscriptions.Subscriptions;
import timber.log.Timber;

class OverviewPresenter extends SchedulerPresenter<Presenter.Empty> {

  @NonNull private final BooleanInterestObserver wifiObserver;
  @NonNull private final BooleanInterestObserver dataObserver;
  @NonNull private final BooleanInterestObserver bluetoothObserver;
  @NonNull private final BooleanInterestObserver syncObserver;
  @NonNull private final BooleanInterestObserver airplaneObserver;
  @NonNull private final BooleanInterestObserver dozeObserver;
  @NonNull private final BooleanInterestObserver wearObserver;
  @NonNull private final OverviewInteractor interactor;
  @NonNull private Subscription onboardingSubscription = Subscriptions.empty();

  @Inject OverviewPresenter(@NonNull OverviewInteractor interactor,
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
    onboardingSubscription = SubscriptionHelper.unsubscribe(onboardingSubscription);
  }

  public void showOnBoarding(@NonNull OnboardingCallback callback) {
    onboardingSubscription = SubscriptionHelper.unsubscribe(onboardingSubscription);
    onboardingSubscription = interactor.hasShownOnboarding()
        .subscribeOn(getSubscribeScheduler())
        .observeOn(getObserveScheduler())
        .subscribe(onboard -> {
          if (!onboard) {
            callback.onShowOnBoarding();
          }
        }, throwable -> Timber.e(throwable, "onError onShowOnboarding"));
  }

  public void setShownOnBoarding() {
    interactor.setShownOnboarding();
  }

  public void getWifiObserver(@NonNull ObserverRetrieveCallback callback) {
    callback.onObserverRetrieved(wifiObserver);
  }

  public void getDataObserver(@NonNull ObserverRetrieveCallback callback) {
    callback.onObserverRetrieved(dataObserver);
  }

  public void getBluetoothObserver(@NonNull ObserverRetrieveCallback callback) {
    callback.onObserverRetrieved(bluetoothObserver);
  }

  public void getSyncObserver(@NonNull ObserverRetrieveCallback callback) {
    callback.onObserverRetrieved(syncObserver);
  }

  public void getAirplaneObserver(@NonNull ObserverRetrieveCallback callback) {
    callback.onObserverRetrieved(airplaneObserver);
  }

  public void getDozeObserver(@NonNull ObserverRetrieveCallback callback) {
    callback.onObserverRetrieved(dozeObserver);
  }

  public void getWearObserver(@NonNull ObserverRetrieveCallback callback) {
    callback.onObserverRetrieved(wearObserver);
  }

  interface OnboardingCallback {

    void onShowOnBoarding();
  }

  interface ObserverRetrieveCallback {

    void onObserverRetrieved(@NonNull BooleanInterestObserver observer);
  }
}
