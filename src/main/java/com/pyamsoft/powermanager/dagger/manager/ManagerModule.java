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

import android.content.Context;
import android.support.annotation.NonNull;
import com.pyamsoft.powermanager.PowerManagerPreferences;
import com.pyamsoft.powermanager.app.manager.ExclusiveManager;
import com.pyamsoft.powermanager.app.manager.Manager;
import com.pyamsoft.powermanager.app.observer.BooleanInterestObserver;
import com.pyamsoft.pydroid.base.app.ActivityScope;
import dagger.Module;
import dagger.Provides;
import javax.inject.Named;
import rx.Scheduler;

@Module public class ManagerModule {

  @ActivityScope @Provides @Named("wifi_manager") Manager provideManagerWifi(
      @Named("wifi_manager_interactor") @NonNull ManagerInteractor interactor,
      @Named("io") Scheduler ioScheduler, @Named("main") Scheduler mainScheduler) {
    return new ManagerWifi(interactor, ioScheduler, mainScheduler);
  }

  @ActivityScope @Provides @Named("wifi_manager_interactor")
  ManagerInteractor provideManagerWifiInteractor(@NonNull Context context,
      @NonNull PowerManagerPreferences preferences,
      @Named("obs_wifi_manage") BooleanInterestObserver manageObserver,
      @Named("obs_wifi_state") BooleanInterestObserver stateObserver) {
    return new ManagerWifiInteractor(context, preferences, manageObserver, stateObserver);
  }

  @ActivityScope @Provides @Named("data_manager") Manager provideManagerData(
      @Named("data_manager_interactor") @NonNull ManagerInteractor interactor,
      @Named("io") Scheduler ioScheduler, @Named("main") Scheduler mainScheduler) {
    return new ManagerData(interactor, ioScheduler, mainScheduler);
  }

  @ActivityScope @Provides @Named("data_manager_interactor")
  ManagerInteractor provideManagerDataInteractor(@NonNull Context context,
      @NonNull PowerManagerPreferences preferences,
      @Named("obs_data_manage") BooleanInterestObserver manageObserver,
      @Named("obs_data_state") BooleanInterestObserver stateObserver) {
    return new ManagerDataInteractor(context, preferences, manageObserver, stateObserver);
  }

  @ActivityScope @Provides @Named("bluetooth_manager") Manager provideManagerBluetooth(
      @Named("bluetooth_manager_interactor") @NonNull ManagerInteractor interactor,
      @Named("io") Scheduler ioScheduler, @Named("main") Scheduler mainScheduler) {
    return new ManagerBluetooth(interactor, ioScheduler, mainScheduler);
  }

  @ActivityScope @Provides @Named("bluetooth_manager_interactor")
  ManagerInteractor provideManagerBluetoothInteractor(@NonNull Context context,
      @NonNull PowerManagerPreferences preferences,
      @Named("obs_bluetooth_manage") BooleanInterestObserver manageObserver,
      @Named("obs_bluetooth_state") BooleanInterestObserver stateObserver) {
    return new ManagerBluetoothInteractor(context, preferences, manageObserver, stateObserver);
  }

  @ActivityScope @Provides @Named("sync_manager") Manager provideManagerSync(
      @Named("sync_manager_interactor") @NonNull ManagerInteractor interactor,
      @Named("io") Scheduler ioScheduler, @Named("main") Scheduler mainScheduler) {
    return new ManagerSync(interactor, ioScheduler, mainScheduler);
  }

  @ActivityScope @Provides @Named("sync_manager_interactor")
  ManagerInteractor provideManagerSyncInteractor(@NonNull Context context,
      @NonNull PowerManagerPreferences preferences,
      @Named("obs_sync_manage") BooleanInterestObserver manageObserver,
      @Named("obs_sync_state") BooleanInterestObserver stateObserver) {
    return new ManagerSyncInteractor(context, preferences, manageObserver, stateObserver);
  }

  @ActivityScope @Provides @Named("doze_manager") ExclusiveManager provideManagerDoze(
      @Named("doze_manager_interactor") @NonNull ExclusiveManagerInteractor interactor,
      @Named("io") Scheduler ioScheduler, @Named("main") Scheduler mainScheduler) {
    return new ManagerDoze(interactor, ioScheduler, mainScheduler);
  }

  @ActivityScope @Provides @Named("doze_manager_interactor")
  ExclusiveManagerInteractor provideManagerDozeInteractor(@NonNull Context context,
      @NonNull PowerManagerPreferences preferences,
      @Named("obs_doze_manage") BooleanInterestObserver manageObserver,
      @Named("obs_doze_state") BooleanInterestObserver stateObserver) {
    return new ManagerDozeInteractor(context, preferences, manageObserver, stateObserver);
  }
}
