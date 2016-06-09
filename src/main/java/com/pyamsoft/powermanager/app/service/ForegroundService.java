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
import android.support.annotation.Nullable;
import com.pyamsoft.powermanager.PowerManager;
import com.pyamsoft.powermanager.app.receiver.ScreenOnOffReceiver;
import javax.inject.Inject;
import timber.log.Timber;

public class ForegroundService extends Service implements ForegroundPresenter.ForegroundProvider {

  private static final int NOTIFICATION_ID = 1000;
  @Nullable @Inject ForegroundPresenter presenter;
  @Nullable ScreenOnOffReceiver screenOnOffReceiver;

  @Nullable @Override public IBinder onBind(Intent intent) {
    return null;
  }

  @Override public void onCreate() {
    super.onCreate();

    screenOnOffReceiver = new ScreenOnOffReceiver(getApplication());
    screenOnOffReceiver.register();

    PowerManager.powerManagerComponent(this).inject(this);

    assert presenter != null;
    presenter.bindView(this);

    startForeground();
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
    return START_STICKY;
  }

  private void startForeground() {
    assert presenter != null;
    startForeground(NOTIFICATION_ID, presenter.createNotification());
  }
}
