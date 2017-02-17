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

package com.pyamsoft.powermanager.base.observer.state;

import android.content.Context;
import android.support.annotation.NonNull;
import com.pyamsoft.powermanager.base.wrapper.DeviceFunctionWrapper;
import javax.inject.Inject;
import timber.log.Timber;

class DataStateObserver extends ContentObserverStateObserver {

  @NonNull private final DeviceFunctionWrapper wrapper;

  @Inject DataStateObserver(@NonNull Context context, @NonNull DeviceFunctionWrapper wrapper) {
    super(context, DeviceFunctionWrapper.SETTINGS_URI_MOBILE_DATA);
    this.wrapper = wrapper;
    Timber.d("New StateObserver for Data");
  }

  @Override public boolean is() {
    final boolean enabled = wrapper.isEnabled();
    Timber.d("Is mobile data enabled?: %s", enabled);
    return enabled;
  }
}