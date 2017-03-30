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

import android.content.ContentResolver;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.pyamsoft.powermanager.base.wrapper.DeviceFunctionWrapper;
import com.pyamsoft.powermanager.model.overlord.StateChangeObserver;
import com.pyamsoft.powermanager.model.overlord.States;
import java.util.HashMap;
import java.util.Map;
import javax.inject.Inject;
import timber.log.Timber;

class SyncStateObserver implements StateChangeObserver {
  @SuppressWarnings("WeakerAccess") @NonNull final Map<String, SetCallback> setMap;
  @SuppressWarnings("WeakerAccess") @NonNull final Map<String, UnsetCallback> unsetMap;
  @NonNull private final DeviceFunctionWrapper wrapper;
  private boolean registered;
  @Nullable private Object listener;

  @Inject SyncStateObserver(@NonNull DeviceFunctionWrapper wrapper) {
    this.wrapper = wrapper;
    Timber.d("New StateObserver for Sync");
    registered = false;

    setMap = new HashMap<>();
    unsetMap = new HashMap<>();
  }

  @CheckResult @NonNull private Object addStatusChangeListener() {
    return ContentResolver.addStatusChangeListener(ContentResolver.SYNC_OBSERVER_TYPE_SETTINGS,
        i -> {
          if (unknown()) {
            Timber.w("Sync state is unknown");
            return;
          }

          if (is()) {
            //noinspection Convert2streamapi
            for (final SetCallback setCallback : setMap.values()) {
              if (setCallback != null) {
                setCallback.call();
              }
            }
          } else {
            //noinspection Convert2streamapi
            for (final UnsetCallback unsetCallback : unsetMap.values()) {
              if (unsetCallback != null) {
                unsetCallback.call();
              }
            }
          }
        });
  }

  private void registerListener() {
    unregisterListener();
    if (!setMap.isEmpty() && !unsetMap.isEmpty()) {
      if (!registered) {
        Timber.d("Register real listener");
        listener = addStatusChangeListener();
        registered = true;
      }
    }
  }

  private void unregisterListener() {
    if (setMap.isEmpty() && unsetMap.isEmpty()) {
      if (listener != null) {
        if (registered) {
          Timber.d("Unregister real listener");
          ContentResolver.removeStatusChangeListener(listener);
          listener = null;
          registered = false;
        }
      }
    }
  }

  @Override public void register(@NonNull String tag, @Nullable SetCallback setCallback,
      @Nullable UnsetCallback unsetCallback) {
    if (!setMap.containsKey(tag) && !unsetMap.containsKey(tag)) {
      setMap.put(tag, setCallback);
      unsetMap.put(tag, unsetCallback);
      Timber.d("Register new state observer with tag: %s", tag);
      registerListener();
    } else {
      Timber.e("Already registered with tag: %s", tag);
    }
  }

  @Override public void unregister(@NonNull String tag) {
    if (setMap.containsKey(tag) && unsetMap.containsKey(tag)) {
      Timber.d("Unregister state observer for tag: %s", tag);
      unregisterListener();
      setMap.remove(tag);
      unsetMap.remove(tag);
    } else {
      Timber.e("Already unregistered with tag: %s", tag);
    }
  }

  @Override public boolean is() {
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
