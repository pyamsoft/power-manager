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

import com.pyamsoft.powermanager.dagger.job.JobComponent;
import com.pyamsoft.powermanager.dagger.managepreference.ManagePreferenceComponent;
import com.pyamsoft.powermanager.dagger.manager.ManagerComponent;
import com.pyamsoft.powermanager.dagger.modifier.preference.manage.ManageModifierModule;
import com.pyamsoft.powermanager.dagger.modifier.preference.periodic.PeriodicModifierModule;
import com.pyamsoft.powermanager.dagger.modifier.state.StateModifierModule;
import com.pyamsoft.powermanager.dagger.observer.preference.manage.ManageObserverModule;
import com.pyamsoft.powermanager.dagger.observer.preference.periodic.PeriodicObserverModule;
import com.pyamsoft.powermanager.dagger.observer.state.StateObserverModule;
import com.pyamsoft.powermanager.dagger.periodpreference.PeriodPreferenceComponent;
import com.pyamsoft.powermanager.dagger.preference.CustomPreferenceComponent;
import com.pyamsoft.powermanager.dagger.screen.BluetoothScreenComponent;
import com.pyamsoft.powermanager.dagger.screen.DataScreenComponent;
import com.pyamsoft.powermanager.dagger.screen.SyncScreenComponent;
import com.pyamsoft.powermanager.dagger.screen.WifiScreenComponent;
import com.pyamsoft.powermanager.dagger.service.ForegroundModule;
import com.pyamsoft.powermanager.dagger.service.ForegroundServiceComponent;
import com.pyamsoft.powermanager.dagger.service.FullDialogComponent;
import com.pyamsoft.powermanager.dagger.service.FullNotificationComponent;
import com.pyamsoft.powermanager.dagger.service.jobs.JobServiceComponent;
import com.pyamsoft.powermanager.dagger.settings.SettingsPreferenceComponent;
import com.pyamsoft.powermanager.dagger.trigger.TriggerComponent;
import com.pyamsoft.powermanager.dagger.trigger.TriggerJobComponent;
import com.pyamsoft.powermanager.dagger.wrapper.JobSchedulerCompatModule;
import dagger.Component;
import javax.inject.Singleton;

@Singleton @Component(modules = {
    PowerManagerModule.class, StateModifierModule.class, StateObserverModule.class,
    ManageModifierModule.class, ManageObserverModule.class, PeriodicObserverModule.class,
    PeriodicModifierModule.class, ForegroundModule.class, JobSchedulerCompatModule.class
}) public interface PowerManagerComponent {

  // Subcomponent Trigger
  TriggerComponent plusTrigger();

  FullNotificationComponent plusFullNotificationComponent();

  FullDialogComponent plusFullDialogComponent();

  ForegroundServiceComponent plusForegroundServiceComponent();

  TriggerJobComponent plusTriggerJobComponent();

  ManagerComponent plusManagerComponent();

  JobComponent plusJobComponent();

  ManagePreferenceComponent plusManagePreferenceComponent();

  PeriodPreferenceComponent plusPeriodPreferenceComponent();

  CustomPreferenceComponent plusCustomPreferenceComponent();

  WifiScreenComponent plusWifiScreenComponent();

  DataScreenComponent plusDataScreenComponent();

  BluetoothScreenComponent plusBluetoothScreenComponent();

  SyncScreenComponent plusSyncScreenComponent();

  SettingsPreferenceComponent plusSettingsPreferenceComponent();

  JobServiceComponent plusJobServiceComponent();
}
