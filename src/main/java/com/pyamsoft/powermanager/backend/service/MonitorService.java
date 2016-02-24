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

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import com.pyamsoft.powermanager.backend.notification.PersistentNotification;
import com.pyamsoft.powermanager.backend.receiver.BatteryStateReceiver;
import com.pyamsoft.powermanager.backend.receiver.ScreenStateReceiver;
import com.pyamsoft.powermanager.backend.util.GlobalPreferenceUtil;
import com.pyamsoft.pydroid.base.AutoRestartServiceBase;
import com.pyamsoft.pydroid.util.LogUtil;

public final class MonitorService extends AutoRestartServiceBase {

  private static final String TAG = MonitorService.class.getSimpleName();
  private static final String COMMAND = MonitorService.class.getName() + ".COMMAND";
  private static final String CMD_SERVICE = COMMAND + ".SERVICE";
  private static final String CMD_NOTIFICATION = COMMAND + ".CMD_NOTIFICATION";
  private static final String NOTIFICATION = MonitorService.class.getName() + ".NOTIFICATION";
  private static final String NTF_ON = NOTIFICATION + ".NTF_ON";
  private static final String NTF_OFF = NOTIFICATION + ".NTF_OFF";
  private ScreenStateReceiver screenStateReceiver;
  private BatteryStateReceiver batteryStateReceiver;

  public static void start(final Context context) {
    final Intent intent = new Intent(context.getApplicationContext(), MonitorService.class);
    intent.putExtra(COMMAND, CMD_SERVICE);
    context.getApplicationContext().startService(intent);
  }

  public static void stop(final Context context) {
    final Intent intent = new Intent(context.getApplicationContext(), MonitorService.class);
    context.getApplicationContext().stopService(intent);
  }

  public static void startForeground(final Context context) {
    final Intent intent = new Intent(context.getApplicationContext(), MonitorService.class);
    intent.putExtra(COMMAND, CMD_NOTIFICATION);
    intent.putExtra(NOTIFICATION, NTF_ON);
    context.getApplicationContext().startService(intent);
  }

  public static void stopForeground(final Context context) {
    final Intent intent = new Intent(context.getApplicationContext(), MonitorService.class);
    intent.putExtra(COMMAND, CMD_NOTIFICATION);
    intent.putExtra(NOTIFICATION, NTF_OFF);
    context.getApplicationContext().startService(intent);
  }

  public static void launchPowerManagerService(final Context context) {
    if (context != null) {
      powerManagerService(context);
      updateNotification(context);
    }
  }

  public static void updateNotification(final Context context) {
    if (context != null) {
      final GlobalPreferenceUtil p = GlobalPreferenceUtil.with(context);
      if (p.powerManagerMonitor().isNotificationEnabled()) {
        if (p.powerManagerMonitor().isForeground()) {
          MonitorService.startForeground(context);
        } else {
          PersistentNotification.update(context);
        }
      }
    }
  }

  private static void powerManagerService(final Context context) {
    synchronized (MonitorService.class) {
      final GlobalPreferenceUtil.PowerManagerMonitor p =
          GlobalPreferenceUtil.with(context).powerManagerMonitor();
      final boolean b = !p.isEnabled();
      if (b) {
        p.setEnabled(true);
        p.setStartedByNotification(false);
        LogUtil.d(TAG, "Service starting normally");
        start(context);
      } else {
        p.setEnabled(false);
        p.setStartedByNotification(false);
        LogUtil.d(TAG, "Service stopping normally, clear all reasons");
        stop(context);
      }
    }
  }

  @Override protected Class<? extends AutoRestartServiceBase> getServiceClass() {
    return MonitorService.class;
  }

  @Nullable @Override public IBinder onBind(Intent intent) {
    return null;
  }

  @Override public void onCreate() {
    super.onCreate();
    LogUtil.d(TAG, "onCreate");
    screenStateReceiver = new ScreenStateReceiver();
    batteryStateReceiver = new BatteryStateReceiver();
    LogUtil.d(TAG, "Created");
  }

  @Override public void onDestroy() {
    super.onDestroy();
    LogUtil.d(TAG, "onDestroy");
    if (!batteryStateReceiver.unregister(getApplicationContext())) {
      LogUtil.e(TAG, "BatteryStateReceiver already unregistered");
    }
    if (!screenStateReceiver.unregister(getApplicationContext())) {
      LogUtil.e(TAG, "ScreenStateReceiver already unregistered");
    }
    screenStateReceiver = null;
    batteryStateReceiver = null;
    LogUtil.d(TAG, "Destroyed");
  }

  @Override public int onStartCommand(Intent intent, int flags, int startId) {
    LogUtil.d(TAG, "onStartCommand ID: ", startId);
    if (intent == null) {
      LogUtil.e(TAG, "Intent is NULL");
    } else {
      final String command = intent.getStringExtra(COMMAND);
      if (command == null) {
        LogUtil.e(TAG, "No command provided to service");
      } else {
        if (command.equals(CMD_SERVICE)) {
          serviceCommand();
        } else if (command.equals(CMD_NOTIFICATION)) {
          notificationCommand(intent);
        } else {
          LogUtil.e(TAG, "Invalid command passed to service: ", command);
        }
      }
    }
    return Service.START_STICKY;
  }

  private void serviceCommand() {
    LogUtil.d(TAG, "Performing Service command");
    if (!batteryStateReceiver.register(getApplicationContext())) {
      LogUtil.e(TAG, "BatteryStateReceiver already registered");
    }
    if (!screenStateReceiver.register(getApplicationContext())) {
      LogUtil.e(TAG, "ScreenStateReceiver already registered");
    }
  }

  private void notificationCommand(final Intent intent) {
    LogUtil.d(TAG, "Performing Notification command");
    final String notification = intent.getStringExtra(NOTIFICATION);
    if (notification == null) {
      LogUtil.e(TAG, "No command provided to notification");
    } else {
      if (notification.equals(NTF_ON)) {
        LogUtil.d(TAG, "Start notification in foreground");
        startForeground(PersistentNotification.ID,
            PersistentNotification.with(getApplicationContext()).notification());
      } else if (notification.equals(NTF_OFF)) {
        final boolean keepNotification = GlobalPreferenceUtil.with(getApplicationContext())
            .powerManagerMonitor()
            .isNotificationEnabled();
        LogUtil.d(TAG, "Stop foreground notification. Keep notification: ", keepNotification);
        stopForeground(!keepNotification);
      } else {
        LogUtil.e(TAG, "Invalid command passed to notification: ", notification);
      }
    }
  }
}

