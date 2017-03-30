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

package com.pyamsoft.powermanager.base.observer.preference.preference;

import android.support.annotation.NonNull;
import com.pyamsoft.powermanager.base.PowerManagerPreferences;
import com.pyamsoft.powermanager.model.overlord.StateObserver;
import dagger.Module;
import dagger.Provides;
import javax.inject.Named;
import javax.inject.Singleton;

@Module public class PeriodicObserverModule {

  @Singleton @Named("obs_wifi_periodic") @Provides StateObserver provideWifiObserver(
      @NonNull PowerManagerPreferences preferences) {
    return new WifiPeriodicObserver(preferences);
  }

  @Singleton @Named("obs_data_periodic") @Provides StateObserver provideDataObserver(
      @NonNull PowerManagerPreferences preferences) {
    return new DataPeriodicObserver(preferences);
  }

  @Singleton @Named("obs_bluetooth_periodic") @Provides StateObserver provideBluetoothObserver(
      @NonNull PowerManagerPreferences preferences) {
    return new BluetoothPeriodicObserver(preferences);
  }

  @Singleton @Named("obs_sync_periodic") @Provides StateObserver provideSyncObserver(
      @NonNull PowerManagerPreferences preferences) {
    return new SyncPeriodicObserver(preferences);
  }

  @Singleton @Named("obs_airplane_periodic") @Provides StateObserver provideAirplaneObserver(
      @NonNull PowerManagerPreferences preferences) {
    return new AirplanePeriodicObserver(preferences);
  }

  @Singleton @Named("obs_doze_periodic") @Provides StateObserver provideDozeObserver(
      @NonNull PowerManagerPreferences preferences) {
    return new DozePeriodicObserver(preferences);
  }
}
