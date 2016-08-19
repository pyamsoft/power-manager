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

package com.pyamsoft.powermanager.dagger.manager.backend;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import javax.inject.Inject;
import javax.inject.Named;
import rx.Scheduler;
import timber.log.Timber;

public final class ManagerData extends BaseManager {

  @NonNull private final ManagerInteractor interactor;

  @Inject ManagerData(@NonNull @Named("data") ManagerInteractor interactor,
      @NonNull @Named("io") Scheduler ioScheduler,
      @NonNull @Named("main") Scheduler mainScheduler) {
    super(interactor, mainScheduler, ioScheduler);
    Timber.d("new ManagerData");
    this.interactor = interactor;
  }

  @CheckResult public static boolean needsPermissionToToggle() {
    return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
  }

  @CheckResult public static boolean checkWriteSettingsPermission(@NonNull Context context) {
    return context.getApplicationContext()
        .checkCallingOrSelfPermission(Manifest.permission.WRITE_SECURE_SETTINGS)
        == PackageManager.PERMISSION_GRANTED;
  }

  @Override void onEnableComplete() {
    Timber.d("Enable complete");
    cleanup();
  }

  @Override void onDisableComplete() {
    Timber.d("Disable complete");
    cleanup();
  }
}