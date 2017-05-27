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

package com.pyamsoft.powermanager.base.states

import android.content.ContentResolver
import android.content.Context
import android.provider.Settings
import com.pyamsoft.powermanager.base.logger.Logger
import com.pyamsoft.powermanager.base.preference.RootPreferences
import com.pyamsoft.powermanager.base.shell.ShellCommandHelper
import com.pyamsoft.powermanager.model.States
import javax.inject.Inject

internal class AirplaneModeWrapperImpl @Inject constructor(context: Context,
    private val logger: Logger, private val preferences: RootPreferences,
    private val shellCommandHelper: ShellCommandHelper) : DeviceFunctionWrapper {
  private val contentResolver: ContentResolver = context.applicationContext.contentResolver

  private fun setAirplaneModeEnabled(enabled: Boolean) {
    if (preferences.rootEnabled) {
      logger.i("Airplane Mode: %s", if (enabled) "enable" else "disable")
      val airplaneSettingsCommand = AIRPLANE_SETTINGS_COMMAND + if (enabled) "1" else "0"
      val airplaneBroadcastCommand = AIRPLANE_BROADCAST_COMMAND + if (enabled) "true" else "false"
      shellCommandHelper.runSUCommand(airplaneSettingsCommand, airplaneBroadcastCommand)
    }
  }

  override fun enable() {
    setAirplaneModeEnabled(true)
  }

  override fun disable() {
    setAirplaneModeEnabled(false)
  }

  override val state: States
    get() = if (Settings.Global.getInt(contentResolver, Settings.Global.AIRPLANE_MODE_ON,
        0) == 1) States.ENABLED
    else States.DISABLED

  companion object {
    private const val AIRPLANE_SETTINGS_COMMAND = "settings put global airplane_mode_on "
    private const val AIRPLANE_BROADCAST_COMMAND = "am broadcast -a android.intent.action.AIRPLANE_MODE --ez state "
  }
}
