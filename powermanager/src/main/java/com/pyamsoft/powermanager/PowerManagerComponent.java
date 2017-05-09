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

package com.pyamsoft.powermanager;

import com.pyamsoft.powermanager.airplane.AirplaneComponent;
import com.pyamsoft.powermanager.airplane.AirplaneSingletonModule;
import com.pyamsoft.powermanager.base.PowerManagerModule;
import com.pyamsoft.powermanager.base.logger.LoggerModule;
import com.pyamsoft.powermanager.base.permission.PermissionObserverModule;
import com.pyamsoft.powermanager.base.shell.ShellCommandModule;
import com.pyamsoft.powermanager.base.states.StateModifierModule;
import com.pyamsoft.powermanager.base.states.StateObserverModule;
import com.pyamsoft.powermanager.base.states.WrapperModule;
import com.pyamsoft.powermanager.bluetooth.BluetoothComponent;
import com.pyamsoft.powermanager.bluetooth.BluetoothSingletonModule;
import com.pyamsoft.powermanager.data.DataComponent;
import com.pyamsoft.powermanager.data.DataSingletonModule;
import com.pyamsoft.powermanager.doze.DozeComponent;
import com.pyamsoft.powermanager.doze.DozeSingletonModule;
import com.pyamsoft.powermanager.job.AirplaneJob;
import com.pyamsoft.powermanager.job.BluetoothJob;
import com.pyamsoft.powermanager.job.DataJob;
import com.pyamsoft.powermanager.job.DozeJob;
import com.pyamsoft.powermanager.job.JobModule;
import com.pyamsoft.powermanager.job.SyncJob;
import com.pyamsoft.powermanager.job.TriggerJob;
import com.pyamsoft.powermanager.job.WifiJob;
import com.pyamsoft.powermanager.logger.LoggerPreferenceFragment;
import com.pyamsoft.powermanager.main.MainActivity;
import com.pyamsoft.powermanager.manager.ManagerComponent;
import com.pyamsoft.powermanager.overview.OverviewFragment;
import com.pyamsoft.powermanager.overview.OverviewItem;
import com.pyamsoft.powermanager.service.ActionToggleService;
import com.pyamsoft.powermanager.service.ForegroundService;
import com.pyamsoft.powermanager.settings.SettingsPreferenceFragment;
import com.pyamsoft.powermanager.sync.SyncComponent;
import com.pyamsoft.powermanager.sync.SyncSingletonModule;
import com.pyamsoft.powermanager.trigger.TriggerComponent;
import com.pyamsoft.powermanager.trigger.db.PowerTriggerDBModule;
import com.pyamsoft.powermanager.wear.WearComponent;
import com.pyamsoft.powermanager.wear.WearSingletonModule;
import com.pyamsoft.powermanager.wifi.WifiComponent;
import com.pyamsoft.powermanager.wifi.WifiSingletonModule;
import dagger.Component;
import javax.inject.Singleton;

@Singleton @Component(modules = {
    PowerManagerModule.class, WrapperModule.class, PowerTriggerDBModule.class, LoggerModule.class,
    ShellCommandModule.class, PermissionObserverModule.class, StateObserverModule.class,
    StateModifierModule.class, JobModule.class, AirplaneSingletonModule.class,
    BluetoothSingletonModule.class, DataSingletonModule.class, DozeSingletonModule.class,
    SyncSingletonModule.class, WearSingletonModule.class, WifiSingletonModule.class,
    ManagerSingletonModule.class
}) public interface PowerManagerComponent {

  AirplaneComponent plusAirplaneComponent();

  BluetoothComponent plusBluetoothComponent();

  DataComponent plusDataComponent();

  DozeComponent plusDozeComponent();

  SyncComponent plusSyncComponent();

  WifiComponent plusWifiComponent();

  WearComponent plusWearComponent();

  ManagerComponent plusManagerComponent();

  TriggerComponent plusTriggerComponent();

  void inject(AirplaneJob airplaneJob);

  void inject(BluetoothJob bluetoothJob);

  void inject(DataJob dataJob);

  void inject(DozeJob dozeJob);

  void inject(SyncJob syncJob);

  void inject(TriggerJob triggerJob);

  void inject(WifiJob wifiJob);

  void inject(LoggerPreferenceFragment loggerPreferenceFragment);

  void inject(MainActivity mainActivity);

  void inject(OverviewFragment overviewFragment);

  void inject(OverviewItem overviewItem);

  void inject(ActionToggleService actionToggleService);

  void inject(ForegroundService foregroundService);

  void inject(SettingsPreferenceFragment settingsPreferenceFragment);
}
