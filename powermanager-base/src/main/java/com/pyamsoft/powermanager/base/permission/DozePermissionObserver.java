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

package com.pyamsoft.powermanager.base.permission;

import android.Manifest;
import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import com.pyamsoft.powermanager.base.preference.RootPreferences;
import com.pyamsoft.powermanager.base.shell.RootChecker;
import javax.inject.Inject;
import timber.log.Timber;

class DozePermissionObserver extends RootPermissionObserver {

  @Inject DozePermissionObserver(@NonNull Context context, @NonNull RootPreferences preferences,
      @NonNull RootChecker rootChecker) {
    super(context, preferences, rootChecker, Manifest.permission.DUMP);
  }

  @Override protected boolean checkPermission(@NonNull Context appContext) {
    final boolean hasPermission;
    if (Build.VERSION.SDK_INT == Build.VERSION_CODES.M) {
      // Doze can run without root on M
      // Doze can also run with root
      hasPermission = hasRuntimePermission() || super.checkPermission(appContext);
    } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
      // Doze needs root on N
      hasPermission = super.checkPermission(appContext);
    } else {
      Timber.e("This API level cannot run Doze");
      hasPermission = false;
    }

    Timber.d("Has doze permission? %s", hasPermission);
    return hasPermission;
  }
}
