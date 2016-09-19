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

import android.content.Context;
import android.content.Intent;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.google.android.gms.common.GoogleApiAvailability;
import com.pyamsoft.powermanager.app.service.ForegroundService;
import com.pyamsoft.powermanager.dagger.DaggerPowerManagerComponent;
import com.pyamsoft.powermanager.dagger.PowerManagerComponent;
import com.pyamsoft.powermanager.dagger.PowerManagerModule;
import com.pyamsoft.pydroid.lib.PYDroidApplication;

public class PowerManager extends PYDroidApplication
    implements IPowerManager<PowerManagerComponent> {

  private PowerManagerComponent component;

  @NonNull @CheckResult
  public static IPowerManager<PowerManagerComponent> get(@NonNull Context context) {
    final Context appContext = context.getApplicationContext();
    if (appContext instanceof IPowerManager) {
      return PowerManager.class.cast(appContext);
    } else {
      throw new ClassCastException("Cannot cast Application Context to IPowerManager");
    }
  }

  @Override protected void onFirstCreate() {
    super.onFirstCreate();
    component = DaggerPowerManagerComponent.builder()
        .powerManagerModule(new PowerManagerModule(getApplicationContext()))
        .build();

    startService(new Intent(this, ForegroundService.class));
  }

  @NonNull @Override public PowerManagerComponent provideComponent() {
    if (component == null) {
      throw new NullPointerException("PowerManagerComponent is NULL");
    }
    return component;
  }

  @Nullable @Override public String provideGoogleOpenSourceLicenses() {
    return GoogleApiAvailability.getInstance().getOpenSourceSoftwareLicenseInfo(this);
  }
}
