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
import com.pyamsoft.powermanager.app.observer.InterestObserver;
import javax.inject.Inject;
import timber.log.Timber;

class SyncStateObserver implements InterestObserver {

  @NonNull private final Handler handler;
  private boolean registered;
  private boolean enabled;
  private boolean disabled;
  @Nullable private Object listener;
  @Nullable private Callback callback;

  @Inject SyncStateObserver() {
    handler = new Handler(Looper.getMainLooper());
    registered = false;
    enabled = false;
    disabled = false;
  }

  @CheckResult @NonNull private Object addStatusChangeListener() {
    return ContentResolver.addStatusChangeListener(ContentResolver.SYNC_OBSERVER_TYPE_SETTINGS,
        i -> handler.post(() -> {
          if (callback == null) {
            Timber.e("Received change event with no callback");
          } else {
            if (is()) {
              // Reset status of other flag here
              disabled = false;

              // Only call hook once
              if (!enabled) {
                enabled = true;
                Timber.d("Enabled");
                callback.onSet();
              }
            } else {
              // Reset status of other flag here
              enabled = false;

              // Only call hook once
              if (!disabled) {
                disabled = true;
                Timber.d("Disabled");
                callback.onUnset();
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
    final boolean b = ContentResolver.getMasterSyncAutomatically();
    Timber.d("Is %s", b);
    return b;
  }

  @Override public void register(@NonNull Callback callback) {
    handler.removeCallbacksAndMessages(null);
    if (!registered) {
      this.callback = callback;
      Timber.d("Register new state observer");
      registerListener();
      registered = true;
    } else {
      Timber.e("Already registered");
    }
  }

  @Override public void unregister() {
    handler.removeCallbacksAndMessages(null);
    handler.post(() -> {
      if (registered) {
        Timber.d("Unregister new state observer");
        unregisterListener();
        this.callback = null;
        registered = false;
      } else {
        Timber.e("Already unregistered");
      }
    });
  }
}
