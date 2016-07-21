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
import com.pyamsoft.powermanager.app.service.FullNotificationActivity;
import com.pyamsoft.pydroid.util.AppUtil;
import javax.inject.Inject;
import rx.Observable;
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

  @NonNull @CheckResult private Observable<Boolean> isFullNotificationEnabled() {
    return Observable.defer(() -> Observable.just(preferences.isFullNotificationEnabled()));
  }

  @NonNull @Override public Observable<Notification> createNotification(boolean explicit) {
    return isFullNotificationEnabled().map(enabled -> {
      Intent intent;
      if (explicit || enabled) {
        intent = new Intent(appContext, FullNotificationActivity.class).setFlags(
            Intent.FLAG_ACTIVITY_SINGLE_TOP);
      } else {
        intent =
            new Intent(appContext, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
      }
      return intent;
    }).map(intent -> {
      final PendingIntent pendingIntent =
          PendingIntent.getActivity(appContext, PENDING_RC, intent, 0);
      final RemoteViews customRemoteView = createCustomRemoteViews();
      return new NotificationCompat.Builder(appContext).setContentTitle(
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
          .setCustomContentView(customRemoteView)
          .build();
    });
  }

  @CheckResult @NonNull private RemoteViews createCustomRemoteViews() {
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

    @DrawableRes final int wearIcon =
        preferences.isWearableManaged() ? R.drawable.ic_watch_24dp : R.drawable.ic_watch_off_24dp;
    @DrawableRes final int wifiIcon = preferences.isWifiManaged() ? R.drawable.ic_network_wifi_24dp
        : R.drawable.ic_signal_wifi_off_24dp;
    @DrawableRes final int dataIcon = preferences.isDataManaged() ? R.drawable.ic_network_cell_24dp
        : R.drawable.ic_signal_cellular_off_24dp;
    @DrawableRes final int bluetoothIcon =
        preferences.isBluetoothManaged() ? R.drawable.ic_bluetooth_24dp
            : R.drawable.ic_bluetooth_disabled_24dp;
    @DrawableRes final int syncIcon =
        preferences.isSyncManaged() ? R.drawable.ic_sync_24dp : R.drawable.ic_sync_disabled_24dp;

    AppUtil.setVectorIconForNotification(appContext, customView,
        R.id.remoteview_notification_wear_image, wearIcon);
    AppUtil.setVectorIconForNotification(appContext, customView,
        R.id.remoteview_notification_wifi_image, wifiIcon);
    AppUtil.setVectorIconForNotification(appContext, customView,
        R.id.remoteview_notification_data_image, dataIcon);
    AppUtil.setVectorIconForNotification(appContext, customView,
        R.id.remoteview_notification_bluetooth_image, bluetoothIcon);
    AppUtil.setVectorIconForNotification(appContext, customView,
        R.id.remoteview_notification_sync_image, syncIcon);

    customView.setOnClickPendingIntent(R.id.remoteview_notification_wear_touch, wearAction);
    customView.setOnClickPendingIntent(R.id.remoteview_notification_wifi_touch, wifiAction);
    customView.setOnClickPendingIntent(R.id.remoteview_notification_data_touch, dataAction);
    customView.setOnClickPendingIntent(R.id.remoteview_notification_bluetooth_touch,
        bluetoothAction);
    customView.setOnClickPendingIntent(R.id.remoteview_notification_sync_touch, syncAction);
    return customView;
  }

  @Override public void updateWearablePreferenceStatus() {
    // TODO should we talk to manager instead?
    final boolean state = preferences.isWearableManaged();
    preferences.setWearableManaged(!state);
    Timber.d("Update wearable managed from %s to %s", state, !state);
  }

  @Override public void updateWifiPreferenceStatus() {
    // TODO should we talk to manager instead?
    final boolean state = preferences.isWifiManaged();
    preferences.setWifiManaged(!state);
    Timber.d("Update wifi managed from %s to %s", state, !state);
  }

  @Override public void updateDataPreferenceStatus() {
    // TODO should we talk to manager instead?
    final boolean state = preferences.isDataManaged();
    preferences.setDataManaged(!state);
    Timber.d("Update data managed from %s to %s", state, !state);
  }

  @Override public void updateBluetoothPreferenceStatus() {
    // TODO should we talk to manager instead?
    final boolean state = preferences.isBluetoothManaged();
    preferences.setBluetoothManaged(!state);
    Timber.d("Update bluetooth managed from %s to %s", state, !state);
  }

  @Override public void updateSyncPreferenceStatus() {
    // TODO should we talk to manager instead?
    final boolean state = preferences.isSyncManaged();
    preferences.setSyncManaged(!state);
    Timber.d("Update sync managed from %s to %s", state, !state);
  }
}
