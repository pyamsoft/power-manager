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
import android.os.Build;
import android.support.annotation.CheckResult;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.RemoteViews;
import com.birbit.android.jobqueue.JobManager;
import com.birbit.android.jobqueue.TagConstraint;
import com.pyamsoft.powermanager.PowerManagerPreferences;
import com.pyamsoft.powermanager.R;
import com.pyamsoft.powermanager.app.main.MainActivity;
import com.pyamsoft.powermanager.app.modifier.BooleanInterestModifier;
import com.pyamsoft.powermanager.app.observer.BooleanInterestObserver;
import com.pyamsoft.powermanager.app.service.ForegroundService;
import com.pyamsoft.powermanager.app.service.FullNotificationActivity;
import com.pyamsoft.powermanager.dagger.job.TriggerJob;
import com.pyamsoft.powermanager.dagger.wrapper.JobSchedulerCompat;
import com.pyamsoft.pydroid.util.AppUtil;
import javax.inject.Inject;
import rx.Observable;
import timber.log.Timber;

class ForegroundInteractorImpl implements ForegroundInteractor {

  private static final int PENDING_RC = 1004;
  @SuppressWarnings("WeakerAccess") @NonNull final PowerManagerPreferences preferences;
  @NonNull private final BooleanInterestObserver wifiManageObserver;
  @NonNull private final BooleanInterestObserver dataManageObserver;
  @NonNull private final BooleanInterestObserver bluetoothManageObserver;
  @NonNull private final BooleanInterestObserver syncManageObserver;
  @NonNull private final BooleanInterestObserver wearManageObserver;
  @NonNull private final BooleanInterestObserver dozeManageObserver;
  @NonNull private final BooleanInterestModifier wifiManageModifier;
  @NonNull private final BooleanInterestModifier dataManageModifier;
  @NonNull private final BooleanInterestModifier bluetoothManageModifier;
  @NonNull private final BooleanInterestModifier syncManageModifier;
  @NonNull private final BooleanInterestModifier wearManageModifier;
  @NonNull private final BooleanInterestModifier dozeManageModifier;
  @NonNull private final Context appContext;
  @NonNull private final JobSchedulerCompat jobManager;

  @Inject ForegroundInteractorImpl(@NonNull JobSchedulerCompat jobManager, @NonNull Context context,
      @NonNull PowerManagerPreferences preferences,
      @NonNull BooleanInterestObserver wifiManageObserver,
      @NonNull BooleanInterestObserver dataManageObserver,
      @NonNull BooleanInterestObserver bluetoothManageObserver,
      @NonNull BooleanInterestObserver syncManageObserver,
      @NonNull BooleanInterestObserver wearManageObserver,
      @NonNull BooleanInterestObserver dozeManageObserver,
      @NonNull BooleanInterestModifier wifiManageModifier,
      @NonNull BooleanInterestModifier dataManageModifier,
      @NonNull BooleanInterestModifier bluetoothManageModifier,
      @NonNull BooleanInterestModifier syncManageModifier,
      @NonNull BooleanInterestModifier wearManageModifier,
      @NonNull BooleanInterestModifier dozeManageModifier) {
    this.jobManager = jobManager;
    this.dozeManageObserver = dozeManageObserver;
    this.wifiManageModifier = wifiManageModifier;
    this.dataManageModifier = dataManageModifier;
    this.bluetoothManageModifier = bluetoothManageModifier;
    this.syncManageModifier = syncManageModifier;
    this.wearManageModifier = wearManageModifier;
    this.dozeManageModifier = dozeManageModifier;
    this.wifiManageObserver = wifiManageObserver;
    this.dataManageObserver = dataManageObserver;
    this.bluetoothManageObserver = bluetoothManageObserver;
    this.syncManageObserver = syncManageObserver;
    this.wearManageObserver = wearManageObserver;
    this.appContext = context.getApplicationContext();
    this.preferences = preferences;
  }

  @Override public void create() {
    // For now, trigger every 5 minutes
    TriggerJob.queue(jobManager, new TriggerJob(5 * 60 * 1000));
  }

  @Override public void destroy() {
    Timber.d("Cancel all trigger jobs");
    jobManager.cancelJobsInBackground(TagConstraint.ANY, TriggerJob.TRIGGER_TAG);
  }

  @NonNull @CheckResult private Observable<Boolean> isFullNotificationEnabled() {
    return Observable.defer(() -> Observable.just(preferences.isFullNotificationEnabled()));
  }

  @NonNull @CheckResult private Observable<Integer> getNotificationPriority() {
    return Observable.defer(() -> Observable.just(preferences.getNotificationPriority()));
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
          .setCustomContentView(customRemoteView);
    }).zipWith(getNotificationPriority(), (builder, priority) -> {
      return builder.setPriority(priority).build();
    });
  }

  @SuppressWarnings("WeakerAccess") @CheckResult @NonNull RemoteViews createCustomRemoteViews() {
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
    final Intent dozeIntent =
        new Intent(appContext, ForegroundService.class).putExtra(ForegroundService.EXTRA_DOZE,
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
    final PendingIntent dozeAction =
        PendingIntent.getService(appContext, PENDING_RC + 9, dozeIntent, 0);

    @DrawableRes final int wearIcon =
        wearManageObserver.is() ? R.drawable.ic_watch_24dp : R.drawable.ic_watch_off_24dp;
    @ColorRes final int wearIconTint =
        wearManageObserver.is() ? android.R.color.white : R.color.grey500;
    @DrawableRes final int wifiIcon = wifiManageObserver.is() ? R.drawable.ic_network_wifi_24dp
        : R.drawable.ic_signal_wifi_off_24dp;
    @ColorRes final int wifiIconTint =
        wifiManageObserver.is() ? android.R.color.white : R.color.grey500;
    @DrawableRes final int dataIcon = dataManageObserver.is() ? R.drawable.ic_network_cell_24dp
        : R.drawable.ic_signal_cellular_off_24dp;
    @ColorRes final int dataIconTint =
        dataManageObserver.is() ? android.R.color.white : R.color.grey500;
    @DrawableRes final int bluetoothIcon =
        bluetoothManageObserver.is() ? R.drawable.ic_bluetooth_24dp
            : R.drawable.ic_bluetooth_disabled_24dp;
    @ColorRes final int bluetoothIconTint =
        bluetoothManageObserver.is() ? android.R.color.white : R.color.grey500;
    @DrawableRes final int syncIcon =
        syncManageObserver.is() ? R.drawable.ic_sync_24dp : R.drawable.ic_sync_disabled_24dp;
    @ColorRes final int syncIconTint =
        syncManageObserver.is() ? android.R.color.white : R.color.grey500;
    @ColorRes final int dozeIconTint =
        dozeManageObserver.is() ? android.R.color.white : R.color.grey500;

    AppUtil.setVectorIconForNotification(appContext, customView,
        R.id.remoteview_notification_wear_image, wearIcon, wearIconTint);
    AppUtil.setVectorIconForNotification(appContext, customView,
        R.id.remoteview_notification_wifi_image, wifiIcon, wifiIconTint);
    AppUtil.setVectorIconForNotification(appContext, customView,
        R.id.remoteview_notification_data_image, dataIcon, dataIconTint);
    AppUtil.setVectorIconForNotification(appContext, customView,
        R.id.remoteview_notification_bluetooth_image, bluetoothIcon, bluetoothIconTint);
    AppUtil.setVectorIconForNotification(appContext, customView,
        R.id.remoteview_notification_sync_image, syncIcon, syncIconTint);
    AppUtil.setVectorIconForNotification(appContext, customView,
        R.id.remoteview_notification_doze_image, R.drawable.ic_doze_24dp, dozeIconTint);

    customView.setOnClickPendingIntent(R.id.remoteview_notification_wear_touch, wearAction);
    customView.setOnClickPendingIntent(R.id.remoteview_notification_wifi_touch, wifiAction);
    customView.setOnClickPendingIntent(R.id.remoteview_notification_data_touch, dataAction);
    customView.setOnClickPendingIntent(R.id.remoteview_notification_bluetooth_touch,
        bluetoothAction);
    customView.setOnClickPendingIntent(R.id.remoteview_notification_sync_touch, syncAction);
    customView.setOnClickPendingIntent(R.id.remoteview_notification_doze_touch, dozeAction);

    if (Build.VERSION.SDK_INT != Build.VERSION_CODES.M) {
      Timber.d("Hide doze button in notification");
      customView.setViewVisibility(R.id.remoteview_notification_doze_touch, View.GONE);
    }

    return customView;
  }

  @Override public void updateWearablePreferenceStatus() {
    final boolean state = wearManageObserver.is();
    if (state) {
      wearManageModifier.unset();
    } else {
      wearManageModifier.set();
    }
    Timber.d("Update wearable managed from %s to %s", state, !state);
  }

  @Override public void updateWifiPreferenceStatus() {
    final boolean state = wifiManageObserver.is();
    if (state) {
      wifiManageModifier.unset();
    } else {
      wifiManageModifier.set();
    }
    Timber.d("Update wifi managed from %s to %s", state, !state);
  }

  @Override public void updateDataPreferenceStatus() {
    final boolean state = dataManageObserver.is();
    if (state) {
      dataManageModifier.unset();
    } else {
      dataManageModifier.set();
    }
    Timber.d("Update data managed from %s to %s", state, !state);
  }

  @Override public void updateBluetoothPreferenceStatus() {
    final boolean state = bluetoothManageObserver.is();
    if (state) {
      bluetoothManageModifier.unset();
    } else {
      bluetoothManageModifier.set();
    }
    Timber.d("Update bluetooth managed from %s to %s", state, !state);
  }

  @Override public void updateSyncPreferenceStatus() {
    final boolean state = syncManageObserver.is();
    if (state) {
      syncManageModifier.unset();
    } else {
      syncManageModifier.set();
    }
    Timber.d("Update sync managed from %s to %s", state, !state);
  }

  @Override public void updateDozePreferenceStatus() {
    final boolean state = dozeManageObserver.is();
    if (state) {
      dozeManageModifier.unset();
    } else {
      dozeManageModifier.set();
    }
    Timber.d("Update doze managed from %s to %s", state, !state);
  }
}
