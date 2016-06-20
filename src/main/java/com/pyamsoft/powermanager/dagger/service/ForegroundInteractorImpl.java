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

package com.pyamsoft.powermanager.dagger.service;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.CheckResult;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.widget.RemoteViews;
import com.pyamsoft.powermanager.PowerManagerPreferences;
import com.pyamsoft.powermanager.R;
import com.pyamsoft.powermanager.app.main.MainActivity;
import com.pyamsoft.powermanager.app.service.ForegroundService;
import javax.inject.Inject;
import timber.log.Timber;

final class ForegroundInteractorImpl implements ForegroundInteractor {

  private static final int PENDING_RC = 1004;
  @NonNull private final Context appContext;
  @NonNull private final PowerManagerPreferences preferences;

  @Inject ForegroundInteractorImpl(@NonNull Context context,
      @NonNull PowerManagerPreferences preferences) {
    this.preferences = preferences;
    this.appContext = context.getApplicationContext();
  }

  @NonNull @Override public Notification createNotification() {
    final Intent intent = new Intent(appContext, MainActivity.class);
    final PendingIntent pendingIntent =
        PendingIntent.getActivity(appContext, PENDING_RC, intent, 0);
    final RemoteViews customRemoteView = createCustomRemoteViews();
    final Notification notification = new NotificationCompat.Builder(appContext).setContentTitle(
        appContext.getString(R.string.app_name))
        .setSmallIcon(R.drawable.ic_notification)
        .setColor(ContextCompat.getColor(appContext, R.color.amber500))
        .setContentText("Managing Power...")
        .setWhen(0)
        .setOngoing(true)
        .setAutoCancel(false)
        .setNumber(0)
        .setContentIntent(pendingIntent)
        .setPriority(preferences.getNotificationPriority())
        .build();
    notification.contentView = customRemoteView;
    notification.bigContentView = customRemoteView;
    return notification;
  }

  @CheckResult @NonNull final RemoteViews createCustomRemoteViews() {
    final RemoteViews customView =
        new RemoteViews(appContext.getPackageName(), R.layout.remoteview_notification);

    final Intent wearIntent =
        new Intent(appContext, ForegroundService.class).putExtra(ForegroundService.EXTRA_WEARABLE,
            true);
    final Intent wifiIntent =
        new Intent(appContext, ForegroundService.class).putExtra(ForegroundService.EXTRA_WIFI,
            true);
    final Intent dataIntent =
        new Intent(appContext, ForegroundService.class).putExtra(ForegroundService.EXTRA_DATA,
            true);
    final Intent bluetoothIntent =
        new Intent(appContext, ForegroundService.class).putExtra(ForegroundService.EXTRA_BLUETOOTH,
            true);
    final Intent syncIntent =
        new Intent(appContext, ForegroundService.class).putExtra(ForegroundService.EXTRA_SYNC,
            true);
    final PendingIntent wearAction =
        PendingIntent.getService(appContext, PENDING_RC + 4, wearIntent, 0);
    final PendingIntent wifiAction =
        PendingIntent.getService(appContext, PENDING_RC + 5, wifiIntent, 0);
    final PendingIntent dataAction =
        PendingIntent.getService(appContext, PENDING_RC + 6, dataIntent, 0);
    final PendingIntent bluetoothAction =
        PendingIntent.getService(appContext, PENDING_RC + 7, bluetoothIntent, 0);
    final PendingIntent syncAction =
        PendingIntent.getService(appContext, PENDING_RC + 8, syncIntent, 0);

    @DrawableRes final int wearIcon = preferences.isWearableManaged() ? R.drawable.ic_watch_24dp
        : R.drawable.ic_watch_off_24dp;
    @DrawableRes final int wifiIcon = preferences.isWifiManaged() ? R.drawable.ic_network_wifi_24dp
        : R.drawable.ic_signal_wifi_off_24dp;
    @DrawableRes final int dataIcon = preferences.isDataManaged() ? R.drawable.ic_network_cell_24dp
        : R.drawable.ic_signal_cellular_off_24dp;
    @DrawableRes final int bluetoothIcon =
        preferences.isBluetoothManaged() ? R.drawable.ic_bluetooth_24dp
            : R.drawable.ic_bluetooth_disabled_24dp;
    @DrawableRes final int syncIcon =
        preferences.isSyncManaged() ? R.drawable.ic_sync_24dp : R.drawable.ic_sync_disabled_24dp;

    customView.setImageViewResource(R.id.remoteview_notification_wear_image, wearIcon);
    customView.setImageViewResource(R.id.remoteview_notification_wifi_image, wifiIcon);
    customView.setImageViewResource(R.id.remoteview_notification_data_image, dataIcon);
    customView.setImageViewResource(R.id.remoteview_notification_bluetooth_image, bluetoothIcon);
    customView.setImageViewResource(R.id.remoteview_notification_sync_image, syncIcon);

    customView.setOnClickPendingIntent(R.id.remoteview_notification_wear_touch, wearAction);
    customView.setOnClickPendingIntent(R.id.remoteview_notification_wifi_touch, wifiAction);
    customView.setOnClickPendingIntent(R.id.remoteview_notification_data_touch, dataAction);
    customView.setOnClickPendingIntent(R.id.remoteview_notification_bluetooth_touch,
        bluetoothAction);
    customView.setOnClickPendingIntent(R.id.remoteview_notification_sync_touch, syncAction);
    return customView;
  }

  @Override public void updateWearablePreferenceStatus() {
    final boolean state = preferences.isWearableManaged();
    preferences.setWearableManaged(!state);
    Timber.d("Update wearable managed from %s to %s", state, !state);
  }

  @Override public void updateWifiPreferenceStatus() {
    final boolean state = preferences.isWifiManaged();
    preferences.setWifiManaged(!state);
    Timber.d("Update wifi managed from %s to %s", state, !state);
  }

  @Override public void updateDataPreferenceStatus() {
    final boolean state = preferences.isDataManaged();
    preferences.setDataManaged(!state);
    Timber.d("Update data managed from %s to %s", state, !state);
  }

  @Override public void updateBluetoothPreferenceStatus() {
    final boolean state = preferences.isBluetoothManaged();
    preferences.setBluetoothManaged(!state);
    Timber.d("Update bluetooth managed from %s to %s", state, !state);
  }

  @Override public void updateSyncPreferenceStatus() {
    final boolean state = preferences.isSyncManaged();
    preferences.setSyncManaged(!state);
    Timber.d("Update sync managed from %s to %s", state, !state);
  }
}
