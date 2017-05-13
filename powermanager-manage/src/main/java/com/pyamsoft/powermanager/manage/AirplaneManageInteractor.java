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

import android.support.annotation.NonNull;
import com.pyamsoft.powermanager.base.preference.AirplanePreferences;
import com.pyamsoft.powermanager.model.PermissionObserver;
import com.pyamsoft.powermanager.model.States;
import io.reactivex.Completable;
import io.reactivex.Single;
import javax.inject.Inject;

class AirplaneManageInteractor extends ManageInteractor {

  @SuppressWarnings("WeakerAccess") @NonNull final AirplanePreferences preferences;
  @SuppressWarnings("WeakerAccess") @NonNull final PermissionObserver permissionObserver;

  @Inject AirplaneManageInteractor(@NonNull AirplanePreferences preferences,
      @NonNull PermissionObserver permissionObserver) {
    this.preferences = preferences;
    this.permissionObserver = permissionObserver;
  }

  @NonNull @Override Completable setManaged(boolean state) {
    return Completable.fromAction(() -> preferences.setAirplaneManaged(state));
  }

  @NonNull @Override Single<Boolean> isManaged() {
    return Single.fromCallable(() -> preferences.isAirplaneManaged() ? Boolean.TRUE : Boolean.FALSE);
  }

  @NonNull @Override Single<Boolean> isManagedEnabled() {
    return Single.fromCallable(permissionObserver::hasPermission);
  }
}
