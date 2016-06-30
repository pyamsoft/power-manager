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

package com.pyamsoft.powermanager.dagger.manager;

import android.support.annotation.NonNull;
import com.pyamsoft.powermanager.app.manager.BluetoothPresenter;
import com.pyamsoft.powermanager.app.manager.DataPresenter;
import com.pyamsoft.powermanager.app.manager.SyncPresenter;
import com.pyamsoft.powermanager.app.manager.WifiPresenter;
import com.pyamsoft.powermanager.dagger.ActivityScope;
import com.pyamsoft.powermanager.dagger.manager.backend.ManagerInteractorBluetooth;
import com.pyamsoft.powermanager.dagger.manager.backend.ManagerInteractorData;
import com.pyamsoft.powermanager.dagger.manager.backend.ManagerInteractorSync;
import com.pyamsoft.powermanager.dagger.manager.backend.ManagerInteractorWifi;
import dagger.Module;
import dagger.Provides;
import javax.inject.Named;
import rx.Scheduler;

@Module public class ManagerSettingsModule {

  @ActivityScope @Provides WifiPresenter provideWifiPresenter(
      @NonNull ManagerInteractorWifi interactor, @Named("main") Scheduler mainScheduler,
      @Named("io") Scheduler ioScheduler) {
    return new WifiPresenter(interactor, mainScheduler, ioScheduler);
  }

  @ActivityScope @Provides DataPresenter provideDataPresenter(
      @NonNull ManagerInteractorData interactor, @Named("main") Scheduler mainScheduler,
      @Named("io") Scheduler ioScheduler) {
    return new DataPresenter(interactor, mainScheduler, ioScheduler);
  }

  @ActivityScope @Provides BluetoothPresenter provideBluetoothPresenter(
      @NonNull ManagerInteractorBluetooth interactor, @Named("main") Scheduler mainScheduler,
      @Named("io") Scheduler ioScheduler) {
    return new BluetoothPresenter(interactor, mainScheduler, ioScheduler);
  }

  @ActivityScope @Provides SyncPresenter provideSyncPresenter(
      @NonNull ManagerInteractorSync interactor, @Named("main") Scheduler mainScheduler,
      @Named("io") Scheduler ioScheduler) {
    return new SyncPresenter(interactor, mainScheduler, ioScheduler);
  }
}
