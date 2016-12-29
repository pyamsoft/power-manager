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

package com.pyamsoft.powermanager.presenter.preference.data;

import android.support.annotation.NonNull;
import com.pyamsoft.powermanager.presenter.PowerManagerPreferences;
import com.pyamsoft.powermanager.presenter.preference.CustomTimeInputPreferenceInteractor;
import com.pyamsoft.powermanager.presenter.preference.CustomTimeInputPreferencePresenter;
import dagger.Module;
import dagger.Provides;
import javax.inject.Named;
import rx.Scheduler;

@Module public class DataCustomPreferenceModule {

  @Provides @Named("data_custom_delay")
  CustomTimeInputPreferencePresenter provideDataCustomDelayPresenter(
      @NonNull @Named("data_custom_delay_interactor")
          CustomTimeInputPreferenceInteractor interactor, @Named("obs") Scheduler obsScheduler,
      @Named("sub") Scheduler subScheduler) {
    return new DataCustomTimePreferencePresenterImpl(interactor, obsScheduler, subScheduler);
  }

  @Provides @Named("data_custom_delay_interactor")
  CustomTimeInputPreferenceInteractor provideDataCustomDelayInteractor(
      @NonNull PowerManagerPreferences preferences) {
    return new DataDelayPreferenceInteractorImpl(preferences);
  }

  @Provides @Named("data_custom_enable")
  CustomTimeInputPreferencePresenter provideDataCustomEnablePresenter(
      @NonNull @Named("data_custom_enable_interactor")
          CustomTimeInputPreferenceInteractor interactor, @Named("obs") Scheduler obsScheduler,
      @Named("sub") Scheduler subScheduler) {
    return new DataCustomTimePreferencePresenterImpl(interactor, obsScheduler, subScheduler);
  }

  @Provides @Named("data_custom_enable_interactor")
  CustomTimeInputPreferenceInteractor provideDataCustomEnableInteractor(
      @NonNull PowerManagerPreferences preferences) {
    return new DataEnableTimePreferenceInteractorImpl(preferences);
  }

  @Provides @Named("data_custom_disable")
  CustomTimeInputPreferencePresenter provideDataCustomDisablePresenter(
      @NonNull @Named("data_custom_disable_interactor")
          CustomTimeInputPreferenceInteractor interactor, @Named("obs") Scheduler obsScheduler,
      @Named("sub") Scheduler subScheduler) {
    return new DataCustomTimePreferencePresenterImpl(interactor, obsScheduler, subScheduler);
  }

  @Provides @Named("data_custom_disable_interactor")
  CustomTimeInputPreferenceInteractor provideDataCustomDisableInteractor(
      @NonNull PowerManagerPreferences preferences) {
    return new DataDisableTimePreferenceInteractorImpl(preferences);
  }
}