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

package com.pyamsoft.powermanager.modifier.state;

import android.support.annotation.NonNull;
import com.pyamsoft.powermanager.base.wrapper.DeviceFunctionWrapper;
import com.pyamsoft.powermanager.model.BooleanInterestModifier;
import dagger.Module;
import dagger.Provides;
import javax.inject.Named;

@Module public class StateModifierModule {

  @Named("mod_wifi_state") @Provides BooleanInterestModifier provideWifiModifier(
      @NonNull @Named("wrapper_wifi") DeviceFunctionWrapper wrapper) {
    return new WifiStateModifier(wrapper);
  }

  @Named("mod_data_state") @Provides BooleanInterestModifier provideDataModifier(
      @Named("wrapper_data") DeviceFunctionWrapper wrapper) {
    return new DataStateModifier(wrapper);
  }

  @Named("mod_bluetooth_state") @Provides BooleanInterestModifier provideBluetoothModifier(
      @NonNull @Named("wrapper_bluetooth") DeviceFunctionWrapper wrapper) {
    return new BluetoothStateModifier(wrapper);
  }

  @Named("mod_sync_state") @Provides BooleanInterestModifier provideSyncModifier(
      @Named("wrapper_sync") DeviceFunctionWrapper wrapper) {
    return new SyncStateModifier(wrapper);
  }

  @Named("mod_airplane_state") @Provides BooleanInterestModifier provideAirplaneModeModifier(
      @Named("wrapper_airplane") DeviceFunctionWrapper wrapper) {
    return new AirplaneStateModifier(wrapper);
  }

  @Named("mod_doze_state") @Provides BooleanInterestModifier provideDozeModifier(
      @Named("wrapper_doze") DeviceFunctionWrapper wrapper) {
    return new DozeStateModifier(wrapper);
  }
}
