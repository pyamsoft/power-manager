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

import android.support.annotation.NonNull;
import com.pyamsoft.powermanager.model.StateModifier;
import dagger.Module;
import dagger.Provides;
import javax.inject.Named;
import javax.inject.Singleton;

@Module public class StateModifierModule {

  @Singleton @Named("mod_wifi_state") @Provides StateModifier provideWifiModifier(
      @NonNull @Named("wrapper_wifi") ConnectedDeviceFunctionWrapper wrapper) {
    return new WifiStateModifier(wrapper);
  }

  @Singleton @Named("mod_data_state") @Provides StateModifier provideDataModifier(
      @Named("wrapper_data") DeviceFunctionWrapper wrapper) {
    return new DataStateModifier(wrapper);
  }

  @Singleton @Named("mod_bluetooth_state") @Provides StateModifier provideBluetoothModifier(
      @NonNull @Named("wrapper_bluetooth") ConnectedDeviceFunctionWrapper wrapper) {
    return new BluetoothStateModifier(wrapper);
  }

  @Singleton @Named("mod_sync_state") @Provides StateModifier provideSyncModifier(
      @Named("wrapper_sync") DeviceFunctionWrapper wrapper) {
    return new SyncStateModifier(wrapper);
  }

  @Singleton @Named("mod_airplane_state") @Provides StateModifier provideAirplaneModeModifier(
      @Named("wrapper_airplane") DeviceFunctionWrapper wrapper) {
    return new AirplaneStateModifier(wrapper);
  }

  @Singleton @Named("mod_doze_state") @Provides StateModifier provideDozeModifier(
      @Named("wrapper_doze") DeviceFunctionWrapper wrapper) {
    return new DozeStateModifier(wrapper);
  }
}
