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

package com.pyamsoft.powermanager.presenter.preference.wifi;

import android.support.annotation.NonNull;
import com.pyamsoft.powermanager.presenter.PowerManagerPreferences;
import com.pyamsoft.powermanager.presenter.preference.CustomTimeInputPreferenceInteractor;
import com.pyamsoft.powermanager.presenter.preference.CustomTimeInputPreferencePresenter;
import dagger.Module;
import dagger.Provides;
import javax.inject.Named;
import rx.Scheduler;

@Module public class WifiCustomPreferenceModule {

  @Provides @Named("wifi_custom_delay")
  CustomTimeInputPreferencePresenter provideWifiCustomDelayPresenter(
      @NonNull @Named("wifi_custom_delay_interactor")
          CustomTimeInputPreferenceInteractor interactor, @Named("obs") Scheduler obsScheduler,
      @Named("sub") Scheduler computationScheduler) {
    return new WifiCustomTimePreferencePresenterImpl(interactor, obsScheduler,
        computationScheduler);
  }

  @Provides @Named("wifi_custom_delay_interactor")
  CustomTimeInputPreferenceInteractor provideWifiCustomDelayInteractor(
      @NonNull PowerManagerPreferences preferences) {
    return new WifiDelayPreferenceInteractorImpl(preferences);
  }

  @Provides @Named("wifi_custom_enable")
  CustomTimeInputPreferencePresenter provideWifiCustomEnablePresenter(
      @NonNull @Named("wifi_custom_enable_interactor")
          CustomTimeInputPreferenceInteractor interactor, @Named("obs") Scheduler obsScheduler,
      @Named("sub") Scheduler computationScheduler) {
    return new WifiCustomTimePreferencePresenterImpl(interactor, obsScheduler,
        computationScheduler);
  }

  @Provides @Named("wifi_custom_enable_interactor")
  CustomTimeInputPreferenceInteractor provideWifiCustomEnableInteractor(
      @NonNull PowerManagerPreferences preferences) {
    return new WifiEnableTimePreferenceInteractorImpl(preferences);
  }

  @Provides @Named("wifi_custom_disable")
  CustomTimeInputPreferencePresenter provideWifiCustomDisablePresenter(
      @NonNull @Named("wifi_custom_disable_interactor")
          CustomTimeInputPreferenceInteractor interactor, @Named("obs") Scheduler obsScheduler,
      @Named("sub") Scheduler computationScheduler) {
    return new WifiCustomTimePreferencePresenterImpl(interactor, obsScheduler,
        computationScheduler);
  }

  @Provides @Named("wifi_custom_disable_interactor")
  CustomTimeInputPreferenceInteractor provideWifiCustomDisableInteractor(
      @NonNull PowerManagerPreferences preferences) {
    return new WifiDisableTimePreferenceInteractorImpl(preferences);
  }
}