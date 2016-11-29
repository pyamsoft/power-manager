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

package com.pyamsoft.powermanager.dagger.modifier.state;

import android.content.Context;
import android.support.annotation.NonNull;
import com.pyamsoft.powermanager.app.modifier.BooleanInterestModifier;
import com.pyamsoft.powermanager.dagger.wrapper.DeviceFunctionWrapper;
import dagger.Module;
import dagger.Provides;
import javax.inject.Named;
import javax.inject.Singleton;

@Module public class StateModifierModule {

  @Singleton @Named("mod_wifi_state") @Provides BooleanInterestModifier provideWifiModifier(
      @NonNull Context context, @NonNull @Named("wrapper_wifi") DeviceFunctionWrapper wrapper) {
    return new WifiStateModifier(context, wrapper);
  }

  @Singleton @Named("mod_data_state") @Provides BooleanInterestModifier provideDataModifier(
      @NonNull Context context, @Named("wrapper_data") DeviceFunctionWrapper wrapper) {
    return new DataStateModifier(context, wrapper);
  }

  @Singleton @Named("mod_bluetooth_state") @Provides
  BooleanInterestModifier provideBluetoothModifier(@NonNull Context context,
      @NonNull @Named("wrapper_bluetooth") DeviceFunctionWrapper wrapper) {
    return new BluetoothStateModifier(context, wrapper);
  }

  @Singleton @Named("mod_sync_state") @Provides BooleanInterestModifier provideSyncModifier(
      @NonNull Context context, @Named("wrapper_sync") DeviceFunctionWrapper wrapper) {
    return new SyncStateModifier(context, wrapper);
  }

  @Singleton @Named("mod_airplane_state") @Provides
  BooleanInterestModifier provideAirplaneModeModifier(@NonNull Context context,
      @Named("wrapper_airplane") DeviceFunctionWrapper wrapper) {
    return new AirplaneStateModifier(context, wrapper);
  }

  @Singleton @Named("mod_doze_state") @Provides BooleanInterestModifier provideDozeModifier(
      @NonNull Context context, @Named("wrapper_doze") DeviceFunctionWrapper wrapper) {
    return new DozeStateModifier(context, wrapper);
  }
}
