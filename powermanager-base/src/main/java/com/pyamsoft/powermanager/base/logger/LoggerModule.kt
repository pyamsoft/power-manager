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

package com.pyamsoft.powermanager.base.logger

import android.content.Context
import com.pyamsoft.powermanager.base.preference.LoggerPreferences
import dagger.Module
import dagger.Provides
import io.reactivex.Scheduler
import javax.inject.Named
import javax.inject.Singleton

@Module class LoggerModule {

  @Singleton @Provides @Named("logger_manager") fun provideLoggerManager(
      @Named("logger_presenter_manager") loggerPresenter: LoggerPresenter): Logger {
    return LoggerImpl(loggerPresenter)
  }

  @Singleton @Provides @Named("logger_presenter_manager") fun provideLoggerPresenterManager(
      @Named("logger_interactor_manager") interactor: LoggerInteractor,
      @Named("obs") obsScheduler: Scheduler,
      @Named("sub") subScheduler: Scheduler): LoggerPresenter {
    return LoggerPresenter(interactor, obsScheduler, subScheduler)
  }

  @Singleton @Provides @Named("logger_interactor_manager") fun provideLoggerInteractorManager(
      context: Context, preferences: LoggerPreferences): LoggerInteractor {
    return LoggerInteractor(context, preferences, LoggerInteractor.MANAGER_LOG_ID)
  }

  @Singleton @Provides @Named("logger_wifi") fun provideLoggerWifi(
      @Named("logger_presenter_wifi") loggerPresenter: LoggerPresenter): Logger {
    return LoggerImpl(loggerPresenter)
  }

  @Singleton @Provides @Named("logger_presenter_wifi") fun provideLoggerPresenterWifi(
      @Named("logger_interactor_wifi") interactor: LoggerInteractor,
      @Named("obs") obsScheduler: Scheduler,
      @Named("sub") subScheduler: Scheduler): LoggerPresenter {
    return LoggerPresenter(interactor, obsScheduler, subScheduler)
  }

  @Singleton @Provides @Named("logger_interactor_wifi") fun provideLoggerInteractorWifi(
      context: Context, preferences: LoggerPreferences): LoggerInteractor {
    return LoggerInteractor(context, preferences, LoggerInteractor.WIFI_LOG_ID)
  }

  @Singleton @Provides @Named("logger_data") fun provideLoggerData(
      @Named("logger_presenter_data") loggerPresenter: LoggerPresenter): Logger {
    return LoggerImpl(loggerPresenter)
  }

  @Singleton @Provides @Named("logger_presenter_data") fun provideLoggerPresenterData(
      @Named("logger_interactor_data") interactor: LoggerInteractor,
      @Named("obs") obsScheduler: Scheduler,
      @Named("sub") subScheduler: Scheduler): LoggerPresenter {
    return LoggerPresenter(interactor, obsScheduler, subScheduler)
  }

  @Singleton @Provides @Named("logger_interactor_data") fun provideLoggerInteractorData(
      context: Context, preferences: LoggerPreferences): LoggerInteractor {
    return LoggerInteractor(context, preferences, LoggerInteractor.DATA_LOG_ID)
  }

  @Singleton @Provides @Named("logger_bluetooth") fun provideLoggerBluetooth(
      @Named("logger_presenter_bluetooth") loggerPresenter: LoggerPresenter): Logger {
    return LoggerImpl(loggerPresenter)
  }

  @Singleton @Provides @Named("logger_presenter_bluetooth") fun provideLoggerPresenterBluetooth(
      @Named("logger_interactor_bluetooth") interactor: LoggerInteractor,
      @Named("obs") obsScheduler: Scheduler,
      @Named("sub") subScheduler: Scheduler): LoggerPresenter {
    return LoggerPresenter(interactor, obsScheduler, subScheduler)
  }

  @Singleton @Provides @Named("logger_interactor_bluetooth") fun provideLoggerInteractorBluetooth(
      context: Context, preferences: LoggerPreferences): LoggerInteractor {
    return LoggerInteractor(context, preferences, LoggerInteractor.BLUETOOTH_LOG_ID)
  }

  @Singleton @Provides @Named("logger_sync") fun provideLoggerSync(
      @Named("logger_presenter_sync") loggerPresenter: LoggerPresenter): Logger {
    return LoggerImpl(loggerPresenter)
  }

  @Singleton @Provides @Named("logger_presenter_sync") fun provideLoggerPresenterSync(
      @Named("logger_interactor_sync") interactor: LoggerInteractor,
      @Named("obs") obsScheduler: Scheduler,
      @Named("sub") subScheduler: Scheduler): LoggerPresenter {
    return LoggerPresenter(interactor, obsScheduler, subScheduler)
  }

  @Singleton @Provides @Named("logger_interactor_sync") fun provideLoggerInteractorSync(
      context: Context, preferences: LoggerPreferences): LoggerInteractor {
    return LoggerInteractor(context, preferences, LoggerInteractor.SYNC_LOG_ID)
  }

  @Singleton @Provides @Named("logger_airplane") fun provideLoggerAirplane(
      @Named("logger_presenter_airplane") loggerPresenter: LoggerPresenter): Logger {
    return LoggerImpl(loggerPresenter)
  }

  @Singleton @Provides @Named("logger_presenter_airplane") fun provideLoggerPresenterAirplane(
      @Named("logger_interactor_airplane") interactor: LoggerInteractor,
      @Named("obs") obsScheduler: Scheduler,
      @Named("sub") subScheduler: Scheduler): LoggerPresenter {
    return LoggerPresenter(interactor, obsScheduler, subScheduler)
  }

  @Singleton @Provides @Named("logger_interactor_airplane") fun provideLoggerInteractorAirplane(
      context: Context, preferences: LoggerPreferences): LoggerInteractor {
    return LoggerInteractor(context, preferences, LoggerInteractor.AIRPLANE_LOG_ID)
  }

  @Singleton @Provides @Named("logger_doze") fun provideLoggerDoze(
      @Named("logger_presenter_doze") loggerPresenter: LoggerPresenter): Logger {
    return LoggerImpl(loggerPresenter)
  }

  @Singleton @Provides @Named("logger_presenter_doze") fun provideLoggerPresenterDoze(
      @Named("logger_interactor_doze") interactor: LoggerInteractor,
      @Named("obs") obsScheduler: Scheduler,
      @Named("sub") subScheduler: Scheduler): LoggerPresenter {
    return LoggerPresenter(interactor, obsScheduler, subScheduler)
  }

  @Singleton @Provides @Named("logger_interactor_doze") fun provideLoggerInteractorDoze(
      context: Context, preferences: LoggerPreferences): LoggerInteractor {
    return LoggerInteractor(context, preferences, LoggerInteractor.DOZE_LOG_ID)
  }

  @Singleton @Provides @Named("logger_trigger") fun provideLoggerTrigger(
      @Named("logger_presenter_trigger") loggerPresenter: LoggerPresenter): Logger {
    return LoggerImpl(loggerPresenter)
  }

  @Singleton @Provides @Named("logger_presenter_trigger") fun provideLoggerPresenterTrigger(
      @Named("logger_interactor_trigger") interactor: LoggerInteractor,
      @Named("obs") obsScheduler: Scheduler,
      @Named("sub") subScheduler: Scheduler): LoggerPresenter {
    return LoggerPresenter(interactor, obsScheduler, subScheduler)
  }

  @Singleton @Provides @Named("logger_interactor_trigger") fun provideLoggerInteractorTrigger(
      context: Context, preferences: LoggerPreferences): LoggerInteractor {
    return LoggerInteractor(context, preferences, LoggerInteractor.TRIGGER_LOG_ID)
  }

  @Singleton @Provides @Named("logger_data_saver") fun provideLoggerDataSaver(
      @Named("logger_presenter_data_saver") loggerPresenter: LoggerPresenter): Logger {
    return LoggerImpl(loggerPresenter)
  }

  @Singleton @Provides @Named("logger_presenter_data_saver") fun provideLoggerPresenterDataSaver(
      @Named("logger_interactor_data_saver") interactor: LoggerInteractor,
      @Named("obs") obsScheduler: Scheduler,
      @Named("sub") subScheduler: Scheduler): LoggerPresenter {
    return LoggerPresenter(interactor, obsScheduler, subScheduler)
  }

  @Singleton @Provides @Named("logger_interactor_data_saver") fun provideLoggerInteractorDataSaver(
      context: Context, preferences: LoggerPreferences): LoggerInteractor {
    return LoggerInteractor(context, preferences, LoggerInteractor.DATA_SAVER_LOG_ID)
  }
}
