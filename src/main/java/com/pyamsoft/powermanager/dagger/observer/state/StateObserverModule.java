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
import com.pyamsoft.powermanager.app.observer.InterestObserver;
import dagger.Module;
import dagger.Provides;
import javax.inject.Named;
import javax.inject.Singleton;

@Module public class StateObserverModule {

  @Singleton @Named("obs_wifi_state") @Provides InterestObserver provideWifiObserver(
      @NonNull Context context) {
    return new WifiStateObserver(context);
  }

  @Singleton @Named("obs_data_state") @Provides InterestObserver provideDataObserver(
      @NonNull Context context) {
    return new DataStateObserver(context);
  }

  @Singleton @Named("obs_bluetooth_state") @Provides InterestObserver provideBluetoothObserver(
      @NonNull Context context) {
    return new BluetoothStateObserver(context);
  }

  @Singleton @Named("obs_sync_state") @Provides InterestObserver provideSyncObserver() {
    return new SyncStateObserver();
  }

  @Singleton @Named("obs_doze_state") @Provides InterestObserver provideDozeObserver(
      @NonNull Context context) {
    return new DozeStateObserver(context);
  }
}
