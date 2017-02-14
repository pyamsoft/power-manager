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

package com.pyamsoft.powermanager.airplane.preference;

import android.support.annotation.NonNull;
import com.pyamsoft.powermanager.base.PowerManagerPreferences;
import com.pyamsoft.powermanager.uicore.preference.CustomTimePreferenceInteractor;
import com.pyamsoft.powermanager.uicore.preference.CustomTimePreferencePresenter;
import dagger.Module;
import dagger.Provides;
import javax.inject.Named;
import rx.Scheduler;

@Module public class AirplanePreferenceModule {

  @Provides @Named("airplane_custom_delay")
  CustomTimePreferencePresenter provideAirplaneCustomDelayPresenter(
      @NonNull @Named("airplane_custom_delay_interactor") CustomTimePreferenceInteractor interactor, @Named("obs") Scheduler obsScheduler,
      @Named("sub") Scheduler subScheduler) {
    return new CustomTimePreferencePresenter(interactor, obsScheduler, subScheduler);
  }

  @Provides @Named("airplane_custom_delay_interactor")
  CustomTimePreferenceInteractor provideAirplaneCustomDelayInteractor(
      @NonNull PowerManagerPreferences preferences) {
    return new AirplaneDelayPreferenceInteractor(preferences);
  }

  @Provides @Named("airplane_custom_enable")
  CustomTimePreferencePresenter provideAirplaneCustomEnablePresenter(
      @NonNull @Named("airplane_custom_enable_interactor") CustomTimePreferenceInteractor interactor, @Named("obs") Scheduler obsScheduler,
      @Named("sub") Scheduler subScheduler) {
    return new CustomTimePreferencePresenter(interactor, obsScheduler, subScheduler);
  }

  @Provides @Named("airplane_custom_enable_interactor")
  CustomTimePreferenceInteractor provideAirplaneCustomEnableInteractor(
      @NonNull PowerManagerPreferences preferences) {
    return new AirplaneEnablePreferenceInteractor(preferences);
  }

  @Provides @Named("airplane_custom_disable")
  CustomTimePreferencePresenter provideAirplaneCustomDisablePresenter(
      @NonNull @Named("airplane_custom_disable_interactor")
          CustomTimePreferenceInteractor interactor, @Named("obs") Scheduler obsScheduler,
      @Named("sub") Scheduler subScheduler) {
    return new CustomTimePreferencePresenter(interactor, obsScheduler, subScheduler);
  }

  @Provides @Named("airplane_custom_disable_interactor")
  CustomTimePreferenceInteractor provideAirplaneCustomDisableInteractor(
      @NonNull PowerManagerPreferences preferences) {
    return new AirplaneDisablePreferenceInteractor(preferences);
  }
}
