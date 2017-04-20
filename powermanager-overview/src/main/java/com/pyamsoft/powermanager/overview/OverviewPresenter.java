/*
 * Copyright 2017 Peter Kenji Yamanaka
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
import com.pyamsoft.powermanager.model.States;
import com.pyamsoft.pydroid.presenter.SchedulerPresenter;
import io.reactivex.Scheduler;
import javax.inject.Inject;
import javax.inject.Named;
import timber.log.Timber;

class OverviewPresenter extends SchedulerPresenter {

  @NonNull private final OverviewInteractor interactor;

  @Inject OverviewPresenter(@NonNull OverviewInteractor interactor,
      @Named("obs") Scheduler obsScheduler, @Named("sub") Scheduler subScheduler) {
    super(obsScheduler, subScheduler);
    this.interactor = interactor;
  }

  /**
   * public
   */
  void showOnBoarding(@NonNull OnboardingCallback callback) {
    disposeOnStop(interactor.hasShownOnboarding()
        .subscribeOn(getSubscribeScheduler())
        .observeOn(getObserveScheduler())
        .subscribe(onboard -> {
          if (!onboard) {
            callback.onShowOnBoarding();
          }
        }, throwable -> Timber.e(throwable, "onError onShowOnboarding")));
  }

  /**
   * public
   */
  void getWifiObserver(@NonNull ObserverRetrieveCallback callback) {
    callback.onObserverRetrieved(
        interactor.isWifiManaged().blockingGet() ? States.ENABLED : States.DISABLED);
  }

  /**
   * public
   */
  void getDataObserver(@NonNull ObserverRetrieveCallback callback) {
    callback.onObserverRetrieved(
        interactor.isDataManaged().blockingGet() ? States.ENABLED : States.DISABLED);
  }

  /**
   * public
   */
  void getBluetoothObserver(@NonNull ObserverRetrieveCallback callback) {
    callback.onObserverRetrieved(
        interactor.isBluetoothManaged().blockingGet() ? States.ENABLED : States.DISABLED);
  }

  /**
   * public
   */
  void getSyncObserver(@NonNull ObserverRetrieveCallback callback) {
    callback.onObserverRetrieved(
        interactor.isSyncManaged().blockingGet() ? States.ENABLED : States.DISABLED);
  }

  /**
   * public
   */
  void getAirplaneObserver(@NonNull ObserverRetrieveCallback callback) {
    callback.onObserverRetrieved(
        interactor.isAirplaneManaged().blockingGet() ? States.ENABLED : States.DISABLED);
  }

  /**
   * public
   */
  void getDozeObserver(@NonNull ObserverRetrieveCallback callback) {
    callback.onObserverRetrieved(
        interactor.isDozeManaged().blockingGet() ? States.ENABLED : States.DISABLED);
  }

  interface OnboardingCallback {

    void onShowOnBoarding();
  }

  interface ObserverRetrieveCallback {

    void onObserverRetrieved(@NonNull States state);
  }
}
