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

package com.pyamsoft.powermanager.service;

import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import com.pyamsoft.powermanager.base.PowerManagerPreferences;
import javax.inject.Inject;
import rx.Observable;

class ActionToggleInteractor {

  @NonNull private final PowerManagerPreferences preferences;

  @Inject ActionToggleInteractor(@NonNull PowerManagerPreferences preferences) {
    this.preferences = preferences;
  }

  @NonNull @CheckResult public Observable<Boolean> toggleEnabledState() {
    return isServiceEnabled().map(enabled -> {
      final boolean newState = !enabled;
      setServiceEnabled(newState);
      return newState;
    });
  }

  void setServiceEnabled(boolean state) {
    preferences.setServiceEnabled(state);
  }

  @CheckResult @NonNull protected Observable<Boolean> isServiceEnabled() {
    return Observable.fromCallable(() -> getPreferences().isServiceEnabled());
  }

  @NonNull @CheckResult PowerManagerPreferences getPreferences() {
    return preferences;
  }
}
