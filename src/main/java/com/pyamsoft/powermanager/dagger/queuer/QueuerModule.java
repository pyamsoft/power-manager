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

package com.pyamsoft.powermanager.dagger.queuer;

import android.app.AlarmManager;
import android.support.annotation.NonNull;
import com.pyamsoft.powermanager.app.modifier.BooleanInterestModifier;
import com.pyamsoft.powermanager.app.observer.BooleanInterestObserver;
import dagger.Module;
import dagger.Provides;
import javax.inject.Named;
import rx.Scheduler;

@Module public class QueuerModule {

  @Provides @Named("queuer_wifi") Queuer provideWifiQueuer(@NonNull AlarmManager alarmManager,
      @NonNull @Named("sub") Scheduler subScheduler,
      @NonNull @Named("obs_wifi_state") BooleanInterestObserver stateObserver,
      @NonNull @Named("mod_wifi_state") BooleanInterestModifier stateModifier) {
    return new QueuerWifiImpl(alarmManager, subScheduler, stateObserver, stateModifier);
  }

  @Provides @Named("queuer_data") Queuer provideDataQueuer(@NonNull AlarmManager alarmManager,
      @NonNull @Named("sub") Scheduler subScheduler,
      @NonNull @Named("obs_data_state") BooleanInterestObserver stateObserver,
      @NonNull @Named("mod_data_state") BooleanInterestModifier stateModifier) {
    return new QueuerDataImpl(alarmManager, subScheduler, stateObserver, stateModifier);
  }

  @Provides @Named("queuer_bluetooth") Queuer provideBluetoothQueuer(@NonNull AlarmManager alarmManager,
      @NonNull @Named("sub") Scheduler subScheduler,
      @NonNull @Named("obs_bluetooth_state") BooleanInterestObserver stateObserver,
      @NonNull @Named("mod_bluetooth_state") BooleanInterestModifier stateModifier) {
    return new QueuerBluetoothImpl(alarmManager, subScheduler, stateObserver, stateModifier);
  }

  @Provides @Named("queuer_sync") Queuer provideSyncQueuer(@NonNull AlarmManager alarmManager,
      @NonNull @Named("sub") Scheduler subScheduler,
      @NonNull @Named("obs_sync_state") BooleanInterestObserver stateObserver,
      @NonNull @Named("mod_sync_state") BooleanInterestModifier stateModifier) {
    return new QueuerSyncImpl(alarmManager, subScheduler, stateObserver, stateModifier);
  }

  @Provides @Named("queuer_doze") Queuer provideDozeQueuer(@NonNull AlarmManager alarmManager,
      @NonNull @Named("sub") Scheduler subScheduler,
      @NonNull @Named("obs_doze_state") BooleanInterestObserver stateObserver,
      @NonNull @Named("mod_doze_state") BooleanInterestModifier stateModifier) {
    return new QueuerDozeImpl(alarmManager, subScheduler, stateObserver, stateModifier);
  }

  @Provides @Named("queuer_airplane") Queuer provideAirplaneQueuer(@NonNull AlarmManager alarmManager,
      @NonNull @Named("sub") Scheduler subScheduler,
      @NonNull @Named("obs_airplane_state") BooleanInterestObserver stateObserver,
      @NonNull @Named("mod_airplane_state") BooleanInterestModifier stateModifier) {
    return new QueuerAirplaneImpl(alarmManager, subScheduler, stateObserver, stateModifier);
  }
}
