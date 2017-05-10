/*
 * Copyright 2017 Peter Kenji Yamanaka
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

package com.pyamsoft.powermanager.manage;

import android.os.Build;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import com.pyamsoft.powermanager.base.preference.DataPreferences;
import com.pyamsoft.powermanager.model.PermissionObserver;
import com.pyamsoft.powermanager.model.States;
import io.reactivex.Completable;
import io.reactivex.Single;
import java.util.concurrent.Callable;
import javax.inject.Inject;

class DataManageInteractor extends ManageInteractor {

  @SuppressWarnings("WeakerAccess") @NonNull final DataPreferences preferences;
  @SuppressWarnings("WeakerAccess") @NonNull final PermissionObserver permissionObserver;

  @Inject DataManageInteractor(@NonNull DataPreferences preferences,
      @NonNull PermissionObserver permissionObserver) {
    this.preferences = preferences;
    this.permissionObserver = permissionObserver;
  }

  @NonNull @Override Completable setManaged(boolean state) {
    return Completable.fromAction(() -> preferences.setDataManaged(state));
  }

  @NonNull @Override Single<States> isManaged() {
    return Single.fromCallable(new Callable<States>() {
      @Override public States call() throws Exception {
        // Above KitKat, we must have Root
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
          if (!permissionObserver.hasPermission()) {
            return States.UNKNOWN;
          } else {
            return getStateFromPreferences();
          }
        } else {
          return getStateFromPreferences();
        }
      }

      @CheckResult @NonNull private States getStateFromPreferences() {
        return preferences.isDataManaged() ? States.ENABLED : States.DISABLED;
      }
    });
  }
}
