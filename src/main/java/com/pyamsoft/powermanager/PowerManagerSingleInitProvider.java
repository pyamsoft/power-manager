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
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.google.android.gms.common.GoogleApiAvailability;
import com.pyamsoft.powermanager.app.service.ForegroundService;
import com.pyamsoft.powermanager.dagger.DaggerPowerManagerComponent;
import com.pyamsoft.powermanager.dagger.PowerManagerComponent;
import com.pyamsoft.powermanager.dagger.PowerManagerModule;
import com.pyamsoft.pydroid.IPYDroidApp;
import com.pyamsoft.pydroid.SingleInitContentProvider;
import com.pyamsoft.pydroid.about.Licenses;

public class PowerManagerSingleInitProvider extends SingleInitContentProvider
    implements IPYDroidApp<PowerManagerComponent> {

  @Nullable private PowerManagerComponent component;

  @Override protected void onInstanceCreated(@NonNull Context context) {
    Injector.set(component);
    ForegroundService.start(context);
  }

  @Override protected void onFirstCreate(@NonNull Context context) {
    super.onFirstCreate(context);

    component = DaggerPowerManagerComponent.builder()
        .powerManagerModule(new PowerManagerModule(context))
        .build();
  }

  @Nullable @Override public String provideGoogleOpenSourceLicenses(@NonNull Context context) {
    return GoogleApiAvailability.getInstance().getOpenSourceSoftwareLicenseInfo(context);
  }

  @Override public void insertCustomLicensesIntoMap() {
    Licenses.create("Android Priority Job Queue",
        "https://github.com/yigit/android-priority-jobqueue", "licenses/androidpriorityjobqueue");
    Licenses.create("SQLBrite", "https://github.com/square/sqlbrite", "licenses/sqlbrite");
    Licenses.create("SQLDelight", "https://github.com/square/sqldelight", "licenses/sqldelight");
    Licenses.create("libsuperuser", "http://su.chainfire.eu/", "licenses/libsuperuser");
  }

  @NonNull @Override public PowerManagerComponent provideComponent() {
    if (component == null) {
      throw new NullPointerException("PowerManagerComponent is NULL");
    }
    return component;
  }
}
