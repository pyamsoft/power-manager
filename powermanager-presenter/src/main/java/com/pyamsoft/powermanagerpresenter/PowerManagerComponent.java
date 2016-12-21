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

package com.pyamsoft.powermanagerpresenter;

import com.pyamsoft.powermanagerpresenter.airplane.AirplaneScreenComponent;
import com.pyamsoft.powermanagerpresenter.bluetooth.BluetoothScreenComponent;
import com.pyamsoft.powermanagerpresenter.data.DataScreenComponent;
import com.pyamsoft.powermanagerpresenter.doze.DozeScreenComponent;
import com.pyamsoft.powermanagerpresenter.logger.LoggerComponent;
import com.pyamsoft.powermanagerpresenter.logger.LoggerModule;
import com.pyamsoft.powermanagerpresenter.main.MainComponent;
import com.pyamsoft.powermanagerpresenter.manager.ManagerComponent;
import com.pyamsoft.powermanagerpresenter.modifier.state.StateModifierModule;
import com.pyamsoft.powermanagerpresenter.observer.permission.PermissionObserverModule;
import com.pyamsoft.powermanagerpresenter.observer.preference.manage.ManageObserverModule;
import com.pyamsoft.powermanagerpresenter.observer.preference.periodic.PeriodicObserverModule;
import com.pyamsoft.powermanagerpresenter.observer.state.StateObserverModule;
import com.pyamsoft.powermanagerpresenter.overview.OverviewComponent;
import com.pyamsoft.powermanagerpresenter.preference.CustomPreferenceComponent;
import com.pyamsoft.powermanagerpresenter.queuer.QueuerComponent;
import com.pyamsoft.powermanagerpresenter.queuer.QueuerModule;
import com.pyamsoft.powermanagerpresenter.service.ActionToggleModule;
import com.pyamsoft.powermanagerpresenter.service.ActionToggleServiceComponent;
import com.pyamsoft.powermanagerpresenter.service.ForegroundModule;
import com.pyamsoft.powermanagerpresenter.service.ForegroundServiceComponent;
import com.pyamsoft.powermanagerpresenter.settings.SettingsPreferenceComponent;
import com.pyamsoft.powermanagerpresenter.sync.SyncScreenComponent;
import com.pyamsoft.powermanagerpresenter.trigger.PowerTriggerDBModule;
import com.pyamsoft.powermanagerpresenter.trigger.TriggerComponent;
import com.pyamsoft.powermanagerpresenter.trigger.TriggerRunnerComponent;
import com.pyamsoft.powermanagerpresenter.wear.WearScreenComponent;
import com.pyamsoft.powermanagerpresenter.wifi.WifiScreenComponent;
import com.pyamsoft.powermanagerpresenter.wrapper.WrapperModule;
import dagger.Component;
import javax.inject.Singleton;

@Singleton @Component(modules = {
    PowerManagerModule.class, StateModifierModule.class, StateObserverModule.class,
    ManageObserverModule.class, PeriodicObserverModule.class, ForegroundModule.class,
    ActionToggleModule.class, WrapperModule.class, PermissionObserverModule.class,
    PowerTriggerDBModule.class, LoggerModule.class, QueuerModule.class
}) public interface PowerManagerComponent {

  MainComponent plusMainComponent();

  TriggerComponent plusTriggerComponent();

  ForegroundServiceComponent plusForegroundServiceComponent();

  ActionToggleServiceComponent plusActionToggleServiceComponent();

  ManagerComponent plusManagerComponent();

  CustomPreferenceComponent plusCustomPreferenceComponent();

  WifiScreenComponent plusWifiScreenComponent();

  DataScreenComponent plusDataScreenComponent();

  BluetoothScreenComponent plusBluetoothScreenComponent();

  AirplaneScreenComponent plusAirplaneScreenComponent();

  SyncScreenComponent plusSyncScreenComponent();

  DozeScreenComponent plusDozeScreenComponent();

  WearScreenComponent plusWearScreenComponent();

  SettingsPreferenceComponent plusSettingsPreferenceComponent();

  OverviewComponent plusOverviewComponent();

  LoggerComponent plusLoggerComponent();

  QueuerComponent plusQueuerComponent();

  TriggerRunnerComponent plusTriggerRunnerComponent();
}
