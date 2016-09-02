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

package com.pyamsoft.powermanager;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import com.pyamsoft.powermanager.app.service.ForegroundService;
import com.pyamsoft.pydroid.base.app.ApplicationBase;
import com.squareup.leakcanary.LeakCanary;
import timber.log.Timber;

public class PowerManager extends ApplicationBase {

  // KLUDGE Move to better location
  @CheckResult public static boolean hasDozePermission(@NonNull Context context) {
    final boolean hasPermission = Build.VERSION.SDK_INT == Build.VERSION_CODES.M
        && context.getApplicationContext().checkCallingOrSelfPermission(Manifest.permission.DUMP)
        == PackageManager.PERMISSION_GRANTED;
    Timber.d("Has doze permission? %s", hasPermission);
    return hasPermission;
  }

  @Override public void onCreate() {
    super.onCreate();

    // Start stuff
    startForegroundService();
  }

  @Override protected void installInDebugMode() {
    super.installInDebugMode();
    LeakCanary.install(this);
  }

  private void startForegroundService() {
    startService(new Intent(this, ForegroundService.class));
  }

  @Override protected boolean buildConfigDebug() {
    return BuildConfig.DEBUG;
  }
}
