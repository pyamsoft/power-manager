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

package com.pyamsoft.powermanager.base.states;

import android.content.ContentResolver;
import android.content.Context;
import android.provider.Settings;
import android.support.annotation.NonNull;
import com.pyamsoft.powermanager.base.PowerManagerPreferences;
import com.pyamsoft.powermanager.base.shell.ShellCommandHelper;
import com.pyamsoft.powermanager.model.Logger;
import com.pyamsoft.powermanager.model.states.States;
import javax.inject.Inject;

class AirplaneModeWrapperImpl implements DeviceFunctionWrapper {

  @NonNull private static final String AIRPLANE_SETTINGS_COMMAND =
      "settings put global airplane_mode_on ";
  @NonNull private static final String AIRPLANE_BROADCAST_COMMAND =
      "am broadcast -a android.intent.action.AIRPLANE_MODE --ez state ";
  @NonNull private final Logger logger;
  @NonNull private final ContentResolver contentResolver;
  @NonNull private final PowerManagerPreferences preferences;
  @NonNull private final ShellCommandHelper shellCommandHelper;

  @Inject AirplaneModeWrapperImpl(@NonNull Context context, @NonNull Logger logger,
      @NonNull PowerManagerPreferences preferences,
      @NonNull ShellCommandHelper shellCommandHelper) {
    this.contentResolver = context.getApplicationContext().getContentResolver();
    this.logger = logger;
    this.preferences = preferences;
    this.shellCommandHelper = shellCommandHelper;
  }

  private void setAirplaneModeEnabled(boolean enabled) {
    if (preferences.isRootEnabled()) {
      logger.i("Airplane Mode: %s", enabled ? "enable" : "disable");
      final String airplaneSettingsCommand = AIRPLANE_SETTINGS_COMMAND + (enabled ? "1" : "0");
      final String airplaneBroadcastCommand =
          AIRPLANE_BROADCAST_COMMAND + (enabled ? "true" : "false");
      shellCommandHelper.runSUCommand(airplaneSettingsCommand, airplaneBroadcastCommand);
    }
  }

  @Override public void enable() {
    setAirplaneModeEnabled(true);
  }

  @Override public void disable() {
    setAirplaneModeEnabled(false);
  }

  @NonNull @Override public States getState() {
    return (Settings.Global.getInt(contentResolver, Settings.Global.AIRPLANE_MODE_ON, 0) == 1)
        ? States.ENABLED : States.DISABLED;
  }
}
