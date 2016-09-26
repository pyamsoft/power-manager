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

package com.pyamsoft.powermanager.dagger.wifi;

import com.pyamsoft.powermanager.app.base.BaseManagePreferencePresenter;
import com.pyamsoft.powermanager.app.modifier.BooleanInterestModifier;
import com.pyamsoft.powermanager.app.observer.BooleanInterestObserver;
import com.pyamsoft.powermanager.dagger.base.BaseManagePreferenceInteractor;
import com.pyamsoft.pydroid.ActivityScope;
import dagger.Module;
import dagger.Provides;
import javax.inject.Named;
import rx.Scheduler;

@Module public class WifiManagePreferenceModule {

  @ActivityScope @Provides @Named("wifi_manage_pref")
  BaseManagePreferencePresenter provideWifiManagePreferencePresenter(
      @Named("wifi_manage_pref_interactor") BaseManagePreferenceInteractor interactor,
      @Named("main") Scheduler mainScheduler, @Named("computation") Scheduler ioScheduler,
      @Named("obs_wifi_manage") BooleanInterestObserver manageObserver) {
    return new WifiManagePreferencePresenterImpl(interactor, mainScheduler, ioScheduler,
        manageObserver);
  }

  @ActivityScope @Provides @Named("wifi_manage_pref_interactor")
  BaseManagePreferenceInteractor provideWifiManagePreferenceInteractor(
      @Named("mod_wifi_manage") BooleanInterestModifier manageModifier) {
    return new WifiManagePreferenceInteractorImpl(manageModifier);
  }
}
