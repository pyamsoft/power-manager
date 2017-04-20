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

package com.pyamsoft.powermanager.airplane;

import android.support.annotation.NonNull;
import com.pyamsoft.powermanager.base.preference.OnboardingPreferences;
import com.pyamsoft.powermanager.model.PermissionObserver;
import com.pyamsoft.powermanager.uicore.ManagePreferencePresenter;
import com.pyamsoft.powermanager.uicore.PermissionPreferenceInteractor;
import com.pyamsoft.powermanager.uicore.PermissionPreferencePresenter;
import dagger.Module;
import dagger.Provides;
import io.reactivex.Scheduler;
import javax.inject.Named;

@Module public class AirplaneManagePreferenceModule {

  @Provides @Named("airplane_manage_pref")
  ManagePreferencePresenter provideAirplaneManagePreferencePresenter(
      @Named("airplane_manage_pref_interactor") PermissionPreferenceInteractor interactor,
      @Named("obs") Scheduler obsScheduler, @Named("sub") Scheduler subScheduler) {
    return new PermissionPreferencePresenter(interactor, obsScheduler, subScheduler);
  }

  @Provides @Named("airplane_manage_pref_interactor")
  PermissionPreferenceInteractor provideAirplaneManagePreferenceInteractor(
      @Named("obs_root_permission") PermissionObserver rootPermissionObserver,
      @NonNull OnboardingPreferences preferences) {
    return new PermissionPreferenceInteractor(preferences, rootPermissionObserver);
  }
}
