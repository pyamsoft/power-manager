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

import android.content.Context
import android.os.Build
import android.os.PowerManager
import com.pyamsoft.powermanager.base.logger.Logger
import com.pyamsoft.powermanager.base.preference.RootPreferences
import com.pyamsoft.powermanager.base.shell.ShellHelper
import com.pyamsoft.powermanager.model.States
import javax.inject.Inject

internal class DozeModeWrapperImpl @Inject internal constructor(context: Context,
    private val logger: Logger, private val preferences: RootPreferences,
    private val shellHelper: ShellHelper) : DeviceFunctionWrapper {
  private val androidPowerManager: android.os.PowerManager = context.applicationContext.getSystemService(
      Context.POWER_SERVICE) as PowerManager

  private fun setDozeEnabled(enabled: Boolean) {
    val command: String
    logger.i("Doze mode: ${if (enabled) "enable" else "disable"}")
    if (Build.VERSION.SDK_INT == Build.VERSION_CODES.M) {
      command = "dumpsys deviceidle ${if (enabled) "force-idle" else "step"}"
      if (preferences.rootEnabled) {
        // If root is enabled, we attempt with root
        shellHelper.runSUCommand(command)
      } else {
        // API 23 can do this without root
        shellHelper.runSHCommand(command)
      }
    } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
      if (preferences.rootEnabled) {
        // API 24 requires root
        command = "dumpsys deviceidle ${if (enabled) "force-idle deep" else "unforce"}"
        shellHelper.runSUCommand(command)
      } else {
        logger.w("Root not enabled, cannot toggle Doze")
      }
    } else {
      logger.w("This API level cannot run Doze")
    }
  }

  override fun enable() {
    setDozeEnabled(true)
  }

  override fun disable() {
    setDozeEnabled(false)
  }

  override val state: States
    get() {
      if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
        return States.UNKNOWN
      } else {
        return if (androidPowerManager.isDeviceIdleMode) States.ENABLED else States.DISABLED
      }
    }
}
