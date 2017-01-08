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

package com.pyamsoft.powermanager.service;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.support.annotation.NonNull;
import com.pyamsoft.powermanager.base.PowerManagerPreferences;
import com.pyamsoft.powermanager.base.db.PowerTriggerDB;
import com.pyamsoft.powermanager.base.jobs.JobQueuer;
import com.pyamsoft.powermanager.model.BooleanInterestModifier;
import com.pyamsoft.powermanager.model.BooleanInterestObserver;
import com.pyamsoft.powermanager.model.Logger;
import dagger.Module;
import dagger.Provides;
import javax.inject.Named;
import rx.Scheduler;

@Module class ForegroundModule {

  @Provides ForegroundPresenter provideForegroundPresenter(@NonNull ForegroundInteractor interactor,
      @Named("obs") Scheduler obsScheduler, @Named("sub") Scheduler subScheduler) {
    return new ForegroundPresenterImpl(interactor, obsScheduler, subScheduler);
  }

  @Provides ForegroundInteractor provideForegroundInteractor(@NonNull Context context,
      @NonNull JobQueuer jobQueuer, @NonNull PowerManagerPreferences preferences,
      @Named("main") Class<? extends Activity> mainActivityClass,
      @Named("toggle") Class<? extends Service> toggleServiceClass,
      @NonNull PowerTriggerDB powerTriggerDB,
      @NonNull @Named("obs_charging_state") BooleanInterestObserver chargingObserver,
      @Named("logger_trigger") @NonNull Logger triggerLogger,
      @NonNull @Named("obs_wifi_state") BooleanInterestObserver wifiObserver,
      @Named("obs_data_state") @NonNull BooleanInterestObserver dataObserver,
      @NonNull @Named("obs_bluetooth_state") BooleanInterestObserver bluetoothObserver,
      @NonNull @Named("obs_sync_state") BooleanInterestObserver syncObserver,
      @Named("mod_wifi_state") @NonNull BooleanInterestModifier wifiModifier,
      @NonNull @Named("mod_data_state") BooleanInterestModifier dataModifier,
      @NonNull @Named("mod_bluetooth_state") BooleanInterestModifier bluetoothModifier,
      @NonNull @Named("mod_sync_state") BooleanInterestModifier syncModifier) {
    return new ForegroundInteractorImpl(jobQueuer, context, preferences, mainActivityClass,
        toggleServiceClass, powerTriggerDB, chargingObserver, triggerLogger, wifiObserver,
        dataObserver, bluetoothObserver, syncObserver, wifiModifier, dataModifier,
        bluetoothModifier, syncModifier);
  }
}
