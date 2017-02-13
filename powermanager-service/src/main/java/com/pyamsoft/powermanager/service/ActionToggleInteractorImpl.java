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
import javax.inject.Inject;
import rx.Observable;

class ActionToggleInteractorImpl implements ActionToggleInteractor {

  @NonNull private final PowerManagerPreferences preferences;

  @Inject ActionToggleInteractorImpl(@NonNull PowerManagerPreferences preferences) {
    this.preferences = preferences;
  }

  @Override public void setServiceEnabled(boolean state) {
    preferences.setServiceEnabled(state);
  }

  @NonNull @Override public Observable<Boolean> isServiceEnabled() {
    return Observable.defer(() -> Observable.just(getPreferences().isServiceEnabled()));
  }

  @NonNull @CheckResult PowerManagerPreferences getPreferences() {
    return preferences;
  }
}
