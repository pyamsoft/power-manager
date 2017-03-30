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

package com.pyamsoft.powermanager.base.modifier;

import android.support.annotation.NonNull;
import com.pyamsoft.powermanager.base.wrapper.DeviceFunctionWrapper;
import com.pyamsoft.powermanager.model.overlord.StateModifier;
import javax.inject.Inject;

class DozeStateModifier implements StateModifier {

  @NonNull private final DeviceFunctionWrapper wrapper;

  @Inject DozeStateModifier(@NonNull DeviceFunctionWrapper wrapper) {
    this.wrapper = wrapper;
  }

  @Override public void set() {
    wrapper.enable();
  }

  @Override public void unset() {
    wrapper.disable();
  }
}
