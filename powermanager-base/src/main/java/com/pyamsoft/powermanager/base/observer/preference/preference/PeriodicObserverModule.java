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

import android.content.Context;
import android.support.annotation.NonNull;
import com.pyamsoft.powermanager.base.PowerManagerPreferences;
import com.pyamsoft.powermanager.model.StateChangeObserver;
import dagger.Module;
import dagger.Provides;
import javax.inject.Named;
import javax.inject.Singleton;

@Module public class PeriodicObserverModule {

  @Singleton @Named("obs_wifi_periodic") @Provides StateChangeObserver provideWifiObserver(
      @NonNull Context context, @NonNull PowerManagerPreferences preferences) {
    return new WifiPeriodicObserver(context, preferences);
  }

  @Singleton @Named("obs_data_periodic") @Provides StateChangeObserver provideDataObserver(
      @NonNull Context context, @NonNull PowerManagerPreferences preferences) {
    return new DataPeriodicObserver(context, preferences);
  }

  @Singleton @Named("obs_bluetooth_periodic") @Provides StateChangeObserver provideBluetoothObserver(@NonNull Context context,
      @NonNull PowerManagerPreferences preferences) {
    return new BluetoothPeriodicObserver(context, preferences);
  }

  @Singleton @Named("obs_sync_periodic") @Provides StateChangeObserver provideSyncObserver(
      @NonNull Context context, @NonNull PowerManagerPreferences preferences) {
    return new SyncPeriodicObserver(context, preferences);
  }

  @Singleton @Named("obs_airplane_periodic") @Provides StateChangeObserver provideAirplaneObserver(@NonNull Context context,
      @NonNull PowerManagerPreferences preferences) {
    return new AirplanePeriodicObserver(context, preferences);
  }

  @Singleton @Named("obs_doze_periodic") @Provides StateChangeObserver provideDozeObserver(
      @NonNull Context context, @NonNull PowerManagerPreferences preferences) {
    return new DozePeriodicObserver(context, preferences);
  }
}
