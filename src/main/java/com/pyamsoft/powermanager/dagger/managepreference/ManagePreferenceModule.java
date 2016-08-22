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

package com.pyamsoft.powermanager.dagger.managepreference;

import com.pyamsoft.powermanager.app.observer.InterestObserver;
import com.pyamsoft.powermanager.dagger.ActivityScope;
import dagger.Module;
import dagger.Provides;
import javax.inject.Named;
import rx.Scheduler;

@Module public class ManagePreferenceModule {

  @ActivityScope @Provides DozeManagePreferencePresenter provideDozeManagePreferencePresenter(
      @Named("main") Scheduler mainScheduler, @Named("io") Scheduler ioScheduler,
      @Named("obs_doze_manage") InterestObserver manageObserver) {
    return new DozeManagePreferencePresenter(mainScheduler, ioScheduler, manageObserver);
  }

  @ActivityScope @Provides WifiManagePreferencePresenter provideWifiManagePreferencePresenter(
      @Named("main") Scheduler mainScheduler, @Named("io") Scheduler ioScheduler,
      @Named("obs_wifi_manage") InterestObserver manageObserver) {
    return new WifiManagePreferencePresenter(mainScheduler, ioScheduler, manageObserver);
  }
}
