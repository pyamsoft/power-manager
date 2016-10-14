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

package com.pyamsoft.powermanager.dagger.wifi;

import android.support.annotation.NonNull;
import com.pyamsoft.powermanager.PowerManagerPreferences;
import com.pyamsoft.powermanager.app.base.BasePeriodPreferencePresenter;
import com.pyamsoft.powermanager.app.modifier.BooleanInterestModifier;
import com.pyamsoft.powermanager.app.observer.BooleanInterestObserver;
import com.pyamsoft.powermanager.dagger.base.BasePeriodPreferenceInteractor;
import dagger.Module;
import dagger.Provides;
import javax.inject.Named;
import rx.Scheduler;

@Module public class WifiPeriodPreferenceModule {

  @Provides @Named("wifi_period_pref")
  BasePeriodPreferencePresenter provideWifiManagePreferencePresenter(
      @Named("wifi_period_pref_interactor") BasePeriodPreferenceInteractor interactor,
      @Named("obs") Scheduler obsScheduler, @Named("sub") Scheduler subScheduler,
      @Named("obs_wifi_periodic") BooleanInterestObserver periodicObserver) {
    return new WifiPeriodPreferencePresenterImpl(interactor, obsScheduler, subScheduler,
        periodicObserver);
  }

  @Provides @Named("wifi_period_pref_interactor")
  BasePeriodPreferenceInteractor provideWifiManagePreferenceInteractor(
      @Named("mod_wifi_periodic") BooleanInterestModifier periodicModifier,
      @NonNull PowerManagerPreferences preferences) {
    return new WifiPeriodPreferenceInteractorImpl(periodicModifier, preferences);
  }
}
