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

package com.pyamsoft.powermanager.dagger.manager.backend;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.support.annotation.NonNull;
import com.pyamsoft.powermanager.PowerManagerPreferences;
import com.pyamsoft.powermanager.app.manager.backend.ManagerBluetooth;
import com.pyamsoft.powermanager.app.manager.backend.ManagerData;
import com.pyamsoft.powermanager.app.manager.backend.ManagerSync;
import com.pyamsoft.powermanager.app.manager.backend.ManagerWifi;
import dagger.Module;
import dagger.Provides;
import javax.inject.Named;
import javax.inject.Singleton;
import rx.Scheduler;

@Module public class ManagerModule {

  final BluetoothAdapter getBluetoothAdapter(@NonNull Context context) {
    BluetoothAdapter adapter;
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR2) {
      adapter = BluetoothAdapter.getDefaultAdapter();
    } else {
      final BluetoothManager bluetoothManager = (BluetoothManager) context.getApplicationContext()
          .getSystemService(Context.BLUETOOTH_SERVICE);
      adapter = bluetoothManager.getAdapter();
    }
    return adapter;
  }

  @Singleton @Provides BluetoothAdapterWrapper provideBluetoothAdapterWrapper(
      @NonNull Context context) {
    return new BluetoothAdapterWrapper(getBluetoothAdapter(context));
  }

  @Singleton @Provides WifiManager provideWifiManager(@NonNull Context context) {
    return (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
  }

  @Singleton @Provides ManagerInteractorWifi provideManagerInteractorWifi(
      @NonNull WifiManager wifiManager, @NonNull PowerManagerPreferences preferences,
      @NonNull Context context) {
    return new ManagerInteractorWifi(preferences, context, wifiManager);
  }

  @Singleton @Provides ManagerInteractorData provideManagerInteractorData(
      @NonNull PowerManagerPreferences preferences, @NonNull Context context) {
    return new ManagerInteractorData(preferences, context);
  }

  @Singleton @Provides ManagerInteractorBluetooth provideManagerInteractorBluetooth(
      @NonNull BluetoothAdapterWrapper bluetoothAdapter,
      @NonNull PowerManagerPreferences preferences, @NonNull Context context) {
    return new ManagerInteractorBluetooth(preferences, context, bluetoothAdapter);
  }

  @Singleton @Provides ManagerInteractorSync provideManagerInteractorSync(
      @NonNull PowerManagerPreferences preferences, @NonNull Context context) {
    return new ManagerInteractorSync(preferences, context);
  }

  @Singleton @Provides ManagerWifi provideManagerWifi(@NonNull ManagerInteractorWifi wifi,
      @NonNull @Named("io") Scheduler ioScheduler,
      @NonNull @Named("main") Scheduler mainScheduler) {
    return new ManagerWifi(wifi, ioScheduler, mainScheduler);
  }

  @Singleton @Provides ManagerBluetooth provideManagerBluetooth(
      @NonNull ManagerInteractorBluetooth bluetooth, @NonNull @Named("io") Scheduler ioScheduler,
      @NonNull @Named("main") Scheduler mainScheduler) {
    return new ManagerBluetooth(bluetooth, ioScheduler, mainScheduler);
  }

  @Singleton @Provides ManagerSync provideManagerSync(@NonNull ManagerInteractorSync sync,
      @NonNull @Named("io") Scheduler ioScheduler,
      @NonNull @Named("main") Scheduler mainScheduler) {
    return new ManagerSync(sync, ioScheduler, mainScheduler);
  }

  @Singleton @Provides ManagerData provideManagerData(@NonNull ManagerInteractorData data,
      @NonNull @Named("io") Scheduler ioScheduler,
      @NonNull @Named("main") Scheduler mainScheduler) {
    return new ManagerData(data, ioScheduler, mainScheduler);
  }
}
