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

package com.pyamsoft.powermanager.app.receiver;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import timber.log.Timber;

public class DozeReceiver extends ChargingStateAwareReceiver {

  @NonNull private final Context appContext;
  @Nullable private final IntentFilter filter;
  private boolean registered = false;

  public DozeReceiver(@NonNull Context context) {
    this.appContext = context.getApplicationContext();
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
      filter = new IntentFilter(android.os.PowerManager.ACTION_DEVICE_IDLE_MODE_CHANGED);
    } else {
      filter = null;
    }
  }

  @CheckResult static boolean isDozeMode(Context context) {
    final android.os.PowerManager pm = (android.os.PowerManager) context.getApplicationContext()
        .getSystemService(Context.POWER_SERVICE);
    boolean doze;
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
      Timber.d("Get doze state");
      doze = pm.isDeviceIdleMode();
    } else {
      Timber.e("Default doze state false");
      doze = false;
    }

    return doze;
  }

  @Override public void onReceive(Context context, Intent intent) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
      Timber.d("onReceive: Doze change event");

      final boolean charging = getCurrentChargingState(context);
      final boolean state = isDozeMode(context);
      Timber.d("Doze state: %s", state);
    }
  }

  public void register() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
      if (!registered) {
        Timber.d("Register DozeReceiver");
        registered = true;
        appContext.registerReceiver(this, filter);
      } else {
        Timber.e("Already registered");
      }
    }
  }

  public void unregister() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
      if (registered) {
        Timber.d("Unregister DozeReceiver");
        registered = false;
        appContext.unregisterReceiver(this);
      } else {
        Timber.e("Already registered");
      }
    }
  }
}
