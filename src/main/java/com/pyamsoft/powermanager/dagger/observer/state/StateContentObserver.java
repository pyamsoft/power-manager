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
import timber.log.Timber;

abstract class StateContentObserver<V> extends ContentObserver implements InterestObserver<V> {

  @NonNull private final Context appContext;
  @NonNull private final Handler handler;
  private Uri uri;
  private boolean registered;

  public StateContentObserver(@NonNull Context context) {
    this(context, null);
  }

  public StateContentObserver(@NonNull Context context, @Nullable Uri uri) {
    super(new Handler(Looper.getMainLooper()));
    appContext = context.getApplicationContext();
    handler = new Handler(Looper.getMainLooper());
    registered = false;
    this.uri = uri;
  }

  @NonNull @CheckResult final Context getAppContext() {
    return appContext;
  }

  public final void setUri(@NonNull Uri uri) {
    this.uri = uri;
  }

  @Override public final void register() {
    handler.removeCallbacksAndMessages(null);
    handler.post(() -> {
      if (!registered) {
        Timber.d("Register new state observer for: %s", uri);
        appContext.getContentResolver().registerContentObserver(uri, false, this);
        registered = true;
      } else {
        Timber.e("Already registered");
      }
    });
  }

  @Override public final void unregister() {
    handler.removeCallbacksAndMessages(null);
    handler.post(() -> {
      if (registered) {
        Timber.d("Unregister new state observer");
        appContext.getContentResolver().unregisterContentObserver(this);
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
    handler.post(() -> onChange(uri));
  }

  abstract void onChange(Uri uri);
}
