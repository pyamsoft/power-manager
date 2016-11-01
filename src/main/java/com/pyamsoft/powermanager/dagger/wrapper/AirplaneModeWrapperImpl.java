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

import android.content.Context;
import android.support.annotation.NonNull;
import com.pyamsoft.powermanager.PowerManagerPreferences;
import com.pyamsoft.powermanager.dagger.ShellCommandHelper;
import javax.inject.Inject;
import timber.log.Timber;

class AirplaneModeWrapperImpl extends AirplaneAwareDeviceWrapper {

  @NonNull private static final String AIRPLANE_SETTINGS_COMMAND =
      "settings put global airplane_mode_on ";
  @NonNull private static final String AIRPLANE_BROADCAST_COMMAND =
      "am broadcast -a android.intent.action.AIRPLANE_MODE --ez state ";

  @NonNull private final PowerManagerPreferences preferences;

  @Inject AirplaneModeWrapperImpl(@NonNull Context context,
      @NonNull PowerManagerPreferences preferences) {
    super(context);
    this.preferences = preferences;
  }

  private void setAirplaneModeEnabled(boolean enabled) {
    if (preferences.isRootEnabled()) {
      Timber.i("Airplane Mode: %s", enabled ? "enable" : "disable");
      final String airplaneSettingsCommand = AIRPLANE_SETTINGS_COMMAND + (enabled ? "1" : "0");
      final String airplaneBroadcastCommand =
          AIRPLANE_BROADCAST_COMMAND + (enabled ? "true" : "false");
      final boolean result1 = ShellCommandHelper.runRootShellCommand(airplaneSettingsCommand);
      final boolean result2 = ShellCommandHelper.runRootShellCommand(airplaneBroadcastCommand);
      Timber.d("Results: %s %s", result1, result2);
    }
  }

  @Override public void enable() {
    setAirplaneModeEnabled(true);
  }

  @Override public void disable() {
    setAirplaneModeEnabled(false);
  }

  @Override public boolean isEnabled() {
    return isAirplaneMode();
  }
}
