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

package com.pyamsoft.powermanager.dagger.manager;

import android.content.Context;
import android.support.annotation.NonNull;
import com.pyamsoft.powermanager.PowerManagerPreferences;
import com.pyamsoft.powermanager.app.manager.Manager;
import com.pyamsoft.powermanager.app.observer.InterestObserver;
import com.pyamsoft.powermanager.dagger.ActivityScope;
import dagger.Module;
import dagger.Provides;
import javax.inject.Named;
import rx.Scheduler;

@Module public class ManagerModule {

  @ActivityScope @Provides @Named("wifi_manager") Manager provideManagerWifi(
      @NonNull ManagerInteractor interactor, @Named("io") Scheduler ioScheduler,
      @Named("main") Scheduler mainScheduler) {
    return new ManagerWifi(interactor, ioScheduler, mainScheduler);
  }

  @ActivityScope @Provides ManagerInteractor provideManagerWifiInteractor(@NonNull Context context,
      @NonNull PowerManagerPreferences preferences,
      @Named("obs_wifi_state") InterestObserver interestObserver) {
    return new ManagerWifiInteractor(context, preferences, interestObserver);
  }
}
