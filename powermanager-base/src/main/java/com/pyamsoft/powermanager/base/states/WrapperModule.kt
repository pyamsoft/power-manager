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

package com.pyamsoft.powermanager.base.states

import android.content.Context
import com.pyamsoft.powermanager.base.logger.Logger
import com.pyamsoft.powermanager.base.preference.RootPreferences
import com.pyamsoft.powermanager.base.shell.ShellCommandHelper
import dagger.Module
import dagger.Provides
import javax.inject.Named
import javax.inject.Singleton

@Module class WrapperModule {

  @Singleton @Provides @Named("wrapper_wifi") fun provideWifiManagerWrapper(context: Context,
      @Named("logger_wifi") logger: Logger): ConnectedDeviceFunctionWrapper {
    return WifiManagerWrapperImpl(context, logger)
  }

  @Singleton @Provides @Named("wrapper_bluetooth") fun provideBluetoothAdapterWrapper(
      context: Context, @Named("logger_bluetooth") logger: Logger): ConnectedDeviceFunctionWrapper {
    return BluetoothAdapterWrapperImpl(context, logger)
  }

  @Singleton @Provides @Named("wrapper_data") fun provideDataConnectionWrapper(context: Context,
      preferences: RootPreferences, shellCommandHelper: ShellCommandHelper,
      @Named("logger_data") logger: Logger,
      @Named("data_uri") dataUri: String): DeviceFunctionWrapper {
    return DataConnectionWrapperImpl(context, shellCommandHelper, logger, preferences, dataUri)
  }

  @Singleton @Provides @Named("wrapper_sync") fun provideSyncConnectionWrapper(
      @Named("logger_sync") logger: Logger): DeviceFunctionWrapper {
    return SyncConnectionWrapperImpl(logger)
  }

  @Singleton @Provides @Named("wrapper_airplane") fun provideAirplaneModeWrapper(context: Context,
      preferences: RootPreferences, shellCommandHelper: ShellCommandHelper,
      @Named("logger_airplane") logger: Logger): DeviceFunctionWrapper {
    return AirplaneModeWrapperImpl(context, logger, preferences, shellCommandHelper)
  }

  @Singleton @Provides @Named("wrapper_doze") fun provideDozeWrapper(context: Context,
      preferences: RootPreferences, shellCommandHelper: ShellCommandHelper,
      @Named("logger_doze") logger: Logger): DeviceFunctionWrapper {
    return DozeDeviceWrapperImpl(context, logger, preferences, shellCommandHelper)
  }
}
