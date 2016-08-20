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

import android.content.Context;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.pyamsoft.powermanager.app.observer.InterestObserver;
import java.util.HashMap;
import java.util.Map;
import timber.log.Timber;

abstract class StateObserver extends ContentObserver implements InterestObserver {

  @NonNull private final Context appContext;
  @NonNull private final Handler handler;
  @NonNull private final Map<String, SetCallback> setMap;
  @NonNull private final Map<String, UnsetCallback> unsetMap;
  private Uri uri;
  private boolean registered;

  StateObserver(@NonNull Context context) {
    super(new Handler(Looper.getMainLooper()));
    appContext = context.getApplicationContext();
    handler = new Handler(Looper.getMainLooper());
    registered = false;
    setMap = new HashMap<>();
    unsetMap = new HashMap<>();
  }

  @NonNull @CheckResult final Context getAppContext() {
    return appContext;
  }

  final void setUri(@NonNull Uri uri) {
    Timber.d("New StateObserver with uri: %s", uri);
    this.uri = uri;
  }

  @Override public final void register(@NonNull String tag, @Nullable SetCallback setCallback,
      @Nullable UnsetCallback unsetCallback) {
    handler.removeCallbacksAndMessages(null);
    handler.post(() -> {
      if (!registered) {
        Timber.d("Register new state observer for: %s", uri);
        setMap.put(tag, setCallback);
        unsetMap.put(tag, unsetCallback);
        appContext.getContentResolver().registerContentObserver(uri, false, this);
        registered = true;
      } else {
        Timber.e("Already registered");
      }
    });
  }

  @Override public final void unregister(@NonNull String tag) {
    handler.removeCallbacksAndMessages(null);
    handler.post(() -> {
      if (registered) {
        Timber.d("Unregister new state observer");
        appContext.getContentResolver().unregisterContentObserver(this);
        setMap.remove(tag);
        unsetMap.remove(tag);
        registered = false;
      } else {
        Timber.e("Already unregistered");
      }
    });
  }

  @Override public final boolean deliverSelfNotifications() {
    return false;
  }

  @Override public final void onChange(boolean selfChange) {
    onChange(selfChange, null);
  }

  @Override public final void onChange(boolean selfChange, Uri uri) {
    handler.removeCallbacksAndMessages(null);
    handler.post(() -> {
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
    });
  }
}
