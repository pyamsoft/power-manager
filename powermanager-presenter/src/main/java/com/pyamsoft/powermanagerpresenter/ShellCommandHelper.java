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

package com.pyamsoft.powermanagerpresenter;

import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.support.annotation.WorkerThread;
import com.pyamsoft.pydroidrx.SubscriptionHelper;
import eu.chainfire.libsuperuser.Shell;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import rx.Observable;
import rx.Subscription;
import rx.subscriptions.Subscriptions;
import timber.log.Timber;

public class ShellCommandHelper {

  private static final int SHELL_TYPE_ROOT = 0;
  private static final int SHELL_TYPE_NORMAL = 1;
  @NonNull private static final ShellCommandHelper INSTANCE = createInstance();
  @SuppressWarnings("WeakerAccess") @NonNull volatile Subscription shellTimeoutSubcscription =
      Subscriptions.empty();
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
  @WorkerThread public static void runRootShellCommand(@NonNull String... commands) {
    INSTANCE.runSUCommand(commands);
  }

  @WorkerThread public static void runShellCommand(@NonNull String... commands) {
    INSTANCE.runSHCommand(commands);
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

  private void afterCommandResult(int exitCode, boolean rootShell, @NonNull String... commands) {
    final boolean recreate;
    if (exitCode == Shell.OnCommandResultListener.SHELL_DIED) {
      Timber.e("Command failed. '%s'", Arrays.toString(commands));
      recreate = decideRecreation(rootShell);
    } else {
      recreate = false;
    }

    if (recreate) {
      Timber.w("Recreating %s shell", (rootShell) ? "SU" : "SH");
      recreateShell(rootShell);

      // Attempt to run the command again
      Timber.w("Re-run command");
      if (rootShell) {
        runSUCommand(commands);
      } else {
        runSHCommand(commands);
      }
    }
  }

  @SuppressWarnings("WeakerAccess") @WorkerThread void parseCommandResult(int exitCode,
      @Nullable List<String> output, boolean rootShell, @NonNull String... commands) {
    if (output != null) {
      if (!output.isEmpty()) {
        Timber.d("%s Command output", (rootShell) ? "SU" : "SH");
        //noinspection Convert2streamapi
        for (final String line : output) {
          if (line != null) {
            Timber.d("%s", line);
          }
        }
      }
    }

    afterCommandResult(exitCode, rootShell, commands);
  }

  @CheckResult private boolean decideRecreation(boolean rootShell) {
    final boolean recreate;
    if (rootShell) {
      Timber.i("Decide recreation of root shell");
      if (isSUAvailable()) {
        Timber.i("SU is available, re-create the shell");
        recreate = true;
      } else {
        Timber.w("SU is not available, stay dead");
        recreate = false;
      }
    } else {
      Timber.i("Always recreate shell");
      recreate = true;
    }
    return recreate;
  }

  private void queueShellTimeout(@NonNull Shell.Interactive session) {
    SubscriptionHelper.unsubscribe(shellTimeoutSubcscription);
    shellTimeoutSubcscription = Observable.defer(() -> {
      Timber.d("Wait a bit and then close up");

      // Returning just Obs.just(true) performs auto boxing. Returning the constant is more efficient
      return Observable.just(Boolean.TRUE);
    }).delay(15, TimeUnit.SECONDS).subscribe(aBoolean -> {
          Timber.w("Closing session!");
          session.close();
        }, throwable -> Timber.e(throwable, "onError queueShellTimeout"),
        () -> SubscriptionHelper.unsubscribe(shellTimeoutSubcscription));
  }

  @WorkerThread private void runSUCommand(@NonNull String... commands) {
    queueShellTimeout(rootSession);
    Timber.d("Run command '%s' in SU session", Arrays.toString(commands));
    rootSession.addCommand(commands, SHELL_TYPE_ROOT, (commandCode, exitCode, output) -> {
      parseCommandResult(exitCode, output, commandCode == SHELL_TYPE_ROOT, commands);
    });
  }

  @WorkerThread private void runSHCommand(@NonNull String... commands) {
    queueShellTimeout(shellSession);
    Timber.d("Run command '%s' in Shell session", Arrays.toString(commands));
    shellSession.addCommand(commands, SHELL_TYPE_NORMAL, (commandCode, exitCode, output) -> {
      parseCommandResult(exitCode, output, commandCode == SHELL_TYPE_ROOT, commands);
    });
  }
}