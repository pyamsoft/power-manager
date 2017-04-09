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

package com.pyamsoft.powermanager.base.states;

import android.content.Context;
import android.provider.Settings;
import android.support.annotation.NonNull;
import com.pyamsoft.powermanager.model.States;
import javax.inject.Inject;
import timber.log.Timber;

class AirplaneStateObserver extends ContentObserverStateObserver {

  @NonNull private final DeviceFunctionWrapper wrapper;

  @Inject AirplaneStateObserver(@NonNull Context context, @NonNull DeviceFunctionWrapper wrapper) {
    super(context, Settings.Global.AIRPLANE_MODE_ON);
    this.wrapper = wrapper;
    Timber.d("New StateObserver for Airplane Mode");
  }

  @Override public boolean enabled() {
    final boolean enabled = wrapper.getState() == States.ENABLED;
    Timber.d("Enabled: %s", enabled);
    return enabled;
  }

  @Override public boolean unknown() {
    final boolean unknown = wrapper.getState() == States.UNKNOWN;
    Timber.d("Unknown: %s", unknown);
    return unknown;
  }
}