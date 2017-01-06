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
import com.pyamsoft.powermanager.model.BooleanInterestObserver;
import com.pyamsoft.powermanager.model.ExclusiveManager;
import com.pyamsoft.powermanager.model.Manager;
import com.pyamsoft.powermanager.queuer.Queuer;
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
      @NonNull @Named("queuer_wifi") Queuer queuer, @NonNull PowerManagerPreferences preferences,
      @Named("obs_wifi_manage") BooleanInterestObserver manageObserver,
      @Named("obs_wifi_state") BooleanInterestObserver stateObserver,
      @Named("obs_wear_manage") BooleanInterestObserver wearManageObserver,
      @Named("obs_wear_state") BooleanInterestObserver wearStateObserver) {
    return new ManagerWifiInteractorImpl(queuer, preferences, manageObserver, stateObserver,
        wearManageObserver, wearStateObserver);
  }

  @Provides @Named("data_manager") Manager provideManagerData(
      @Named("data_manager_interactor") @NonNull ManagerInteractor interactor,
      @Named("sub") Scheduler subScheduler, @Named("obs") Scheduler obsScheduler) {
    return new WearUnawareManagerImpl(interactor, obsScheduler, subScheduler);
  }

  @Provides @Named("data_manager_interactor") ManagerInteractor provideManagerDataInteractor(
      @NonNull @Named("queuer_data") Queuer queuer, @NonNull PowerManagerPreferences preferences,
      @Named("obs_data_manage") BooleanInterestObserver manageObserver,
      @Named("obs_data_state") BooleanInterestObserver stateObserver) {
    return new ManagerDataInteractorImpl(queuer, preferences, manageObserver, stateObserver);
  }

  @Provides @Named("bluetooth_manager") Manager provideManagerBluetooth(
      @Named("bluetooth_manager_interactor") @NonNull WearAwareManagerInteractor interactor,
      @Named("sub") Scheduler subScheduler, @Named("obs") Scheduler obsScheduler) {
    return new WearAwareManagerImpl(interactor, obsScheduler, subScheduler);
  }

  @Provides @Named("bluetooth_manager_interactor")
  WearAwareManagerInteractor provideManagerBluetoothInteractor(
      @NonNull @Named("queuer_bluetooth") Queuer queuer,
      @NonNull PowerManagerPreferences preferences,
      @Named("obs_bluetooth_manage") BooleanInterestObserver manageObserver,
      @Named("obs_bluetooth_state") BooleanInterestObserver stateObserver,
      @Named("obs_wear_manage") BooleanInterestObserver wearManageObserver,
      @Named("obs_wear_state") BooleanInterestObserver wearStateObserver) {
    return new ManagerBluetoothInteractorImpl(queuer, preferences, manageObserver, stateObserver,
        wearManageObserver, wearStateObserver);
  }

  @Provides @Named("sync_manager") Manager provideManagerSync(
      @Named("sync_manager_interactor") @NonNull ManagerInteractor interactor,
      @Named("sub") Scheduler subScheduler, @Named("obs") Scheduler obsScheduler) {
    return new WearUnawareManagerImpl(interactor, obsScheduler, subScheduler);
  }

  @Provides @Named("sync_manager_interactor") ManagerInteractor provideManagerSyncInteractor(
      @NonNull @Named("queuer_sync") Queuer queuer, @NonNull PowerManagerPreferences preferences,
      @Named("obs_sync_manage") BooleanInterestObserver manageObserver,
      @Named("obs_sync_state") BooleanInterestObserver stateObserver) {
    return new ManagerSyncInteractorImpl(queuer, preferences, manageObserver, stateObserver);
  }

  @Provides @Named("doze_manager") ExclusiveManager provideManagerDoze(
      @Named("doze_manager_interactor") @NonNull ExclusiveWearUnawareManagerInteractor interactor,
      @Named("sub") Scheduler subScheduler, @Named("obs") Scheduler obsScheduler) {
    return new ExclusiveWearUnawareManagerImpl(interactor, obsScheduler, subScheduler);
  }

  @Provides @Named("doze_manager_interactor")
  ExclusiveWearUnawareManagerInteractor provideManagerDozeInteractor(
      @NonNull PowerManagerPreferences preferences, @NonNull @Named("queuer_doze") Queuer queuer,
      @Named("obs_doze_manage") BooleanInterestObserver manageObserver,
      @Named("obs_doze_state") BooleanInterestObserver stateObserver) {
    return new ManagerDozeInteractorImpl(queuer, preferences, manageObserver, stateObserver);
  }

  @Provides @Named("airplane_manager") Manager provideManagerAirplane(
      @Named("airplane_manager_interactor") @NonNull WearAwareManagerInteractor interactor,
      @Named("sub") Scheduler subScheduler, @Named("obs") Scheduler obsScheduler) {
    return new WearAwareManagerImpl(interactor, obsScheduler, subScheduler);
  }

  @Provides @Named("airplane_manager_interactor")
  WearAwareManagerInteractor provideManagerAirplaneInteractor(
      @NonNull PowerManagerPreferences preferences,
      @NonNull @Named("queuer_airplane") Queuer queuer,
      @Named("obs_airplane_manage") BooleanInterestObserver manageObserver,
      @Named("obs_airplane_state") BooleanInterestObserver stateObserver,
      @Named("obs_wear_manage") BooleanInterestObserver wearManageObserver,
      @Named("obs_wear_state") BooleanInterestObserver wearStateObserver) {
    return new ManagerAirplaneInteractorImpl(queuer, preferences, manageObserver, stateObserver,
        wearManageObserver, wearStateObserver);
  }
}
