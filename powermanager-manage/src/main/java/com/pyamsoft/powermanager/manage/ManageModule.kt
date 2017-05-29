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

package com.pyamsoft.powermanager.manage

import com.pyamsoft.powermanager.manage.ManageTargets.AIRPLANE
import com.pyamsoft.powermanager.manage.ManageTargets.BLUETOOTH
import com.pyamsoft.powermanager.manage.ManageTargets.DATA
import com.pyamsoft.powermanager.manage.ManageTargets.DOZE
import com.pyamsoft.powermanager.manage.ManageTargets.SYNC
import com.pyamsoft.powermanager.manage.ManageTargets.WIFI
import dagger.Module
import dagger.Provides
import io.reactivex.Scheduler
import javax.inject.Named

@Module class ManageModule {
  @Provides @Named("manage_wifi") internal fun provideWifi(
      @Named("manage_wifi_interactor") interactor: ManageInteractor,
      @Named("obs") obsScheduler: Scheduler,
      @Named("sub") subScheduler: Scheduler): ManagePresenter {
    return object : ManagePresenter(interactor, obsScheduler, subScheduler) {
      override val target: ManageTargets
        get() = WIFI
    }
  }

  @Provides @Named("manage_data") internal fun provideData(
      @Named("manage_data_interactor") interactor: ManageInteractor,
      @Named("obs") obsScheduler: Scheduler,
      @Named("sub") subScheduler: Scheduler): ManagePresenter {
    return object : ManagePresenter(interactor, obsScheduler, subScheduler) {
      override val target: ManageTargets
        get() = DATA
    }
  }

  @Provides @Named("manage_bluetooth") internal fun provideBluetooth(
      @Named("manage_bluetooth_interactor") interactor: ManageInteractor,
      @Named("obs") obsScheduler: Scheduler,
      @Named("sub") subScheduler: Scheduler): ManagePresenter {
    return object : ManagePresenter(interactor, obsScheduler, subScheduler) {
      override val target: ManageTargets
        get() = BLUETOOTH
    }
  }

  @Provides @Named("manage_sync") internal fun provideSync(
      @Named("manage_sync_interactor") interactor: ManageInteractor,
      @Named("obs") obsScheduler: Scheduler,
      @Named("sub") subScheduler: Scheduler): ManagePresenter {
    return object : ManagePresenter(interactor, obsScheduler, subScheduler) {
      override val target: ManageTargets
        get() = SYNC
    }
  }

  @Provides @Named("manage_airplane") internal fun provideAirplane(
      @Named("manage_airplane_interactor") interactor: ManageInteractor,
      @Named("obs") obsScheduler: Scheduler,
      @Named("sub") subScheduler: Scheduler): ManagePresenter {
    return object : ManagePresenter(interactor, obsScheduler, subScheduler) {
      override val target: ManageTargets
        get() = AIRPLANE
    }
  }

  @Provides @Named("manage_doze") internal fun provideDoze(
      @Named("manage_doze_interactor") interactor: ManageInteractor,
      @Named("obs") obsScheduler: Scheduler,
      @Named("sub") subScheduler: Scheduler): ManagePresenter {
    return object : ManagePresenter(interactor, obsScheduler, subScheduler) {
      override val target: ManageTargets
        get() = DOZE
    }
  }

  @Provides @Named("exception_wifi") internal fun provideWifiException(
      @Named("exception_wifi_interactor") interactor: ExceptionInteractor,
      @Named("obs") obsScheduler: Scheduler,
      @Named("sub") subScheduler: Scheduler): ExceptionPresenter {
    return object : ExceptionPresenter(interactor, obsScheduler, subScheduler) {
      override val target: ManageTargets
        get() = WIFI
    }
  }

  @Provides @Named("exception_data") internal fun provideDataException(
      @Named("exception_data_interactor") interactor: ExceptionInteractor,
      @Named("obs") obsScheduler: Scheduler,
      @Named("sub") subScheduler: Scheduler): ExceptionPresenter {
    return object : ExceptionPresenter(interactor, obsScheduler, subScheduler) {
      override val target: ManageTargets
        get() = DATA
    }
  }

  @Provides @Named("exception_bluetooth") internal fun provideBluetoothException(
      @Named("exception_bluetooth_interactor") interactor: ExceptionInteractor,
      @Named("obs") obsScheduler: Scheduler,
      @Named("sub") subScheduler: Scheduler): ExceptionPresenter {
    return object : ExceptionPresenter(interactor, obsScheduler, subScheduler) {
      override val target: ManageTargets
        get() = BLUETOOTH
    }
  }

  @Provides @Named("exception_sync") internal fun provideSyncException(
      @Named("exception_sync_interactor") interactor: ExceptionInteractor,
      @Named("obs") obsScheduler: Scheduler,
      @Named("sub") subScheduler: Scheduler): ExceptionPresenter {
    return object : ExceptionPresenter(interactor, obsScheduler, subScheduler) {
      override val target: ManageTargets
        get() = SYNC
    }
  }

  @Provides @Named("exception_airplane") internal fun provideAirplaneException(
      @Named("exception_airplane_interactor") interactor: ExceptionInteractor,
      @Named("obs") obsScheduler: Scheduler,
      @Named("sub") subScheduler: Scheduler): ExceptionPresenter {
    return object : ExceptionPresenter(interactor, obsScheduler, subScheduler) {
      override val target: ManageTargets
        get() = AIRPLANE
    }
  }

  @Provides @Named("exception_doze") internal fun provideDozeException(
      @Named("exception_doze_interactor") interactor: ExceptionInteractor,
      @Named("obs") obsScheduler: Scheduler,
      @Named("sub") subScheduler: Scheduler): ExceptionPresenter {
    return object : ExceptionPresenter(interactor, obsScheduler, subScheduler) {
      override val target: ManageTargets
        get() = DOZE
    }
  }
}
