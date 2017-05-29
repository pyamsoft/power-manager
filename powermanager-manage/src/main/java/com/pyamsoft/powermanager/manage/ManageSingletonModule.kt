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
import com.pyamsoft.powermanager.base.preference.DozePreferences
import com.pyamsoft.powermanager.base.preference.SyncPreferences
import com.pyamsoft.powermanager.base.preference.WifiPreferences
import com.pyamsoft.powermanager.model.PermissionObserver
import dagger.Module
import dagger.Provides
import javax.inject.Named
import javax.inject.Singleton

@Module class ManageSingletonModule {
  @Singleton @Provides @Named("manage_wifi_interactor") internal fun provideWifi(
      preferences: WifiPreferences): ManageInteractor {
    return WifiManageInteractor(preferences)
  }

  @Singleton @Provides @Named("manage_data_interactor") internal fun provideData(
      preferences: DataPreferences,
      @Named("obs_root_permission") permissionObserver: PermissionObserver): ManageInteractor {
    return DataManageInteractor(preferences, permissionObserver)
  }

  @Singleton @Provides @Named("manage_bluetooth_interactor") internal fun provideBluetooth(
      preferences: BluetoothPreferences): ManageInteractor {
    return BluetoothManageInteractor(preferences)
  }

  @Singleton @Provides @Named("manage_sync_interactor") internal fun provideSync(
      preferences: SyncPreferences): ManageInteractor {
    return SyncManageInteractor(preferences)
  }

  @Singleton @Provides @Named("manage_airplane_interactor") internal fun provideAirplane(
      preferences: AirplanePreferences,
      @Named("obs_root_permission") permissionObserver: PermissionObserver): ManageInteractor {
    return AirplaneManageInteractor(preferences, permissionObserver)
  }

  @Singleton @Provides @Named("manage_doze_interactor") internal fun provideDoze(
      preferences: DozePreferences,
      @Named("obs_doze_permission") permissionObserver: PermissionObserver): ManageInteractor {
    return DozeManageInteractor(preferences, permissionObserver)
  }

  @Singleton @Provides @Named("exception_wifi_interactor") internal fun provideWifiException(
      preferences: WifiPreferences): ExceptionInteractor {
    return WifiExceptionInteractor(preferences)
  }

  @Singleton @Provides @Named("exception_data_interactor") internal fun provideDataException(
      preferences: DataPreferences): ExceptionInteractor {
    return DataExceptionInteractor(preferences)
  }

  @Singleton @Provides @Named(
      "exception_bluetooth_interactor") internal fun provideBluetoothException(
      preferences: BluetoothPreferences): ExceptionInteractor {
    return BluetoothExceptionInteractor(preferences)
  }

  @Singleton @Provides @Named("exception_sync_interactor") internal fun provideSyncException(
      preferences: SyncPreferences): ExceptionInteractor {
    return SyncExceptionInteractor(preferences)
  }

  @Singleton @Provides @Named(
      "exception_airplane_interactor") internal fun provideAirplaneException(
      preferences: AirplanePreferences): ExceptionInteractor {
    return AirplaneExceptionInteractor(preferences)
  }

  @Singleton @Provides @Named("exception_doze_interactor") internal fun provideDozeException(
      preferences: DozePreferences): ExceptionInteractor {
    return DozeExceptionInteractor(preferences)
  }
}
