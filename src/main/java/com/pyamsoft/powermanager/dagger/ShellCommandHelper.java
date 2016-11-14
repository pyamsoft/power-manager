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

import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.support.annotation.WorkerThread;
import eu.chainfire.libsuperuser.Shell;
import java.util.List;
import timber.log.Timber;

public class ShellCommandHelper {

  private static final int SHELL_TYPE_ROOT = 0;
  private static final int SHELL_TYPE_NORMAL = 1;
  @NonNull private static final ShellCommandHelper INSTANCE = createInstance();
  @NonNull private Shell.Interactive shellSession;
  @NonNull private Shell.Interactive rootSession;

  private ShellCommandHelper() {
    shellSession = openShellSession(false);
    rootSession = openShellSession(true);
  }

  @SuppressWarnings("WeakerAccess") @VisibleForTesting @CheckResult @NonNull
  static ShellCommandHelper createInstance() {
    return new ShellCommandHelper();
  }

  @WorkerThread @CheckResult public static boolean isSUAvailable() {
    final boolean available = Shell.SU.available();
    Timber.d("Is SU available: %s", available);
    return available;
  }

  /**
   * Requires ROOT for su binary
   */
  @WorkerThread public static void runRootShellCommand(@NonNull String command) {
    INSTANCE.runSUCommand(command);
  }

  @WorkerThread public static void runShellCommand(@NonNull String command) {
    INSTANCE.runSHCommand(command);
  }

  @CheckResult @NonNull private Shell.Interactive openShellSession(boolean useRoot) {
    final Shell.Builder builder =
        new Shell.Builder().setWantSTDERR(false).setWatchdogTimeout(5).setMinimalLogging(true);
    if (useRoot) {
      builder.useSU();
    } else {
      builder.useSH();
    }

    Timber.d("Open new %s session", (useRoot ? "SU" : "Shell"));
    return builder.open();
  }

  @WorkerThread private void recreateShell(boolean useRoot) {
    if (useRoot) {
      rootSession.close();
      rootSession = openShellSession(true);
    } else {
      shellSession.close();
      shellSession = openShellSession(false);
    }
  }

  @SuppressWarnings("WeakerAccess") @WorkerThread void parseCommandResult(@NonNull String command,
      int exitCode, @Nullable List<String> output, boolean rootShell) {
    final boolean recreate;
    if (exitCode == Shell.OnCommandResultListener.SHELL_DIED) {
      Timber.e("Command failed. '%s'", command);
      if (isSUAvailable()) {
        Timber.d("SU is available, re-create the shell");
        recreate = true;
      } else {
        Timber.d("SU is not available, stay dead");
        recreate = false;
      }
    } else {
      recreate = false;
    }

    if (output != null) {
      if (!output.isEmpty()) {
        Timber.d("Command output");
        for (final String line : output) {
          if (line != null) {
            Timber.d("%s", line);
          }
        }
      }
    }

    if (recreate) {
      Timber.w("Recreating %s shell", (rootShell) ? "SU" : "SH");
      recreateShell(rootShell);

      // Attempt to run the command again
      Timber.w("Re-run command");
      if (rootShell) {
        runSUCommand(command);
      } else {
        runSHCommand(command);
      }
    }
  }

  @WorkerThread private void runSUCommand(@NonNull String command) {
    Timber.d("Run command '%s' in SU session", command);
    rootSession.addCommand(command, SHELL_TYPE_ROOT, (commandCode, exitCode, output) -> {
      parseCommandResult(command, exitCode, output, commandCode == SHELL_TYPE_ROOT);
    });
  }

  @WorkerThread private void runSHCommand(@NonNull String command) {
    Timber.d("Run command '%s' in Shell session", command);
    shellSession.addCommand(command, SHELL_TYPE_NORMAL, (commandCode, exitCode, output) -> {
      parseCommandResult(command, exitCode, output, commandCode == SHELL_TYPE_ROOT);
    });
  }
}
