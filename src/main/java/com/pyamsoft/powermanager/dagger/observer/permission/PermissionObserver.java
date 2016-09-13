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
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.pyamsoft.powermanager.app.observer.BooleanInterestObserver;
import timber.log.Timber;

abstract class PermissionObserver implements BooleanInterestObserver {

  @NonNull private final Context appContext;

  PermissionObserver(@NonNull Context context) {
    this.appContext = context.getApplicationContext();
  }

  @NonNull @CheckResult Context getAppContext() {
    return appContext;
  }

  @Override public void register(@NonNull String tag, @Nullable SetCallback setCallback,
      @Nullable UnsetCallback unsetCallback) {
    Timber.e("Cannot register to observe permissions");
  }

  @Override public void unregister(@NonNull String tag) {
    Timber.e("Cannot unregister to observe permissions");
  }
}
