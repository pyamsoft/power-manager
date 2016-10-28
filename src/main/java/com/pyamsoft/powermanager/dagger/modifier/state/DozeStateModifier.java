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

package com.pyamsoft.powermanager.dagger.modifier.state;

import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import com.pyamsoft.powermanager.app.observer.PermissionObserver;
import com.pyamsoft.powermanager.dagger.ShellCommandHelper;
import javax.inject.Inject;
import javax.inject.Named;
import rx.Subscription;
import rx.subscriptions.Subscriptions;
import timber.log.Timber;

class DozeStateModifier extends StateModifier {

  @NonNull private static final String DUMPSYS_DOZE_START_M = "deviceidle force-idle";
  @NonNull private static final String DUMPSYS_DOZE_START_N = "deviceidle force-idle deep";
  @NonNull private static final String DUMPSYS_DOZE_END_M = "deviceidle step";
  @NonNull private static final String DUMPSYS_DOZE_END_N = "deviceidle unforce";
  @NonNull private final PermissionObserver dozePermissionObserver;
  @Named private Subscription subscription = Subscriptions.empty();

  @Inject DozeStateModifier(@NonNull Context context,
      @NonNull PermissionObserver dozePermissionObserver) {
    super(context);
    this.dozePermissionObserver = dozePermissionObserver;
  }

  @SuppressWarnings("WeakerAccess") void unsub() {
    if (!subscription.isUnsubscribed()) {
      subscription.unsubscribe();
    }
  }

  @Override void set(@NonNull Context context) {
    unsub();
    // We dont explicitly state subscribeOn and observeOn because it is up to the caller to implement
    // the proper threading
    subscription = dozePermissionObserver.hasPermission().subscribe(hasPermission -> {
      if (hasPermission) {
        Timber.d("Begin Doze");
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.M) {
          // API 23 can do this without root
          ShellCommandHelper.runShellCommand(DUMPSYS_DOZE_START_M);
        } else if (Build.VERSION.SDK_INT == Build.VERSION_CODES.N) {
          // API 24 requires root
          ShellCommandHelper.runRootShellCommand(DUMPSYS_DOZE_START_N);
        } else {
          throw new RuntimeException("Invalid API level attempting to dumpsys");
        }
      }
    }, throwable -> {
      Timber.e(throwable, "onError set");
      unsub();
    }, this::unsub);
  }

  @Override void unset(@NonNull Context context) {
    unsub();
    // We dont explicitly state subscribeOn and observeOn because it is up to the caller to implement
    // the proper threading
    subscription = dozePermissionObserver.hasPermission().subscribe(hasPermission -> {
      if (hasPermission) {
        Timber.d("End Doze");
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.M) {
          // API 23 can do this without root
          ShellCommandHelper.runShellCommand(DUMPSYS_DOZE_END_M);
        } else if (Build.VERSION.SDK_INT == Build.VERSION_CODES.N) {
          // API 24 requires root
          ShellCommandHelper.runRootShellCommand(DUMPSYS_DOZE_END_N);
        } else {
          throw new RuntimeException("Invalid API level attempting to dumpsys");
        }
      }
    }, throwable -> {
      Timber.e(throwable, "onError unset");
      unsub();
    }, this::unsub);
  }
}
