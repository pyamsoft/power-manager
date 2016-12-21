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

package com.pyamsoft.powermanagerpresenter.wear;

import android.support.annotation.NonNull;
import com.pyamsoft.powermanagermodel.BooleanInterestObserver;
import com.pyamsoft.powermanagerpresenter.PowerManagerPreferences;
import com.pyamsoft.powermanagerpresenter.base.ManagePreferenceInteractor;
import com.pyamsoft.powermanagerpresenter.base.ManagePreferencePresenter;
import dagger.Module;
import dagger.Provides;
import javax.inject.Named;
import rx.Scheduler;

@Module public class WearManagePreferenceModule {

  @Provides @Named("wear_manage_pref")
  ManagePreferencePresenter provideDozeManagePreferencePresenter(
      @Named("wear_manage_pref_interactor") ManagePreferenceInteractor interactor,
      @Named("obs") Scheduler obsScheduler, @Named("sub") Scheduler subScheduler,
      @Named("obs_wear_manage") BooleanInterestObserver manageObserver) {
    return new WearManagePreferencePresenterImpl(interactor, obsScheduler, subScheduler,
        manageObserver);
  }

  @Provides @Named("wear_manage_pref_interactor")
  ManagePreferenceInteractor provideDozeManagePreferenceInteractor(
      @NonNull PowerManagerPreferences preferences) {
    return new WearManagePreferenceInteractorImpl(preferences);
  }
}