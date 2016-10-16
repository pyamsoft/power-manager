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

package com.pyamsoft.powermanager.dagger.doze;

import android.support.annotation.NonNull;
import com.pyamsoft.powermanager.PowerManagerPreferences;
import com.pyamsoft.powermanager.app.base.BaseManagePreferencePresenter;
import com.pyamsoft.powermanager.app.doze.DozeOnlyPresenter;
import com.pyamsoft.powermanager.app.modifier.BooleanInterestModifier;
import com.pyamsoft.powermanager.app.observer.BooleanInterestObserver;
import com.pyamsoft.powermanager.app.observer.PermissionObserver;
import com.pyamsoft.powermanager.dagger.base.BaseManagePreferenceInteractor;
import dagger.Module;
import dagger.Provides;
import javax.inject.Named;
import rx.Scheduler;

@Module public class DozeManagePreferenceModule {

  @Provides DozeOnlyPresenter provideDozeOnlyPresenter(@Named("obs") Scheduler obsScheduler,
      @Named("sub") Scheduler subScheduler,
      @Named("obs_doze_permission") PermissionObserver dozePermissionObserver,
      @Named("obs_write_permission") PermissionObserver writePermissionObserver) {
    return new DozeOnlyPresenterImpl(obsScheduler, subScheduler, dozePermissionObserver,
        writePermissionObserver);
  }

  @Provides @Named("doze_manage_pref")
  BaseManagePreferencePresenter provideDozeManagePreferencePresenter(
      @Named("doze_manage_pref_interactor") BaseManagePreferenceInteractor interactor,
      @Named("obs") Scheduler obsScheduler, @Named("sub") Scheduler subScheduler,
      @Named("obs_doze_manage") BooleanInterestObserver manageObserver) {
    return new DozeManagePreferencePresenter(interactor, obsScheduler, subScheduler,
        manageObserver);
  }

  @Provides @Named("doze_manage_pref_interactor")
  BaseManagePreferenceInteractor provideDozeManagePreferenceInteractor(
      @Named("mod_doze_manage") BooleanInterestModifier manageModifier,
      @NonNull PowerManagerPreferences preferences) {
    return new DozeManagePreferenceInteractorImpl(manageModifier, preferences);
  }
}
