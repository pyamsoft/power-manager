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

package com.pyamsoft.powermanager.base.observer.preference.manage;

import android.support.annotation.NonNull;
import com.pyamsoft.powermanager.base.PowerManagerPreferences;
import com.pyamsoft.powermanager.model.overlord.PermissionObserver;
import com.pyamsoft.powermanager.model.overlord.StateObserver;
import dagger.Module;
import dagger.Provides;
import javax.inject.Named;
import javax.inject.Singleton;

@Module public class ManageObserverModule {

  @Singleton @Named("obs_wifi_manage") @Provides StateObserver provideWifiObserver(
      @NonNull PowerManagerPreferences preferences) {
    return new WifiManageObserver(preferences);
  }

  @Singleton @Named("obs_data_manage") @Provides StateObserver provideDataObserver(
      @NonNull PowerManagerPreferences preferences,
      @Named("obs_root_permission") PermissionObserver rootPermissionObserver) {
    return new DataManageObserver(preferences, rootPermissionObserver);
  }

  @Singleton @Named("obs_bluetooth_manage") @Provides StateObserver provideBluetoothObserver(
      @NonNull PowerManagerPreferences preferences) {
    return new BluetoothManageObserver(preferences);
  }

  @Singleton @Named("obs_sync_manage") @Provides StateObserver provideSyncObserver(
      @NonNull PowerManagerPreferences preferences) {
    return new SyncManageObserver(preferences);
  }

  @Singleton @Named("obs_wear_manage") @Provides StateObserver provideWearableObserver(
      @NonNull PowerManagerPreferences preferences) {
    return new WearableManageObserver(preferences);
  }

  @Singleton @Named("obs_doze_manage") @Provides StateObserver provideDozeObserver(
      @NonNull PowerManagerPreferences preferences,
      @Named("obs_doze_permission") PermissionObserver dozePermissionObserver) {
    return new DozeManageObserver(preferences, dozePermissionObserver);
  }

  @Singleton @Named("obs_airplane_manage") @Provides StateObserver provideAirplaneObserver(
      @NonNull PowerManagerPreferences preferences,
      @Named("obs_root_permission") PermissionObserver rootPermissionObserver) {
    return new AirplaneManageObserver(preferences, rootPermissionObserver);
  }
}
