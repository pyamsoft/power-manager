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
import com.pyamsoft.pydroid.util.NotificationUtil;

public final class MonitorService extends ServiceBase {

  private static final String TAG = MonitorService.class.getSimpleName();
  private static final String OTHER_CMD = MonitorService.class.getName() + ".other_cmd";
  private static final String NOTIFICATION_ON = MonitorService.class.getName() + ".notification_on";
  private static final String NOTIFICATION_OFF =
      MonitorService.class.getName() + ".notification_off";
  private ScreenStateReceiver screenStateReceiver;
  private BatteryStateReceiver batteryStateReceiver;
  private GlobalPreferenceUtil preferenceUtil;
  private PersistentNotification notification;

  public static void powerManagerService(final Context context) {
    final GlobalPreferenceUtil.PowerManagerMonitor p =
        GlobalPreferenceUtil.with(context).powerManagerMonitor();
    final boolean b = !p.isEnabled();
    if (b) {
      p.setEnabled(true);
      startService(context);
    } else {
      p.setEnabled(false);
      stopService(context);

      // Completely stop the service
      killService(context);
    }
  }

  public static void stopService(final Context context) {
    stopService(context, MonitorService.class);
  }

  public static void startService(final Context context) {
    startService(context, MonitorService.class);
  }

  public static void killService(final Context context) {
    killService(context, MonitorService.class);
  }

  public static void startPersistentNotification(final Context c) {
    final Context context = c.getApplicationContext();
    final Intent intent = new Intent(context, MonitorService.class);
    intent.putExtra(OTHER_CMD, NOTIFICATION_ON);
    context.startService(intent);
  }

  public static void stopPersistentNotification(final Context c) {
    final Context context = c.getApplicationContext();
    final Intent intent = new Intent(context, MonitorService.class);
    intent.putExtra(OTHER_CMD, NOTIFICATION_OFF);
    context.startService(intent);
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
    return preferenceUtil.powerManagerMonitor().isEnabled();
  }

  @Override public void onCreate() {
    super.onCreate();
    screenStateReceiver = new ScreenStateReceiver();
    batteryStateReceiver = new BatteryStateReceiver();
    preferenceUtil = GlobalPreferenceUtil.with(this);
    notification = PersistentNotification.with(this);
  }

  private void updatePersistentNotification(final Intent intent) {
    if (intent == null) {
      LogUtil.d(TAG, "Intent is NULL");
      return;
    }

    final String state = intent.getStringExtra(OTHER_CMD);
    if (state != null) {
      if (state.equals(NOTIFICATION_ON)) {
        LogUtil.d(TAG, "Start foreground notification");
        startForeground(PersistentNotification.ID, notification.notification());
      } else if (state.equals(NOTIFICATION_OFF)) {
        LogUtil.d(TAG, "Stop foreground notification");
        // Only remove the notification is it is also not enabled
        stopForeground(!preferenceUtil.powerManagerMonitor().isNotificationEnabled());
      } else {
        LogUtil.e(TAG, "Invalid command");
      }
    } else {
      LogUtil.e(TAG, "NULL command passed in notification update");
    }
  }

  @Override public int onStartCommand(Intent intent, int flags, int startId) {
    LogUtil.d(TAG, "onStartCommand");
    updatePersistentNotification(intent);
    return runServiceHook(intent);
  }

  @Override public void onDestroy() {
    super.onDestroy();
    serviceStopHook();
    screenStateReceiver = null;
    batteryStateReceiver = null;
    preferenceUtil = null;
    notification = null;
  }

  @Nullable @Override public IBinder onBind(Intent intent) {
    return null;
  }

  public static void updateNotification(final Context context) {
    final GlobalPreferenceUtil p = GlobalPreferenceUtil.with(context);
    // If user wants notification, figure out which notification
    if (p.powerManagerMonitor().isNotificationEnabled()) {
      if (p.powerManagerMonitor().isForeground()) {
        MonitorService.startPersistentNotification(context);
      } else {
        LogUtil.d(TAG, "Start normal Notification");
        NotificationUtil.start(context, PersistentNotification.with(context).notification(),
            PersistentNotification.ID);
      }
    } else {
      LogUtil.d(TAG, "Notification is not enabled, do nothing");
    }
  }
}

