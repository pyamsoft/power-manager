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

import android.content.ContentResolver;
import android.content.Context;
import android.database.ContentObserver;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.pyamsoft.powermanager.app.observer.BooleanInterestObserver;
import com.pyamsoft.powermanager.dagger.wrapper.DeviceFunctionWrapper;
import java.util.HashMap;
import java.util.Map;
import javax.inject.Inject;
import timber.log.Timber;

class DataStateObserver extends ContentObserver implements BooleanInterestObserver {

  @SuppressWarnings("WeakerAccess") @NonNull final Map<String, SetCallback> setMap;
  @SuppressWarnings("WeakerAccess") @NonNull final Map<String, UnsetCallback> unsetMap;
  @NonNull private final DeviceFunctionWrapper wrapper;
  @NonNull private final ContentResolver contentResolver;
  private boolean registered;

  @Inject DataStateObserver(@NonNull Context context, @NonNull DeviceFunctionWrapper wrapper) {
    super(new Handler(Looper.getMainLooper()));
    contentResolver = context.getApplicationContext().getContentResolver();
    this.wrapper = wrapper;
    setMap = new HashMap<>();
    unsetMap = new HashMap<>();
    Timber.d("New StateObserver for Data");
  }

  @Override public boolean is() {
    final boolean enabled = wrapper.isEnabled();
    Timber.d("Is mobile data enabled?: %s", enabled);
    return enabled;
  }

  private void registerListener() {
    unregisterListener();
    if (!setMap.isEmpty() && !unsetMap.isEmpty()) {
      if (!registered) {
        Timber.d("Register real listener");
        contentResolver.registerContentObserver(
            Settings.Global.getUriFor(DeviceFunctionWrapper.SETTINGS_URI_MOBILE_DATA), false, this);
        registered = true;
      }
    }
  }

  private void unregisterListener() {
    if (setMap.isEmpty() && unsetMap.isEmpty()) {
      if (registered) {
        Timber.d("Unregister real listener");
        contentResolver.unregisterContentObserver(this);
        registered = false;
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

  @Override public boolean deliverSelfNotifications() {
    return false;
  }

  @Override public void onChange(boolean selfChange) {
    super.onChange(selfChange);
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
