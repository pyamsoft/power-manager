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

package com.pyamsoft.powermanager.app.observer;

import android.content.Context;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import timber.log.Timber;

public abstract class StateObserver extends ContentObserver {

  @NonNull private final Context appContext;
  private boolean registered;

  public StateObserver(@NonNull Context context) {
    super(new Handler(Looper.getMainLooper()));
    appContext = context.getApplicationContext();
    registered = false;
  }

  @NonNull @CheckResult final Context getAppContext() {
    return appContext;
  }

  final void register(@NonNull Uri uri) {
    if (!registered) {
      Timber.d("Register new state observer for: %s", uri);
      appContext.getContentResolver().registerContentObserver(uri, false, this);
      registered = true;
    } else {
      Timber.e("Already registered");
    }
  }

  public final void unregister() {
    if (registered) {
      Timber.d("Unregister new state observer");
      appContext.getContentResolver().unregisterContentObserver(this);
      registered = false;
    } else {
      Timber.e("Already unregistered");
    }
  }

  @CheckResult abstract boolean isEnabled();

  public abstract void register();
}
