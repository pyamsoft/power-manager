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
import android.support.annotation.WorkerThread;
import eu.chainfire.libsuperuser.Shell;
import timber.log.Timber;

public class ShellCommandHelper {

  @NonNull private static final ShellCommandHelper INSTANCE = new ShellCommandHelper();
  @NonNull private final Shell.Interactive shellSession;
  @NonNull private final Shell.Interactive rootSession;

  private ShellCommandHelper() {
    shellSession = openShellSession(false);
    rootSession = openShellSession(true);
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

  @WorkerThread private void runSUCommand(@NonNull String command) {
    Timber.d("Run command '%s' in SU session", command);
    rootSession.addCommand(command);
  }

  @WorkerThread private void runSHCommand(@NonNull String command) {
    Timber.d("Run command '%s' in Shell session", command);
    shellSession.addCommand(command);
  }
}
