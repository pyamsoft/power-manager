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

import android.content.Context;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import com.pyamsoft.powermanager.PowerManagerPreferences;
import rx.Observable;

// KLUDGE needs a better name
abstract class ManagerInteractorDozeBase implements ManagerInteractorDoze {

  @NonNull private final PowerManagerPreferences preferences;
  @NonNull private final Context appContext;

  ManagerInteractorDozeBase(@NonNull Context context,
      @NonNull PowerManagerPreferences preferences) {
    this.appContext = context.getApplicationContext();
    this.preferences = preferences;
  }

  @CheckResult @NonNull final Context getAppContext() {
    return appContext;
  }

  @NonNull @CheckResult final PowerManagerPreferences getPreferences() {
    return preferences;
  }

  @NonNull @Override public Observable<Boolean> isDozeEnabled() {
    return Observable.defer(() -> Observable.just(preferences.isDozeEnabled()));
  }

  @NonNull @Override public Observable<Boolean> isDozeExclusive() {
    // TODO replace with setting
    return Observable.defer(() -> Observable.just(false));
  }

  @NonNull @Override public Observable<Boolean> hasDumpSysPermission() {
    return Observable.defer(() -> Observable.just(ManagerDoze.checkDumpsysPermission(appContext)));
  }

  @NonNull @Override public Observable<Boolean> isDozeIgnoreCharging() {
    return Observable.defer(() -> Observable.just(preferences.isIgnoreChargingDoze()));
  }
}
