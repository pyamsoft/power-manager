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

package com.pyamsoft.powermanager.dagger.observer.state;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.support.annotation.NonNull;
import com.pyamsoft.powermanager.dagger.wrapper.BluetoothAdapterWrapper;
import javax.inject.Inject;

class BluetoothStateObserver extends StateObserver {

  @NonNull private final BluetoothAdapterWrapper wrapper;

  @Inject BluetoothStateObserver(@NonNull Context context) {
    super(context);

    setFilterActions(BluetoothAdapter.ACTION_STATE_CHANGED);
    wrapper = new BluetoothAdapterWrapper(context);
  }

  @Override public boolean is() {
    return wrapper.isEnabled();
  }
}
