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

package com.pyamsoft.powermanager.dagger.manager.custom;

import android.support.annotation.NonNull;
import com.pyamsoft.powermanager.app.manager.custom.ManagerTimePresenter;
import com.pyamsoft.powermanager.dagger.ActivityScope;
import dagger.Module;
import dagger.Provides;
import javax.inject.Named;

@Module public class ManagerTimeModule {

  @ActivityScope @Provides @Named("delay") ManagerTimePresenter provideManagerDelayPresenter(
      @NonNull ManagerDelayPresenter presenter) {
    return presenter;
  }

  @ActivityScope @Provides ManagerDelayInteractor provideManagerDelayInteractor(
      @NonNull ManagerDelayInteractorImpl interactor) {
    return interactor;
  }

  @ActivityScope @Provides @Named("periodic") ManagerTimePresenter provideManagerPeriodicPresenter(
      @NonNull ManagerPeriodicPresenter presenter) {
    return presenter;
  }

  @ActivityScope @Provides ManagerPeriodicInteractor provideManagerPeriodicDisableInteractor(
      @NonNull ManagerPeriodicInteractorImpl interactor) {
    return interactor;
  }
}
