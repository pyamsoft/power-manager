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
import com.pyamsoft.powermanager.app.logger.Logger;
import com.pyamsoft.powermanager.app.modifier.BooleanInterestModifier;
import com.pyamsoft.powermanager.app.observer.BooleanInterestObserver;
import com.pyamsoft.powermanager.app.service.ForegroundPresenter;
import com.pyamsoft.powermanager.app.wrapper.JobSchedulerCompat;
import com.pyamsoft.powermanager.app.wrapper.PowerTriggerDB;
import dagger.Module;
import dagger.Provides;
import javax.inject.Named;
import javax.inject.Singleton;
import rx.Scheduler;

@Module public class ForegroundModule {

  @Singleton @Provides ForegroundPresenter provideForegroundPresenter(
      @NonNull ForegroundInteractor interactor, @Named("obs") Scheduler obsScheduler,
      @Named("sub") Scheduler subScheduler) {
    return new ForegroundPresenterImpl(interactor, obsScheduler, subScheduler);
  }

  @Singleton @Provides ForegroundInteractor provideForegroundInteractor(@NonNull Context context,
      @NonNull JobSchedulerCompat jobManager, @NonNull PowerManagerPreferences preferences,
      @NonNull PowerTriggerDB powerTriggerDB, @Named("logger_manager") @NonNull Logger logger,
      @NonNull @Named("obs_wifi_state") BooleanInterestObserver wifiObserver,
      @NonNull @Named("obs_data_state") BooleanInterestObserver dataObserver,
      @NonNull @Named("obs_bluetooth_state") BooleanInterestObserver bluetoothObserver,
      @NonNull @Named("obs_sync_state") BooleanInterestObserver syncObserver,
      @NonNull @Named("mod_wifi_state") BooleanInterestModifier wifiModifier,
      @NonNull @Named("mod_data_state") BooleanInterestModifier dataModifier,
      @NonNull @Named("mod_bluetooth_state") BooleanInterestModifier bluetoothModifier,
      @NonNull @Named("mod_sync_state") BooleanInterestModifier syncModifier) {
    return new ForegroundInteractorImpl(jobManager, context, preferences, wifiObserver,
        dataObserver, bluetoothObserver, syncObserver, wifiModifier, dataModifier,
        bluetoothModifier, syncModifier, powerTriggerDB, logger);
  }
}
