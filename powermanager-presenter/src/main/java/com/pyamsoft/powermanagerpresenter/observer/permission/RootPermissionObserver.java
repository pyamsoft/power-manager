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

package com.pyamsoft.powermanagerpresenter.observer.permission;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.pyamsoft.powermanagerpresenter.PowerManagerPreferences;
import com.pyamsoft.powermanagerpresenter.ShellCommandHelper;
import javax.inject.Inject;
import timber.log.Timber;

class RootPermissionObserver extends PermissionObserverImpl {

  @NonNull private final PowerManagerPreferences preferences;

  @Inject RootPermissionObserver(@NonNull Context context,
      @NonNull PowerManagerPreferences preferences) {
    this(context, preferences, null);
  }

  RootPermissionObserver(@NonNull Context context, @NonNull PowerManagerPreferences preferences,
      @Nullable String permission) {
    super(context, permission);
    this.preferences = preferences;
  }

  @Override protected boolean checkPermission(@NonNull Context appContext) {
    if (preferences.isRootEnabled()) {
      final boolean hasPermission = ShellCommandHelper.isSUAvailable();
      Timber.d("Has root permission? %s", hasPermission);
      return hasPermission;
    } else {
      Timber.w("Root is not enabled");
      return false;
    }
  }
}
