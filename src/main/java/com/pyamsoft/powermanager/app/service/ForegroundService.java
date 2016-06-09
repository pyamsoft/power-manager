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

import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.NotificationCompat;
import com.pyamsoft.powermanager.R;
import com.pyamsoft.powermanager.app.main.MainActivity;
import timber.log.Timber;

public class ForegroundService extends Service {

  @Nullable @Override public IBinder onBind(Intent intent) {
    return null;
  }

  @Override public void onCreate() {
    super.onCreate();
    Timber.d("onCreate");
  }

  @Override public void onDestroy() {
    super.onDestroy();
    Timber.d("onDestroy");
    stopForeground(true);
  }

  @Override public int onStartCommand(Intent intent, int flags, int startId) {
    Timber.d("onStartCommand");
    startForeground(1000, new NotificationCompat.Builder(this).setOngoing(true)
        .setNumber(0)
        .setAutoCancel(false)
        .setWhen(0)
        .setColor(ContextCompat.getColor(this, R.color.amber500))
        .setContentIntent(
            PendingIntent.getActivity(this, 1000, new Intent(this, MainActivity.class), 0))
        .setPriority(NotificationCompat.PRIORITY_MIN)
        .setSmallIcon(R.mipmap.ic_launcher)
        .setContentTitle(getString(R.string.app_name))
        .setContentInfo("Tap to launch application")
        .setTicker("Tap to launch application")
        .build());
    return START_STICKY;
  }
}
