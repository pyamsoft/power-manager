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

import android.bluetooth.BluetoothAdapter;
import android.support.annotation.CheckResult;
import android.support.annotation.Nullable;
import javax.inject.Inject;

public final class BluetoothAdapterWrapper {

  @Nullable private final BluetoothAdapter adapter;

  @Inject public BluetoothAdapterWrapper(@Nullable BluetoothAdapter adapter) {
    this.adapter = adapter;
  }

  public final void enable() {
    if (adapter != null) {
      adapter.enable();
    }
  }

  public final void disable() {
    if (adapter != null) {
      adapter.disable();
    }
  }

  @CheckResult public final boolean isEnabled() {
    return adapter != null && adapter.isEnabled();
  }
}
