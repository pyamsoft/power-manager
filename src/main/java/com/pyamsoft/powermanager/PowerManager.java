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
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import com.pyamsoft.powermanager.dagger.DaggerPowerManagerComponent;
import com.pyamsoft.powermanager.dagger.PowerManagerComponent;
import com.pyamsoft.powermanager.dagger.PowerManagerModule;
import com.pyamsoft.pydroid.lib.PYDroidApplication;
import timber.log.Timber;

public class PowerManager extends PYDroidApplication implements IPowerManager {

  private PowerManagerComponent component;

  // KLUDGE Move to better location
  @CheckResult public static boolean hasDozePermission(@NonNull Context context) {
    final boolean hasPermission = Build.VERSION.SDK_INT == Build.VERSION_CODES.M
        && context.getApplicationContext().checkCallingOrSelfPermission(Manifest.permission.DUMP)
        == PackageManager.PERMISSION_GRANTED;
    Timber.d("Has doze permission? %s", hasPermission);
    return hasPermission;
  }

  @NonNull @CheckResult public static IPowerManager get(@NonNull Context context) {
    final Context appContext = context.getApplicationContext();
    if (appContext instanceof IPowerManager) {
      return (IPowerManager) appContext;
    } else {
      throw new ClassCastException("Cannot cast Application Context to IPowerManager");
    }
  }

  @Override protected void onFirstCreate() {
    super.onFirstCreate();
    component = DaggerPowerManagerComponent.builder()
        .powerManagerModule(new PowerManagerModule(getApplicationContext()))
        .build();
  }

  @SuppressWarnings("unchecked") @NonNull @Override
  public PowerManagerComponent provideComponent() {
    if (component == null) {
      throw new NullPointerException("PowerManagerComponent is NULL");
    }
    return component;
  }
}
