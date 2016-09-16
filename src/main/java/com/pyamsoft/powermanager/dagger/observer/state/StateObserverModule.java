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

package com.pyamsoft.powermanager.dagger.observer.state;

import android.content.Context;
import android.support.annotation.NonNull;
import com.pyamsoft.powermanager.app.observer.BooleanInterestObserver;
import com.pyamsoft.powermanager.dagger.wrapper.DeviceFunctionWrapper;
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

  @Singleton @Named("obs_sync_state") @Provides BooleanInterestObserver provideSyncObserver() {
    return new SyncStateObserver();
  }

  @Singleton @Named("obs_doze_state") @Provides BooleanInterestObserver provideDozeObserver(
      @NonNull Context context) {
    return new DozeStateObserver(context);
  }

  @Singleton @Named("obs_wear_state") @Provides BooleanInterestObserver provideWearObserver(
      @NonNull Context context) {
    return new WearStateObserver(context);
  }
}
