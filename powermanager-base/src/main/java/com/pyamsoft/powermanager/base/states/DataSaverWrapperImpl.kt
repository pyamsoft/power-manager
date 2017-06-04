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
import android.net.ConnectivityManager
import android.os.Build
import com.pyamsoft.powermanager.base.logger.Logger
import com.pyamsoft.powermanager.base.preference.RootPreferences
import com.pyamsoft.powermanager.base.shell.ShellHelper
import com.pyamsoft.powermanager.model.States
import com.pyamsoft.powermanager.model.States.DISABLED
import javax.inject.Inject

internal class DataSaverWrapperImpl @Inject internal constructor(context: Context,
    private val logger: Logger, private val preferences: RootPreferences,
    private val shellHelper: ShellHelper) : DeviceFunctionWrapper {

  private val appContext = context.applicationContext
  private val connectionManager = appContext.getSystemService(
      Context.CONNECTIVITY_SERVICE) as ConnectivityManager

  private fun setDataSaver(enabled: Boolean) {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
      logger.w("Cannot setDataSaver on API below Nougat")
      return
    }

    if (preferences.rootEnabled) {
      val command = "cmd netpolicy set restrict-background ${if (enabled) "true" else "false"}"
      logger.d("Data saver mode: ${if (enabled) "enable" else "disable"}")
      shellHelper.runSUCommand(command)
    } else {
      logger.w("Cannot setDataSaver without root permission")
    }
  }

  override fun enable() {
    setDataSaver(true)
  }

  override fun disable() {
    setDataSaver(false)
  }

  override val state: States
    get() {
      if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
        return States.UNKNOWN
      } else {
        return if (connectionManager.restrictBackgroundStatus != ConnectivityManager.RESTRICT_BACKGROUND_STATUS_DISABLED) States.ENABLED
        else DISABLED
      }
    }
}

