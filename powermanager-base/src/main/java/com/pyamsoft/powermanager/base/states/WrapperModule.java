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

package com.pyamsoft.powermanager.base.states;

import android.content.Context;
import android.support.annotation.NonNull;
import com.pyamsoft.powermanager.base.PowerManagerPreferences;
import com.pyamsoft.powermanager.base.logger.Logger;
import com.pyamsoft.powermanager.base.shell.ShellCommandHelper;
import dagger.Module;
import dagger.Provides;
import javax.inject.Named;
import javax.inject.Singleton;

@Module public class WrapperModule {

  @Singleton @Provides @Named("wrapper_wifi")
  ConnectedDeviceFunctionWrapper provideWifiManagerWrapper(@NonNull Context context,
      @Named("logger_wifi") Logger logger) {
    return new WifiManagerWrapperImpl(context, logger);
  }

  @Singleton @Provides @Named("wrapper_bluetooth")
  DeviceFunctionWrapper provideBluetoothAdapterWrapper(@NonNull Context context,
      @Named("logger_bluetooth") Logger logger) {
    return new BluetoothAdapterWrapperImpl(context, logger);
  }

  @Singleton @Provides @Named("wrapper_data") DeviceFunctionWrapper provideDataConnectionWrapper(
      @NonNull Context context, @NonNull PowerManagerPreferences preferences,
      @NonNull ShellCommandHelper shellCommandHelper, @Named("logger_data") Logger logger,
      @Named("data_uri") String dataUri) {
    return new DataConnectionWrapperImpl(context, shellCommandHelper, logger, preferences, dataUri);
  }

  @Singleton @Provides @Named("wrapper_sync") DeviceFunctionWrapper provideSyncConnectionWrapper(
      @Named("logger_sync") Logger logger) {
    return new SyncConnectionWrapperImpl(logger);
  }

  @Singleton @Provides @Named("wrapper_airplane") DeviceFunctionWrapper provideAirplaneModeWrapper(
      @NonNull Context context, @NonNull PowerManagerPreferences preferences,
      @NonNull ShellCommandHelper shellCommandHelper, @Named("logger_airplane") Logger logger) {
    return new AirplaneModeWrapperImpl(context, logger, preferences, shellCommandHelper);
  }

  @Singleton @Provides @Named("wrapper_doze") DeviceFunctionWrapper provideDozeWrapper(
      @NonNull Context context, @NonNull PowerManagerPreferences preferences,
      @NonNull ShellCommandHelper shellCommandHelper, @Named("logger_doze") Logger logger) {
    return new DozeDeviceWrapperImpl(context, logger, preferences, shellCommandHelper);
  }
}
