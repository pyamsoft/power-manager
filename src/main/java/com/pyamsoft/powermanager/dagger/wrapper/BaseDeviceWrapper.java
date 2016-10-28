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

package com.pyamsoft.powermanager.dagger.wrapper;

import android.support.annotation.NonNull;
import com.pyamsoft.powermanager.app.wrapper.DeviceFunctionWrapper;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import timber.log.Timber;

abstract class BaseDeviceWrapper implements DeviceFunctionWrapper {

  /**
   * Requires ROOT for su binary
   */
  void runRootShellCommand(@NonNull String command) {
    final String rootCommand = "su -c " + command;
    runShellCommand(rootCommand);
  }

  private void runShellCommand(@NonNull String command) {
    final Process process;
    boolean caughtPermissionDenial = false;
    try {
      process = Runtime.getRuntime().exec(command);
      try (final BufferedReader bufferedReader = new BufferedReader(
          new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8))) {
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
        throw new RuntimeException("Permission denied running: " + command);
      }

      try {
        process.waitFor();
        Timber.i("Command %s exited with value: %d", command, process.exitValue());
      } catch (InterruptedException e) {
        Timber.e(e, "Interrupted while waiting for exit");
      }
      // Will always be 0
    } catch (IOException e) {
      Timber.e(e, "Error running shell command");
    }
  }
}
