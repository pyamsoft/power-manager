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

package com.pyamsoft.powermanager.base.logger;

import android.content.Context;
import android.support.annotation.NonNull;
import com.pyamsoft.powermanager.base.PowerManagerPreferences;
import com.pyamsoft.powermanager.model.Logger;
import dagger.Module;
import dagger.Provides;
import javax.inject.Named;
import javax.inject.Singleton;
import rx.Scheduler;

@Module public class LoggerModule {

  @Singleton @Provides @Named("logger_manager") Logger provideLoggerManager(
      @NonNull @Named("logger_presenter_manager") LoggerPresenter loggerPresenter) {
    return new LoggerImpl(loggerPresenter);
  }

  @Singleton @Provides @Named("logger_presenter_manager")
  LoggerPresenter provideLoggerPresenterManager(
      @NonNull @Named("logger_interactor_manager") LoggerInteractor interactor,
      @Named("obs") Scheduler obsScheduler, @Named("sub") Scheduler subScheduler) {
    return new LoggerPresenter(interactor, obsScheduler, subScheduler);
  }

  @Singleton @Provides @Named("logger_interactor_manager")
  LoggerInteractor provideLoggerInteractorManager(@NonNull Context context,
      @NonNull PowerManagerPreferences preferences) {
    return new LoggerInteractor(context, preferences, LoggerInteractor.MANAGER_LOG_ID);
  }

  @Singleton @Provides @Named("logger_wifi") Logger provideLoggerWifi(
      @NonNull @Named("logger_presenter_wifi") LoggerPresenter loggerPresenter) {
    return new LoggerImpl(loggerPresenter);
  }

  @Singleton @Provides @Named("logger_presenter_wifi") LoggerPresenter provideLoggerPresenterWifi(
      @NonNull @Named("logger_interactor_wifi") LoggerInteractor interactor,
      @Named("obs") Scheduler obsScheduler, @Named("sub") Scheduler subScheduler) {
    return new LoggerPresenter(interactor, obsScheduler, subScheduler);
  }

  @Singleton @Provides @Named("logger_interactor_wifi")
  LoggerInteractor provideLoggerInteractorWifi(@NonNull Context context,
      @NonNull PowerManagerPreferences preferences) {
    return new LoggerInteractor(context, preferences, LoggerInteractor.WIFI_LOG_ID);
  }

  @Singleton @Provides @Named("logger_data") Logger provideLoggerData(
      @NonNull @Named("logger_presenter_data") LoggerPresenter loggerPresenter) {
    return new LoggerImpl(loggerPresenter);
  }

  @Singleton @Provides @Named("logger_presenter_data") LoggerPresenter provideLoggerPresenterData(
      @NonNull @Named("logger_interactor_data") LoggerInteractor interactor,
      @Named("obs") Scheduler obsScheduler, @Named("sub") Scheduler subScheduler) {
    return new LoggerPresenter(interactor, obsScheduler, subScheduler);
  }

  @Singleton @Provides @Named("logger_interactor_data")
  LoggerInteractor provideLoggerInteractorData(@NonNull Context context,
      @NonNull PowerManagerPreferences preferences) {
    return new LoggerInteractor(context, preferences, LoggerInteractor.DATA_LOG_ID);
  }

  @Singleton @Provides @Named("logger_bluetooth") Logger provideLoggerBluetooth(
      @NonNull @Named("logger_presenter_bluetooth") LoggerPresenter loggerPresenter) {
    return new LoggerImpl(loggerPresenter);
  }

  @Singleton @Provides @Named("logger_presenter_bluetooth")
  LoggerPresenter provideLoggerPresenterBluetooth(
      @NonNull @Named("logger_interactor_bluetooth") LoggerInteractor interactor,
      @Named("obs") Scheduler obsScheduler, @Named("sub") Scheduler subScheduler) {
    return new LoggerPresenter(interactor, obsScheduler, subScheduler);
  }

  @Singleton @Provides @Named("logger_interactor_bluetooth")
  LoggerInteractor provideLoggerInteractorBluetooth(@NonNull Context context,
      @NonNull PowerManagerPreferences preferences) {
    return new LoggerInteractor(context, preferences, LoggerInteractor.BLUETOOTH_LOG_ID);
  }

  @Singleton @Provides @Named("logger_sync") Logger provideLoggerSync(
      @NonNull @Named("logger_presenter_sync") LoggerPresenter loggerPresenter) {
    return new LoggerImpl(loggerPresenter);
  }

  @Singleton @Provides @Named("logger_presenter_sync") LoggerPresenter provideLoggerPresenterSync(
      @NonNull @Named("logger_interactor_sync") LoggerInteractor interactor,
      @Named("obs") Scheduler obsScheduler, @Named("sub") Scheduler subScheduler) {
    return new LoggerPresenter(interactor, obsScheduler, subScheduler);
  }

  @Singleton @Provides @Named("logger_interactor_sync")
  LoggerInteractor provideLoggerInteractorSync(@NonNull Context context,
      @NonNull PowerManagerPreferences preferences) {
    return new LoggerInteractor(context, preferences, LoggerInteractor.SYNC_LOG_ID);
  }

  @Singleton @Provides @Named("logger_airplane") Logger provideLoggerAirplane(
      @NonNull @Named("logger_presenter_airplane") LoggerPresenter loggerPresenter) {
    return new LoggerImpl(loggerPresenter);
  }

  @Singleton @Provides @Named("logger_presenter_airplane")
  LoggerPresenter provideLoggerPresenterAirplane(
      @NonNull @Named("logger_interactor_airplane") LoggerInteractor interactor,
      @Named("obs") Scheduler obsScheduler, @Named("sub") Scheduler subScheduler) {
    return new LoggerPresenter(interactor, obsScheduler, subScheduler);
  }

  @Singleton @Provides @Named("logger_interactor_airplane")
  LoggerInteractor provideLoggerInteractorAirplane(@NonNull Context context,
      @NonNull PowerManagerPreferences preferences) {
    return new LoggerInteractor(context, preferences, LoggerInteractor.AIRPLANE_LOG_ID);
  }

  @Singleton @Provides @Named("logger_doze") Logger provideLoggerDoze(
      @NonNull @Named("logger_presenter_doze") LoggerPresenter loggerPresenter) {
    return new LoggerImpl(loggerPresenter);
  }

  @Singleton @Provides @Named("logger_presenter_doze") LoggerPresenter provideLoggerPresenterDoze(
      @NonNull @Named("logger_interactor_doze") LoggerInteractor interactor,
      @Named("obs") Scheduler obsScheduler, @Named("sub") Scheduler subScheduler) {
    return new LoggerPresenter(interactor, obsScheduler, subScheduler);
  }

  @Singleton @Provides @Named("logger_interactor_doze")
  LoggerInteractor provideLoggerInteractorDoze(@NonNull Context context,
      @NonNull PowerManagerPreferences preferences) {
    return new LoggerInteractor(context, preferences, LoggerInteractor.DOZE_LOG_ID);
  }

  @Singleton @Provides @Named("logger_trigger") Logger provideLoggerTrigger(
      @NonNull @Named("logger_presenter_trigger") LoggerPresenter loggerPresenter) {
    return new LoggerImpl(loggerPresenter);
  }

  @Singleton @Provides @Named("logger_presenter_trigger")
  LoggerPresenter provideLoggerPresenterTrigger(
      @NonNull @Named("logger_interactor_trigger") LoggerInteractor interactor,
      @Named("obs") Scheduler obsScheduler, @Named("sub") Scheduler subScheduler) {
    return new LoggerPresenter(interactor, obsScheduler, subScheduler);
  }

  @Singleton @Provides @Named("logger_interactor_trigger")
  LoggerInteractor provideLoggerInteractorTrigger(@NonNull Context context,
      @NonNull PowerManagerPreferences preferences) {
    return new LoggerInteractor(context, preferences, LoggerInteractor.TRIGGER_LOG_ID);
  }
}
