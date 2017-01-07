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

package com.pyamsoft.powermanager.manager.queuer;

import android.support.annotation.NonNull;
import com.pyamsoft.powermanager.base.logger.Logger;
import com.pyamsoft.powermanager.base.wrapper.JobQueuerWrapper;
import com.pyamsoft.powermanager.model.BooleanInterestModifier;
import com.pyamsoft.powermanager.model.BooleanInterestObserver;
import dagger.Module;
import dagger.Provides;
import javax.inject.Named;
import rx.Scheduler;

@Module public class QueuerModule {

  @Provides @Named("queuer_wifi") Queuer provideWifiQueuer(
      @NonNull JobQueuerWrapper jobQueuerWrapper, @NonNull @Named("sub") Scheduler subScheduler,
      @NonNull @Named("obs_wifi_state") BooleanInterestObserver stateObserver,
      @NonNull @Named("mod_wifi_state") BooleanInterestModifier stateModifier,
      @NonNull @Named("obs_charging_state") BooleanInterestObserver chargingObserver,
      @NonNull @Named("logger_wifi") Logger logger) {
    return new QueuerWifiImpl(jobQueuerWrapper, subScheduler, stateObserver, stateModifier,
        chargingObserver, logger);
  }

  @Provides @Named("queuer_data") Queuer provideDataQueuer(
      @NonNull JobQueuerWrapper jobQueuerWrapper, @NonNull @Named("sub") Scheduler subScheduler,
      @NonNull @Named("obs_data_state") BooleanInterestObserver stateObserver,
      @NonNull @Named("mod_data_state") BooleanInterestModifier stateModifier,
      @NonNull @Named("obs_charging_state") BooleanInterestObserver chargingObserver,
      @NonNull @Named("logger_data") Logger logger) {
    return new QueuerDataImpl(jobQueuerWrapper, subScheduler, stateObserver, stateModifier,
        chargingObserver, logger);
  }

  @Provides @Named("queuer_bluetooth") Queuer provideBluetoothQueuer(
      @NonNull JobQueuerWrapper jobQueuerWrapper, @NonNull @Named("sub") Scheduler subScheduler,
      @NonNull @Named("obs_bluetooth_state") BooleanInterestObserver stateObserver,
      @NonNull @Named("mod_bluetooth_state") BooleanInterestModifier stateModifier,
      @NonNull @Named("obs_charging_state") BooleanInterestObserver chargingObserver,
      @NonNull @Named("logger_bluetooth") Logger logger) {
    return new QueuerBluetoothImpl(jobQueuerWrapper, subScheduler, stateObserver, stateModifier,
        chargingObserver, logger);
  }

  @Provides @Named("queuer_sync") Queuer provideSyncQueuer(
      @NonNull JobQueuerWrapper jobQueuerWrapper, @NonNull @Named("sub") Scheduler subScheduler,
      @NonNull @Named("obs_sync_state") BooleanInterestObserver stateObserver,
      @NonNull @Named("mod_sync_state") BooleanInterestModifier stateModifier,
      @NonNull @Named("obs_charging_state") BooleanInterestObserver chargingObserver,
      @NonNull @Named("logger_sync") Logger logger) {
    return new QueuerSyncImpl(jobQueuerWrapper, subScheduler, stateObserver, stateModifier,
        chargingObserver, logger);
  }

  @Provides @Named("queuer_doze") Queuer provideDozeQueuer(
      @NonNull JobQueuerWrapper jobQueuerWrapper, @NonNull @Named("sub") Scheduler subScheduler,
      @NonNull @Named("obs_doze_state") BooleanInterestObserver stateObserver,
      @NonNull @Named("mod_doze_state") BooleanInterestModifier stateModifier,
      @NonNull @Named("obs_charging_state") BooleanInterestObserver chargingObserver,
      @NonNull @Named("logger_doze") Logger logger) {
    return new QueuerDozeImpl(jobQueuerWrapper, subScheduler, stateObserver, stateModifier,
        chargingObserver, logger);
  }

  @Provides @Named("queuer_airplane") Queuer provideAirplaneQueuer(
      @NonNull JobQueuerWrapper jobQueuerWrapper, @NonNull @Named("sub") Scheduler subScheduler,
      @NonNull @Named("obs_airplane_state") BooleanInterestObserver stateObserver,
      @NonNull @Named("mod_airplane_state") BooleanInterestModifier stateModifier,
      @NonNull @Named("obs_charging_state") BooleanInterestObserver chargingObserver,
      @NonNull @Named("logger_airplane") Logger logger) {
    return new QueuerAirplaneImpl(jobQueuerWrapper, subScheduler, stateObserver, stateModifier,
        chargingObserver, logger);
  }
}
