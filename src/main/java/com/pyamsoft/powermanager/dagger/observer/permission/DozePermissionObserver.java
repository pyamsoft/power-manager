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

package com.pyamsoft.powermanager.dagger.observer.permission;

import android.Manifest;
import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import com.pyamsoft.powermanager.PowerManagerPreferences;
import javax.inject.Inject;
import timber.log.Timber;

class DozePermissionObserver extends RootPermissionObserver {

  @Inject DozePermissionObserver(@NonNull Context context,
      @NonNull PowerManagerPreferences preferences) {
    super(context, preferences, Manifest.permission.DUMP);
  }

  @Override protected boolean checkPermission(@NonNull Context appContext) {
    final boolean hasPermission;
    switch (Build.VERSION.SDK_INT) {
      case Build.VERSION_CODES.M:
        // Doze can run without root on M
        hasPermission = hasRuntimePermission();
        break;
      case Build.VERSION_CODES.N:
        // Doze needs root on N
        hasPermission = hasRuntimePermission() && super.checkPermission(appContext);
        break;
      default:
        hasPermission = false;
    }

    Timber.d("Has doze permission? %s", hasPermission);
    return hasPermission;
  }
}
