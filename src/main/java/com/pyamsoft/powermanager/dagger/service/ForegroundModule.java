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

package com.pyamsoft.powermanager.dagger.service;

import android.content.Context;
import android.support.annotation.NonNull;
import com.pyamsoft.powermanager.PowerManagerPreferences;
import com.pyamsoft.powermanager.app.modifier.InterestModifier;
import com.pyamsoft.powermanager.app.observer.InterestObserver;
import com.pyamsoft.powermanager.dagger.ActivityScope;
import dagger.Module;
import dagger.Provides;
import javax.inject.Named;
import rx.Scheduler;

@Module public class ForegroundModule {

  @ActivityScope @Provides ForegroundPresenter provideForegroundPresenter(
      @NonNull ForegroundInteractor interactor, @Named("main") Scheduler mainScheduler,
      @Named("io") Scheduler ioScheduler) {
    return new ForegroundPresenter(interactor, mainScheduler, ioScheduler);
  }

  @ActivityScope @Provides ForegroundInteractor provideForegroundInteractor(
      @NonNull Context context, @NonNull PowerManagerPreferences preferences,
      @Named("obs_wifi_manage") @NonNull InterestObserver wifiManageObserver,
      @Named("obs_data_manage") @NonNull InterestObserver dataManageObserver,
      @Named("obs_bluetooth_manage") @NonNull InterestObserver bluetoothManageObserver,
      @Named("obs_sync_manage") @NonNull InterestObserver syncManageObserver,
      @Named("obs_wear_manage") @NonNull InterestObserver wearManageObserver,
      @Named("mod_wifi_manage") @NonNull InterestModifier wifiManageModifier,
      @Named("mod_data_manage") @NonNull InterestModifier dataManageModifier,
      @Named("mod_bluetooth_manage") @NonNull InterestModifier bluetoothManageModifier,
      @Named("mod_sync_manage") @NonNull InterestModifier syncManageModifier,
      @Named("mod_wear_manage") @NonNull InterestModifier wearManageModifier) {
    return new ForegroundInteractorImpl(context, preferences, wifiManageObserver,
        dataManageObserver, bluetoothManageObserver, syncManageObserver, wearManageObserver,
        wifiManageModifier, dataManageModifier, bluetoothManageModifier, syncManageModifier,
        wearManageModifier);
  }
}
