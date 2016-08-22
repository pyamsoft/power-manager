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
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.pyamsoft.powermanager.app.observer.BooleanInterestObserver;
import com.pyamsoft.powermanager.app.observer.InterestObserver;
import java.util.HashMap;
import java.util.Map;
import javax.inject.Inject;
import timber.log.Timber;

class SyncStateObserver implements BooleanInterestObserver {

  @NonNull private final Handler handler;
  @NonNull private final Map<String, SetCallback> setMap;
  @NonNull private final Map<String, UnsetCallback> unsetMap;
  private boolean registered;
  private boolean enabled;
  private boolean disabled;
  @Nullable private Object listener;

  @Inject SyncStateObserver() {
    Timber.d("New StateObserver for Sync");
    handler = new Handler(Looper.getMainLooper());
    registered = false;
    enabled = false;
    disabled = false;

    setMap = new HashMap<>();
    unsetMap = new HashMap<>();
  }

  @CheckResult @NonNull private Object addStatusChangeListener() {
    return ContentResolver.addStatusChangeListener(ContentResolver.SYNC_OBSERVER_TYPE_SETTINGS,
        i -> handler.post(() -> {
          if (is()) {
            // Reset status of other flag here
            disabled = false;

            // Only call hook once
            if (!enabled) {
              enabled = true;
              Timber.d("Run enable hooks");
              for (final SetCallback setCallback : setMap.values()) {
                if (setCallback != null) {
                  setCallback.call();
                }
              }
            }
          } else {
            // Reset status of other flag here
            enabled = false;

            // Only call hook once
            if (!disabled) {
              disabled = true;
              Timber.d("Run disable hooks");
              for (final UnsetCallback unsetCallback : unsetMap.values()) {
                if (unsetCallback != null) {
                  unsetCallback.call();
                }
              }
            }
          }
        }));
  }

  private void registerListener() {
    unregisterListener();
    listener = addStatusChangeListener();
  }

  private void unregisterListener() {
    if (listener != null) {
      ContentResolver.removeStatusChangeListener(listener);
      listener = null;
    }
  }

  @Override public boolean is() {
    final boolean enabled = ContentResolver.getMasterSyncAutomatically();
    Timber.d("Is sync enabled?: %s", enabled);
    return enabled;
  }

  @Override public void register(@NonNull String tag, @Nullable SetCallback setCallback,
      @Nullable UnsetCallback unsetCallback) {
    handler.removeCallbacksAndMessages(null);
    if (!registered) {
      setMap.put(tag, setCallback);
      unsetMap.put(tag, unsetCallback);
      Timber.d("Register new state observer");
      registerListener();
      registered = true;
    } else {
      Timber.e("Already registered");
    }
  }

  @Override public void unregister(@NonNull String tag) {
    handler.removeCallbacksAndMessages(null);
    handler.post(() -> {
      if (registered) {
        Timber.d("Unregister new state observer");
        unregisterListener();
        setMap.remove(tag);
        unsetMap.remove(tag);
        registered = false;
      } else {
        Timber.e("Already unregistered");
      }
    });
  }
}
