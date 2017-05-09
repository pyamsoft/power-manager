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

package com.pyamsoft.powermanager.base.states;

import android.content.Context;
import android.support.annotation.NonNull;
import com.pyamsoft.powermanager.base.preference.WearablePreferences;
import com.pyamsoft.powermanager.model.ConnectedStateObserver;
import com.pyamsoft.powermanager.model.StateObserver;
import dagger.Module;
import dagger.Provides;
import javax.inject.Named;
import javax.inject.Singleton;

@Module public class StateObserverModule {

  @Singleton @Named("obs_wifi") @Provides ConnectedStateObserver provideWifiObserver(
      @NonNull Context context,
      @NonNull @Named("wrapper_wifi") ConnectedDeviceFunctionWrapper wrapper) {
    return new WifiStateObserver(context, wrapper);
  }

  @Singleton @Named("obs_data") @Provides StateObserver provideDataObserver(
      @NonNull Context context, @Named("wrapper_data") DeviceFunctionWrapper wrapper,
      @Named("data_uri") String dataUri) {
    return new DataStateObserver(context, wrapper, dataUri);
  }

  @Singleton @Named("obs_bluetooth") @Provides ConnectedStateObserver provideBluetoothObserver(
      @NonNull Context context,
      @NonNull @Named("wrapper_bluetooth") ConnectedDeviceFunctionWrapper wrapper) {
    return new BluetoothStateObserver(context, wrapper);
  }

  @Singleton @Named("obs_sync") @Provides StateObserver provideSyncObserver(
      @Named("wrapper_sync") DeviceFunctionWrapper wrapper) {
    return new SyncStateObserver(wrapper);
  }

  @Singleton @Named("obs_doze") @Provides StateObserver provideDozeObserver(
      @NonNull Context context, @Named("wrapper_doze") DeviceFunctionWrapper wrapper) {
    return new DozeStateObserver(context, wrapper);
  }

  @Singleton @Named("obs_wear") @Provides StateObserver provideWearObserver(
      @NonNull Context context, @NonNull WearablePreferences preferences) {
    return new WearStateObserver(context, preferences);
  }

  @Singleton @Named("obs_airplane") @Provides StateObserver provideAirplaneObserver(
      @NonNull Context context, @Named("wrapper_airplane") DeviceFunctionWrapper wrapper) {
    return new AirplaneStateObserver(context, wrapper);
  }

  @Singleton @Named("obs_charging") @Provides StateObserver provideChargingObserver(
      @NonNull Context context) {
    return new ChargingStateObserver(context);
  }
}
