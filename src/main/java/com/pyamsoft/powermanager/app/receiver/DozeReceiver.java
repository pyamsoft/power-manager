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

import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import com.pyamsoft.powermanager.app.manager.backend.ManagerDoze;
import timber.log.Timber;

@TargetApi(Build.VERSION_CODES.M) public class DozeReceiver extends BroadcastReceiver {

  @NonNull private final IntentFilter filter =
      new IntentFilter(android.os.PowerManager.ACTION_DEVICE_IDLE_MODE_CHANGED);
  private boolean registered = false;
  private Context appContext;

  public DozeReceiver() {
    if (!ManagerDoze.isDozeAvailable()) {
      throw new RuntimeException("Doze not available!");
    }
  }

  @CheckResult public static boolean isDozeMode(Context context) {
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
    Timber.d("onReceive: Doze change event");
    Timber.d("Doze state: %s", isDozeMode(context));
  }

  public void register(@NonNull Context context) {
    if (!registered) {
      Timber.d("Register DozeReceiver");
      registered = true;
      appContext = context.getApplicationContext();
      appContext.registerReceiver(this, filter);
    } else {
      Timber.e("Already registered");
    }
  }

  public void unregister() {
    if (registered) {
      Timber.d("Unregister DozeReceiver");
      registered = false;
      appContext.unregisterReceiver(this);
      appContext = null;
    } else {
      Timber.e("Already registered");
    }
  }
}