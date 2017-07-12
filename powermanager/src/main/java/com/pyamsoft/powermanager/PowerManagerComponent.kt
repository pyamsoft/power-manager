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

package com.pyamsoft.powermanager

import android.support.annotation.CheckResult
import com.pyamsoft.powermanager.base.PowerManagerModule
import com.pyamsoft.powermanager.base.logger.LoggerModule
import com.pyamsoft.powermanager.base.permission.PermissionObserverModule
import com.pyamsoft.powermanager.base.shell.ShellModule
import com.pyamsoft.powermanager.base.states.StateModifierModule
import com.pyamsoft.powermanager.base.states.StateObserverModule
import com.pyamsoft.powermanager.base.states.WrapperModule
import com.pyamsoft.powermanager.job.JobModule
import com.pyamsoft.powermanager.logger.LoggerPreferenceFragment
import com.pyamsoft.powermanager.main.MainActivity
import com.pyamsoft.powermanager.main.MainFragment
import com.pyamsoft.powermanager.manage.ExpanderView
import com.pyamsoft.powermanager.manage.ManageComponent
import com.pyamsoft.powermanager.manage.ManageFragment
import com.pyamsoft.powermanager.manage.ManageSingletonModule
import com.pyamsoft.powermanager.manage.TimeSingletonModule
import com.pyamsoft.powermanager.receiver.BootCompletedReceiver
import com.pyamsoft.powermanager.receiver.ScreenOnOffReceiver
import com.pyamsoft.powermanager.service.ActionToggleService
import com.pyamsoft.powermanager.service.ForegroundService
import com.pyamsoft.powermanager.settings.ConfirmationDialog
import com.pyamsoft.powermanager.settings.SettingsPreferenceFragment
import com.pyamsoft.powermanager.trigger.DeleteTriggerDialog
import com.pyamsoft.powermanager.trigger.PowerTriggerFragment
import com.pyamsoft.powermanager.trigger.PowerTriggerPreferenceFragment
import com.pyamsoft.powermanager.trigger.db.PowerTriggerDBModule
import com.pyamsoft.powermanager.workaround.WorkaroundFragment
import dagger.Component
import javax.inject.Singleton

@Singleton @Component(
    modules = arrayOf(PowerManagerModule::class, WrapperModule::class, ShellModule::class,
        PowerTriggerDBModule::class, LoggerModule::class, PermissionObserverModule::class,
        StateObserverModule::class, StateModifierModule::class, JobModule::class,
        ManageSingletonModule::class, TimeSingletonModule::class)) interface PowerManagerComponent {

  @CheckResult fun plusManageComponent(): ManageComponent

  fun inject(application: PowerManager)

  fun inject(receiver: ScreenOnOffReceiver)

  fun inject(receiver: BootCompletedReceiver)

  fun inject(loggerPreferenceFragment: LoggerPreferenceFragment)

  fun inject(mainActivity: MainActivity)

  fun inject(actionToggleService: ActionToggleService)

  fun inject(foregroundService: ForegroundService)

  fun inject(manageFragment: ManageFragment)

  fun inject(settingsPreferenceFragment: SettingsPreferenceFragment)

  fun inject(deleteTriggerDialog: DeleteTriggerDialog)

  fun inject(confirmationDialog: ConfirmationDialog)

  fun inject(mainFragment: MainFragment)

  fun inject(workaroundFragment: WorkaroundFragment)

  fun inject(powerTriggerPreferenceFragment: PowerTriggerPreferenceFragment)

  fun inject(powerTriggerFragment: PowerTriggerFragment)

  fun inject(expanderView: ExpanderView)
}
