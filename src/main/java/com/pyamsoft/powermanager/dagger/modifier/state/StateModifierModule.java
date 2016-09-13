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
import com.pyamsoft.powermanager.PowerManagerPreferences;
import com.pyamsoft.powermanager.app.modifier.BooleanInterestModifier;
import com.pyamsoft.powermanager.app.observer.BooleanInterestObserver;
import com.pyamsoft.powermanager.dagger.receiver.SensorFixReceiver;
import com.pyamsoft.powermanager.dagger.wrapper.BluetoothAdapterWrapper;
import com.pyamsoft.powermanager.dagger.wrapper.WifiManagerWrapper;
import dagger.Module;
import dagger.Provides;
import javax.inject.Named;
import javax.inject.Singleton;

@Module public class StateModifierModule {

  @Singleton @Named("mod_wifi_state") @Provides BooleanInterestModifier provideWifiModifier(
      @NonNull Context context, @NonNull PowerManagerPreferences preferences,
      @NonNull WifiManagerWrapper wrapper) {
    return new WifiStateModifier(context, preferences, wrapper);
  }

  @Singleton @Named("mod_data_state") @Provides BooleanInterestModifier provideDataModifier(
      @NonNull Context context, @NonNull PowerManagerPreferences preferences) {
    return new DataStateModifier(context, preferences);
  }

  @Singleton @Named("mod_bluetooth_state") @Provides
  BooleanInterestModifier provideBluetoothModifier(@NonNull Context context,
      @NonNull PowerManagerPreferences preferences, @NonNull BluetoothAdapterWrapper wrapper) {
    return new BluetoothStateModifier(context, preferences, wrapper);
  }

  @Singleton @Named("mod_sync_state") @Provides BooleanInterestModifier provideSyncModifier(
      @NonNull Context context, @NonNull PowerManagerPreferences preferences) {
    return new SyncStateModifier(context, preferences);
  }

  @Singleton @Named("mod_doze_state") @Provides BooleanInterestModifier provideDozeModifier(
      @NonNull Context context, @NonNull PowerManagerPreferences preferences,
      @NonNull SensorFixReceiver sensorFixReceiver,
      @Named("obs_doze_permission") BooleanInterestObserver dozePermissionObserver) {
    return new DozeStateModifier(context, preferences, sensorFixReceiver, dozePermissionObserver);
  }
}
