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

package com.pyamsoft.powermanager.dagger.managepreference.data;

import com.pyamsoft.powermanager.app.base.BaseManagePreferencePresenter;
import com.pyamsoft.powermanager.app.modifier.BooleanInterestModifier;
import com.pyamsoft.powermanager.app.observer.BooleanInterestObserver;
import com.pyamsoft.powermanager.dagger.managepreference.BaseManagePreferenceInteractor;
import com.pyamsoft.pydroid.base.app.ActivityScope;
import dagger.Module;
import dagger.Provides;
import javax.inject.Named;
import rx.Scheduler;

@Module public class DataManagePreferenceModule {

  @ActivityScope @Provides @Named("data_manage_pref")
  BaseManagePreferencePresenter provideDataManagePreferencePresenter(
      @Named("data_manage_pref_interactor") BaseManagePreferenceInteractor interactor,
      @Named("main") Scheduler mainScheduler, @Named("io") Scheduler ioScheduler,
      @Named("obs_data_manage") BooleanInterestObserver manageObserver) {
    return new DataManagePreferencePresenter(interactor, mainScheduler, ioScheduler,
        manageObserver);
  }

  @ActivityScope @Provides @Named("data_manage_pref_interactor")
  BaseManagePreferenceInteractor provideDataManagePreferenceInteractor(
      @Named("mod_data_manage") BooleanInterestModifier manageModifier) {
    return new DataManagePreferenceInteractorImpl(manageModifier);
  }
}
