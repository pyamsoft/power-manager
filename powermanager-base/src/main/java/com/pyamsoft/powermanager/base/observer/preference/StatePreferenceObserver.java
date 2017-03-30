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

package com.pyamsoft.powermanager.base.observer.preference;

import android.content.SharedPreferences;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.pyamsoft.powermanager.base.PowerManagerPreferences;
import com.pyamsoft.powermanager.model.StateChangeObserver;
import com.pyamsoft.pydroid.app.OnRegisteredSharedPreferenceChangeListener;
import java.util.HashMap;
import java.util.Map;
import timber.log.Timber;

public abstract class StatePreferenceObserver extends OnRegisteredSharedPreferenceChangeListener
    implements StateChangeObserver {

  @NonNull private final PowerManagerPreferences preferences;
  @NonNull private final String key;
  @NonNull private final Map<String, SetCallback> setMap;
  @NonNull private final Map<String, UnsetCallback> unsetMap;
  private boolean registered;

  protected StatePreferenceObserver(@NonNull PowerManagerPreferences preferences,
      @NonNull String key) {
    Timber.d("New PreferenceObserver with key: %s", key);
    this.preferences = preferences;
    this.key = key;
    this.registered = false;

    setMap = new HashMap<>();
    unsetMap = new HashMap<>();
  }

  @Override public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
    if (key.equals(s)) {
      Timber.d("Received preference change for key: %s", s);
      if (unknown()) {
        Timber.w("Current state is unknown for key: %s", s);
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
    }
  }

  private void registerListener() {
    unregisterListener();
    if (!setMap.isEmpty() && !unsetMap.isEmpty()) {
      if (!registered) {
        Timber.d("Register real listener for key: %s", key);
        preferences.register(this);
        registered = true;
      }
    }
  }

  private void unregisterListener() {
    if (setMap.isEmpty() && unsetMap.isEmpty()) {
      if (registered) {
        Timber.d("Unregister real listener for key: %s", key);
        preferences.unregister(this);
        registered = false;
      }
    }
  }

  @Override public final void register(@NonNull String tag, @Nullable SetCallback setCallback,
      @Nullable UnsetCallback unsetCallback) {
    if (!setMap.containsKey(tag) && !unsetMap.containsKey(tag)) {
      Timber.d("Register new preference observer for: %s", tag);
      setMap.put(tag, setCallback);
      unsetMap.put(tag, unsetCallback);
      registerListener();
    } else {
      Timber.e("Already registered with tag: %s", tag);
    }
  }

  @Override public final void unregister(@NonNull String tag) {
    if (setMap.containsKey(tag) && unsetMap.containsKey(tag)) {
      Timber.d("Unregister preference observer for tag: %s", tag);
      setMap.remove(tag);
      unsetMap.remove(tag);
      unregisterListener();
    } else {
      Timber.e("Already unregistered with tag: %s", tag);
    }
  }

  @Override public boolean is() {
    return is(preferences);
  }

  @Override public boolean unknown() {
    return false;
  }

  @CheckResult protected abstract boolean is(@NonNull PowerManagerPreferences preferences);
}
