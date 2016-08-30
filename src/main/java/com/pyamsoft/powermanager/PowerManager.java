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
import android.os.StrictMode;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import com.pyamsoft.powermanager.app.service.ForegroundService;
import com.pyamsoft.pydroid.base.app.ApplicationBase;
import com.pyamsoft.pydroid.crash.CrashHandler;
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

    if (buildConfigDebug()) {
      new CrashHandler(getApplicationContext(), this).register();
      setStrictMode();
    }

    // Start stuff
    startForegroundService();
  }

  private void setStrictMode() {
    StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectAll()
        .penaltyLog()
        .penaltyDeath()
        .permitDiskWrites()
        .permitDiskReads()
        .penaltyFlashScreen()
        .build());
    StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().detectAll().penaltyLog().build());
  }

  private void startForegroundService() {
    startService(new Intent(this, ForegroundService.class));
  }

  @Override protected boolean buildConfigDebug() {
    return BuildConfig.DEBUG;
  }

  @NonNull @Override public String appName() {
    return getString(R.string.app_name);
  }

  @NonNull @Override public String buildConfigApplicationId() {
    return BuildConfig.APPLICATION_ID;
  }

  @NonNull @Override public String buildConfigVersionName() {
    return BuildConfig.VERSION_NAME;
  }

  @Override public int buildConfigVersionCode() {
    return BuildConfig.VERSION_CODE;
  }
}
