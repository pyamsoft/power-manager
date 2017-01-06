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

package com.pyamsoft.powermanager.base;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import com.pyamsoft.powermanager.base.db.PowerTriggerDB;
import com.pyamsoft.powermanager.base.db.PowerTriggerDBModule;
import com.pyamsoft.powermanager.base.logger.Logger;
import com.pyamsoft.powermanager.base.logger.LoggerLoader;
import com.pyamsoft.powermanager.base.logger.LoggerModule;
import com.pyamsoft.powermanager.base.logger.LoggerPresenter;
import com.pyamsoft.powermanager.base.wrapper.DeviceFunctionWrapper;
import com.pyamsoft.powermanager.base.wrapper.JobQueuerWrapper;
import com.pyamsoft.powermanager.base.wrapper.WrapperModule;
import dagger.Component;
import javax.inject.Named;
import javax.inject.Singleton;
import rx.Scheduler;

@Singleton @Component(modules = {
    PowerManagerModule.class, WrapperModule.class, PowerTriggerDBModule.class, LoggerModule.class
}) public interface PowerManagerComponent {

  void inject(LoggerLoader loader);

  PowerTriggerDB providePowerTriggerDb();

  @Named("main") Class<? extends Activity> provideMainClass();

  @Named("toggle") Class<? extends Service> provideToggleServiceClass();

  @Named("triggerrunner") Class<? extends Service> provideTriggerRunnerServiceClass();

  Context provideContext();

  PowerManagerPreferences providePreferences();

  @Named("io") Scheduler provideIoScheduler();

  @Named("sub") Scheduler provideSubScheduler();

  @Named("obs") Scheduler provideObsScheduler();

  @Named("wrapper_wifi") DeviceFunctionWrapper provideWifiManagerWrapper();

  @Named("wrapper_bluetooth") DeviceFunctionWrapper provideBluetoothAdapterWrapper();

  @Named("wrapper_data") DeviceFunctionWrapper provideDataConnectionWrapper();

  @Named("wrapper_sync") DeviceFunctionWrapper provideSyncConnectionWrapper();

  @Named("wrapper_airplane") DeviceFunctionWrapper provideAirplaneModeWrapper();

  @Named("wrapper_doze") DeviceFunctionWrapper provideDozeWrapper();

  JobQueuerWrapper provideJobQueuerWrapper();

  @Named("logger_manager") Logger provideLoggerManager();

  @Named("logger_presenter_manager") LoggerPresenter provideLoggerPresenterManager();

  @Named("logger_wifi") Logger provideLoggerWifi();

  @Named("logger_presenter_wifi") LoggerPresenter provideLoggerPresenterWifi();

  @Named("logger_data") Logger provideLoggerData();

  @Named("logger_presenter_data") LoggerPresenter provideLoggerPresenterData();

  @Named("logger_bluetooth") Logger provideLoggerBluetooth();

  @Named("logger_presenter_bluetooth") LoggerPresenter provideLoggerPresenterBluetooth();

  @Named("logger_sync") Logger provideLoggerSync();

  @Named("logger_presenter_sync") LoggerPresenter provideLoggerPresenterSync();

  @Named("logger_airplane") Logger provideLoggerAirplane();

  @Named("logger_presenter_airplane") LoggerPresenter provideLoggerPresenterAirplane();

  @Named("logger_doze") Logger provideLoggerDoze();

  @Named("logger_presenter_doze") LoggerPresenter provideLoggerPresenterDoze();

  @Named("logger_trigger") Logger provideLoggerTrigger();

  @Named("logger_presenter_trigger") LoggerPresenter provideLoggerPresenterTrigger();
}
