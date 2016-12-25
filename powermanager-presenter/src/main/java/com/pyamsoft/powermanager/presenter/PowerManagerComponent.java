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

package com.pyamsoft.powermanager.presenter;

import com.pyamsoft.powermanager.presenter.airplane.AirplaneScreenComponent;
import com.pyamsoft.powermanager.presenter.bluetooth.BluetoothScreenComponent;
import com.pyamsoft.powermanager.presenter.data.DataScreenComponent;
import com.pyamsoft.powermanager.presenter.doze.DozeScreenComponent;
import com.pyamsoft.powermanager.presenter.logger.LoggerComponent;
import com.pyamsoft.powermanager.presenter.logger.LoggerModule;
import com.pyamsoft.powermanager.presenter.main.MainComponent;
import com.pyamsoft.powermanager.presenter.manager.ManagerComponent;
import com.pyamsoft.powermanager.presenter.modifier.state.StateModifierModule;
import com.pyamsoft.powermanager.presenter.observer.permission.PermissionObserverModule;
import com.pyamsoft.powermanager.presenter.observer.preference.manage.ManageObserverModule;
import com.pyamsoft.powermanager.presenter.observer.preference.periodic.PeriodicObserverModule;
import com.pyamsoft.powermanager.presenter.observer.state.StateObserverModule;
import com.pyamsoft.powermanager.presenter.overview.OverviewComponent;
import com.pyamsoft.powermanager.presenter.preference.CustomPreferenceComponent;
import com.pyamsoft.powermanager.presenter.queuer.QueuerComponent;
import com.pyamsoft.powermanager.presenter.queuer.QueuerModule;
import com.pyamsoft.powermanager.presenter.service.ActionToggleModule;
import com.pyamsoft.powermanager.presenter.service.ActionToggleServiceComponent;
import com.pyamsoft.powermanager.presenter.service.ForegroundModule;
import com.pyamsoft.powermanager.presenter.service.ForegroundServiceComponent;
import com.pyamsoft.powermanager.presenter.settings.SettingsPreferenceComponent;
import com.pyamsoft.powermanager.presenter.sync.SyncScreenComponent;
import com.pyamsoft.powermanager.presenter.trigger.PowerTriggerDBModule;
import com.pyamsoft.powermanager.presenter.trigger.TriggerComponent;
import com.pyamsoft.powermanager.presenter.trigger.TriggerRunnerComponent;
import com.pyamsoft.powermanager.presenter.wear.WearScreenComponent;
import com.pyamsoft.powermanager.presenter.wifi.WifiScreenComponent;
import com.pyamsoft.powermanager.presenter.wrapper.WrapperModule;
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
