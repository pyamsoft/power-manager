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
import android.os.Build;
import android.os.PowerManager;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import javax.inject.Inject;
import timber.log.Timber;

class DozeStateObserver extends StateObserver {

  @NonNull private final PowerManager androidPowerManager;

  @Inject DozeStateObserver(@NonNull Context context) {
    super(context);
    if (isDozeAvailable()) {
      setFilterActions(PowerManager.ACTION_DEVICE_IDLE_MODE_CHANGED);
    }

    androidPowerManager = (android.os.PowerManager) getAppContext().getApplicationContext()
        .getSystemService(Context.POWER_SERVICE);
  }

  @CheckResult static boolean isDozeAvailable() {
    return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
  }

  @Override public void register(@NonNull String tag, @Nullable SetCallback setCallback,
      @Nullable UnsetCallback unsetCallback) {
    if (isDozeAvailable()) {
      super.register(tag, setCallback, unsetCallback);
    }
  }

  @Override public void unregister(@NonNull String tag) {
    if (isDozeAvailable()) {
      super.unregister(tag);
    }
  }

  @Override public boolean is() {
    boolean doze;
    if (isDozeAvailable()) {
      Timber.d("Get doze state");
      doze = androidPowerManager.isDeviceIdleMode();
    } else {
      Timber.e("Default doze state false");
      doze = false;
    }

    return doze;
  }
}
