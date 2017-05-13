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

package com.pyamsoft.powermanager.manage;

import android.support.annotation.NonNull;
import com.pyamsoft.powermanager.base.preference.AirplanePreferences;
import com.pyamsoft.powermanager.base.preference.BluetoothPreferences;
import com.pyamsoft.powermanager.base.preference.DataPreferences;
import com.pyamsoft.powermanager.base.preference.DozePreferences;
import com.pyamsoft.powermanager.base.preference.SyncPreferences;
import com.pyamsoft.powermanager.base.preference.WifiPreferences;
import com.pyamsoft.powermanager.model.PermissionObserver;
import dagger.Module;
import dagger.Provides;
import javax.inject.Named;
import javax.inject.Singleton;

@Module public class ManageSingletonModule {

  @Singleton @Provides @Named("manage_wifi_interactor") ManageInteractor provideWifi(
      @NonNull WifiPreferences preferences) {
    return new WifiManageInteractor(preferences);
  }

  @Singleton @Provides @Named("manage_data_interactor") ManageInteractor provideData(
      @NonNull DataPreferences preferences,
      @NonNull @Named("obs_root_permission") PermissionObserver permissionObserver) {
    return new DataManageInteractor(preferences, permissionObserver);
  }

  @Singleton @Provides @Named("manage_bluetooth_interactor") ManageInteractor provideBluetooth(
      @NonNull BluetoothPreferences preferences) {
    return new BluetoothManageInteractor(preferences);
  }

  @Singleton @Provides @Named("manage_sync_interactor") ManageInteractor provideSync(
      @NonNull SyncPreferences preferences) {
    return new SyncManageInteractor(preferences);
  }

  @Singleton @Provides @Named("manage_airplane_interactor") ManageInteractor provideAirplane(
      @NonNull AirplanePreferences preferences,
      @NonNull @Named("obs_root_permission") PermissionObserver permissionObserver) {
    return new AirplaneManageInteractor(preferences, permissionObserver);
  }

  @Singleton @Provides @Named("manage_doze_interactor") ManageInteractor provideDoze(
      @NonNull DozePreferences preferences,
      @NonNull @Named("obs_doze_permission") PermissionObserver permissionObserver) {
    return new DozeManageInteractor(preferences, permissionObserver);
  }

  @Singleton @Provides @Named("exception_wifi_interactor") ExceptionInteractor provideWifiException(
      @NonNull WifiPreferences preferences) {
    return new WifiExceptionInteractor(preferences);
  }

  @Singleton @Provides @Named("exception_data_interactor") ExceptionInteractor provideDataException(
      @NonNull DataPreferences preferences) {
    return new DataExceptionInteractor(preferences);
  }

  @Singleton @Provides @Named("exception_bluetooth_interactor")
  ExceptionInteractor provideBluetoothException(@NonNull BluetoothPreferences preferences) {
    return new BluetoothExceptionInteractor(preferences);
  }

  @Singleton @Provides @Named("exception_sync_interactor") ExceptionInteractor provideSyncException(
      @NonNull SyncPreferences preferences) {
    return new SyncExceptionInteractor(preferences);
  }

  @Singleton @Provides @Named("exception_airplane_interactor")
  ExceptionInteractor provideAirplaneException(@NonNull AirplanePreferences preferences) {
    return new AirplaneExceptionInteractor(preferences);
  }

  @Singleton @Provides @Named("exception_doze_interactor") ExceptionInteractor provideDozeException(
      @NonNull DozePreferences preferences) {
    return new DozeExceptionInteractor(preferences);
  }
}
