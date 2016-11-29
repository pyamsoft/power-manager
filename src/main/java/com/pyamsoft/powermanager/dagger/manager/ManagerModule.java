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
import com.pyamsoft.powermanager.app.observer.BooleanInterestObserver;
import com.pyamsoft.powermanager.app.observer.PermissionObserver;
import com.pyamsoft.powermanager.dagger.queuer.Queuer;
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
  WearAwareManagerInteractor provideManagerWifiInteractor(
      @NonNull @Named("queuer_wifi") Queuer queuer, @NonNull PowerManagerPreferences preferences,
      @NonNull @Named("logger_wifi") Logger logger,
      @Named("obs_wifi_manage") BooleanInterestObserver manageObserver,
      @Named("obs_wifi_state") BooleanInterestObserver stateObserver,
      @Named("obs_wear_manage") BooleanInterestObserver wearManageObserver,
      @Named("obs_wear_state") BooleanInterestObserver wearStateObserver) {
    return new ManagerWifiInteractorImpl(queuer, preferences, manageObserver, stateObserver,
        wearManageObserver, wearStateObserver, logger);
  }

  @Provides @Named("data_manager") Manager provideManagerData(
      @Named("data_manager_interactor") @NonNull ManagerInteractor interactor,
      @Named("sub") Scheduler subScheduler, @Named("obs") Scheduler obsScheduler) {
    return new ManagerDataImpl(interactor, obsScheduler, subScheduler);
  }

  @Provides @Named("data_manager_interactor") ManagerInteractor provideManagerDataInteractor(
      @NonNull @Named("queuer_data") Queuer queuer, @NonNull PowerManagerPreferences preferences,
      @NonNull @Named("logger_data") Logger logger,
      @Named("obs_data_manage") BooleanInterestObserver manageObserver,
      @Named("obs_data_state") BooleanInterestObserver stateObserver,
      @Named("obs_root_permission") PermissionObserver rootPermissionObserver) {
    return new ManagerDataInteractorImpl(queuer, preferences, manageObserver, stateObserver,
        rootPermissionObserver, logger);
  }

  @Provides @Named("bluetooth_manager") Manager provideManagerBluetooth(
      @Named("bluetooth_manager_interactor") @NonNull WearAwareManagerInteractor interactor,
      @Named("sub") Scheduler subScheduler, @Named("obs") Scheduler obsScheduler) {
    return new ManagerBluetoothImpl(interactor, obsScheduler, subScheduler);
  }

  @Provides @Named("bluetooth_manager_interactor")
  WearAwareManagerInteractor provideManagerBluetoothInteractor(
      @NonNull @Named("queuer_bluetooth") Queuer queuer,
      @NonNull PowerManagerPreferences preferences,
      @NonNull @Named("logger_bluetooth") Logger logger,
      @Named("obs_bluetooth_manage") BooleanInterestObserver manageObserver,
      @Named("obs_bluetooth_state") BooleanInterestObserver stateObserver,
      @Named("obs_wear_manage") BooleanInterestObserver wearManageObserver,
      @Named("obs_wear_state") BooleanInterestObserver wearStateObserver) {
    return new ManagerBluetoothInteractorImpl(queuer, preferences, manageObserver, stateObserver,
        wearManageObserver, wearStateObserver, logger);
  }

  @Provides @Named("sync_manager") Manager provideManagerSync(
      @Named("sync_manager_interactor") @NonNull ManagerInteractor interactor,
      @Named("sub") Scheduler subScheduler, @Named("obs") Scheduler obsScheduler) {
    return new ManagerSyncImpl(interactor, obsScheduler, subScheduler);
  }

  @Provides @Named("sync_manager_interactor") ManagerInteractor provideManagerSyncInteractor(
      @NonNull @Named("queuer_sync") Queuer queuer, @NonNull PowerManagerPreferences preferences,
      @NonNull @Named("logger_sync") Logger logger,
      @Named("obs_sync_manage") BooleanInterestObserver manageObserver,
      @Named("obs_sync_state") BooleanInterestObserver stateObserver) {
    return new ManagerSyncInteractorImpl(queuer, preferences, manageObserver, stateObserver,
        logger);
  }

  @Provides @Named("doze_manager") ExclusiveManager provideManagerDoze(
      @Named("doze_manager_interactor") @NonNull ExclusiveWearUnawareManagerInteractor interactor,
      @Named("sub") Scheduler subScheduler, @Named("obs") Scheduler obsScheduler) {
    return new ManagerDozeImpl(interactor, obsScheduler, subScheduler);
  }

  @Provides @Named("doze_manager_interactor")
  ExclusiveWearUnawareManagerInteractor provideManagerDozeInteractor(
      @NonNull PowerManagerPreferences preferences, @NonNull @Named("queuer_doze") Queuer queuer,
      @NonNull @Named("logger_doze") Logger logger,
      @Named("obs_doze_manage") BooleanInterestObserver manageObserver,
      @Named("obs_doze_state") BooleanInterestObserver stateObserver,
      @Named("obs_doze_permission") PermissionObserver dozePermissionObserver) {
    return new ManagerDozeInteractorImpl(queuer, preferences, manageObserver, stateObserver,
        dozePermissionObserver, logger);
  }

  @Provides @Named("airplane_manager") Manager provideManagerAirplane(
      @Named("airplane_manager_interactor") @NonNull WearAwareManagerInteractor interactor,
      @Named("sub") Scheduler subScheduler, @Named("obs") Scheduler obsScheduler) {
    return new ManagerAirplaneImpl(interactor, obsScheduler, subScheduler);
  }

  @Provides @Named("airplane_manager_interactor")
  WearAwareManagerInteractor provideManagerAirplaneInteractor(
      @NonNull PowerManagerPreferences preferences,
      @NonNull @Named("queuer_airplane") Queuer queuer,
      @NonNull @Named("logger_airplane") Logger logger,
      @Named("obs_airplane_manage") BooleanInterestObserver manageObserver,
      @Named("obs_airplane_state") BooleanInterestObserver stateObserver,
      @Named("obs_wear_manage") BooleanInterestObserver wearManageObserver,
      @Named("obs_wear_state") BooleanInterestObserver wearStateObserver,
      @Named("obs_root_permission") PermissionObserver rootPermissionObserver) {
    return new ManagerAirplaneInteractorImpl(queuer, preferences, manageObserver, stateObserver,
        wearManageObserver, wearStateObserver, rootPermissionObserver, logger);
  }
}
