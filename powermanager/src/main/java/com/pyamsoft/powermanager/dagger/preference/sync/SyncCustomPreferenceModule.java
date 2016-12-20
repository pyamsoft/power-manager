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

package com.pyamsoft.powermanager.dagger.preference.sync;

import android.support.annotation.NonNull;
import com.pyamsoft.powermanager.PowerManagerPreferences;
import com.pyamsoft.powermanager.app.preference.CustomTimeInputPreferencePresenter;
import com.pyamsoft.powermanager.dagger.preference.CustomTimeInputPreferenceInteractor;
import dagger.Module;
import dagger.Provides;
import javax.inject.Named;
import rx.Scheduler;

@Module public class SyncCustomPreferenceModule {

  @Provides @Named("sync_custom_delay")
  CustomTimeInputPreferencePresenter provideSyncCustomDelayPresenter(
      @NonNull @Named("sync_custom_delay_interactor")
          CustomTimeInputPreferenceInteractor interactor, @Named("obs") Scheduler obsScheduler,
      @Named("sub") Scheduler subScheduler) {
    return new SyncDelayPreferencePresenterImpl(interactor, obsScheduler, subScheduler);
  }

  @Provides @Named("sync_custom_delay_interactor")
  CustomTimeInputPreferenceInteractor provideSyncCustomDelayInteractor(
      @NonNull PowerManagerPreferences preferences) {
    return new SyncDelayPreferenceInteractorImpl(preferences);
  }

  @Provides @Named("sync_custom_enable")
  CustomTimeInputPreferencePresenter provideSyncCustomEnablePresenter(
      @NonNull @Named("sync_custom_enable_interactor")
          CustomTimeInputPreferenceInteractor interactor, @Named("obs") Scheduler obsScheduler,
      @Named("sub") Scheduler subScheduler) {
    return new SyncEnableTimePreferencePresenterImpl(interactor, obsScheduler, subScheduler);
  }

  @Provides @Named("sync_custom_enable_interactor")
  CustomTimeInputPreferenceInteractor provideSyncCustomEnableInteractor(
      @NonNull PowerManagerPreferences preferences) {
    return new SyncEnableTimePreferenceInteractorImpl(preferences);
  }

  @Provides @Named("sync_custom_disable")
  CustomTimeInputPreferencePresenter provideSyncCustomDisablePresenter(
      @NonNull @Named("sync_custom_disable_interactor")
          CustomTimeInputPreferenceInteractor interactor, @Named("obs") Scheduler obsScheduler,
      @Named("sub") Scheduler subScheduler) {
    return new SyncDisableTimePreferencePresenterImpl(interactor, obsScheduler, subScheduler);
  }

  @Provides @Named("sync_custom_disable_interactor")
  CustomTimeInputPreferenceInteractor provideSyncCustomDisableInteractor(
      @NonNull PowerManagerPreferences preferences) {
    return new SyncDisableTimePreferenceInteractorImpl(preferences);
  }
}
