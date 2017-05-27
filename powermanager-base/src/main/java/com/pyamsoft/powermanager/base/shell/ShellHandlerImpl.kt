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
import com.pyamsoft.pydroid.helper.DisposableHelper
import eu.chainfire.libsuperuser.Shell
import io.reactivex.Scheduler
import io.reactivex.Single
import timber.log.Timber
import java.util.Arrays
import java.util.concurrent.TimeUnit.MINUTES
import javax.inject.Inject

internal class ShellHandlerImpl @Inject constructor(private val obsScheduler: Scheduler,
    private val subScheduler: Scheduler) : ShellCommandHelper, RootChecker {
  private var shellSession: Shell.Interactive
  private var rootSession: Shell.Interactive
  private var rootDisposable = DisposableHelper.dispose(null)
  private var shellDisposable = DisposableHelper.dispose(null)

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
    setTimer(useRoot)
    return builder.open()
  }

  private fun setTimer(useRoot: Boolean) {
    // Set a timer to auto close the shell in 1 minute if not used
    if (useRoot) {
      rootDisposable = DisposableHelper.dispose(rootDisposable)
      rootDisposable = Single.timer(1L, MINUTES).subscribeOn(subScheduler).observeOn(
          obsScheduler).subscribe({
        Timber.d("Close useRoot session after 1 minute no activity")
        rootSession.close()
        rootDisposable = DisposableHelper.dispose(rootDisposable)
      }, { Timber.e(it, "Error waiting for Root timeout") })
    } else {
      shellDisposable = DisposableHelper.dispose(shellDisposable)
      shellDisposable = Single.timer(1L, MINUTES).subscribeOn(subScheduler).observeOn(
          obsScheduler).subscribe({
        Timber.d("Close shell session after 1 minute no activity")
        shellSession.close()
        shellDisposable = DisposableHelper.dispose(shellDisposable)
      }, { Timber.e(it, "Error waiting for Shell timeout") })
    }
  }

  @WorkerThread private fun recreateShell(useRoot: Boolean) {
    setTimer(useRoot)
    if (useRoot) {
      if (!rootSession.isRunning) {
        // Even though shell is dead, clean up
        rootSession.close()
        rootSession = openShellSession(true)
      }
    } else {
      if (!shellSession.isRunning) {
        // Even though shell is dead, clean up
        shellSession.close()
        shellSession = openShellSession(false)
      }
    }
  }

  private fun afterCommandResult(exitCode: Int, rootShell: Boolean, vararg commands: String) {
    val recreate: Boolean
    if (exitCode == Shell.OnCommandResultListener.SHELL_DIED || exitCode == Shell.OnCommandResultListener.WATCHDOG_EXIT) {
      Timber.w("Command failed, but will recover. '%s'", Arrays.toString(commands))
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

  @WorkerThread fun parseCommandResult(exitCode: Int, output: List<String>?, rootShell: Boolean,
      vararg commands: String) {
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
    recreateShell(useRoot = true)

    Timber.d("Run command '%s' in SU session", Arrays.toString(commands))
    rootSession.addCommand(commands, SHELL_TYPE_ROOT) { commandCode, exitCode, output ->
      parseCommandResult(exitCode, output, commandCode == SHELL_TYPE_ROOT, *commands)
    }
  }

  @WorkerThread override fun runSHCommand(vararg commands: String) {
    recreateShell(useRoot = false)

    Timber.d("Run command '%s' in Shell session", Arrays.toString(commands))
    shellSession.addCommand(commands, SHELL_TYPE_NORMAL) { commandCode, exitCode, output ->
      parseCommandResult(exitCode, output, commandCode == SHELL_TYPE_ROOT, *commands)
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
