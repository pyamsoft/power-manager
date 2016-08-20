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

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.PowerManager;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.pyamsoft.powermanager.app.observer.InterestObserver;
import javax.inject.Inject;
import timber.log.Timber;

class DozeStateObserver extends BroadcastReceiver implements InterestObserver {

  @NonNull private final Context appContext;
  @NonNull private final Handler handler;
  @Nullable private final IntentFilter filter;
  @Nullable private SetCallback setCallback;
  @Nullable private UnsetCallback unsetCallback;
  private boolean registered;

  @Inject DozeStateObserver(@NonNull Context context) {
    appContext = context.getApplicationContext();
    handler = new Handler(Looper.getMainLooper());
    if (isDozeAvailable()) {
      filter = new IntentFilter(PowerManager.ACTION_DEVICE_IDLE_MODE_CHANGED);
    } else {
      filter = null;
    }
  }

  @CheckResult static boolean isDozeAvailable() {
    return Build.VERSION.SDK_INT == Build.VERSION_CODES.M;
  }

  @Override
  public void register(@Nullable SetCallback setCallback, @Nullable UnsetCallback unsetCallback) {
    handler.removeCallbacksAndMessages(null);
    if (isDozeAvailable()) {
      handler.post(() -> {
        if (!registered) {
          Timber.d("Register new state observer for: Doze");
          this.setCallback = setCallback;
          this.unsetCallback = unsetCallback;
          appContext.registerReceiver(this, filter);
          registered = true;
        } else {
          Timber.e("Already registered");
        }
      });
    }
  }

  @Override public void unregister() {
    handler.removeCallbacksAndMessages(null);
    if (isDozeAvailable()) {
      handler.post(() -> {
        if (registered) {
          Timber.d("Unregister new state observer");
          appContext.unregisterReceiver(this);
          this.setCallback = null;
          this.unsetCallback = null;
          registered = false;
        } else {
          Timber.e("Already unregistered");
        }
      });
    }
  }

  @Override public boolean is() {
    final android.os.PowerManager pm =
        (android.os.PowerManager) appContext.getSystemService(Context.POWER_SERVICE);
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
    if (isDozeAvailable()) {
      if (is()) {
        if (setCallback == null) {
          Timber.e("Received set change event with no callback");
        } else {
          setCallback.call();
        }
      } else {
        if (unsetCallback == null) {
          Timber.e("Received unset change event with no callback");
        } else {
          unsetCallback.call();
        }
      }
    }
  }
}
