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

package com.pyamsoft.powermanager.manager;

import android.support.annotation.NonNull;
import com.pyamsoft.powermanager.base.PowerManagerPreferences;
import com.pyamsoft.powermanager.base.wrapper.ConnectedDeviceFunctionWrapper;
import com.pyamsoft.powermanager.job.JobQueuer;
import com.pyamsoft.powermanager.model.overlord.StateObserver;
import dagger.Module;
import dagger.Provides;
import io.reactivex.Scheduler;
import javax.inject.Named;

@Module public class ManagerModule {

  @Provides @Named("wifi_manager") Manager provideManagerWifi(
      @Named("wifi_manager_interactor") @NonNull WearAwareManagerInteractor interactor,
      @Named("sub") Scheduler subScheduler) {
    return new Manager(interactor, subScheduler);
  }

  @Provides @Named("wifi_manager_interactor")
  WearAwareManagerInteractor provideManagerWifiInteractor(
      @Named("wrapper_wifi") ConnectedDeviceFunctionWrapper wrapper,
      @NonNull PowerManagerPreferences preferences,
      @Named("obs_wifi_state") StateObserver stateObserver, @NonNull JobQueuer jobQueuer,
      @Named("obs_wear_state") StateObserver wearStateObserver) {
    return new ManagerWifiInteractor(wrapper, preferences, stateObserver, jobQueuer,
        wearStateObserver);
  }

  @Provides @Named("data_manager") Manager provideManagerData(
      @Named("data_manager_interactor") @NonNull ManagerInteractor interactor,
      @Named("sub") Scheduler subScheduler) {
    return new Manager(interactor, subScheduler);
  }

  @Provides @Named("data_manager_interactor") ManagerInteractor provideManagerDataInteractor(
      @NonNull PowerManagerPreferences preferences, @NonNull JobQueuer jobQueuer,
      @Named("obs_data_state") StateObserver stateObserver) {
    return new ManagerDataInteractorImpl(preferences, stateObserver, jobQueuer);
  }

  @Provides @Named("bluetooth_manager") Manager provideManagerBluetooth(
      @Named("bluetooth_manager_interactor") @NonNull WearAwareManagerInteractor interactor,
      @Named("sub") Scheduler subScheduler) {
    return new Manager(interactor, subScheduler);
  }

  @Provides @Named("bluetooth_manager_interactor")
  WearAwareManagerInteractor provideManagerBluetoothInteractor(
      @NonNull PowerManagerPreferences preferences,
      @Named("obs_bluetooth_state") StateObserver stateObserver, @NonNull JobQueuer jobQueuer,
      @Named("obs_wear_state") StateObserver wearStateObserver) {
    return new ManagerBluetoothInteractor(preferences, stateObserver, jobQueuer, wearStateObserver);
  }

  @Provides @Named("sync_manager") Manager provideManagerSync(
      @Named("sync_manager_interactor") @NonNull ManagerInteractor interactor,
      @Named("sub") Scheduler subScheduler) {
    return new Manager(interactor, subScheduler);
  }

  @Provides @Named("sync_manager_interactor") ManagerInteractor provideManagerSyncInteractor(
      @NonNull PowerManagerPreferences preferences, @NonNull JobQueuer jobQueuer,
      @Named("obs_sync_state") StateObserver stateObserver) {
    return new ManagerSyncInteractor(preferences, stateObserver, jobQueuer);
  }

  @Provides @Named("doze_manager") Manager provideManagerDoze(
      @Named("doze_manager_interactor") @NonNull ManagerInteractor interactor,
      @Named("sub") Scheduler subScheduler) {
    return new Manager(interactor, subScheduler);
  }

  @Provides @Named("doze_manager_interactor") ManagerInteractor provideManagerDozeInteractor(
      @NonNull PowerManagerPreferences preferences, @NonNull JobQueuer jobQueuer,
      @Named("obs_doze_state") StateObserver stateObserver) {
    return new ManagerDozeInteractorImpl(preferences, stateObserver, jobQueuer);
  }

  @Provides @Named("airplane_manager") Manager provideManagerAirplane(
      @Named("airplane_manager_interactor") @NonNull WearAwareManagerInteractor interactor,
      @Named("sub") Scheduler subScheduler) {
    return new Manager(interactor, subScheduler);
  }

  @Provides @Named("airplane_manager_interactor")
  WearAwareManagerInteractor provideManagerAirplaneInteractor(
      @NonNull PowerManagerPreferences preferences,
      @Named("obs_airplane_state") StateObserver stateObserver, @NonNull JobQueuer jobQueuer,
      @Named("obs_wear_state") StateObserver wearStateObserver) {
    return new ManagerAirplaneInteractor(preferences, stateObserver, jobQueuer, wearStateObserver);
  }
}
