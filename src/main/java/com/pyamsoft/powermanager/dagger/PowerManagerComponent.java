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

import com.pyamsoft.powermanager.dagger.main.MainComponent;
import com.pyamsoft.powermanager.dagger.manager.backend.ManagerComponent;
import com.pyamsoft.powermanager.dagger.manager.manage.ManagerManageComponent;
import com.pyamsoft.powermanager.dagger.manager.period.ManagerPeriodicComponent;
import com.pyamsoft.powermanager.dagger.manager.preference.ManagerTimeComponent;
import com.pyamsoft.powermanager.dagger.modifier.manage.ManageModifierComponent;
import com.pyamsoft.powermanager.dagger.modifier.state.StateModifierComponent;
import com.pyamsoft.powermanager.dagger.observer.manage.ManageObserverComponent;
import com.pyamsoft.powermanager.dagger.observer.state.StateObserverComponent;
import com.pyamsoft.powermanager.dagger.service.FullNotificationComponent;
import com.pyamsoft.powermanager.dagger.settings.SettingsComponent;
import com.pyamsoft.powermanager.dagger.trigger.TriggerComponent;
import dagger.Component;
import javax.inject.Singleton;

@Singleton @Component(modules = PowerManagerModule.class) public interface PowerManagerComponent {

  // Subcomponent Trigger
  TriggerComponent plusTrigger();

  // Subcomponent Settings
  SettingsComponent plusSettings();

  // Subcomponent StateObserver
  StateObserverComponent plusStateObserver();

  // Subcomponent ManageObserver
  ManageObserverComponent plusManageObserver();

  // Subcomponent StateModifier
  StateModifierComponent plusStateModifier();

  // Subcomponent ManageModifier
  ManageModifierComponent plusManageModifier();

  // Subcomponent FullNotification
  FullNotificationComponent plusFullNotification();

  // Subcomponent Manager
  ManagerComponent plusManager();

  // Subcomponent ManagerManage
  ManagerManageComponent plusManagerManage();

  // Subcomponent ManagerTime
  ManagerTimeComponent plusManagerTime();

  // Subcomponent ManagerPeriodic
  ManagerPeriodicComponent plusManagerPeriodic();

  // Subcomponent MainComponent
  MainComponent plusMain();
}
