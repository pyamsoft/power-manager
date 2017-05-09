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

import com.pyamsoft.powermanager.base.PowerManagerModule;
import com.pyamsoft.powermanager.base.logger.LoggerModule;
import com.pyamsoft.powermanager.base.permission.PermissionObserverModule;
import com.pyamsoft.powermanager.base.shell.ShellCommandModule;
import com.pyamsoft.powermanager.base.states.StateModifierModule;
import com.pyamsoft.powermanager.base.states.StateObserverModule;
import com.pyamsoft.powermanager.base.states.WrapperModule;
import com.pyamsoft.powermanager.job.JobModule;
import com.pyamsoft.powermanager.logger.LoggerPreferenceFragment;
import com.pyamsoft.powermanager.main.MainActivity;
import com.pyamsoft.powermanager.receiver.ScreenOnOffReceiver;
import com.pyamsoft.powermanager.service.ActionToggleService;
import com.pyamsoft.powermanager.service.ForegroundService;
import com.pyamsoft.powermanager.settings.SettingsPreferenceFragment;
import com.pyamsoft.powermanager.trigger.TriggerComponent;
import com.pyamsoft.powermanager.trigger.db.PowerTriggerDBModule;
import dagger.Component;
import javax.inject.Singleton;

@Singleton @Component(modules = {
    PowerManagerModule.class, WrapperModule.class, PowerTriggerDBModule.class, LoggerModule.class,
    ShellCommandModule.class, PermissionObserverModule.class, StateObserverModule.class,
    StateModifierModule.class, JobModule.class,
}) public interface PowerManagerComponent {

  TriggerComponent plusTriggerComponent();

  void inject(PowerManagerSingleInitProvider provider);

  void inject(ScreenOnOffReceiver receiver);

  void inject(LoggerPreferenceFragment loggerPreferenceFragment);

  void inject(MainActivity mainActivity);

  void inject(ActionToggleService actionToggleService);

  void inject(ForegroundService foregroundService);

  void inject(SettingsPreferenceFragment settingsPreferenceFragment);
}
