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

package com.pyamsoft.powermanager.sync;

import com.pyamsoft.powermanager.base.preference.OnboardingPreferences;
import com.pyamsoft.powermanager.base.preference.SyncPreferences;
import com.pyamsoft.powermanager.uicore.ManagePreferenceInteractor;
import com.pyamsoft.powermanager.uicore.PeriodPreferenceInteractor;
import com.pyamsoft.powermanager.uicore.preference.CustomTimePreferenceInteractor;
import dagger.Module;
import dagger.Provides;
import io.reactivex.annotations.NonNull;
import javax.inject.Named;
import javax.inject.Singleton;

@Module public class SyncSingletonModule {

  @Singleton @Provides @Named("sync_manage_pref_interactor")
  ManagePreferenceInteractor provideSyncManagePreferenceInteractor(
      @NonNull OnboardingPreferences preferences) {
    return new ManagePreferenceInteractor(preferences);
  }

  @Singleton @Provides @Named("sync_period_pref_interactor")
  PeriodPreferenceInteractor provideSyncPeriodPreferenceInteractor(
      @NonNull OnboardingPreferences preferences) {
    return new PeriodPreferenceInteractor(preferences);
  }

  @Singleton @Provides @Named("sync_custom_delay_interactor")
  CustomTimePreferenceInteractor provideSyncCustomDelayInteractor(
      @NonNull SyncPreferences preferences) {
    return new SyncDelayPreferenceInteractor(preferences);
  }

  @Singleton @Provides @Named("sync_custom_enable_interactor")
  CustomTimePreferenceInteractor provideSyncCustomEnableInteractor(
      @NonNull SyncPreferences preferences) {
    return new SyncEnablePreferenceInteractor(preferences);
  }

  @Singleton @Provides @Named("sync_custom_disable_interactor")
  CustomTimePreferenceInteractor provideSyncCustomDisableInteractor(
      @NonNull SyncPreferences preferences) {
    return new SyncDisablePreferenceInteractor(preferences);
  }
}
