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

package com.pyamsoft.powermanager.app.observer.manage;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import com.pyamsoft.powermanager.app.observer.InterestObserver;
import timber.log.Timber;

/**
 * KLUDGE This splits up the architecture as we are accessing Preferences from the View layer
 */
abstract class ManagePreferenceObserver
    implements SharedPreferences.OnSharedPreferenceChangeListener, InterestObserver {

  // KLUDGE limited to only default preference location
  @NonNull private final SharedPreferences defaultSharedPreferences;
  @NonNull private final String key;
  private final boolean defValue;
  private boolean registered;

  protected ManagePreferenceObserver(@NonNull Context context, @NonNull String key,
      boolean defValue) {
    this.key = key;
    this.defValue = defValue;
    defaultSharedPreferences =
        PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
  }

  @Override
  public final void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
    if (key.equals(s)) {
      Timber.d("Received preference change for key: %s", s);
      onChange();
    } else {
      Timber.e("Received preference change for other key: %s", s);
    }
  }

  @Override public final void register() {
    if (!registered) {
      Timber.d("Register new state observer for: %s", key);
      defaultSharedPreferences.registerOnSharedPreferenceChangeListener(this);
      registered = true;
    } else {
      Timber.e("Already registered");
    }
  }

  @Override public final void unregister() {
    if (registered) {
      Timber.d("Unregister new state observer");
      defaultSharedPreferences.unregisterOnSharedPreferenceChangeListener(this);
      registered = false;
    } else {
      Timber.e("Already unregistered");
    }
  }

  @Override public final boolean is() {
    return defaultSharedPreferences.getBoolean(key, defValue);
  }

  abstract void onChange();
}
