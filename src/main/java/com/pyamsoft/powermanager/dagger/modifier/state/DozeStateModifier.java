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

package com.pyamsoft.powermanager.dagger.modifier.state;

import android.content.Context;
import android.support.annotation.NonNull;
import com.pyamsoft.powermanager.PowerManagerPreferences;
import com.pyamsoft.powermanager.app.observer.BooleanInterestObserver;
import com.pyamsoft.powermanager.dagger.receiver.SensorFixReceiver;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import javax.inject.Inject;
import timber.log.Timber;

class DozeStateModifier extends StateModifier {

  @NonNull private static final String DUMPSYS_DOZE_START = "deviceidle force-idle deep";
  @NonNull private static final String DUMPSYS_DOZE_END = "deviceidle step";
  @NonNull private static final String DUMPSYS_SENSOR_ENABLE = "sensorservice enable";
  @NonNull private static final String DUMPSYS_SENSOR_RESTRICT =
      "sensorservice restrict com.google.android.gms*";
  @NonNull private final SensorFixReceiver sensorFixReceiver;
  @NonNull private final BooleanInterestObserver dozePermissionObserver;

  @Inject DozeStateModifier(@NonNull Context context, @NonNull PowerManagerPreferences preferences,
      @NonNull SensorFixReceiver sensorFixReceiver,
      @NonNull BooleanInterestObserver dozePermissionObserver) {
    super(context, preferences);
    this.sensorFixReceiver = sensorFixReceiver;
    this.dozePermissionObserver = dozePermissionObserver;
  }

  private static void executeDumpsys(@NonNull String cmd) {
    final Process process;
    boolean caughtPermissionDenial = false;
    try {
      final String command = "dumpsys " + cmd;
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
        throw new IllegalStateException("Error running command: " + command);
      }

      // Will always be 0
    } catch (IOException e) {
      Timber.e(e, "Error running shell command");
    }
  }

  @Override void set(@NonNull Context context, @NonNull PowerManagerPreferences preferences) {
    sensorFixReceiver.unregister();
    if (dozePermissionObserver.is()) {
      Timber.d("Begin Doze");
      executeDumpsys(DUMPSYS_DOZE_START);
      if (preferences.isSensorsManaged()) {
        Timber.d("Restrict device sensors to Google Play Services only");
        executeDumpsys(DUMPSYS_SENSOR_RESTRICT);
      }
    }
  }

  @Override void unset(@NonNull Context context, @NonNull PowerManagerPreferences preferences) {
    sensorFixReceiver.unregister();
    if (dozePermissionObserver.is()) {
      Timber.d("End Doze");
      executeDumpsys(DUMPSYS_DOZE_END);

      // We always do this to put the device in a 'normal' operation mode
      Timber.d("Enable device sensors");
      executeDumpsys(DUMPSYS_SENSOR_ENABLE);

      // If the sensors were managed then we need to do this work around for brightness and rotation
      if (preferences.isSensorsManaged()) {
        sensorFixReceiver.register();
      }
    }
  }
}
