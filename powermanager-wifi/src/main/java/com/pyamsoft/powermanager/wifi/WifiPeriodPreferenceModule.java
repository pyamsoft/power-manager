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

package com.pyamsoft.powermanager.wifi;

import android.support.annotation.NonNull;
import com.pyamsoft.powermanager.model.BooleanInterestObserver;
import com.pyamsoft.powermanager.uicore.PeriodPreferenceInteractor;
import com.pyamsoft.powermanager.uicore.PeriodPreferenceInteractorImpl;
import com.pyamsoft.powermanager.uicore.PeriodPreferencePresenter;
import com.pyamsoft.powermanager.uicore.PeriodPreferencePresenterImpl;
import dagger.Module;
import dagger.Provides;
import javax.inject.Named;
import rx.Scheduler;

@Module public class WifiPeriodPreferenceModule {

  @Provides @Named("wifi_period_pref")
  PeriodPreferencePresenter provideWifiManagePreferencePresenter(
      @Named("wifi_period_pref_interactor") PeriodPreferenceInteractor interactor,
      @Named("obs") Scheduler obsScheduler, @Named("sub") Scheduler subScheduler,
      @Named("obs_wifi_periodic") BooleanInterestObserver periodicObserver) {
    return new PeriodPreferencePresenterImpl(interactor, obsScheduler, subScheduler,
        periodicObserver);
  }

  @Provides @Named("wifi_period_pref_interactor")
  PeriodPreferenceInteractor provideWifiManagePreferenceInteractor(
      @NonNull PowerManagerPreferences preferences) {
    return new PeriodPreferenceInteractorImpl(preferences);
  }
}
