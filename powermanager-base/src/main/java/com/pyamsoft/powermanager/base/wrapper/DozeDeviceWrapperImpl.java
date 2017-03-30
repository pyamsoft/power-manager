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

package com.pyamsoft.powermanager.base.wrapper;

import android.content.Context;
import android.os.Build;
import android.os.PowerManager;
import android.support.annotation.NonNull;
import com.pyamsoft.powermanager.base.PowerManagerPreferences;
import com.pyamsoft.powermanager.base.shell.ShellCommandHelper;
import com.pyamsoft.powermanager.model.Logger;
import com.pyamsoft.powermanager.model.overlord.States;
import javax.inject.Inject;

class DozeDeviceWrapperImpl implements DeviceFunctionWrapper {

  @NonNull private final Logger logger;
  @NonNull private final android.os.PowerManager androidPowerManager;
  @NonNull private final PowerManagerPreferences preferences;
  @NonNull private final ShellCommandHelper shellCommandHelper;

  @Inject DozeDeviceWrapperImpl(@NonNull Context context, @NonNull Logger logger,
      @NonNull PowerManagerPreferences preferences,
      @NonNull ShellCommandHelper shellCommandHelper) {
    this.logger = logger;
    this.preferences = preferences;
    androidPowerManager =
        (PowerManager) context.getApplicationContext().getSystemService(Context.POWER_SERVICE);
    this.shellCommandHelper = shellCommandHelper;
  }

  private void setDozeEnabled(boolean enabled) {
    final String command;
    logger.i("Doze mode: %s", enabled ? "enable" : "disable");
    if (Build.VERSION.SDK_INT == Build.VERSION_CODES.M) {
      command = "dumpsys deviceidle " + (enabled ? "force-idle" : "step");
      if (preferences.isRootEnabled()) {
        // If root is enabled, we attempt with root
        shellCommandHelper.runSUCommand(command);
      } else {
        // API 23 can do this without root
        shellCommandHelper.runSHCommand(command);
      }
    } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
      if (preferences.isRootEnabled()) {
        // API 24 requires root
        command = "dumpsys deviceidle " + (enabled ? "force-idle deep" : "unforce");
        shellCommandHelper.runSUCommand(command);
      } else {
        logger.w("Root not enabled, cannot toggle Doze");
      }
    } else {
      logger.w("This API level cannot run Doze");
    }
  }

  @Override public void enable() {
    setDozeEnabled(true);
  }

  @Override public void disable() {
    setDozeEnabled(false);
  }

  @NonNull @Override public States getState() {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
      return States.UNKNOWN;
    } else {
      return androidPowerManager.isDeviceIdleMode() ? States.ENABLED : States.DISABLED;
    }
  }
}
