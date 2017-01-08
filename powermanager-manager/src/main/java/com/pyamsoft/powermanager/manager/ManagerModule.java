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
import com.pyamsoft.powermanager.model.BooleanInterestModifier;
import com.pyamsoft.powermanager.model.BooleanInterestObserver;
import com.pyamsoft.powermanager.model.ExclusiveManager;
import com.pyamsoft.powermanager.model.Manager;
import dagger.Module;
import dagger.Provides;
import javax.inject.Named;
import rx.Scheduler;

@Module class ManagerModule {

  @Provides @Named("wifi_manager") Manager provideManagerWifi(
      @Named("wifi_manager_interactor") @NonNull WearAwareManagerInteractor interactor,
      @Named("sub") Scheduler subScheduler, @Named("obs") Scheduler obsScheduler) {
    return new WearAwareManagerImpl(interactor, obsScheduler, subScheduler);
  }

  @Provides @Named("wifi_manager_interactor")
  WearAwareManagerInteractor provideManagerWifiInteractor(
      @NonNull PowerManagerPreferences preferences,
      @Named("obs_wifi_manage") BooleanInterestObserver manageObserver,
      @Named("obs_wifi_state") BooleanInterestObserver stateObserver,
      @Named("mod_wifi_state") BooleanInterestModifier stateModifier, @NonNull JobQueuer jobQueuer,
      @Named("obs_wear_manage") BooleanInterestObserver wearManageObserver,
      @Named("obs_wear_state") BooleanInterestObserver wearStateObserver) {
    return new ManagerWifiInteractorImpl(preferences, manageObserver, stateObserver, stateModifier,
        jobQueuer, wearManageObserver, wearStateObserver);
  }

  @Provides @Named("data_manager") Manager provideManagerData(
      @Named("data_manager_interactor") @NonNull ManagerInteractor interactor,
      @Named("sub") Scheduler subScheduler, @Named("obs") Scheduler obsScheduler) {
    return new WearUnawareManagerImpl(interactor, obsScheduler, subScheduler);
  }

  @Provides @Named("data_manager_interactor") ManagerInteractor provideManagerDataInteractor(
      @NonNull PowerManagerPreferences preferences,
      @Named("obs_data_manage") BooleanInterestObserver manageObserver,
      @Named("mod_data_state") BooleanInterestModifier stateModifier, @NonNull JobQueuer jobQueuer,
      @Named("obs_data_state") BooleanInterestObserver stateObserver) {
    return new ManagerDataInteractorImpl(preferences, manageObserver, stateObserver, stateModifier,
        jobQueuer);
  }

  @Provides @Named("bluetooth_manager") Manager provideManagerBluetooth(
      @Named("bluetooth_manager_interactor") @NonNull WearAwareManagerInteractor interactor,
      @Named("sub") Scheduler subScheduler, @Named("obs") Scheduler obsScheduler) {
    return new WearAwareManagerImpl(interactor, obsScheduler, subScheduler);
  }

  @Provides @Named("bluetooth_manager_interactor")
  WearAwareManagerInteractor provideManagerBluetoothInteractor(

      @NonNull PowerManagerPreferences preferences,
      @Named("obs_bluetooth_manage") BooleanInterestObserver manageObserver,
      @Named("obs_bluetooth_state") BooleanInterestObserver stateObserver,
      @Named("mod_bluetooth_state") BooleanInterestModifier stateModifier,
      @NonNull JobQueuer jobQueuer,
      @Named("obs_wear_manage") BooleanInterestObserver wearManageObserver,
      @Named("obs_wear_state") BooleanInterestObserver wearStateObserver) {
    return new ManagerBluetoothInteractorImpl(preferences, manageObserver, stateObserver,
        stateModifier, jobQueuer, wearManageObserver, wearStateObserver);
  }

  @Provides @Named("sync_manager") Manager provideManagerSync(
      @Named("sync_manager_interactor") @NonNull ManagerInteractor interactor,
      @Named("sub") Scheduler subScheduler, @Named("obs") Scheduler obsScheduler) {
    return new WearUnawareManagerImpl(interactor, obsScheduler, subScheduler);
  }

  @Provides @Named("sync_manager_interactor") ManagerInteractor provideManagerSyncInteractor(
      @NonNull PowerManagerPreferences preferences,
      @Named("mod_sync_state") BooleanInterestModifier stateModifier, @NonNull JobQueuer jobQueuer,
      @Named("obs_sync_manage") BooleanInterestObserver manageObserver,
      @Named("obs_sync_state") BooleanInterestObserver stateObserver) {
    return new ManagerSyncInteractorImpl(preferences, manageObserver, stateObserver, stateModifier,
        jobQueuer);
  }

  @Provides @Named("doze_manager") ExclusiveManager provideManagerDoze(
      @Named("doze_manager_interactor") @NonNull ExclusiveWearUnawareManagerInteractor interactor,
      @Named("sub") Scheduler subScheduler, @Named("obs") Scheduler obsScheduler) {
    return new ExclusiveWearUnawareManagerImpl(interactor, obsScheduler, subScheduler);
  }

  @Provides @Named("doze_manager_interactor")
  ExclusiveWearUnawareManagerInteractor provideManagerDozeInteractor(
      @NonNull PowerManagerPreferences preferences,
      @Named("mod_doze_state") BooleanInterestModifier stateModifier, @NonNull JobQueuer jobQueuer,
      @Named("obs_doze_manage") BooleanInterestObserver manageObserver,
      @Named("obs_doze_state") BooleanInterestObserver stateObserver) {
    return new ManagerDozeInteractorImpl(preferences, manageObserver, stateObserver, stateModifier,
        jobQueuer);
  }

  @Provides @Named("airplane_manager") Manager provideManagerAirplane(
      @Named("airplane_manager_interactor") @NonNull WearAwareManagerInteractor interactor,
      @Named("sub") Scheduler subScheduler, @Named("obs") Scheduler obsScheduler) {
    return new WearAwareManagerImpl(interactor, obsScheduler, subScheduler);
  }

  @Provides @Named("airplane_manager_interactor")
  WearAwareManagerInteractor provideManagerAirplaneInteractor(
      @NonNull PowerManagerPreferences preferences,
      @Named("obs_airplane_manage") BooleanInterestObserver manageObserver,
      @Named("obs_airplane_state") BooleanInterestObserver stateObserver,
      @Named("mod_airplane_state") BooleanInterestModifier stateModifier,
      @NonNull JobQueuer jobQueuer,
      @Named("obs_wear_manage") BooleanInterestObserver wearManageObserver,
      @Named("obs_wear_state") BooleanInterestObserver wearStateObserver) {
    return new ManagerAirplaneInteractorImpl(preferences, manageObserver, stateObserver,
        stateModifier, jobQueuer, wearManageObserver, wearStateObserver);
  }
}
