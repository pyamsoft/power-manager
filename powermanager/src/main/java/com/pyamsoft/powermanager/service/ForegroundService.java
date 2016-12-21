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

package com.pyamsoft.powermanager.service;

import android.app.Notification;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.pyamsoft.powermanagerpresenter.receiver.ScreenOnOffReceiver;
import com.pyamsoft.powermanagerpresenter.service.ForegroundPresenter;
import com.pyamsoft.powermanagerpresenter.service.ForegroundServiceLoader;
import timber.log.Timber;

public class ForegroundService extends Service implements ForegroundPresenter.ForegroundProvider {

  @NonNull private static final String EXTRA_SERVICE_ENABLED = "EXTRA_SERVICE_ENABLED";
  @NonNull private static final String EXTRA_RESTART_TRIGGERS = "EXTRA_RESTART_TRIGGERS";
  private static final int NOTIFICATION_ID = 1000;
  @Nullable private ForegroundPresenter presenter;
  @Nullable private ScreenOnOffReceiver screenOnOffReceiver;

  /**
   * Force the service into a state
   */
  private static void forceService(@NonNull Context context, boolean state) {
    final Context appContext = context.getApplicationContext();
    final Intent service = new Intent(appContext, ForegroundService.class);
    service.putExtra(EXTRA_SERVICE_ENABLED, state);

    Timber.d("Force service: %s", state);
    appContext.startService(service);
  }

  /**
   * Force the service On
   */
  public static void start(@NonNull Context context) {
    forceService(context, true);
  }

  /**
   * Force the service Off
   */
  public static void stop(@NonNull Context context) {
    forceService(context, false);
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
    if (screenOnOffReceiver == null) {
      screenOnOffReceiver = new ScreenOnOffReceiver(this);
    }
    if (presenter == null) {
      presenter = new ForegroundServiceLoader().loadPersistent();
    }
    presenter.bindView(this);
  }

  @Override public void onDestroy() {
    super.onDestroy();
    if (screenOnOffReceiver != null) {
      screenOnOffReceiver.unregister();
    }

    if (presenter != null) {
      presenter.unbindView();
      presenter.destroy();
    }

    stopForeground(true);
  }

  @Override public int onStartCommand(Intent intent, int flags, int startId) {
    if (presenter == null) {
      throw new IllegalStateException("Presenter is NULL, cannot onStartCommand");
    }

    if (intent != null) {
      if (intent.hasExtra(EXTRA_SERVICE_ENABLED)) {
        final boolean enable = intent.getBooleanExtra(EXTRA_SERVICE_ENABLED, true);
        processServiceEnableCommand(enable);
      } else if (intent.hasExtra(EXTRA_RESTART_TRIGGERS)) {
        final boolean restart = intent.getBooleanExtra(EXTRA_RESTART_TRIGGERS, true);
        processTriggerRestartCommand(restart);
      }
    }
    presenter.onStartNotification();
    return START_STICKY;
  }

  private void processTriggerRestartCommand(boolean restart) {
    if (presenter == null) {
      throw new IllegalStateException("Presenter is NULL, cannot process restart command");
    }

    if (restart) {
      presenter.restartTriggerAlarm();
    }
  }

  private void processServiceEnableCommand(boolean enable) {
    if (presenter == null) {
      throw new IllegalStateException("Presenter is NULL, cannot process service enable command");
    }

    if (screenOnOffReceiver == null) {
      throw new IllegalStateException("Screen receiver is null, cannot register");
    }

    presenter.setForegroundState(enable);
    if (enable) {
      Timber.i("Register SCREEN receiver");
      screenOnOffReceiver.register();
    } else {
      Timber.w("Unregister SCREEN receiver");
      screenOnOffReceiver.unregister();
    }
  }

  @Override public void startNotificationInForeground(@NonNull Notification notification) {
    startForeground(NOTIFICATION_ID, notification);
  }
}
