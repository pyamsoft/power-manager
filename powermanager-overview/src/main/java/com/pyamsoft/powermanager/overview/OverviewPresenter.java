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
import com.pyamsoft.pydroid.presenter.Presenter;

interface OverviewPresenter extends Presenter<Presenter.Empty> {

  void showOnBoarding(@NonNull OnboardingCallback callback);

  void setShownOnBoarding();

  void getWifiObserver(@NonNull ObserverRetrieveCallback callback);

  void getDataObserver(@NonNull ObserverRetrieveCallback callback);

  void getBluetoothObserver(@NonNull ObserverRetrieveCallback callback);

  void getSyncObserver(@NonNull ObserverRetrieveCallback callback);

  void getAirplaneObserver(@NonNull ObserverRetrieveCallback callback);

  void getDozeObserver(@NonNull ObserverRetrieveCallback callback);

  void getWearObserver(@NonNull ObserverRetrieveCallback callback);

  interface OnboardingCallback {

    void onShowOnBoarding();
  }

  interface ObserverRetrieveCallback {

    void onObserverRetrieved(@NonNull BooleanInterestObserver observer);
  }
}
