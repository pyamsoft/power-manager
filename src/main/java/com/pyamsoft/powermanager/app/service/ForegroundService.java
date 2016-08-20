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
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.pyamsoft.powermanager.Singleton;
import com.pyamsoft.powermanager.app.receiver.ScreenOnOffReceiver;
import com.pyamsoft.powermanager.dagger.service.ForegroundPresenter;
import javax.inject.Inject;
import timber.log.Timber;

public class ForegroundService extends Service implements ForegroundPresenter.ForegroundProvider {

  @NonNull public static final String EXTRA_WEARABLE = "wearable";
  @NonNull public static final String EXTRA_WIFI = "wifi";
  @NonNull public static final String EXTRA_DATA = "data";
  @NonNull public static final String EXTRA_BLUETOOTH = "bluetooth";
  @NonNull public static final String EXTRA_SYNC = "sync";
  @NonNull public static final String EXTRA_NOTIFICATION = "notification";
  private static final int NOTIFICATION_ID = 1000;
  @Inject ForegroundPresenter presenter;
  private ScreenOnOffReceiver screenOnOffReceiver;

  @Nullable @Override public IBinder onBind(Intent intent) {
    return null;
  }

  @Override public void onCreate() {
    super.onCreate();
    Timber.d("onCreate");

    Singleton.Dagger.with(this).plusForegroundServiceComponent().inject(this);

    screenOnOffReceiver = new ScreenOnOffReceiver(this);
    screenOnOffReceiver.register();

    presenter.bindView(this);
  }

  @Override public void onDestroy() {
    super.onDestroy();
    Timber.d("onDestroy");

    screenOnOffReceiver.unregister();
    presenter.unbindView();

    stopForeground(true);
  }

  @Override public int onStartCommand(Intent intent, int flags, int startId) {
    Timber.d("onStartCommand");
    boolean explicit = false;
    if (intent != null) {
      if (intent.getBooleanExtra(EXTRA_WEARABLE, false)) {
        Timber.d("Update wearable status");
        presenter.updateWearableAction();
      }

      if (intent.getBooleanExtra(EXTRA_WIFI, false)) {
        Timber.d("Update wifi status");
        presenter.updateWifiAction();
      }

      if (intent.getBooleanExtra(EXTRA_DATA, false)) {
        Timber.d("Update data status");
        presenter.updateDataAction();
      }

      if (intent.getBooleanExtra(EXTRA_BLUETOOTH, false)) {
        Timber.d("Update bluetooth status");
        presenter.updateBluetoothAction();
      }

      if (intent.getBooleanExtra(EXTRA_SYNC, false)) {
        Timber.d("Update sync status");
        presenter.updateSyncAction();
      }

      if (intent.getBooleanExtra(EXTRA_NOTIFICATION, false)) {
        Timber.d("Explicit notification state");
        explicit = true;
      }
    }

    presenter.onStartNotification(explicit);
    return START_STICKY;
  }

  @Override public void startNotificationInForeground(@NonNull Notification notification) {
    startForeground(NOTIFICATION_ID, notification);
  }
}
