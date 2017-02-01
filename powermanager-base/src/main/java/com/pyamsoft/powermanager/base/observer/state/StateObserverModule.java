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

package com.pyamsoft.powermanager.base.observer.state;

import android.content.Context;
import android.support.annotation.NonNull;
import com.pyamsoft.powermanager.base.PowerManagerPreferences;
import com.pyamsoft.powermanager.base.wrapper.DeviceFunctionWrapper;
import com.pyamsoft.powermanager.model.BooleanInterestObserver;
import dagger.Module;
import dagger.Provides;
import javax.inject.Named;
import javax.inject.Singleton;

@Module public class StateObserverModule {

  @Singleton @Named("obs_wifi_state") @Provides BooleanInterestObserver provideWifiObserver(
      @NonNull Context context, @NonNull @Named("wrapper_wifi") DeviceFunctionWrapper wrapper) {
    return new WifiStateObserver(context, wrapper);
  }

  @Singleton @Named("obs_data_state") @Provides BooleanInterestObserver provideDataObserver(
      @NonNull Context context, @Named("wrapper_data") DeviceFunctionWrapper wrapper) {
    return new DataStateObserver(context, wrapper);
  }

  @Singleton @Named("obs_bluetooth_state") @Provides
  BooleanInterestObserver provideBluetoothObserver(@NonNull Context context,
      @NonNull @Named("wrapper_bluetooth") DeviceFunctionWrapper wrapper) {
    return new BluetoothStateObserver(context, wrapper);
  }

  @Singleton @Named("obs_sync_state") @Provides BooleanInterestObserver provideSyncObserver(
      @Named("wrapper_sync") DeviceFunctionWrapper wrapper) {
    return new SyncStateObserver(wrapper);
  }

  @Singleton @Named("obs_doze_state") @Provides BooleanInterestObserver provideDozeObserver(
      @NonNull Context context, @Named("wrapper_doze") DeviceFunctionWrapper wrapper) {
    return new DozeStateObserver(context, wrapper);
  }

  @Singleton @Named("obs_wear_state") @Provides BooleanInterestObserver provideWearObserver(
      @NonNull Context context, @NonNull PowerManagerPreferences preferences) {
    return new WearStateObserver(context, preferences);
  }

  @Singleton @Named("obs_airplane_state") @Provides BooleanInterestObserver provideAirplaneObserver(
      @NonNull Context context, @Named("wrapper_airplane") DeviceFunctionWrapper wrapper) {
    return new AirplaneStateObserver(context, wrapper);
  }

  @Singleton @Named("obs_charging_state") @Provides BooleanInterestObserver provideChargingObserver(
      @NonNull Context context) {
    return new ChargingStateObserver(context);
  }
}
