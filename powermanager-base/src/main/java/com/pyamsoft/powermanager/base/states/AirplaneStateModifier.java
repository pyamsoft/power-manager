/*
 * Copyright 2017 Peter Kenji Yamanaka
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

import android.support.annotation.NonNull;
import com.pyamsoft.powermanager.model.StateModifier;
import javax.inject.Inject;

class AirplaneStateModifier implements StateModifier {

  @NonNull private final DeviceFunctionWrapper wrapper;

  @Inject AirplaneStateModifier(@NonNull DeviceFunctionWrapper wrapper) {
    this.wrapper = wrapper;
  }

  @Override public void set() {
    wrapper.enable();
  }

  @Override public void unset() {
    wrapper.disable();
  }
}
