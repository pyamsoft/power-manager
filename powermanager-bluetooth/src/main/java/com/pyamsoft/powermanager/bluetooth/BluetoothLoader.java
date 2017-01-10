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

package com.pyamsoft.powermanager.bluetooth;

import android.support.annotation.NonNull;
import com.pyamsoft.powermanager.base.Injector;
import com.pyamsoft.powermanager.model.BooleanInterestObserver;
import com.pyamsoft.pydroid.FuncNone;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;

class BluetoothLoader implements FuncNone<BooleanInterestObserver> {

  @SuppressWarnings("WeakerAccess") @Inject @Named("obs_bluetooth_state")
  Provider<BooleanInterestObserver> observerProvider;

  @NonNull @Override public BooleanInterestObserver call() {
    DaggerBluetoothScreenComponent.builder()
        .powerManagerComponent(Injector.get().provideComponent())
        .build()
        .inject(this);
    return observerProvider.get();
  }
}
