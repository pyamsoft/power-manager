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

package com.pyamsoft.powermanager.dagger;

import com.pyamsoft.powermanager.dagger.bluetooth.BluetoothScreenComponent;
import com.pyamsoft.powermanager.dagger.data.DataScreenComponent;
import com.pyamsoft.powermanager.dagger.doze.DozeScreenComponent;
import com.pyamsoft.powermanager.dagger.job.JobComponent;
import com.pyamsoft.powermanager.dagger.manager.ManagerComponent;
import com.pyamsoft.powermanager.dagger.modifier.preference.manage.ManageModifierModule;
import com.pyamsoft.powermanager.dagger.modifier.preference.periodic.PeriodicModifierModule;
import com.pyamsoft.powermanager.dagger.modifier.state.StateModifierModule;
import com.pyamsoft.powermanager.dagger.observer.preference.manage.ManageObserverModule;
import com.pyamsoft.powermanager.dagger.observer.preference.periodic.PeriodicObserverModule;
import com.pyamsoft.powermanager.dagger.observer.state.StateObserverModule;
import com.pyamsoft.powermanager.dagger.preference.CustomPreferenceComponent;
import com.pyamsoft.powermanager.dagger.service.ForegroundModule;
import com.pyamsoft.powermanager.dagger.service.ForegroundServiceComponent;
import com.pyamsoft.powermanager.dagger.service.notification.NotificationDialogComponent;
import com.pyamsoft.powermanager.dagger.service.notification.FullNotificationComponent;
import com.pyamsoft.powermanager.dagger.service.jobs.JobServiceComponent;
import com.pyamsoft.powermanager.dagger.settings.SettingsPreferenceComponent;
import com.pyamsoft.powermanager.dagger.sync.SyncScreenComponent;
import com.pyamsoft.powermanager.dagger.trigger.TriggerComponent;
import com.pyamsoft.powermanager.dagger.trigger.TriggerJobComponent;
import com.pyamsoft.powermanager.dagger.wifi.WifiScreenComponent;
import com.pyamsoft.powermanager.dagger.wrapper.JobSchedulerCompatModule;
import dagger.Component;
import javax.inject.Singleton;

@Singleton @Component(modules = {
    PowerManagerModule.class, StateModifierModule.class, StateObserverModule.class,
    ManageModifierModule.class, ManageObserverModule.class, PeriodicObserverModule.class,
    PeriodicModifierModule.class, ForegroundModule.class, JobSchedulerCompatModule.class
}) public interface PowerManagerComponent {

  TriggerComponent plusTrigger();

  FullNotificationComponent plusFullNotificationComponent();

  NotificationDialogComponent plusNotificationDialogComponent();

  ForegroundServiceComponent plusForegroundServiceComponent();

  TriggerJobComponent plusTriggerJobComponent();

  ManagerComponent plusManagerComponent();

  JobComponent plusJobComponent();

  CustomPreferenceComponent plusCustomPreferenceComponent();

  WifiScreenComponent plusWifiScreenComponent();

  DataScreenComponent plusDataScreenComponent();

  BluetoothScreenComponent plusBluetoothScreenComponent();

  SyncScreenComponent plusSyncScreenComponent();

  DozeScreenComponent plusDozeScreenComponent();

  SettingsPreferenceComponent plusSettingsPreferenceComponent();

  JobServiceComponent plusJobServiceComponent();
}
