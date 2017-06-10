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

package com.pyamsoft.powermanager.manage

import com.pyamsoft.powermanager.base.preference.AirplanePreferences
import com.pyamsoft.powermanager.base.preference.BluetoothPreferences
import com.pyamsoft.powermanager.base.preference.DataPreferences
import com.pyamsoft.powermanager.base.preference.DataSaverPreferences
import com.pyamsoft.powermanager.base.preference.DozePreferences
import com.pyamsoft.powermanager.base.preference.SyncPreferences
import com.pyamsoft.powermanager.base.preference.WifiPreferences
import com.pyamsoft.powermanager.model.PermissionObserver
import dagger.Module
import dagger.Provides
import javax.inject.Named
import javax.inject.Singleton

@Module class ManageSingletonModule {

  @Singleton @Provides @Named("manage_wifi_interactor") internal fun provideWifiInteractor(
      preferences: WifiPreferences): ManageInteractor {
    return WifiManageInteractor(preferences)
  }

  @Singleton @Provides @Named("manage_data_interactor") internal fun provideDataInteractor(
      preferences: DataPreferences,
      @Named("obs_data_permission") permissionObserver: PermissionObserver): ManageInteractor {
    return DataManageInteractor(preferences, permissionObserver)
  }

  @Singleton @Provides @Named(
      "manage_bluetooth_interactor") internal fun provideBluetoothInteractor(
      preferences: BluetoothPreferences): ManageInteractor {
    return BluetoothManageInteractor(preferences)
  }

  @Singleton @Provides @Named("manage_sync_interactor") internal fun provideSyncInteractor(
      preferences: SyncPreferences): ManageInteractor {
    return SyncManageInteractor(preferences)
  }

  @Singleton @Provides @Named("manage_airplane_interactor") internal fun provideAirplaneInteractor(
      preferences: AirplanePreferences,
      @Named("obs_root_permission") permissionObserver: PermissionObserver): ManageInteractor {
    return AirplaneManageInteractor(preferences, permissionObserver)
  }

  @Singleton @Provides @Named("manage_doze_interactor") internal fun provideDozeInteractor(
      preferences: DozePreferences,
      @Named("obs_doze_permission") permissionObserver: PermissionObserver): ManageInteractor {
    return DozeManageInteractor(preferences, permissionObserver)
  }

  @Singleton @Provides @Named(
      "manage_data_saver_interactor") internal fun provideDataSaverInteractor(
      preferences: DataSaverPreferences, @Named(
      "obs_data_saver_permission") permissionObserver: PermissionObserver): ManageInteractor {
    return DataSaverManageInteractor(preferences, permissionObserver)
  }

  @Singleton @Provides @Named(
      "exception_wifi_interactor") internal fun provideWifiExceptionInteractor(
      preferences: WifiPreferences): ExceptionInteractor {
    return WifiExceptionInteractor(preferences)
  }

  @Singleton @Provides @Named(
      "exception_data_interactor") internal fun provideDataExceptionInteractor(
      @Named("obs_data_permission") permissionObserver: PermissionObserver,
      preferences: DataPreferences): ExceptionInteractor {
    return DataExceptionInteractor(permissionObserver, preferences)
  }

  @Singleton @Provides @Named(
      "exception_bluetooth_interactor") internal fun provideBluetoothExceptionInteractor(
      preferences: BluetoothPreferences): ExceptionInteractor {
    return BluetoothExceptionInteractor(preferences)
  }

  @Singleton @Provides @Named(
      "exception_sync_interactor") internal fun provideSyncExceptionInteractor(
      preferences: SyncPreferences): ExceptionInteractor {
    return SyncExceptionInteractor(preferences)
  }

  @Singleton @Provides @Named(
      "exception_airplane_interactor") internal fun provideAirplaneExceptionInteractor(
      @Named("obs_root_permission") permissionObserver: PermissionObserver,
      preferences: AirplanePreferences): ExceptionInteractor {
    return AirplaneExceptionInteractor(permissionObserver, preferences)
  }

  @Singleton @Provides @Named(
      "exception_doze_interactor") internal fun provideDozeExceptionInteractor(
      @Named("obs_doze_permission") permissionObserver: PermissionObserver,
      preferences: DozePreferences): ExceptionInteractor {
    return DozeExceptionInteractor(permissionObserver, preferences)
  }

  @Singleton @Provides @Named(
      "exception_data_saver_interactor") internal fun provideDataSaverExceptionInteractor(
      @Named("obs_data_saver_permission") permissionObserver: PermissionObserver,
      preferences: DataSaverPreferences): ExceptionInteractor {
    return DataSaverExceptionInteractor(permissionObserver, preferences)
  }

}
