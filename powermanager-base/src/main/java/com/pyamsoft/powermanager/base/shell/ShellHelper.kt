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

package com.pyamsoft.powermanager.base.shell

import android.support.annotation.CheckResult
import android.support.annotation.WorkerThread
import com.pyamsoft.powermanager.base.BuildConfig
import eu.chainfire.libsuperuser.Shell
import timber.log.Timber
import java.util.Arrays
import javax.inject.Inject

class ShellHelper @Inject internal constructor() {

  init {
    Timber.d("New ShellHelper instance")
  }

  @CheckResult private fun prepareShellSession(useRoot: Boolean,
      vararg commands: String): Shell.Builder {
    val builder = Shell.Builder().setWantSTDERR(!BuildConfig.DEBUG).setOnSTDERRLineListener {
      Timber.e("SHELL: %s", it)
    }.setOnSTDOUTLineListener { Timber.d("SHELL: %s", it) }.setWatchdogTimeout(5).setMinimalLogging(
        !BuildConfig.DEBUG).addCommand(commands)
    if (useRoot) {
      builder.useSU()
    } else {
      builder.useSH()
    }

    Timber.d("Open new %s session", if (useRoot) "SU" else "Shell")
    return builder
  }

  @WorkerThread fun runSUCommand(vararg commands: String) {
    Timber.d("Run command '%s' in SU session", Arrays.toString(commands))
    prepareShellSession(useRoot = true, commands = *commands).open().close()
  }

  @WorkerThread fun runSHCommand(vararg commands: String) {
    Timber.d("Run command '%s' in Shell session", Arrays.toString(commands))
    prepareShellSession(useRoot = false, commands = *commands).open().close()
  }
}
