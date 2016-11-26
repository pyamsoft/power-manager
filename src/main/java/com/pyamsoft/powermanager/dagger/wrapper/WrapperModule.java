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

package com.pyamsoft.powermanager.dagger.wrapper;

import android.content.Context;
import android.support.annotation.NonNull;
import com.pyamsoft.powermanager.PowerManagerPreferences;
import dagger.Module;
import dagger.Provides;
import javax.inject.Named;
import javax.inject.Singleton;

@Module public class WrapperModule {

  @Singleton @Provides @Named("wrapper_wifi") DeviceFunctionWrapper provideWifiManagerWrapper(
      @NonNull Context context) {
    return new WifiManagerWrapperImpl(context);
  }

  @Singleton @Provides @Named("wrapper_bluetooth")
  DeviceFunctionWrapper provideBluetoothAdapterWrapper(@NonNull Context context) {
    return new BluetoothAdapterWrapperImpl(context);
  }

  @Singleton @Provides @Named("wrapper_data") DeviceFunctionWrapper provideDataConnectionWrapper(
      @NonNull Context context, @NonNull PowerManagerPreferences preferences) {
    return new DataConnectionWrapperImpl(context, preferences);
  }

  @Singleton @Provides @Named("wrapper_sync") DeviceFunctionWrapper provideSyncConnectionWrapper() {
    return new SyncConnectionWrapperImpl();
  }

  @Singleton @Provides @Named("wrapper_airplane") DeviceFunctionWrapper provideAirplaneModeWrapper(
      @NonNull Context context, @NonNull PowerManagerPreferences preferences) {
    return new AirplaneModeWrapperImpl(context, preferences);
  }

  @Singleton @Provides @Named("wrapper_doze") DeviceFunctionWrapper provideDozeWrapper(
      @NonNull Context context, @NonNull PowerManagerPreferences preferences) {
    return new DozeDeviceWrapperImpl(context, preferences);
  }

  @Singleton @Provides JobQueuerWrapper provideJobQueuerWrapper(@NonNull Context context) {
    return new JobQueuerWrapperImpl(context);
  }
}
