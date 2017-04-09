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

package com.pyamsoft.powermanager.wifi.preference;

import android.support.annotation.NonNull;
import com.pyamsoft.powermanager.base.preference.WifiPreferences;
import com.pyamsoft.powermanager.uicore.preference.CustomTimePreferenceInteractor;
import com.pyamsoft.powermanager.uicore.preference.CustomTimePreferencePresenter;
import dagger.Module;
import dagger.Provides;
import io.reactivex.Scheduler;
import javax.inject.Named;

@Module public class WifiPreferenceModule {

  @Provides @Named("wifi_custom_delay")
  CustomTimePreferencePresenter provideWifiCustomDelayPresenter(
      @NonNull @Named("wifi_custom_delay_interactor") CustomTimePreferenceInteractor interactor,
      @Named("obs") Scheduler obsScheduler, @Named("sub") Scheduler computationScheduler) {
    return new CustomTimePreferencePresenter(interactor, obsScheduler, computationScheduler);
  }

  @Provides @Named("wifi_custom_delay_interactor")
  CustomTimePreferenceInteractor provideWifiCustomDelayInteractor(
      @NonNull WifiPreferences preferences) {
    return new WifiDelayPreferenceInteractor(preferences);
  }

  @Provides @Named("wifi_custom_enable")
  CustomTimePreferencePresenter provideWifiCustomEnablePresenter(
      @NonNull @Named("wifi_custom_enable_interactor") CustomTimePreferenceInteractor interactor,
      @Named("obs") Scheduler obsScheduler, @Named("sub") Scheduler computationScheduler) {
    return new CustomTimePreferencePresenter(interactor, obsScheduler, computationScheduler);
  }

  @Provides @Named("wifi_custom_enable_interactor")
  CustomTimePreferenceInteractor provideWifiCustomEnableInteractor(
      @NonNull WifiPreferences preferences) {
    return new WifiEnablePreferenceInteractor(preferences);
  }

  @Provides @Named("wifi_custom_disable")
  CustomTimePreferencePresenter provideWifiCustomDisablePresenter(
      @NonNull @Named("wifi_custom_disable_interactor") CustomTimePreferenceInteractor interactor,
      @Named("obs") Scheduler obsScheduler, @Named("sub") Scheduler computationScheduler) {
    return new CustomTimePreferencePresenter(interactor, obsScheduler, computationScheduler);
  }

  @Provides @Named("wifi_custom_disable_interactor")
  CustomTimePreferenceInteractor provideWifiCustomDisableInteractor(
      @NonNull WifiPreferences preferences) {
    return new WifiDisablePreferenceInteractor(preferences);
  }
}
