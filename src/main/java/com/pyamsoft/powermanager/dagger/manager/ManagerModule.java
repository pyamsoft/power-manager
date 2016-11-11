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

package com.pyamsoft.powermanager.dagger.manager;

import android.support.annotation.NonNull;
import com.pyamsoft.powermanager.PowerManagerPreferences;
import com.pyamsoft.powermanager.app.logger.Logger;
import com.pyamsoft.powermanager.app.manager.ExclusiveManager;
import com.pyamsoft.powermanager.app.manager.Manager;
import com.pyamsoft.powermanager.app.modifier.BooleanInterestModifier;
import com.pyamsoft.powermanager.app.observer.BooleanInterestObserver;
import com.pyamsoft.powermanager.app.observer.PermissionObserver;
import com.pyamsoft.powermanager.app.wrapper.JobSchedulerCompat;
import dagger.Module;
import dagger.Provides;
import javax.inject.Named;
import rx.Scheduler;

@Module public class ManagerModule {

  @Provides @Named("wifi_manager") Manager provideManagerWifi(
      @Named("wifi_manager_interactor") @NonNull WearAwareManagerInteractor interactor,
      @Named("sub") Scheduler subScheduler, @Named("obs") Scheduler obsScheduler) {
    return new ManagerWifiImpl(interactor, obsScheduler, subScheduler);
  }

  @Provides @Named("wifi_manager_interactor")
  WearAwareManagerInteractor provideManagerWifiInteractor(@NonNull JobSchedulerCompat jobManager,
      @NonNull PowerManagerPreferences preferences, @NonNull @Named("logger_manager") Logger logger,
      @Named("obs_wifi_manage") BooleanInterestObserver manageObserver,
      @Named("obs_wifi_state") BooleanInterestObserver stateObserver,
      @Named("mod_wifi_state") BooleanInterestModifier stateModifier,
      @Named("obs_wear_manage") BooleanInterestObserver wearManageObserver,
      @Named("obs_wear_state") BooleanInterestObserver wearStateObserver) {
    return new ManagerWifiInteractorImpl(jobManager, preferences, manageObserver, stateObserver,
        stateModifier, wearManageObserver, wearStateObserver, logger);
  }

  @Provides @Named("data_manager") Manager provideManagerData(
      @Named("data_manager_interactor") @NonNull ManagerInteractor interactor,
      @Named("sub") Scheduler subScheduler, @Named("obs") Scheduler obsScheduler) {
    return new ManagerDataImpl(interactor, obsScheduler, subScheduler);
  }

  @Provides @Named("data_manager_interactor") ManagerInteractor provideManagerDataInteractor(
      @NonNull JobSchedulerCompat jobManager, @NonNull PowerManagerPreferences preferences,
      @NonNull @Named("logger_manager") Logger logger,
      @Named("obs_data_manage") BooleanInterestObserver manageObserver,
      @Named("obs_data_state") BooleanInterestObserver stateObserver,
      @Named("mod_data_state") BooleanInterestModifier stateModifier,
      @Named("obs_root_permission") PermissionObserver rootPermissionObserver) {
    return new ManagerDataInteractorImpl(jobManager, preferences, manageObserver, stateObserver,
        stateModifier, rootPermissionObserver, logger);
  }

  @Provides @Named("bluetooth_manager") Manager provideManagerBluetooth(
      @Named("bluetooth_manager_interactor") @NonNull WearAwareManagerInteractor interactor,
      @Named("sub") Scheduler subScheduler, @Named("obs") Scheduler obsScheduler) {
    return new ManagerBluetoothImpl(interactor, obsScheduler, subScheduler);
  }

  @Provides @Named("bluetooth_manager_interactor")
  WearAwareManagerInteractor provideManagerBluetoothInteractor(
      @NonNull JobSchedulerCompat jobManager, @NonNull PowerManagerPreferences preferences,
      @NonNull @Named("logger_manager") Logger logger,
      @Named("obs_bluetooth_manage") BooleanInterestObserver manageObserver,
      @Named("obs_bluetooth_state") BooleanInterestObserver stateObserver,
      @Named("mod_bluetooth_state") BooleanInterestModifier stateModifier,
      @Named("obs_wear_manage") BooleanInterestObserver wearManageObserver,
      @Named("obs_wear_state") BooleanInterestObserver wearStateObserver) {
    return new ManagerBluetoothInteractorImpl(jobManager, preferences, manageObserver,
        stateObserver, stateModifier, wearManageObserver, wearStateObserver, logger);
  }

  @Provides @Named("sync_manager") Manager provideManagerSync(
      @Named("sync_manager_interactor") @NonNull ManagerInteractor interactor,
      @Named("sub") Scheduler subScheduler, @Named("obs") Scheduler obsScheduler) {
    return new ManagerSyncImpl(interactor, obsScheduler, subScheduler);
  }

  @Provides @Named("sync_manager_interactor") ManagerInteractor provideManagerSyncInteractor(
      @NonNull JobSchedulerCompat jobManager, @NonNull PowerManagerPreferences preferences,
      @NonNull @Named("logger_manager") Logger logger,
      @Named("obs_sync_manage") BooleanInterestObserver manageObserver,
      @Named("obs_sync_state") BooleanInterestObserver stateObserver,
      @Named("mod_sync_state") BooleanInterestModifier stateModifier) {
    return new ManagerSyncInteractorImpl(jobManager, preferences, manageObserver, stateObserver,
        stateModifier, logger);
  }

  @Provides @Named("doze_manager") ExclusiveManager provideManagerDoze(
      @Named("doze_manager_interactor") @NonNull ExclusiveWearUnawareManagerInteractor interactor,
      @Named("sub") Scheduler subScheduler, @Named("obs") Scheduler obsScheduler) {
    return new ManagerDozeImpl(interactor, obsScheduler, subScheduler);
  }

  @Provides @Named("doze_manager_interactor")
  ExclusiveWearUnawareManagerInteractor provideManagerDozeInteractor(
      @NonNull PowerManagerPreferences preferences, @NonNull JobSchedulerCompat jobManager,
      @NonNull @Named("logger_manager") Logger logger,
      @Named("obs_doze_manage") BooleanInterestObserver manageObserver,
      @Named("obs_doze_state") BooleanInterestObserver stateObserver,
      @Named("mod_doze_state") BooleanInterestModifier stateModifier,
      @Named("obs_doze_permission") PermissionObserver dozePermissionObserver) {
    return new ManagerDozeInteractorImpl(jobManager, preferences, manageObserver, stateObserver,
        stateModifier, dozePermissionObserver, logger);
  }

  @Provides @Named("airplane_manager") Manager provideManagerAirplane(
      @Named("airplane_manager_interactor") @NonNull WearAwareManagerInteractor interactor,
      @Named("sub") Scheduler subScheduler, @Named("obs") Scheduler obsScheduler) {
    return new ManagerAirplaneImpl(interactor, obsScheduler, subScheduler);
  }

  @Provides @Named("airplane_manager_interactor")
  WearAwareManagerInteractor provideManagerAirplaneInteractor(
      @NonNull PowerManagerPreferences preferences, @NonNull JobSchedulerCompat jobManager,
      @NonNull @Named("logger_manager") Logger logger,
      @Named("obs_airplane_manage") BooleanInterestObserver manageObserver,
      @Named("obs_airplane_state") BooleanInterestObserver stateObserver,
      @Named("mod_airplane_state") BooleanInterestModifier stateModifier,
      @Named("obs_wear_manage") BooleanInterestObserver wearManageObserver,
      @Named("obs_wear_state") BooleanInterestObserver wearStateObserver,
      @Named("obs_root_permission") PermissionObserver rootPermissionObserver) {
    return new ManagerAirplaneInteractorImpl(jobManager, preferences, manageObserver, stateObserver,
        stateModifier, wearManageObserver, wearStateObserver, rootPermissionObserver, logger);
  }
}
