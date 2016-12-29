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

package com.pyamsoft.powermanager.presenter.wrapper;

import android.content.ContentResolver;
import javax.inject.Inject;
import timber.log.Timber;

class SyncConnectionWrapperImpl implements DeviceFunctionWrapper {

  @Inject SyncConnectionWrapperImpl() {

  }

  private void toggle(boolean state) {
    Timber.i("Sync: %s", state ? "enable" : "disable");
    ContentResolver.setMasterSyncAutomatically(state);
  }

  @Override public void enable() {
    toggle(true);
  }

  @Override public void disable() {
    toggle(false);
  }

  @Override public boolean isEnabled() {
    return ContentResolver.getMasterSyncAutomatically();
  }
}