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

package com.pyamsoft.powermanager.dagger.observer.preference;

import android.content.SharedPreferences;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.pyamsoft.powermanager.PowerManagerPreferences;
import com.pyamsoft.powermanager.app.observer.BooleanInterestObserver;
import java.util.HashMap;
import java.util.Map;
import timber.log.Timber;

public abstract class BooleanPreferenceObserver
    implements SharedPreferences.OnSharedPreferenceChangeListener, BooleanInterestObserver {

  @NonNull final PowerManagerPreferences preferences;
  @NonNull final String key;
  @NonNull final Map<String, SetCallback> setMap;
  @NonNull final Map<String, UnsetCallback> unsetMap;
  boolean registered;

  protected BooleanPreferenceObserver(@NonNull PowerManagerPreferences preferences,
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
      if (is()) {
        for (final SetCallback setCallback : setMap.values()) {
          if (setCallback != null) {
            setCallback.call();
          }
        }
      } else {
        for (final UnsetCallback unsetCallback : unsetMap.values()) {
          if (unsetCallback != null) {
            unsetCallback.call();
          }
        }
      }
    }
  }

  void registerListener() {
    unregisterListener();
    if (!setMap.isEmpty() && !unsetMap.isEmpty()) {
      if (!registered) {
        Timber.d("Register real listener for key: %s", key);
        preferences.register(this);
        registered = true;
      }
    }
  }

  void unregisterListener() {
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

  @Override public final boolean is() {
    return is(preferences);
  }

  @CheckResult protected abstract boolean is(@NonNull PowerManagerPreferences preferences);
}
