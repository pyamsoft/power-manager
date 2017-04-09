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

package com.pyamsoft.powermanager.main;

import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import com.pyamsoft.powermanager.base.preference.RootPreferences;
import com.pyamsoft.powermanager.base.preference.ServicePreferences;
import com.pyamsoft.powermanager.model.PermissionObserver;
import io.reactivex.Observable;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

@Singleton class MainInteractor {

  @SuppressWarnings("WeakerAccess") @NonNull final RootPreferences rootPreferences;
  @SuppressWarnings("WeakerAccess") @NonNull final ServicePreferences servicePreferences;
  @SuppressWarnings("WeakerAccess") @NonNull final PermissionObserver rootPermissionObserver;

  @Inject MainInteractor(@NonNull RootPreferences rootPreferences,
      @NonNull ServicePreferences servicePreferences,
      @NonNull @Named("obs_root_permission") PermissionObserver rootPermissionObserver) {
    this.rootPreferences = rootPreferences;
    this.servicePreferences = servicePreferences;
    this.rootPermissionObserver = rootPermissionObserver;
  }

  /**
   * public
   */
  void missingRootPermission() {
    rootPreferences.resetRootEnabled();
  }

  /**
   * public
   */
  @NonNull @CheckResult Observable<Boolean> isStartWhenOpen() {
    return Observable.fromCallable(servicePreferences::isStartWhenOpen);
  }

  /**
   * public
   */
  @CheckResult @NonNull Observable<Boolean> hasRootPermission() {
    return Observable.fromCallable(rootPermissionObserver::hasPermission);
  }
}
