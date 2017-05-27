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
import com.pyamsoft.powermanager.base.preference.WearablePreferences
import com.pyamsoft.powermanager.model.ConnectedStateObserver
import com.pyamsoft.powermanager.model.StateObserver
import dagger.Module
import dagger.Provides
import javax.inject.Named
import javax.inject.Singleton

@Module class StateObserverModule {
  @Singleton @Named("obs_wifi") @Provides fun provideWifiObserver(
      @Named("wrapper_wifi") wrapper: ConnectedDeviceFunctionWrapper): ConnectedStateObserver {
    return WifiStateObserver(wrapper)
  }

  @Singleton @Named("obs_data") @Provides fun provideDataObserver(
      @Named("wrapper_data") wrapper: DeviceFunctionWrapper): StateObserver {
    return DataStateObserver(wrapper)
  }

  @Singleton @Named("obs_bluetooth") @Provides fun provideBluetoothObserver(
      @Named("wrapper_bluetooth") wrapper: ConnectedDeviceFunctionWrapper): ConnectedStateObserver {
    return BluetoothStateObserver(wrapper)
  }

  @Singleton @Named("obs_sync") @Provides fun provideSyncObserver(
      @Named("wrapper_sync") wrapper: DeviceFunctionWrapper): StateObserver {
    return SyncStateObserver(wrapper)
  }

  @Singleton @Named("obs_doze") @Provides fun provideDozeObserver(
      @Named("wrapper_doze") wrapper: DeviceFunctionWrapper): StateObserver {
    return DozeStateObserver(wrapper)
  }

  @Singleton @Named("obs_wear") @Provides fun provideWearObserver(context: Context,
      preferences: WearablePreferences): StateObserver {
    return WearStateObserver(context, preferences)
  }

  @Singleton @Named("obs_airplane") @Provides fun provideAirplaneObserver(
      @Named("wrapper_airplane") wrapper: DeviceFunctionWrapper): StateObserver {
    return AirplaneStateObserver(wrapper)
  }

  @Singleton @Named("obs_charging") @Provides fun provideChargingObserver(
      context: Context): StateObserver {
    return ChargingStateObserver(context)
  }
}
