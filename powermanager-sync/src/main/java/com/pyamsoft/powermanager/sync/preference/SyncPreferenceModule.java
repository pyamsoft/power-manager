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

package com.pyamsoft.powermanager.sync.preference;

import android.support.annotation.NonNull;
import com.pyamsoft.powermanager.base.preference.SyncPreferences;
import com.pyamsoft.powermanager.uicore.preference.CustomTimePreferenceInteractor;
import com.pyamsoft.powermanager.uicore.preference.CustomTimePreferencePresenter;
import dagger.Module;
import dagger.Provides;
import io.reactivex.Scheduler;
import javax.inject.Named;

@Module public class SyncPreferenceModule {

  @Provides @Named("sync_custom_delay")
  CustomTimePreferencePresenter provideSyncCustomDelayPresenter(
      @NonNull @Named("sync_custom_delay_interactor") CustomTimePreferenceInteractor interactor,
      @Named("obs") Scheduler obsScheduler, @Named("sub") Scheduler subScheduler) {
    return new CustomTimePreferencePresenter(interactor, obsScheduler, subScheduler);
  }

  @Provides @Named("sync_custom_delay_interactor")
  CustomTimePreferenceInteractor provideSyncCustomDelayInteractor(
      @NonNull SyncPreferences preferences) {
    return new SyncDelayPreferenceInteractor(preferences);
  }

  @Provides @Named("sync_custom_enable")
  CustomTimePreferencePresenter provideSyncCustomEnablePresenter(
      @NonNull @Named("sync_custom_enable_interactor") CustomTimePreferenceInteractor interactor,
      @Named("obs") Scheduler obsScheduler, @Named("sub") Scheduler subScheduler) {
    return new CustomTimePreferencePresenter(interactor, obsScheduler, subScheduler);
  }

  @Provides @Named("sync_custom_enable_interactor")
  CustomTimePreferenceInteractor provideSyncCustomEnableInteractor(
      @NonNull SyncPreferences preferences) {
    return new SyncEnablePreferenceInteractor(preferences);
  }

  @Provides @Named("sync_custom_disable")
  CustomTimePreferencePresenter provideSyncCustomDisablePresenter(
      @NonNull @Named("sync_custom_disable_interactor") CustomTimePreferenceInteractor interactor,
      @Named("obs") Scheduler obsScheduler, @Named("sub") Scheduler subScheduler) {
    return new CustomTimePreferencePresenter(interactor, obsScheduler, subScheduler);
  }

  @Provides @Named("sync_custom_disable_interactor")
  CustomTimePreferenceInteractor provideSyncCustomDisableInteractor(
      @NonNull SyncPreferences preferences) {
    return new SyncDisablePreferenceInteractor(preferences);
  }
}
