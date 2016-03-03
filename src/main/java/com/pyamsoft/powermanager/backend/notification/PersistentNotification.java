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

package com.pyamsoft.powermanager.backend.notification;

import android.app.IntentService;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.NotificationCompat;
import android.widget.RemoteViews;
import com.pyamsoft.powermanager.R;
import com.pyamsoft.powermanager.backend.manager.ManagerBluetooth;
import com.pyamsoft.powermanager.backend.manager.ManagerData;
import com.pyamsoft.powermanager.backend.manager.ManagerSync;
import com.pyamsoft.powermanager.backend.manager.ManagerWifi;
import com.pyamsoft.powermanager.backend.service.MonitorService;
import com.pyamsoft.powermanager.backend.util.GlobalPreferenceUtil;
import com.pyamsoft.powermanager.ui.MainActivity;
import com.pyamsoft.pydroid.util.LogUtil;
import com.pyamsoft.pydroid.util.NotificationUtil;

public final class PersistentNotification {

  public static final int ID = NotificationUtil.BASE_ID + 5;
  private static final int RQ = NotificationUtil.BASE_RQ + 3;
  private static final String TAG = PersistentNotification.class.getSimpleName();
  private static volatile PersistentNotification instance = null;

  private final NotificationCompat.Builder builder;
  private final GlobalPreferenceUtil preferenceUtil;
  private final PendingIntent toggle;
  private final PendingIntent wifi;
  private final PendingIntent data;
  private final PendingIntent bluetooth;
  private final PendingIntent sync;
  private final RemoteViews remoteViews;

  private PersistentNotification(final Context c) {
    LogUtil.d(TAG, "Initialize PersistentNotification");
    final Context context = c.getApplicationContext();
    builder = new NotificationCompat.Builder(context);
    preferenceUtil = GlobalPreferenceUtil.with(context);

        /* Pending intents for click */
    toggle = PendingIntent.getService(context, RQ + 1, new Intent(context, Toggle.class),
        PendingIntent.FLAG_UPDATE_CURRENT);
    wifi = PendingIntent.getService(context, RQ + 2, new Intent(context, ManagerWifi.Toggle.class),
        PendingIntent.FLAG_UPDATE_CURRENT);
    data = PendingIntent.getService(context, RQ + 3, new Intent(context, ManagerData.Toggle.class),
        PendingIntent.FLAG_UPDATE_CURRENT);
    bluetooth = PendingIntent.getService(context, RQ + 4,
        new Intent(context, ManagerBluetooth.Toggle.class), PendingIntent.FLAG_UPDATE_CURRENT);
    sync = PendingIntent.getService(context, RQ + 5, new Intent(context, ManagerSync.Toggle.class),
        PendingIntent.FLAG_UPDATE_CURRENT);
    final PendingIntent main =
        PendingIntent.getActivity(context, RQ, new Intent(context, MainActivity.class),
            PendingIntent.FLAG_UPDATE_CURRENT);
    remoteViews =
        new RemoteViews(context.getApplicationContext().getPackageName(), R.layout.notification);
    builder.setContentTitle(context.getString(R.string.app_name))
        .setSmallIcon(R.drawable.ic_settings_white_24dp)
        .setNumber(0)
        .setWhen(0)
        .setPriority(NotificationCompat.PRIORITY_MIN)
        .setOngoing(true)
        .setContentIntent(main);
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      builder.setColor(ContextCompat.getColor(context, R.color.amber700));
    }
  }

  public static PersistentNotification with(final Context context) {
    if (instance == null) {
      synchronized (PersistentNotification.class) {
        if (instance == null) {
          instance = new PersistentNotification(context);
        }
      }
    }
    return instance;
  }

  public static void update(final Context context) {
    LogUtil.d(TAG, "Update persistent notification");
    NotificationUtil.start(context, PersistentNotification.with(context).notification(),
        PersistentNotification.ID);
  }

  private int getToggle() {
    return preferenceUtil.powerManagerMonitor().isEnabled() ? R.drawable.ic_pause_compat
        : R.drawable.ic_play_arrow_compat;
  }

  private int getWifi() {
    return preferenceUtil.powerManagerActive().isManagedWifi()
        ? R.drawable.ic_network_wifi_white_24dp : R.drawable.ic_signal_wifi_off_white_24dp;
  }

  private int getData() {
    return preferenceUtil.powerManagerActive().isManagedData()
        ? R.drawable.ic_network_cell_white_24dp : R.drawable.ic_signal_cellular_off_white_24dp;
  }

  private int getBluetooth() {
    return preferenceUtil.powerManagerActive().isManagedBluetooth() ? R.drawable.ic_bluetooth_compat
        : R.drawable.ic_bluetooth_disabled_compat;
  }

  private int getSync() {
    return preferenceUtil.powerManagerActive().isManagedSync() ? R.drawable.ic_sync_white_24dp
        : R.drawable.ic_sync_disabled_white_24dp;
  }

  private void setupRemoteViews() {
    remoteViews.setOnClickPendingIntent(R.id.remoteViewToggle, toggle);
    remoteViews.setOnClickPendingIntent(R.id.remoteViewWifi, wifi);
    remoteViews.setOnClickPendingIntent(R.id.remoteViewData, data);
    remoteViews.setOnClickPendingIntent(R.id.remoteViewBluetooth, bluetooth);
    remoteViews.setOnClickPendingIntent(R.id.remoteViewSync, sync);

    remoteViews.setImageViewResource(R.id.remoteViewToggleImg, getToggle());
    remoteViews.setImageViewResource(R.id.remoteViewWifiImg, getWifi());
    remoteViews.setImageViewResource(R.id.remoteViewDataImg, getData());
    remoteViews.setImageViewResource(R.id.remoteViewBluetoothImg, getBluetooth());
    remoteViews.setImageViewResource(R.id.remoteViewSyncImg, getSync());
  }

  public final Notification notification() {
    setupRemoteViews();
    builder.setContent(remoteViews);
    return builder.build();
  }

  public static final class Toggle extends IntentService {

    public Toggle() {
      super(Toggle.class.getName());
    }

    @Override protected void onHandleIntent(Intent intent) {
      MonitorService.launchPowerManagerService(getApplicationContext());
    }
  }
}
