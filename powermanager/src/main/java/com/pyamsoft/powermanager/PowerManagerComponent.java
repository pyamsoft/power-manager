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

package com.pyamsoft.powermanager;

import com.pyamsoft.powermanager.airplane.AirplaneScreenComponent;
import com.pyamsoft.powermanager.airplane.preference.AirplanePreferenceComponent;
import com.pyamsoft.powermanager.base.PowerManagerModule;
import com.pyamsoft.powermanager.base.db.PowerTriggerDBModule;
import com.pyamsoft.powermanager.base.logger.LoggerModule;
import com.pyamsoft.powermanager.base.modifier.StateModifierModule;
import com.pyamsoft.powermanager.base.observer.permission.PermissionObserverModule;
import com.pyamsoft.powermanager.base.observer.preference.manage.ManageObserverModule;
import com.pyamsoft.powermanager.base.observer.preference.preference.PeriodicObserverModule;
import com.pyamsoft.powermanager.base.observer.state.StateObserverModule;
import com.pyamsoft.powermanager.base.shell.ShellCommandModule;
import com.pyamsoft.powermanager.base.wrapper.WrapperModule;
import com.pyamsoft.powermanager.bluetooth.BluetoothScreenComponent;
import com.pyamsoft.powermanager.bluetooth.preference.BluetoothPreferenceComponent;
import com.pyamsoft.powermanager.data.DataScreenComponent;
import com.pyamsoft.powermanager.data.preference.DataPreferenceComponent;
import com.pyamsoft.powermanager.doze.DozeScreenComponent;
import com.pyamsoft.powermanager.doze.preference.DozePreferenceComponent;
import com.pyamsoft.powermanager.job.JobComponent;
import com.pyamsoft.powermanager.job.JobModule;
import com.pyamsoft.powermanager.logger.LoggerComponent;
import com.pyamsoft.powermanager.main.MainComponent;
import com.pyamsoft.powermanager.manager.ManagerComponent;
import com.pyamsoft.powermanager.overview.OverviewComponent;
import com.pyamsoft.powermanager.service.ActionToggleServiceComponent;
import com.pyamsoft.powermanager.service.ForegroundServiceComponent;
import com.pyamsoft.powermanager.settings.SettingsPreferenceComponent;
import com.pyamsoft.powermanager.sync.SyncScreenComponent;
import com.pyamsoft.powermanager.sync.preference.SyncPreferenceComponent;
import com.pyamsoft.powermanager.trigger.TriggerComponent;
import com.pyamsoft.powermanager.trigger.TriggerInteractorModule;
import com.pyamsoft.powermanager.wear.WearScreenComponent;
import com.pyamsoft.powermanager.wifi.WifiScreenComponent;
import com.pyamsoft.powermanager.wifi.preference.WifiPreferenceComponent;
import dagger.Component;
import javax.inject.Singleton;

@Singleton @Component(modules = {
    PowerManagerModule.class, WrapperModule.class, PowerTriggerDBModule.class, LoggerModule.class,
    ShellCommandModule.class, PermissionObserverModule.class, ManageObserverModule.class,
    PeriodicObserverModule.class, StateObserverModule.class, StateModifierModule.class,
    TriggerInteractorModule.class, JobModule.class
}) public interface PowerManagerComponent {

  AirplaneScreenComponent plusAirplaneScreenComponent();

  AirplanePreferenceComponent plusAirplanePreferenceComponent();

  BluetoothScreenComponent plusBluetoothScreenComponent();

  BluetoothPreferenceComponent plusBluetoothPreferenceComponent();

  DataScreenComponent plusDataScreenComponent();

  DataPreferenceComponent plusDataPreferenceComponent();

  DozeScreenComponent plusDozeScreenComponent();

  DozePreferenceComponent plusDozePreferenceComponent();

  MainComponent plusMainComponent();

  ManagerComponent plusManagerComponent();

  OverviewComponent plusOverviewComponent();

  ActionToggleServiceComponent plusActionToggleServiceComponent();

  ForegroundServiceComponent plusForegroundServiceComponent();

  SettingsPreferenceComponent plusSettingsPreferenceComponent();

  SyncScreenComponent plusSyncScreenComponent();

  SyncPreferenceComponent plusSyncPreferenceComponent();

  WifiScreenComponent plusWifiScreenComponent();

  WifiPreferenceComponent plusWifiPreferenceComponent();

  WearScreenComponent plusWearScreenComponent();

  TriggerComponent plusTriggerComponent();

  LoggerComponent plusLoggerComponent();

  JobComponent plusJobComponent();
}
