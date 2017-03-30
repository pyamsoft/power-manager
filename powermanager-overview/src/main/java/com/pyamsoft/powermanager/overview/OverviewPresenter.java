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
import com.pyamsoft.powermanager.model.overlord.StateObserver;
import com.pyamsoft.pydroid.helper.DisposableHelper;
import com.pyamsoft.pydroid.presenter.Presenter;
import com.pyamsoft.pydroid.presenter.SchedulerPresenter;
import io.reactivex.Scheduler;
import io.reactivex.disposables.Disposable;
import io.reactivex.disposables.Disposables;
import javax.inject.Inject;
import javax.inject.Named;
import timber.log.Timber;

class OverviewPresenter extends SchedulerPresenter<Presenter.Empty> {

  @NonNull private final StateObserver wifiObserver;
  @NonNull private final StateObserver dataObserver;
  @NonNull private final StateObserver bluetoothObserver;
  @NonNull private final StateObserver syncObserver;
  @NonNull private final StateObserver airplaneObserver;
  @NonNull private final StateObserver dozeObserver;
  @NonNull private final StateObserver wearObserver;
  @NonNull private final OverviewInteractor interactor;
  @NonNull private Disposable onboardingDisposable = Disposables.empty();

  @Inject OverviewPresenter(@NonNull OverviewInteractor interactor,
      @Named("obs") Scheduler obsScheduler, @Named("sub") Scheduler subScheduler,
      @NonNull @Named("obs_wifi_manage") StateObserver wifiObserver,
      @NonNull @Named("obs_data_manage") StateObserver dataObserver,
      @NonNull @Named("obs_bluetooth_manage") StateObserver bluetoothObserver,
      @NonNull @Named("obs_sync_manage") StateObserver syncObserver,
      @NonNull @Named("obs_airplane_manage") StateObserver airplaneObserver,
      @NonNull @Named("obs_doze_manage") StateObserver dozeObserver,
      @NonNull @Named("obs_wear_manage") StateObserver wearObserver) {
    super(obsScheduler, subScheduler);
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
    onboardingDisposable = DisposableHelper.dispose(onboardingDisposable);
  }

  public void showOnBoarding(@NonNull OnboardingCallback callback) {
    onboardingDisposable = DisposableHelper.dispose(onboardingDisposable);
    onboardingDisposable = interactor.hasShownOnboarding()
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

    void onObserverRetrieved(@NonNull StateObserver observer);
  }
}
