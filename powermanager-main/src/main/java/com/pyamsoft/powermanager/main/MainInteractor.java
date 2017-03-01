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
import com.pyamsoft.powermanager.base.PowerManagerPreferences;
import javax.inject.Inject;
import javax.inject.Singleton;
import rx.Observable;

@Singleton class MainInteractor {

  @SuppressWarnings("WeakerAccess") @NonNull final PowerManagerPreferences preferences;

  @Inject MainInteractor(@NonNull PowerManagerPreferences preferences) {
    this.preferences = preferences;
  }

  public void missingRootPermission() {
    preferences.resetRootEnabled();
  }

  @NonNull @CheckResult public Observable<Boolean> isStartWhenOpen() {
    return Observable.fromCallable(preferences::isStartWhenOpen);
  }
}
