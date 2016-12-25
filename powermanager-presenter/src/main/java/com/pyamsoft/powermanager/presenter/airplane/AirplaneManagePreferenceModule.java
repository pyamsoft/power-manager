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

package com.pyamsoft.powermanager.presenter.airplane;

import android.support.annotation.NonNull;
import com.pyamsoft.powermanager.model.BooleanInterestObserver;
import com.pyamsoft.powermanager.model.PermissionObserver;
import com.pyamsoft.powermanager.presenter.PowerManagerPreferences;
import com.pyamsoft.powermanager.presenter.base.ManagePreferenceInteractor;
import com.pyamsoft.powermanager.presenter.base.ManagePreferenceInteractorImpl;
import com.pyamsoft.powermanager.presenter.base.ManagePreferencePresenter;
import com.pyamsoft.powermanager.presenter.base.PermissionManagePreferencePresenterImpl;
import dagger.Module;
import dagger.Provides;
import javax.inject.Named;
import rx.Scheduler;

@Module public class AirplaneManagePreferenceModule {

  @Provides @Named("airplane_manage_pref")
  ManagePreferencePresenter provideAirplaneManagePreferencePresenter(
      @Named("airplane_manage_pref_interactor") ManagePreferenceInteractor interactor,
      @Named("obs") Scheduler obsScheduler, @Named("sub") Scheduler subScheduler,
      @Named("obs_airplane_manage") BooleanInterestObserver manageObserver,
      @Named("obs_root_permission") PermissionObserver rootPermissionObserver) {
    return new PermissionManagePreferencePresenterImpl(interactor, obsScheduler, subScheduler,
        manageObserver, rootPermissionObserver);
  }

  @Provides @Named("airplane_manage_pref_interactor")
  ManagePreferenceInteractor provideAirplaneManagePreferenceInteractor(
      @NonNull PowerManagerPreferences preferences) {
    return new ManagePreferenceInteractorImpl(preferences);
  }
}
