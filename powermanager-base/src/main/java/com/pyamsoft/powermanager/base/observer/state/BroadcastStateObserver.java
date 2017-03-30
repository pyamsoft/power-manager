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

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.pyamsoft.powermanager.model.overlord.StateObserver;
import java.util.HashMap;
import java.util.Map;
import timber.log.Timber;

public abstract class BroadcastStateObserver extends BroadcastReceiver
    implements StateObserver {

  // KLUDGE Holds reference to app context
  @NonNull private final Context appContext;
  @NonNull private final IntentFilter filter;
  @NonNull private final Map<String, SetCallback> setMap;
  @NonNull private final Map<String, UnsetCallback> unsetMap;
  private boolean registered;

  protected BroadcastStateObserver(@NonNull Context context, @NonNull String... actions) {
    appContext = context.getApplicationContext();
    filter = new IntentFilter();
    setMap = new HashMap<>();
    unsetMap = new HashMap<>();
    registered = false;

    setFilterActions(actions);
  }

  private void setFilterActions(@NonNull String... actions) {
    for (final String action : actions) {
      if (action != null && !action.isEmpty()) {
        filter.addAction(action);
      }
    }
  }

  @NonNull @CheckResult Context getAppContext() {
    return appContext;
  }

  @NonNull @CheckResult IntentFilter getFilter() {
    return filter;
  }

  private void throwEmptyFilter() {
    if (filter.countActions() == 0) {
      throw new RuntimeException("Filter cannot be empty");
    }
  }

  private void registerListener() {
    unregisterListener();
    if (!setMap.isEmpty() && !unsetMap.isEmpty()) {
      if (!registered) {
        Timber.d("Register real receiver for action: %s", filter.getAction(0));
        appContext.getApplicationContext().registerReceiver(this, filter);
        registered = true;
      }
    }
  }

  private void unregisterListener() {
    if (setMap.isEmpty() && unsetMap.isEmpty()) {
      if (registered) {
        Timber.d("Unregister real receiver for action: %s", filter.getAction(0));
        appContext.getApplicationContext().unregisterReceiver(this);
        registered = false;
      }
    }
  }

  @Override public void register(@NonNull String tag, @Nullable SetCallback setCallback,
      @Nullable UnsetCallback unsetCallback) {
    throwEmptyFilter();
    if (!setMap.containsKey(tag) && !unsetMap.containsKey(tag)) {
      Timber.d("Register new state observer for: %s", tag);
      setMap.put(tag, setCallback);
      unsetMap.put(tag, unsetCallback);
      registerListener();
    } else {
      Timber.e("Already registered with tag: %s", tag);
    }
  }

  @Override public void unregister(@NonNull String tag) {
    throwEmptyFilter();
    if (setMap.containsKey(tag) && unsetMap.containsKey(tag)) {
      Timber.d("Unregister state observer for: %s", tag);
      setMap.remove(tag);
      unsetMap.remove(tag);
      unregisterListener();
    } else {
      Timber.e("Already unregistered with tag: %s", tag);
    }
  }

  @Override public void onReceive(Context context, Intent intent) {
    if (intent == null) {
      Timber.e("Received event from NULL intent");
      return;
    }

    final String action = intent.getAction();
    Timber.d("Received event for action: %s", action);

    if (unknown()) {
      Timber.w("Current state is unknown for action: %s", action);
      return;
    }

    if (enabled()) {
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
