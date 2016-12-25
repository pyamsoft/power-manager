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

package com.pyamsoft.powermanager.presenter.logger;

import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import com.pyamsoft.powermanager.model.LoggerType;
import com.pyamsoft.powermanager.presenter.Injector;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;

public class LoggerLoader {

  @SuppressWarnings("WeakerAccess") @Inject @Named("logger_presenter_manager")
  Provider<LoggerPresenter> managerLogger;
  @SuppressWarnings("WeakerAccess") @Inject @Named("logger_presenter_airplane")
  Provider<LoggerPresenter> airplaneLogger;
  @SuppressWarnings("WeakerAccess") @Inject @Named("logger_presenter_wifi")
  Provider<LoggerPresenter> wifiLogger;
  @SuppressWarnings("WeakerAccess") @Inject @Named("logger_presenter_data")
  Provider<LoggerPresenter> dataLogger;
  @SuppressWarnings("WeakerAccess") @Inject @Named("logger_presenter_bluetooth")
  Provider<LoggerPresenter> bluetoothLogger;
  @SuppressWarnings("WeakerAccess") @Inject @Named("logger_presenter_sync")
  Provider<LoggerPresenter> syncLogger;
  @SuppressWarnings("WeakerAccess") @Inject @Named("logger_presenter_doze")
  Provider<LoggerPresenter> dozeLogger;
  @SuppressWarnings("WeakerAccess") @Inject @Named("logger_presenter_trigger")
  Provider<LoggerPresenter> triggerLogger;

  public LoggerLoader() {
    Injector.get().provideComponent().plusLoggerComponent().inject(this);
  }

  @CheckResult @NonNull public LoggerPresenter loadLoggerPresenter(@NonNull LoggerType type) {
    final LoggerPresenter presenter;
    switch (type) {
      case MANAGER:
        presenter = managerLogger.get();
        break;
      case AIRPLANE:
        presenter = airplaneLogger.get();
        break;
      case WIFI:
        presenter = wifiLogger.get();
        break;
      case DATA:
        presenter = dataLogger.get();
        break;
      case BLUETOOTH:
        presenter = bluetoothLogger.get();
        break;
      case SYNC:
        presenter = syncLogger.get();
        break;
      case DOZE:
        presenter = dozeLogger.get();
        break;
      case TRIGGER:
        presenter = triggerLogger.get();
        break;
      default:
        throw new IllegalStateException("Invalid LoggerType: " + type);
    }
    return presenter;
  }
}
