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

package com.pyamsoft.powermanager.presenter.wrapper;

import android.content.Context;
import android.os.Build;
import android.os.PowerManager;
import android.support.annotation.NonNull;
import com.pyamsoft.powermanager.presenter.PowerManagerPreferences;
import com.pyamsoft.powermanager.presenter.ShellCommandHelper;
import javax.inject.Inject;
import timber.log.Timber;

class DozeDeviceWrapperImpl implements DeviceFunctionWrapper {

  @NonNull private final android.os.PowerManager androidPowerManager;
  @NonNull private final PowerManagerPreferences preferences;

  @Inject DozeDeviceWrapperImpl(@NonNull Context context,
      @NonNull PowerManagerPreferences preferences) {
    this.preferences = preferences;
    androidPowerManager =
        (PowerManager) context.getApplicationContext().getSystemService(Context.POWER_SERVICE);
  }

  private void setDozeEnabled(boolean enabled) {
    final String command;
    Timber.i("Doze mode: %s", enabled ? "enable" : "disable");
    if (Build.VERSION.SDK_INT == Build.VERSION_CODES.M) {
      command = "dumpsys deviceidle " + (enabled ? "force-idle" : "step");
      if (preferences.isRootEnabled()) {
        // If root is enabled, we attempt with root
        ShellCommandHelper.runRootShellCommand(command);
      } else {
        // API 23 can do this without root
        ShellCommandHelper.runShellCommand(command);
      }
    } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
      if (preferences.isRootEnabled()) {
        // API 24 requires root
        command = "dumpsys deviceidle " + (enabled ? "force-idle deep" : "unforce");
        ShellCommandHelper.runRootShellCommand(command);
      } else {
        Timber.w("Root not enabled, cannot toggle Doze");
      }
    } else {
      Timber.w("This API level cannot run Doze");
    }
  }

  @Override public void enable() {
    setDozeEnabled(true);
  }

  @Override public void disable() {
    setDozeEnabled(false);
  }

  @Override public boolean isEnabled() {
    return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && androidPowerManager.isDeviceIdleMode();
  }
}
