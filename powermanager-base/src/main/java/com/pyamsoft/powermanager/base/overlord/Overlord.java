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

package com.pyamsoft.powermanager.base.overlord;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.pyamsoft.powermanager.base.observer.state.BroadcastStateObserver;
import com.pyamsoft.powermanager.base.observer.state.ContentObserverStateObserver;
import com.pyamsoft.powermanager.base.observer.state.SyncStateObserver;
import com.pyamsoft.powermanager.base.wrapper.DeviceFunctionWrapper;
import com.pyamsoft.powermanager.model.overlord.StateChangeObserver;
import com.pyamsoft.powermanager.model.overlord.StateModifier;
import com.pyamsoft.powermanager.model.overlord.States;

class Overlord implements StateModifier, StateChangeObserver {

  @NonNull private final DeviceFunctionWrapper wrapper;
  @NonNull private final StateChangeObserver deviceStateObserver;

  Overlord(@NonNull Context context, @NonNull DeviceFunctionWrapper wrapper,
      @NonNull StateObserverType type) {
    this.wrapper = wrapper;

    if (type == StateObserverType.SYNC) {
      deviceStateObserver = new SyncStateObserver(wrapper);
    } else if (type == StateObserverType.AIRPLANE || type == StateObserverType.DATA) {
      deviceStateObserver = new ContentObserverStateObserver(context, type.action()) {
        @Override public boolean is() {
          return wrapper.getState() == States.ENABLED;
        }

        @Override public boolean unknown() {
          return wrapper.getState() == States.UNKNOWN;
        }
      };
    } else {
      deviceStateObserver = new BroadcastStateObserver(context, type.action()) {

        @Override public boolean is() {
          return wrapper.getState() == States.ENABLED;
        }

        @Override public boolean unknown() {
          return wrapper.getState() == States.UNKNOWN;
        }
      };
    }
  }

  @Override public void set() {
    wrapper.enable();
  }

  @Override public void unset() {
    wrapper.disable();
  }

  @Override public boolean is() {
    return deviceStateObserver.is();
  }

  @Override public boolean unknown() {
    return deviceStateObserver.unknown();
  }

  @Override public void register(@NonNull String tag, @Nullable SetCallback setCallback,
      @Nullable UnsetCallback unsetCallback) {
    deviceStateObserver.register(tag, setCallback, unsetCallback);
  }

  @Override public void unregister(@NonNull String tag) {
    deviceStateObserver.unregister(tag);
  }
}
