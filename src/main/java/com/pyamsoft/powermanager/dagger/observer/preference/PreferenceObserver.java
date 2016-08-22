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

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.pyamsoft.powermanager.PowerManagerPreferences;
import com.pyamsoft.powermanager.app.observer.BooleanInterestObserver;
import com.pyamsoft.powermanager.app.observer.InterestObserver;
import java.util.HashMap;
import java.util.Map;
import timber.log.Timber;

public abstract class PreferenceObserver
    implements SharedPreferences.OnSharedPreferenceChangeListener, BooleanInterestObserver {

  @NonNull private final PowerManagerPreferences preferences;
  @NonNull private final String key;
  @NonNull private final Map<String, SetCallback> setMap;
  @NonNull private final Map<String, UnsetCallback> unsetMap;
  private boolean registered;

  protected PreferenceObserver(@NonNull Context context,
      @NonNull PowerManagerPreferences preferences, @NonNull String key) {
    Timber.d("New ManageObserver with key: %s", key);
    this.preferences = preferences;
    this.key = key;

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

  @Override public final void register(@NonNull String tag, @Nullable SetCallback setCallback,
      @Nullable UnsetCallback unsetCallback) {
    if (!registered) {
      Timber.d("Register new state observer for: %s", key);
      setMap.put(tag, setCallback);
      unsetMap.put(tag, unsetCallback);
      preferences.register(this);
      registered = true;
    } else {
      Timber.e("Already registered");
    }
  }

  @Override public final void unregister(@NonNull String tag) {
    if (registered) {
      Timber.d("Unregister new state observer");
      preferences.unregister(this);
      setMap.remove(tag);
      unsetMap.remove(tag);
      registered = false;
    } else {
      Timber.e("Already unregistered");
    }
  }

  @Override public final boolean is() {
    return is(preferences);
  }

  @CheckResult protected abstract boolean is(PowerManagerPreferences preferences);
}
