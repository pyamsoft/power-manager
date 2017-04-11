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
import com.pyamsoft.powermanager.base.preference.ServicePreferences;
import io.reactivex.Single;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton class ServiceInteractor {

  @NonNull private final ServicePreferences preferences;

  @Inject ServiceInteractor(@NonNull ServicePreferences preferences) {
    this.preferences = preferences;
  }

  /**
   * public
   */
  @CheckResult @NonNull Single<Boolean> isServiceEnabled() {
    return Single.fromCallable(() -> getPreferences().isServiceEnabled());
  }

  /**
   * public
   */
  void setServiceEnabled(boolean newState) {
    preferences.setServiceEnabled(newState);
  }

  @NonNull @CheckResult ServicePreferences getPreferences() {
    return preferences;
  }
}
