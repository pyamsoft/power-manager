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
import android.content.SharedPreferences;
import android.os.IBinder;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.preference.PreferenceManager;
import com.pyamsoft.powermanager.PowerManager;
import com.pyamsoft.powermanager.app.receiver.ScreenOnOffReceiver;
import javax.inject.Inject;
import timber.log.Timber;

public class ForegroundService extends Service implements ForegroundPresenter.ForegroundProvider {

  // KLUDGE: Raw preference access from service
  @NonNull public static final String POWER_MANAGER_SERVICE_ENABLED =
      "POWER_MANAGER_SERVICE_ENABLED";
  private static final int NOTIFICATION_ID = 1000;
  @Inject ForegroundPresenter presenter;
  private ScreenOnOffReceiver screenOnOffReceiver;

  // KLUDGE: Raw preference access from service
  @CheckResult @NonNull private static SharedPreferences getPreferences(@NonNull Context context) {
    final Context appContext = context.getApplicationContext();
    return PreferenceManager.getDefaultSharedPreferences(appContext);
  }

  /**
   * Get enabled state of the service, true by default
   */
  // KLUDGE: Raw preference access from service
  @CheckResult public static boolean isEnabled(@NonNull Context context) {
    return getPreferences(context).getBoolean(POWER_MANAGER_SERVICE_ENABLED, true);
  }

  /**
   * Force the service into a state
   */
  // KLUDGE: Raw preference access from service
  private static void forceService(@NonNull Context context, boolean state) {
    final Context appContext = context.getApplicationContext();
    final Intent service = new Intent(appContext, ForegroundService.class);
    if (state) {
      Timber.d("Starting PowerManager service");
      appContext.startService(service);
    } else {
      Timber.w("Stopping PowerManager service");
      appContext.stopService(service);
    }
  }

  /**
   * Force the service On
   */
  public static void start(@NonNull Context context) {
    if (isEnabled(context)) {
      forceService(context, true);
    }
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

    if (presenter == null) {
      PowerManager.get(getApplicationContext())
          .provideComponent()
          .plusForegroundServiceComponent()
          .inject(this);
    }

    if (screenOnOffReceiver == null) {
      screenOnOffReceiver = new ScreenOnOffReceiver(this);
    }

    screenOnOffReceiver.register();
    presenter.bindView(this);
  }

  @Override public void onDestroy() {
    super.onDestroy();
    Timber.d("onDestroy");

    screenOnOffReceiver.unregister();
    presenter.unbindView();
    presenter.destroy();

    stopForeground(true);
  }

  @Override public int onStartCommand(Intent intent, int flags, int startId) {
    presenter.onStartNotification();
    return START_STICKY;
  }

  @Override public void startNotificationInForeground(@NonNull Notification notification) {
    startForeground(NOTIFICATION_ID, notification);
  }
}
