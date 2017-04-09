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

package com.pyamsoft.powermanager.doze.preference;

import android.support.annotation.NonNull;
import com.pyamsoft.powermanager.base.preference.DozePreferences;
import com.pyamsoft.powermanager.uicore.preference.CustomTimePreferenceInteractor;
import com.pyamsoft.powermanager.uicore.preference.CustomTimePreferencePresenter;
import dagger.Module;
import dagger.Provides;
import io.reactivex.Scheduler;
import javax.inject.Named;

@Module public class DozePreferenceModule {

  @Provides @Named("doze_custom_delay")
  CustomTimePreferencePresenter provideDozeCustomDelayPresenter(
      @NonNull @Named("doze_custom_delay_interactor") CustomTimePreferenceInteractor interactor,
      @Named("obs") Scheduler obsScheduler, @Named("sub") Scheduler subScheduler) {
    return new CustomTimePreferencePresenter(interactor, obsScheduler, subScheduler);
  }

  @Provides @Named("doze_custom_delay_interactor")
  CustomTimePreferenceInteractor provideDozeCustomDelayInteractor(
      @NonNull DozePreferences preferences) {
    return new DozeDelayPreferenceInteractor(preferences);
  }

  @Provides @Named("doze_custom_enable")
  CustomTimePreferencePresenter provideDozeCustomEnablePresenter(
      @NonNull @Named("doze_custom_enable_interactor") CustomTimePreferenceInteractor interactor,
      @Named("obs") Scheduler obsScheduler, @Named("sub") Scheduler subScheduler) {
    return new CustomTimePreferencePresenter(interactor, obsScheduler, subScheduler);
  }

  @Provides @Named("doze_custom_enable_interactor")
  CustomTimePreferenceInteractor provideDozeCustomEnableInteractor(
      @NonNull DozePreferences preferences) {
    return new DozeEnablePreferenceInteractor(preferences);
  }

  @Provides @Named("doze_custom_disable")
  CustomTimePreferencePresenter provideDozeCustomDisablePresenter(
      @NonNull @Named("doze_custom_disable_interactor") CustomTimePreferenceInteractor interactor,
      @Named("obs") Scheduler obsScheduler, @Named("sub") Scheduler subScheduler) {
    return new CustomTimePreferencePresenter(interactor, obsScheduler, subScheduler);
  }

  @Provides @Named("doze_custom_disable_interactor")
  CustomTimePreferenceInteractor provideDozeCustomDisableInteractor(
      @NonNull DozePreferences preferences) {
    return new DozeDisablePreferenceInteractor(preferences);
  }
}
