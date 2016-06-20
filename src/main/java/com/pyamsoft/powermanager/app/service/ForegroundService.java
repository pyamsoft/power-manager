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

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.pyamsoft.powermanager.PowerManager;
import com.pyamsoft.powermanager.app.receiver.ScreenOnOffReceiver;
import javax.inject.Inject;
import timber.log.Timber;

public class ForegroundService extends Service implements ForegroundPresenter.ForegroundProvider {

  @NonNull public static final String EXTRA_WEARABLE = "wearable";
  @NonNull public static final String EXTRA_WIFI = "wifi";
  @NonNull public static final String EXTRA_DATA = "data";
  @NonNull public static final String EXTRA_BLUETOOTH = "bluetooth";
  @NonNull public static final String EXTRA_SYNC = "sync";
  private static final int NOTIFICATION_ID = 1000;
  @Nullable @Inject ForegroundPresenter presenter;
  @Nullable private ScreenOnOffReceiver screenOnOffReceiver;

  @Nullable @Override public IBinder onBind(Intent intent) {
    return null;
  }

  @Override public void onCreate() {
    super.onCreate();

    screenOnOffReceiver = new ScreenOnOffReceiver(getApplication());
    screenOnOffReceiver.register();

    PowerManager.getInstance().getPowerManagerComponent().inject(this);

    assert presenter != null;
    presenter.bindView(this);

    Timber.d("onCreate");
  }

  @Override public void onDestroy() {
    super.onDestroy();
    Timber.d("onDestroy");

    assert screenOnOffReceiver != null;
    screenOnOffReceiver.unregister();

    assert presenter != null;
    presenter.unbindView();

    stopForeground(true);
  }

  @Override public int onStartCommand(Intent intent, int flags, int startId) {
    Timber.d("onStartCommand");
    if (intent != null) {
      if (intent.getBooleanExtra(EXTRA_WEARABLE, false)) {
        Timber.d("Update wearable status");
        assert presenter != null;
        presenter.updateWearableAction();
      }

      if (intent.getBooleanExtra(EXTRA_WIFI, false)) {
        Timber.d("Update wifi status");
        assert presenter != null;
        presenter.updateWifiAction();
      }

      if (intent.getBooleanExtra(EXTRA_DATA, false)) {
        Timber.d("Update data status");
        assert presenter != null;
        presenter.updateDataAction();
      }

      if (intent.getBooleanExtra(EXTRA_BLUETOOTH, false)) {
        Timber.d("Update bluetooth status");
        assert presenter != null;
        presenter.updateBluetoothAction();
      }

      if (intent.getBooleanExtra(EXTRA_SYNC, false)) {
        Timber.d("Update sync status");
        assert presenter != null;
        presenter.updateSyncAction();
      }
    }
    startForeground();
    return START_STICKY;
  }

  private void startForeground() {
    assert presenter != null;
    startForeground(NOTIFICATION_ID, presenter.createNotification());
  }
}
