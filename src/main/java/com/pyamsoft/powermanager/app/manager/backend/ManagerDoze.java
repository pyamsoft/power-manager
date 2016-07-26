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

package com.pyamsoft.powermanager.app.manager.backend;

import android.os.Build;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import javax.inject.Inject;
import javax.inject.Named;
import rx.Scheduler;
import rx.Subscription;
import rx.subscriptions.Subscriptions;
import timber.log.Timber;

public class ManagerDoze implements Manager {

  @NonNull private final Scheduler mainScheduler;
  @NonNull private final Scheduler ioScheduler;
  @NonNull private Subscription subscription = Subscriptions.empty();

  @Inject public ManagerDoze(@NonNull @Named("io") Scheduler ioScheduler,
      @NonNull @Named("main") Scheduler mainScheduler) {
    this.ioScheduler = ioScheduler;
    this.mainScheduler = mainScheduler;
  }

  @CheckResult public static boolean isDozeAvailable() {
    return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
  }

  @Override public void enable() {
    if (!isDozeAvailable()) {
      Timber.e("Doze is not available on this platform");
      return;
    }

    Timber.d("Do doze enable");
  }

  @Override public void disable(boolean charging) {
    if (!isDozeAvailable()) {
      Timber.e("Doze is not available on this platform");
      return;
    }

    Timber.d("Do doze disable");
  }
}
