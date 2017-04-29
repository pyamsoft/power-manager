/*
 * Copyright 2017 Peter Kenji Yamanaka
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

package com.pyamsoft.powermanager.manager;

import android.support.annotation.NonNull;
import com.pyamsoft.powermanager.base.preference.AirplanePreferences;
import com.pyamsoft.powermanager.base.preference.BluetoothPreferences;
import com.pyamsoft.powermanager.base.preference.DataPreferences;
import com.pyamsoft.powermanager.base.preference.DozePreferences;
import com.pyamsoft.powermanager.base.preference.SyncPreferences;
import com.pyamsoft.powermanager.base.preference.WearablePreferences;
import com.pyamsoft.powermanager.base.preference.WifiPreferences;
import com.pyamsoft.powermanager.job.JobQueuer;
import com.pyamsoft.powermanager.model.StateObserver;
import dagger.Module;
import dagger.Provides;
import javax.inject.Named;
import javax.inject.Singleton;

@Module public class ManagerSingletonModule {

  @Singleton @Provides @Named("wifi_manager_interactor")
  WearAwareManagerInteractor provideManagerWifiInteractor(@NonNull WifiPreferences wifiPreferences,
      @NonNull WearablePreferences wearablePreferences,
      @Named("obs_wifi_state") StateObserver stateObserver, @NonNull JobQueuer jobQueuer,
      @Named("obs_wear_state") StateObserver wearStateObserver) {
    return new ManagerWifiInteractor(wifiPreferences, wearablePreferences, stateObserver, jobQueuer,
        wearStateObserver);
  }

  @Singleton @Provides @Named("data_manager_interactor")
  ManagerInteractor provideManagerDataInteractor(@NonNull DataPreferences preferences,
      @NonNull JobQueuer jobQueuer, @Named("obs_data_state") StateObserver stateObserver) {
    return new ManagerDataInteractorImpl(preferences, stateObserver, jobQueuer);
  }

  @Singleton @Provides @Named("bluetooth_manager_interactor")
  WearAwareManagerInteractor provideManagerBluetoothInteractor(
      @NonNull BluetoothPreferences preferences, @NonNull WearablePreferences wearablePreferences,
      @Named("obs_bluetooth_state") StateObserver stateObserver, @NonNull JobQueuer jobQueuer,
      @Named("obs_wear_state") StateObserver wearStateObserver) {
    return new ManagerBluetoothInteractor(wearablePreferences, preferences, stateObserver,
        jobQueuer, wearStateObserver);
  }

  @Singleton @Provides @Named("sync_manager_interactor")
  ManagerInteractor provideManagerSyncInteractor(@NonNull SyncPreferences preferences,
      @NonNull JobQueuer jobQueuer, @Named("obs_sync_state") StateObserver stateObserver) {
    return new ManagerSyncInteractor(preferences, stateObserver, jobQueuer);
  }

  @Singleton @Provides @Named("doze_manager_interactor")
  ManagerInteractor provideManagerDozeInteractor(@NonNull DozePreferences preferences,
      @NonNull JobQueuer jobQueuer, @Named("obs_doze_state") StateObserver stateObserver) {
    return new ManagerDozeInteractorImpl(preferences, stateObserver, jobQueuer);
  }

  @Singleton @Provides @Named("airplane_manager_interactor")
  WearAwareManagerInteractor provideManagerAirplaneInteractor(
      @NonNull AirplanePreferences preferences, @NonNull WearablePreferences wearablePreferences,
      @Named("obs_airplane_state") StateObserver stateObserver, @NonNull JobQueuer jobQueuer,
      @Named("obs_wear_state") StateObserver wearStateObserver) {
    return new ManagerAirplaneInteractor(wearablePreferences, preferences, stateObserver, jobQueuer,
        wearStateObserver);
  }
}
