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

package com.pyamsoft.powermanager.wifi;

import com.pyamsoft.powermanager.model.StateModifier;
import com.pyamsoft.powermanager.uicore.ManagePreferenceInteractor;
import com.pyamsoft.powermanager.uicore.ManagePreferencePresenter;
import com.pyamsoft.powermanager.uicore.OverviewPagerPresenter;
import com.pyamsoft.powermanager.uicore.PeriodPreferenceInteractor;
import com.pyamsoft.powermanager.uicore.PeriodPreferencePresenter;
import com.pyamsoft.powermanager.uicore.preference.CustomTimePreferenceInteractor;
import com.pyamsoft.powermanager.uicore.preference.CustomTimePreferencePresenter;
import dagger.Module;
import dagger.Provides;
import io.reactivex.Scheduler;
import io.reactivex.annotations.NonNull;
import javax.inject.Named;

@Module public class WifiModule {

  @Provides @Named("wifi_overview") OverviewPagerPresenter provideWifiOverviewPagerPresenter(
      @Named("mod_wifi_state") StateModifier stateModifier, @Named("obs") Scheduler obsScheduler,
      @Named("sub") Scheduler subScheduler) {
    return new OverviewPagerPresenter(obsScheduler, subScheduler, stateModifier);
  }

  @Provides @Named("wifi_manage_pref")
  ManagePreferencePresenter provideWifiManagePreferencePresenter(
      @Named("wifi_manage_pref_interactor") ManagePreferenceInteractor interactor,
      @Named("obs") Scheduler obsScheduler, @Named("sub") Scheduler subScheduler) {
    return new ManagePreferencePresenter(interactor, obsScheduler, subScheduler);
  }

  @Provides @Named("wifi_period_pref")
  PeriodPreferencePresenter provideWifiPeriodPreferencePresenter(
      @Named("wifi_period_pref_interactor") PeriodPreferenceInteractor interactor,
      @Named("obs") Scheduler obsScheduler, @Named("sub") Scheduler subScheduler) {
    return new PeriodPreferencePresenter(interactor, obsScheduler, subScheduler);
  }

  @Provides @Named("wifi_custom_delay")
  CustomTimePreferencePresenter provideWifiCustomDelayPresenter(
      @NonNull @Named("wifi_custom_delay_interactor") CustomTimePreferenceInteractor interactor,
      @Named("obs") Scheduler obsScheduler, @Named("sub") Scheduler computationScheduler) {
    return new CustomTimePreferencePresenter(interactor, obsScheduler, computationScheduler);
  }

  @Provides @Named("wifi_custom_enable")
  CustomTimePreferencePresenter provideWifiCustomEnablePresenter(
      @NonNull @Named("wifi_custom_enable_interactor") CustomTimePreferenceInteractor interactor,
      @Named("obs") Scheduler obsScheduler, @Named("sub") Scheduler computationScheduler) {
    return new CustomTimePreferencePresenter(interactor, obsScheduler, computationScheduler);
  }

  @Provides @Named("wifi_custom_disable")
  CustomTimePreferencePresenter provideWifiCustomDisablePresenter(
      @NonNull @Named("wifi_custom_disable_interactor") CustomTimePreferenceInteractor interactor,
      @Named("obs") Scheduler obsScheduler, @Named("sub") Scheduler computationScheduler) {
    return new CustomTimePreferencePresenter(interactor, obsScheduler, computationScheduler);
  }
}
