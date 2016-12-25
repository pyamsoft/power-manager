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
import com.pyamsoft.pydroid.presenter.Presenter;

public interface OverviewPresenter extends Presenter<OverviewPresenter.Overview> {

  void setShownOnBoarding();

  void getWifiObserver(@NonNull View view);

  void getDataObserver(@NonNull View view);

  void getBluetoothObserver(@NonNull View view);

  void getSyncObserver(@NonNull View view);

  void getAirplaneObserver(@NonNull View view);

  void getDozeObserver(@NonNull View view);

  void getWearObserver(@NonNull View view);

  interface Overview {

    void showOnBoarding();

    void onWifiObserverRetrieved(@NonNull View view, @NonNull BooleanInterestObserver observer);

    void onDataObserverRetrieved(@NonNull View view, @NonNull BooleanInterestObserver observer);

    void onBluetoothObserverRetrieved(@NonNull View view,
        @NonNull BooleanInterestObserver observer);

    void onSyncObserverRetrieved(@NonNull View view, @NonNull BooleanInterestObserver observer);

    void onAirplaneObserverRetrieved(@NonNull View view, @NonNull BooleanInterestObserver observer);

    void onDozeObserverRetrieved(@NonNull View view, @NonNull BooleanInterestObserver observer);

    void onWearObserverRetrieved(@NonNull View view, @NonNull BooleanInterestObserver observer);
  }
}
