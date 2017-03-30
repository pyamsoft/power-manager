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

import android.content.ContentResolver;
import android.support.annotation.NonNull;
import com.pyamsoft.powermanager.model.Logger;
import com.pyamsoft.powermanager.model.states.States;
import javax.inject.Inject;

class SyncConnectionWrapperImpl implements DeviceFunctionWrapper {

  @NonNull private final Logger logger;

  @Inject SyncConnectionWrapperImpl(@NonNull Logger logger) {
    this.logger = logger;
  }

  private void toggle(boolean state) {
    logger.i("Sync: %s", state ? "enable" : "disable");
    ContentResolver.setMasterSyncAutomatically(state);
  }

  @Override public void enable() {
    toggle(true);
  }

  @Override public void disable() {
    toggle(false);
  }

  @NonNull @Override public States getState() {
    return ContentResolver.getMasterSyncAutomatically() ? States.ENABLED : States.DISABLED;
  }
}
