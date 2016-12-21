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

package com.pyamsoft.powermanagerpresenter.preference.airplane;

import android.support.annotation.NonNull;
import com.pyamsoft.powermanagerpresenter.PowerManagerPreferences;
import com.pyamsoft.powermanagerpresenter.preference.CustomTimeInputPreferenceInteractor;
import com.pyamsoft.powermanagerpresenter.preference.CustomTimeInputPreferencePresenter;
import dagger.Module;
import dagger.Provides;
import javax.inject.Named;
import rx.Scheduler;

@Module public class AirplaneCustomPreferenceModule {

  @Provides @Named("airplane_custom_delay")
  CustomTimeInputPreferencePresenter provideAirplaneCustomDelayPresenter(
      @NonNull @Named("airplane_custom_delay_interactor")
          CustomTimeInputPreferenceInteractor interactor, @Named("obs") Scheduler obsScheduler,
      @Named("sub") Scheduler subScheduler) {
    return new AirplaneDelayPreferencePresenterImpl(interactor, obsScheduler, subScheduler);
  }

  @Provides @Named("airplane_custom_delay_interactor")
  CustomTimeInputPreferenceInteractor provideAirplaneCustomDelayInteractor(
      @NonNull PowerManagerPreferences preferences) {
    return new AirplaneDelayPreferenceInteractorImpl(preferences);
  }

  @Provides @Named("airplane_custom_enable")
  CustomTimeInputPreferencePresenter provideAirplaneCustomEnablePresenter(
      @NonNull @Named("airplane_custom_enable_interactor")
          CustomTimeInputPreferenceInteractor interactor, @Named("obs") Scheduler obsScheduler,
      @Named("sub") Scheduler subScheduler) {
    return new AirplaneEnableTimePreferencePresenterImpl(interactor, obsScheduler, subScheduler);
  }

  @Provides @Named("airplane_custom_enable_interactor")
  CustomTimeInputPreferenceInteractor provideAirplaneCustomEnableInteractor(
      @NonNull PowerManagerPreferences preferences) {
    return new AirplaneEnableTimePreferenceInteractorImpl(preferences);
  }

  @Provides @Named("airplane_custom_disable")
  CustomTimeInputPreferencePresenter provideAirplaneCustomDisablePresenter(
      @NonNull @Named("airplane_custom_disable_interactor")
          CustomTimeInputPreferenceInteractor interactor, @Named("obs") Scheduler obsScheduler,
      @Named("sub") Scheduler subScheduler) {
    return new AirplaneDisableTimePreferencePresenterImpl(interactor, obsScheduler, subScheduler);
  }

  @Provides @Named("airplane_custom_disable_interactor")
  CustomTimeInputPreferenceInteractor provideAirplaneCustomDisableInteractor(
      @NonNull PowerManagerPreferences preferences) {
    return new AirplaneDisableTimePreferenceInteractorImpl(preferences);
  }
}
