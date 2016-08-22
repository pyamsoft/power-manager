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

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.pyamsoft.powermanager.app.observer.BooleanInterestObserver;
import com.pyamsoft.powermanager.app.observer.InterestObserver;
import java.util.HashMap;
import java.util.Map;
import timber.log.Timber;

abstract class StateObserver extends BroadcastReceiver implements BooleanInterestObserver {

  @NonNull private final Context appContext;
  @NonNull private final IntentFilter filter;
  @NonNull private final Map<String, SetCallback> setMap;
  @NonNull private final Map<String, UnsetCallback> unsetMap;
  private boolean registered;
  private boolean enabled;
  private boolean disabled;

  StateObserver(@NonNull Context context) {
    appContext = context.getApplicationContext();
    filter = new IntentFilter();
    registered = false;
    setMap = new HashMap<>();
    unsetMap = new HashMap<>();
  }

  @NonNull @CheckResult final Context getAppContext() {
    return appContext;
  }

  final void setFilterActions(@NonNull String... actions) {
    for (final String action : actions) {
      if (action != null) {
        filter.addAction(action);
      }
    }
  }

  void throwEmptyFilter() {
    if (filter.countActions() == 0) {
      throw new RuntimeException("Filter cannot be empty");
    }
  }

  @Override public void register(@NonNull String tag, @Nullable SetCallback setCallback,
      @Nullable UnsetCallback unsetCallback) {
    throwEmptyFilter();
    if (!registered) {
      Timber.d("Register new state observer for: %s", filter.actionsIterator());
      setMap.put(tag, setCallback);
      unsetMap.put(tag, unsetCallback);
      appContext.getApplicationContext().registerReceiver(this, filter);
      registered = true;
    } else {
      Timber.e("Already registered");
    }
  }

  @Override public void unregister(@NonNull String tag) {
    throwEmptyFilter();
    if (registered) {
      Timber.d("Unregister new state observer");
      appContext.getApplicationContext().unregisterReceiver(this);
      setMap.remove(tag);
      unsetMap.remove(tag);
      registered = false;
    } else {
      Timber.e("Already unregistered");
    }
  }

  @Override public void onReceive(Context context, Intent intent) {
    if (intent == null) {
      Timber.e("Received event from NULL intent");
      return;
    }

    final String action = intent.getAction();
    Timber.d("Received event for action: %s", action);

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
  }
}
