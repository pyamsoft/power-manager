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
import eu.chainfire.libsuperuser.Shell
import timber.log.Timber
import java.util.Arrays
import javax.inject.Inject

internal class ShellHandlerImpl @Inject constructor() : ShellCommandHelper, RootChecker {
  private var shellSession: Shell.Interactive
  private var rootSession: Shell.Interactive

  init {
    shellSession = openShellSession(false)
    rootSession = openShellSession(true)
  }

  @CheckResult private fun openShellSession(useRoot: Boolean): Shell.Interactive {
    val builder = Shell.Builder().setWantSTDERR(false).setWatchdogTimeout(5).setMinimalLogging(true)
    if (useRoot) {
      builder.useSU()
    } else {
      builder.useSH()
    }

    Timber.d("Open new %s session", if (useRoot) "SU" else "Shell")
    return builder.open()
  }

  @WorkerThread private fun recreateShell(useRoot: Boolean) {
    if (useRoot) {
      rootSession.close()
      rootSession = openShellSession(true)
    } else {
      shellSession.close()
      shellSession = openShellSession(false)
    }
  }

  private fun afterCommandResult(exitCode: Int, rootShell: Boolean, vararg commands: String) {
    val recreate: Boolean
    if (exitCode == Shell.OnCommandResultListener.SHELL_DIED) {
      Timber.e("Command failed. '%s'", Arrays.toString(commands))
      recreate = decideRecreation(rootShell)
    } else {
      recreate = false
    }

    if (recreate) {
      Timber.w("Recreating %s shell", if (rootShell) "SU" else "SH")
      recreateShell(rootShell)

      // Attempt to run the command again
      Timber.w("Re-run command")
      if (rootShell) {
        runSUCommand(*commands)
      } else {
        runSHCommand(*commands)
      }
    }
  }

  @WorkerThread fun parseCommandResult(exitCode: Int,
      output: List<String>?, rootShell: Boolean, vararg commands: String) {
    if (output != null) {
      if (!output.isEmpty()) {
        Timber.d("%s Command output", if (rootShell) "SU" else "SH")

        for (line in output) {
          Timber.d("%s", line)
        }
      }
    }

    afterCommandResult(exitCode, rootShell, *commands)
  }

  @CheckResult private fun decideRecreation(rootShell: Boolean): Boolean {
    val recreate: Boolean
    if (rootShell) {
      Timber.i("Decide recreation of root shell")
      if (isSUAvailable) {
        Timber.i("SU is available, re-create the shell")
        recreate = true
      } else {
        Timber.w("SU is not available, stay dead")
        recreate = false
      }
    } else {
      Timber.i("Always recreate shell")
      recreate = true
    }
    return recreate
  }

  @WorkerThread override fun runSUCommand(vararg commands: String) {
    Timber.d("Run command '%s' in SU session", Arrays.toString(commands))
    rootSession.addCommand(commands, SHELL_TYPE_ROOT) { commandCode, exitCode, output ->
      parseCommandResult(exitCode, output,
          commandCode == SHELL_TYPE_ROOT, *commands)
    }
  }

  @WorkerThread override fun runSHCommand(vararg commands: String) {
    Timber.d("Run command '%s' in Shell session", Arrays.toString(commands))
    shellSession.addCommand(commands, SHELL_TYPE_NORMAL) { commandCode, exitCode, output ->
      parseCommandResult(exitCode, output,
          commandCode == SHELL_TYPE_ROOT, *commands)
    }
  }

  override val isSUAvailable: Boolean
    @WorkerThread @CheckResult get() {
      val available = Shell.SU.available()
      Timber.d("Is SU available: %s", available)
      return available
    }

  companion object {

    private const val SHELL_TYPE_ROOT = 0
    private const val SHELL_TYPE_NORMAL = 1
  }
}
