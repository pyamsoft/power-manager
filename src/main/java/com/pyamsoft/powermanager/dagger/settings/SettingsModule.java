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

package com.pyamsoft.powermanager.dagger.settings;

import android.support.annotation.NonNull;
import com.pyamsoft.powermanager.app.settings.SettingsPresenter;
import com.pyamsoft.powermanager.dagger.ActivityScope;
import dagger.Module;
import dagger.Provides;
import javax.inject.Named;
import rx.Scheduler;

@Module public class SettingsModule {

  @ActivityScope @Provides SettingsPresenter provideMainSettingsPresenter(
      @NonNull SettingsInteractor interactor, @NonNull @Named("io") Scheduler ioScheduler,
      @NonNull @Named("main") Scheduler mainScheduler) {
    return new SettingsPresenter(interactor, ioScheduler, mainScheduler);
  }

  @ActivityScope @Provides SettingsInteractor provideMainSettingsInteractor(
      @NonNull SettingsInteractorImpl interactor) {
    return interactor;
  }
}