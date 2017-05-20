/*
 * Copyright 2017 Peter Kenji Yamanaka
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

package com.pyamsoft.powermanager.service;

import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationManagerCompat;
import com.pyamsoft.powermanager.Injector;
import com.pyamsoft.powermanager.receiver.ScreenOnOffReceiver;
import com.pyamsoft.pydroid.ui.app.AutoRestartService;
import javax.inject.Inject;
import timber.log.Timber;

public class ForegroundService extends AutoRestartService {

  static final int NOTIFICATION_ID = 1000;
  @NonNull private static final String EXTRA_RESTART_TRIGGERS = "EXTRA_RESTART_TRIGGERS";
  @Inject ForegroundPresenter presenter;
  private ScreenOnOffReceiver screenOnOffReceiver;
  private NotificationManagerCompat notificationManager;

  /**
   * Force the service On
   */
  public static void start(@NonNull Context context) {
    context.getApplicationContext()
        .startService(new Intent(context.getApplicationContext(), ForegroundService.class));
  }

  /**
   * Force the service Off
   */
  public static void stop(@NonNull Context context) {
    context.getApplicationContext()
        .stopService(new Intent(context.getApplicationContext(), ForegroundService.class));
  }

  /**
   * Restart the triggers
   */
  public static void restartTriggers(@NonNull Context context) {
    final Context appContext = context.getApplicationContext();
    final Intent service = new Intent(appContext, ForegroundService.class);
    service.putExtra(EXTRA_RESTART_TRIGGERS, true);
    Timber.d("Restart Power Triggers");
    appContext.startService(service);
  }

  @Nullable @Override public IBinder onBind(Intent intent) {
    return null;
  }

  @Override public void onCreate() {
    super.onCreate();
    Injector.get().provideComponent().inject(this);

    notificationManager = NotificationManagerCompat.from(getApplicationContext());
    notificationManager.cancel(NOTIFICATION_ID);

    presenter.setForegroundState(true);
    presenter.queueRepeatingTriggerJob();

    screenOnOffReceiver = new ScreenOnOffReceiver(this);
    screenOnOffReceiver.register();
  }

  @Override public void onDestroy() {
    super.onDestroy();
    presenter.setForegroundState(false);
    screenOnOffReceiver.unregister();

    presenter.stop();
    presenter.destroy();

    stopForeground(true);

    notificationManager.notify(NOTIFICATION_ID, presenter.hangNotification());
  }

  @Override public int onStartCommand(Intent intent, int flags, int startId) {
    if (intent != null) {
      if (intent.getBooleanExtra(EXTRA_RESTART_TRIGGERS, false)) {
        presenter.restartTriggerAlarm();
      }
    }
    presenter.startNotification(notification -> startForeground(NOTIFICATION_ID, notification));
    return START_STICKY;
  }
}
