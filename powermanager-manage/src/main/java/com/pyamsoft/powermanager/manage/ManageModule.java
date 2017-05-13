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

package com.pyamsoft.powermanager.manage;

import android.support.annotation.NonNull;
import dagger.Module;
import dagger.Provides;
import io.reactivex.Scheduler;
import javax.inject.Named;

@Module public class ManageModule {

  @Provides @Named("manage_wifi") ManagePresenter provideWifi(
      @NonNull @Named("manage_wifi_interactor") ManageInteractor interactor,
      @NonNull @Named("obs") Scheduler obsScheduler,
      @NonNull @Named("sub") Scheduler subScheduler) {
    return new ManagePresenter(interactor, obsScheduler, subScheduler);
  }

  @Provides @Named("manage_data") ManagePresenter provideData(
      @NonNull @Named("manage_data_interactor") ManageInteractor interactor,
      @NonNull @Named("obs") Scheduler obsScheduler,
      @NonNull @Named("sub") Scheduler subScheduler) {
    return new ManagePresenter(interactor, obsScheduler, subScheduler);
  }

  @Provides @Named("manage_bluetooth") ManagePresenter provideBluetooth(
      @NonNull @Named("manage_bluetooth_interactor") ManageInteractor interactor,
      @NonNull @Named("obs") Scheduler obsScheduler,
      @NonNull @Named("sub") Scheduler subScheduler) {
    return new ManagePresenter(interactor, obsScheduler, subScheduler);
  }

  @Provides @Named("manage_sync") ManagePresenter provideSync(
      @NonNull @Named("manage_sync_interactor") ManageInteractor interactor,
      @NonNull @Named("obs") Scheduler obsScheduler,
      @NonNull @Named("sub") Scheduler subScheduler) {
    return new ManagePresenter(interactor, obsScheduler, subScheduler);
  }

  @Provides @Named("manage_airplane") ManagePresenter provideAirplane(
      @NonNull @Named("manage_airplane_interactor") ManageInteractor interactor,
      @NonNull @Named("obs") Scheduler obsScheduler,
      @NonNull @Named("sub") Scheduler subScheduler) {
    return new ManagePresenter(interactor, obsScheduler, subScheduler);
  }

  @Provides @Named("manage_doze") ManagePresenter provideDoze(
      @NonNull @Named("manage_doze_interactor") ManageInteractor interactor,
      @NonNull @Named("obs") Scheduler obsScheduler,
      @NonNull @Named("sub") Scheduler subScheduler) {
    return new ManagePresenter(interactor, obsScheduler, subScheduler);
  }

  @Provides @Named("exception_wifi") ExceptionPresenter provideWifiException(
      @NonNull @Named("exception_wifi_interactor") ExceptionInteractor interactor,
      @NonNull @Named("obs") Scheduler obsScheduler,
      @NonNull @Named("sub") Scheduler subScheduler) {
    return new ExceptionPresenter(interactor, obsScheduler, subScheduler);
  }

  @Provides @Named("exception_data") ExceptionPresenter provideDataException(
      @NonNull @Named("exception_data_interactor") ExceptionInteractor interactor,
      @NonNull @Named("obs") Scheduler obsScheduler,
      @NonNull @Named("sub") Scheduler subScheduler) {
    return new ExceptionPresenter(interactor, obsScheduler, subScheduler);
  }

  @Provides @Named("exception_bluetooth") ExceptionPresenter provideBluetoothException(
      @NonNull @Named("exception_bluetooth_interactor") ExceptionInteractor interactor,
      @NonNull @Named("obs") Scheduler obsScheduler,
      @NonNull @Named("sub") Scheduler subScheduler) {
    return new ExceptionPresenter(interactor, obsScheduler, subScheduler);
  }

  @Provides @Named("exception_sync") ExceptionPresenter provideSyncException(
      @NonNull @Named("exception_sync_interactor") ExceptionInteractor interactor,
      @NonNull @Named("obs") Scheduler obsScheduler,
      @NonNull @Named("sub") Scheduler subScheduler) {
    return new ExceptionPresenter(interactor, obsScheduler, subScheduler);
  }

  @Provides @Named("exception_airplane") ExceptionPresenter provideAirplaneException(
      @NonNull @Named("exception_airplane_interactor") ExceptionInteractor interactor,
      @NonNull @Named("obs") Scheduler obsScheduler,
      @NonNull @Named("sub") Scheduler subScheduler) {
    return new ExceptionPresenter(interactor, obsScheduler, subScheduler);
  }

  @Provides @Named("exception_doze") ExceptionPresenter provideDozeException(
      @NonNull @Named("exception_doze_interactor") ExceptionInteractor interactor,
      @NonNull @Named("obs") Scheduler obsScheduler,
      @NonNull @Named("sub") Scheduler subScheduler) {
    return new ExceptionPresenter(interactor, obsScheduler, subScheduler);
  }
}
