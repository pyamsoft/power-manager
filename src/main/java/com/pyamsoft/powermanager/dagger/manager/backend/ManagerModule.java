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

import android.content.Context;
import android.support.annotation.NonNull;
import com.pyamsoft.powermanager.PowerManagerPreferences;
import com.pyamsoft.powermanager.app.manager.backend.ManagerBluetooth;
import com.pyamsoft.powermanager.app.manager.backend.ManagerData;
import com.pyamsoft.powermanager.app.manager.backend.ManagerSync;
import com.pyamsoft.powermanager.app.manager.backend.ManagerWifi;
import com.pyamsoft.powermanager.dagger.ActivityScope;
import com.pyamsoft.powermanager.dagger.observer.state.BluetoothStateObserver;
import com.pyamsoft.powermanager.dagger.observer.state.DataStateObserver;
import com.pyamsoft.powermanager.dagger.observer.state.SyncStateObserver;
import com.pyamsoft.powermanager.dagger.observer.state.WifiStateObserver;
import dagger.Module;
import dagger.Provides;
import javax.inject.Named;
import rx.Scheduler;

@Module public class ManagerModule {

  @ActivityScope @Provides ManagerInteractorWifi provideManagerInteractorWifi(
      @NonNull WifiStateObserver observer, @NonNull PowerManagerPreferences preferences,
      @NonNull Context context) {
    return new ManagerInteractorWifi(preferences, context, observer);
  }

  @ActivityScope @Provides ManagerInteractorData provideManagerInteractorData(
      @NonNull DataStateObserver observer, @NonNull PowerManagerPreferences preferences) {
    return new ManagerInteractorData(preferences, observer);
  }

  @ActivityScope @Provides ManagerInteractorBluetooth provideManagerInteractorBluetooth(
      @NonNull BluetoothStateObserver observer, @NonNull PowerManagerPreferences preferences,
      @NonNull Context context) {
    return new ManagerInteractorBluetooth(preferences, context, observer);
  }

  @ActivityScope @Provides ManagerInteractorSync provideManagerInteractorSync(
      @NonNull SyncStateObserver observer, @NonNull PowerManagerPreferences preferences) {
    return new ManagerInteractorSync(preferences, observer);
  }

  @ActivityScope @Provides ManagerWifi provideManagerWifi(@NonNull ManagerInteractorWifi wifi,
      @NonNull @Named("io") Scheduler ioScheduler,
      @NonNull @Named("main") Scheduler mainScheduler) {
    return new ManagerWifi(wifi, ioScheduler, mainScheduler);
  }

  @ActivityScope @Provides ManagerBluetooth provideManagerBluetooth(
      @NonNull ManagerInteractorBluetooth bluetooth, @NonNull @Named("io") Scheduler ioScheduler,
      @NonNull @Named("main") Scheduler mainScheduler) {
    return new ManagerBluetooth(bluetooth, ioScheduler, mainScheduler);
  }

  @ActivityScope @Provides ManagerSync provideManagerSync(@NonNull ManagerInteractorSync sync,
      @NonNull @Named("io") Scheduler ioScheduler,
      @NonNull @Named("main") Scheduler mainScheduler) {
    return new ManagerSync(sync, ioScheduler, mainScheduler);
  }

  @ActivityScope @Provides ManagerData provideManagerData(@NonNull ManagerInteractorData data,
      @NonNull @Named("io") Scheduler ioScheduler,
      @NonNull @Named("main") Scheduler mainScheduler) {
    return new ManagerData(data, ioScheduler, mainScheduler);
  }
}
