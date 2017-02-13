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

package com.pyamsoft.powermanager.data;

import android.support.annotation.NonNull;
import com.pyamsoft.powermanager.model.BooleanInterestObserver;
import com.pyamsoft.powermanager.model.PermissionObserver;
import com.pyamsoft.powermanager.uicore.ManagePreferenceInteractor;
import com.pyamsoft.powermanager.uicore.PermissionManagePreferencePresenter;
import dagger.Module;
import dagger.Provides;
import javax.inject.Named;
import rx.Scheduler;

@Module public class DataManagePreferenceModule {

  @Provides @Named("data_manage_pref")
  ManagePreferencePresenter provideDataManagePreferencePresenter(
      @Named("data_manage_pref_interactor") ManagePreferenceInteractor interactor,
      @Named("obs") Scheduler obsScheduler, @Named("sub") Scheduler subScheduler,
      @Named("obs_data_manage") BooleanInterestObserver manageObserver,
      @Named("obs_root_permission") PermissionObserver rootPermissionObserver) {
    return new PermissionManagePreferencePresenter(interactor, obsScheduler, subScheduler,
        manageObserver, rootPermissionObserver);
  }

  @Provides @Named("data_manage_pref_interactor")
  ManagePreferenceInteractor provideDataManagePreferenceInteractor(
      @NonNull PowerManagerPreferences preferences) {
    return new ManagePreferenceInteractor(preferences);
  }
}
