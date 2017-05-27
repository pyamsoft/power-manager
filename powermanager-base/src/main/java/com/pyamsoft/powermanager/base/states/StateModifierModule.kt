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

import com.pyamsoft.powermanager.model.StateModifier
import dagger.Module
import dagger.Provides
import javax.inject.Named
import javax.inject.Singleton

@Module class StateModifierModule {
  @Singleton @Named("mod_wifi") @Provides fun provideWifiModifier(
      @Named("wrapper_wifi") wrapper: ConnectedDeviceFunctionWrapper): StateModifier {
    return WifiStateModifier(wrapper)
  }

  @Singleton @Named("mod_data") @Provides fun provideDataModifier(
      @Named("wrapper_data") wrapper: DeviceFunctionWrapper): StateModifier {
    return DataStateModifier(wrapper)
  }

  @Singleton @Named("mod_bluetooth") @Provides fun provideBluetoothModifier(
      @Named("wrapper_bluetooth") wrapper: ConnectedDeviceFunctionWrapper): StateModifier {
    return BluetoothStateModifier(wrapper)
  }

  @Singleton @Named("mod_sync") @Provides fun provideSyncModifier(
      @Named("wrapper_sync") wrapper: DeviceFunctionWrapper): StateModifier {
    return SyncStateModifier(wrapper)
  }

  @Singleton @Named("mod_airplane") @Provides fun provideAirplaneModeModifier(
      @Named("wrapper_airplane") wrapper: DeviceFunctionWrapper): StateModifier {
    return AirplaneStateModifier(wrapper)
  }

  @Singleton @Named("mod_doze") @Provides fun provideDozeModifier(
      @Named("wrapper_doze") wrapper: DeviceFunctionWrapper): StateModifier {
    return DozeStateModifier(wrapper)
  }
}
