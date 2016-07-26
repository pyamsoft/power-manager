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

package com.pyamsoft.powermanager.dagger.manager.backend;

import android.annotation.SuppressLint;
import android.os.Build;
import android.support.annotation.NonNull;
import com.birbit.android.jobqueue.Params;
import com.pyamsoft.powermanager.app.receiver.DozeReceiver;
import com.pyamsoft.powermanager.dagger.base.BaseJob;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import timber.log.Timber;

public abstract class DozeJob extends BaseJob {

  @NonNull public static final String GRANT_PERMISSION_COMMAND =
      "adb -d shell pm grant com.pyamsoft.powermanager android.permission.DUMP";
  @NonNull public static final String DOZE_TAG = "doze_tag";
  @NonNull private static final String DUMPSYS_COMMAND = "dumpsys";
  @NonNull private static final String DUMPSYS_DOZE_START_COMMAND;
  @NonNull private static final String DUMPSYS_DOZE_END_COMMAND;
  private static final int PRIORITY = 5;

  static {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
      DUMPSYS_DOZE_START_COMMAND = DUMPSYS_COMMAND + " deviceidle force-idle deep";
      DUMPSYS_DOZE_END_COMMAND = DUMPSYS_COMMAND + " deviceidle unforce";
    } else {
      DUMPSYS_DOZE_START_COMMAND = DUMPSYS_COMMAND + " deviceidle force-idle";
      DUMPSYS_DOZE_END_COMMAND = DUMPSYS_COMMAND + " deviceidle step";
    }
  }

  private final boolean doze;

  DozeJob(long delay, boolean enable) {
    super(new Params(PRIORITY).setRequiresNetwork(false).addTags(DOZE_TAG).setDelayMs(delay));
    this.doze = enable;
  }

  @SuppressLint("NewApi") private static void executeShellCommand(@NonNull String command) {
    final Process process;
    boolean caughtPermissionDenial = false;
    try {
      process = Runtime.getRuntime().exec(command);
      try (final BufferedReader bufferedReader = new BufferedReader(
          new InputStreamReader(process.getInputStream()))) {
        Timber.d("Read results of exec: '%s'", command);
        String line = bufferedReader.readLine();
        while (line != null && !line.isEmpty()) {
          if (line.startsWith("Permission Denial")) {
            Timber.e("Command resulted in permission denial");
            caughtPermissionDenial = true;
            break;
          }
          Timber.d("%s", line);
          line = bufferedReader.readLine();
        }
      }

      if (caughtPermissionDenial) {
        throw new IllegalStateException("Error running command: " + command);
      }

      // Will always be 0
    } catch (IOException e) {
      Timber.e(e, "Error running shell command");
    }
  }

  @Override public void onRun() throws Throwable {
    Timber.d("Run DozeJob");
    final boolean isDoze = DozeReceiver.isDozeMode(getApplicationContext());
    if (doze) {
      if (!isDoze) {
        Timber.d("Do doze startDoze");
        executeShellCommand(DUMPSYS_DOZE_START_COMMAND);
      } else {
        Timber.e("Doze already running");
      }
    } else {
      if (isDoze) {
        Timber.d("Do doze stopDoze");
        executeShellCommand(DUMPSYS_DOZE_END_COMMAND);
      } else {
        Timber.e("Doze already not running");
      }
    }
  }

  public static final class EnableJob extends DozeJob {

    public EnableJob() {
      super(100, false);
    }
  }

  public static final class DisableJob extends DozeJob {

    public DisableJob(long delay) {
      super(delay, true);
    }
  }
}
