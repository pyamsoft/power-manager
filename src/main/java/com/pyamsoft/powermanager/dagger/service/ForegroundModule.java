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
import com.pyamsoft.powermanager.app.modifier.BooleanInterestModifier;
import com.pyamsoft.powermanager.app.observer.BooleanInterestObserver;
import com.pyamsoft.powermanager.app.service.ForegroundPresenter;
import dagger.Module;
import dagger.Provides;
import javax.inject.Named;
import javax.inject.Singleton;
import rx.Scheduler;

@Module public class ForegroundModule {

  @Singleton @Provides ForegroundPresenter provideForegroundPresenter(
      @NonNull ForegroundInteractor interactor, @Named("main") Scheduler mainScheduler,
      @Named("io") Scheduler ioScheduler) {
    return new ForegroundPresenterImpl(interactor, mainScheduler, ioScheduler);
  }

  @Singleton @Provides ForegroundInteractor provideForegroundInteractor(@NonNull Context context,
      @NonNull PowerManagerPreferences preferences,
      @Named("obs_wifi_manage") @NonNull BooleanInterestObserver wifiManageObserver,
      @Named("obs_data_manage") @NonNull BooleanInterestObserver dataManageObserver,
      @Named("obs_bluetooth_manage") @NonNull BooleanInterestObserver bluetoothManageObserver,
      @Named("obs_sync_manage") @NonNull BooleanInterestObserver syncManageObserver,
      @Named("obs_wear_manage") @NonNull BooleanInterestObserver wearManageObserver,
      @Named("obs_doze_manage") @NonNull BooleanInterestObserver dozeManageObserver,
      @Named("mod_wifi_manage") @NonNull BooleanInterestModifier wifiManageModifier,
      @Named("mod_data_manage") @NonNull BooleanInterestModifier dataManageModifier,
      @Named("mod_bluetooth_manage") @NonNull BooleanInterestModifier bluetoothManageModifier,
      @Named("mod_sync_manage") @NonNull BooleanInterestModifier syncManageModifier,
      @Named("mod_wear_manage") @NonNull BooleanInterestModifier wearManageModifier,
      @Named("mod_doze_manage") @NonNull BooleanInterestModifier dozeManageModifier) {
    return new ForegroundInteractorImpl(context, preferences, wifiManageObserver,
        dataManageObserver, bluetoothManageObserver, syncManageObserver, wearManageObserver,
        dozeManageObserver, wifiManageModifier, dataManageModifier, bluetoothManageModifier,
        syncManageModifier, wearManageModifier, dozeManageModifier);
  }
}
