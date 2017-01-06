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
import com.pyamsoft.powermanager.base.wrapper.DeviceFunctionWrapper;
import com.pyamsoft.powermanager.base.wrapper.JobQueuerWrapper;
import com.pyamsoft.powermanager.base.wrapper.WrapperModule;
import dagger.Component;
import javax.inject.Named;
import javax.inject.Singleton;
import rx.Scheduler;

@Singleton
@Component(modules = { PowerManagerModule.class, WrapperModule.class, PowerTriggerDBModule.class })
public interface PowerManagerComponent {

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
}
