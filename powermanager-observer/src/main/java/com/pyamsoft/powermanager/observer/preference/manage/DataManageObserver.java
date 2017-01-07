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

package com.pyamsoft.powermanager.observer.preference.manage;

import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import com.pyamsoft.powermanager.base.PowerManagerPreferences;
import com.pyamsoft.powermanager.model.PermissionObserver;
import com.pyamsoft.powermanager.observer.R;
import com.pyamsoft.powermanager.observer.preference.BooleanPreferenceObserver;
import javax.inject.Inject;
import timber.log.Timber;

class DataManageObserver extends BooleanPreferenceObserver {

  @NonNull private final PermissionObserver rootPermissionObserver;

  @Inject DataManageObserver(@NonNull Context context, @NonNull PowerManagerPreferences preferences,
      @NonNull PermissionObserver permissionObserver) {
    super(preferences, context.getString(R.string.manage_data_key));
    rootPermissionObserver = permissionObserver;
  }

  @Override protected boolean is(@NonNull PowerManagerPreferences preferences) {
    final boolean preferenceManaged = preferences.isDataManaged();
    if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
      Timber.d("isManaged: check for root on API > 19");
      return preferenceManaged && rootPermissionObserver.hasPermission();
    } else {
      return preferenceManaged;
    }
  }
}