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

package com.pyamsoft.powermanager.dagger.observer.permission;

import android.content.Context;
import android.content.pm.PackageManager;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.pyamsoft.powermanager.app.observer.PermissionObserver;
import rx.Observable;
import timber.log.Timber;

abstract class PermissionObserverImpl implements PermissionObserver {

  // KLUDGE Holds onto App context
  @SuppressWarnings("WeakerAccess") @NonNull final Context appContext;
  @Nullable private final String permission;

  PermissionObserverImpl(@NonNull Context context, @Nullable String permission) {
    this.appContext = context.getApplicationContext();
    this.permission = permission;
  }

  @CheckResult boolean hasRuntimePermission() {
    if (permission == null) {
      Timber.w("No permission watched");
      return false;
    } else {
      return appContext.getApplicationContext().checkCallingOrSelfPermission(permission)
          == PackageManager.PERMISSION_GRANTED;
    }
  }

  @NonNull @Override public Observable<Boolean> hasPermission() {
    return Observable.defer(() -> Observable.just(checkPermission(appContext)));
  }

  @CheckResult protected abstract boolean checkPermission(Context appContext);

  @Override public void register(@NonNull String tag, @Nullable SetCallback setCallback,
      @Nullable UnsetCallback unsetCallback) {
    Timber.e("Cannot register to observe permissions");
  }

  @Override public void unregister(@NonNull String tag) {
    Timber.e("Cannot unregister to observe permissions");
  }
}
