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

package com.pyamsoft.powermanager.dagger.observer.permission;

import android.Manifest;
import android.content.Context;
import android.os.Build;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import javax.inject.Inject;
import timber.log.Timber;

class DozePermissionObserver extends PermissionObserver {

  @Inject DozePermissionObserver(@NonNull Context context) {
    super(context, Manifest.permission.DUMP);
  }

  @Override protected boolean checkPermission(@NonNull Context appContext) {
    final boolean hasPermission;
    switch (Build.VERSION.SDK_INT) {
      case Build.VERSION_CODES.M:
        hasPermission = hasRuntimePermission();
        break;
      case Build.VERSION_CODES.N:
        hasPermission = checkRootAvailable();
        break;
      default:
        hasPermission = false;
    }

    Timber.d("Has doze permission? %s", hasPermission);
    return hasPermission;
  }

  /**
   * Requires ROOT to work properly
   *
   * Will exit with a failed 137 code or otherwise if ROOT is not allowed
   */
  @CheckResult private boolean checkRootAvailable() {
    final Process process;
    try {
      final String command = "true";
      process = Runtime.getRuntime().exec(new String[] { "su", "-c", command });
      try (final BufferedReader bufferedReader = new BufferedReader(
          new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8))) {
        Timber.d("Read results of exec: '%s'", command);
        String line = bufferedReader.readLine();
        while (line != null && !line.isEmpty()) {
          Timber.d("%s", line);
          line = bufferedReader.readLine();
        }
      }

      try {
        process.waitFor();
        final int exitValue = process.exitValue();
        Timber.i("Command %s exited with value: %d", command, exitValue);
        return exitValue == 0;
      } catch (InterruptedException e) {
        Timber.e(e, "Interrupted while waiting for exit");
        return false;
      }
      // Will always be 0
    } catch (IOException e) {
      Timber.e(e, "Error running shell command");
      return false;
    }
  }
}
