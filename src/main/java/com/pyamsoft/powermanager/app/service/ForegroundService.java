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

package com.pyamsoft.powermanager.app.service;

import android.app.Notification;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.pyamsoft.powermanager.PowerManager;
import com.pyamsoft.powermanager.app.receiver.ScreenOnOffReceiver;
import javax.inject.Inject;
import timber.log.Timber;

public class ForegroundService extends Service implements ForegroundPresenter.ForegroundProvider {

  @NonNull public static final String POWER_MANAGER_SERVICE_ENABLED =
      "POWER_MANAGER_SERVICE_ENABLED";
  private static final int NOTIFICATION_ID = 1000;
  private static boolean enabled;
  @Inject ForegroundPresenter presenter;
  private ScreenOnOffReceiver screenOnOffReceiver;

  @CheckResult public static boolean isEnabled() {
    return enabled;
  }

  private static void setEnabled(boolean enabled) {
    ForegroundService.enabled = enabled;
  }

  /**
   * Force the service into a state
   */
  private static void forceService(@NonNull Context context, boolean state) {
    final Context appContext = context.getApplicationContext();
    final Intent service = new Intent(appContext, ForegroundService.class);
    service.putExtra(POWER_MANAGER_SERVICE_ENABLED, state);

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

  @Nullable @Override public IBinder onBind(Intent intent) {
    return null;
  }

  @Override public void onCreate() {
    super.onCreate();
    Timber.d("onCreate");

    PowerManager.get(getApplicationContext())
        .provideComponent()
        .plusForegroundServiceComponent()
        .inject(this);

    setEnabled(false);
    screenOnOffReceiver = new ScreenOnOffReceiver(this);
    presenter.bindView(this);
  }

  @Override public void onDestroy() {
    super.onDestroy();
    Timber.d("onDestroy");

    setEnabled(false);
    screenOnOffReceiver.unregister();
    presenter.unbindView();
    presenter.destroy();

    stopForeground(true);
  }

  @Override public int onStartCommand(Intent intent, int flags, int startId) {
    if (intent != null) {
      if (intent.hasExtra(POWER_MANAGER_SERVICE_ENABLED)) {
        final boolean enable = intent.getBooleanExtra(POWER_MANAGER_SERVICE_ENABLED, true);
        if (enable) {
          setEnabled(true);
          Timber.i("Register SCREEN receiver");
          screenOnOffReceiver.register();
        } else {
          setEnabled(false);
          Timber.w("Unregister SCREEN receiver");
          screenOnOffReceiver.unregister();
        }
      }
    }
    presenter.onStartNotification();
    return START_STICKY;
  }

  @Override public void startNotificationInForeground(@NonNull Notification notification) {
    startForeground(NOTIFICATION_ID, notification);
  }
}
