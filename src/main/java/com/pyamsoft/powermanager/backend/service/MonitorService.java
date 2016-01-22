/*
 * Copyright 2013 - 2016 Peter Kenji Yamanaka
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
package com.pyamsoft.powermanager.backend.service;

import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import com.pyamsoft.powermanager.R;
import com.pyamsoft.powermanager.backend.notification.PersistentNotification;
import com.pyamsoft.powermanager.backend.receiver.BatteryStateReceiver;
import com.pyamsoft.powermanager.backend.receiver.ScreenStateReceiver;
import com.pyamsoft.powermanager.backend.util.GlobalPreferenceUtil;
import com.pyamsoft.pydroid.base.ServiceBase;
import com.pyamsoft.pydroid.util.LogUtil;

public final class MonitorService extends ServiceBase {

  private static final String TAG = MonitorService.class.getSimpleName();
  private static boolean enabled;
  private ScreenStateReceiver screenStateReceiver;
  private BatteryStateReceiver batteryStateReceiver;
  private GlobalPreferenceUtil preferenceUtil;
  private PersistentNotification persistentNotification;

  public static void powerManagerService(final Context context) {
    if (enabled) {
      enabled = true;
      startService(context);
    } else {
      enabled = false;
      stopService(context);

      // Completely stop the service
      killService(context);
    }
  }

  @Override protected Class<? extends ServiceBase> getServiceClass() {
    return MonitorService.class;
  }

  @Override protected void serviceStartHook() {
    if (!screenStateReceiver.register(getApplicationContext())) {
      LogUtil.e(TAG, getString(R.string.screen_receiver_registered));
    }
    if (!batteryStateReceiver.register(getApplicationContext())) {
      LogUtil.e(TAG, getString(R.string.battery_state_register));
    }
  }

  @Override protected void serviceStopHook() {
    if (!screenStateReceiver.unregister(getApplicationContext())) {
      LogUtil.e(TAG, getString(R.string.screen_receiver_unregistered));
    }
    if (!batteryStateReceiver.unregister(getApplicationContext())) {
      LogUtil.e(TAG, getString(R.string.battery_state_unregister));
    }
  }

  @Override protected boolean isEnabled() {
    return enabled;
  }

  @Override public void onCreate() {
    super.onCreate();
    screenStateReceiver = new ScreenStateReceiver();
    batteryStateReceiver = new BatteryStateReceiver();
    preferenceUtil = GlobalPreferenceUtil.get();
    persistentNotification = PersistentNotification.get();
  }

  @Override public int onStartCommand(Intent intent, int flags, int startId) {
    return runServiceHook(intent, preferenceUtil.powerManagerMonitor().isNotificationEnabled(),
        preferenceUtil.powerManagerMonitor().isForeground(), persistentNotification.notification(),
        PersistentNotification.ID);
  }

  @Override public void onDestroy() {
    super.onDestroy();
    serviceStopHook();
    screenStateReceiver = null;
    batteryStateReceiver = null;
    preferenceUtil = null;
    persistentNotification = null;
  }

  @Nullable @Override public IBinder onBind(Intent intent) {
    return null;
  }
}

