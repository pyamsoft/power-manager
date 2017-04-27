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

package com.pyamsoft.powermanager.airplane;

import com.pyamsoft.powermanager.base.preference.AirplanePreferences;
import com.pyamsoft.powermanager.base.preference.OnboardingPreferences;
import com.pyamsoft.powermanager.model.PermissionObserver;
import com.pyamsoft.powermanager.uicore.PeriodPreferenceInteractor;
import com.pyamsoft.powermanager.uicore.PermissionPreferenceInteractor;
import com.pyamsoft.powermanager.uicore.preference.CustomTimePreferenceInteractor;
import dagger.Module;
import dagger.Provides;
import io.reactivex.annotations.NonNull;
import javax.inject.Named;
import javax.inject.Singleton;

@Module public class AirplaneSingletonModule {

  @Singleton @Provides @Named("airplane_manage_pref_interactor")
  PermissionPreferenceInteractor provideAirplaneManagePreferenceInteractor(
      @Named("obs_root_permission") PermissionObserver rootPermissionObserver,
      @NonNull OnboardingPreferences preferences) {
    return new PermissionPreferenceInteractor(preferences, rootPermissionObserver);
  }

  @Singleton @Provides @Named("airplane_period_pref_interactor")
  PeriodPreferenceInteractor provideAirplanePeriodPreferenceInteractor(
      @NonNull OnboardingPreferences preferences) {
    return new PeriodPreferenceInteractor(preferences);
  }

  @Singleton @Provides @Named("airplane_custom_delay_interactor")
  CustomTimePreferenceInteractor provideAirplaneCustomDelayInteractor(
      @NonNull AirplanePreferences preferences) {
    return new AirplaneDelayPreferenceInteractor(preferences);
  }

  @Singleton @Provides @Named("airplane_custom_enable_interactor")
  CustomTimePreferenceInteractor provideAirplaneCustomEnableInteractor(
      @NonNull AirplanePreferences preferences) {
    return new AirplaneEnablePreferenceInteractor(preferences);
  }

  @Singleton @Provides @Named("airplane_custom_disable_interactor")
  CustomTimePreferenceInteractor provideAirplaneCustomDisableInteractor(
      @NonNull AirplanePreferences preferences) {
    return new AirplaneDisablePreferenceInteractor(preferences);
  }
}
