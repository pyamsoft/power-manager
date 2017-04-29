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

package com.pyamsoft.powermanager.manager;

import android.support.annotation.NonNull;
import dagger.Module;
import dagger.Provides;
import io.reactivex.Scheduler;
import javax.inject.Named;

@Module public class ManagerModule {

  @Provides @Named("wifi_manager") Manager provideManagerWifi(
      @Named("wifi_manager_interactor") @NonNull WearAwareManagerInteractor interactor,
      @Named("sub") Scheduler subScheduler) {
    return new Manager(interactor, subScheduler);
  }

  @Provides @Named("data_manager") Manager provideManagerData(
      @Named("data_manager_interactor") @NonNull ManagerInteractor interactor,
      @Named("sub") Scheduler subScheduler) {
    return new Manager(interactor, subScheduler);
  }

  @Provides @Named("bluetooth_manager") Manager provideManagerBluetooth(
      @Named("bluetooth_manager_interactor") @NonNull WearAwareManagerInteractor interactor,
      @Named("sub") Scheduler subScheduler) {
    return new Manager(interactor, subScheduler);
  }

  @Provides @Named("sync_manager") Manager provideManagerSync(
      @Named("sync_manager_interactor") @NonNull ManagerInteractor interactor,
      @Named("sub") Scheduler subScheduler) {
    return new Manager(interactor, subScheduler);
  }

  @Provides @Named("doze_manager") Manager provideManagerDoze(
      @Named("doze_manager_interactor") @NonNull ManagerInteractor interactor,
      @Named("sub") Scheduler subScheduler) {
    return new Manager(interactor, subScheduler);
  }

  @Provides @Named("airplane_manager") Manager provideManagerAirplane(
      @Named("airplane_manager_interactor") @NonNull WearAwareManagerInteractor interactor,
      @Named("sub") Scheduler subScheduler) {
    return new Manager(interactor, subScheduler);
  }
}
