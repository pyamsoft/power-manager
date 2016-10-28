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
import android.os.Build;
import android.os.PowerManager;
import android.support.annotation.NonNull;
import com.pyamsoft.powermanager.app.wrapper.DeviceFunctionWrapper;
import com.pyamsoft.powermanager.dagger.ShellCommandHelper;
import javax.inject.Inject;

class DozeDeviceWrapperImpl implements DeviceFunctionWrapper {

  @NonNull private final android.os.PowerManager androidPowerManager;

  @Inject DozeDeviceWrapperImpl(@NonNull Context context) {
    androidPowerManager =
        (PowerManager) context.getApplicationContext().getSystemService(Context.POWER_SERVICE);
  }

  private void setDozeEnabled(boolean enabled) {
    if (Build.VERSION.SDK_INT == Build.VERSION_CODES.M) {
      // API 23 can do this without root
      final String command = "dumpsys deviceidle " + (enabled ? "force-idle" : "step");
      ShellCommandHelper.runShellCommand(command);
    } else if (Build.VERSION.SDK_INT == Build.VERSION_CODES.N) {
      // API 24 requires root
      final String command = "dumpsys deviceidle " + (enabled ? "force-idle deep" : "unforce");
      ShellCommandHelper.runRootShellCommand(command);
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
