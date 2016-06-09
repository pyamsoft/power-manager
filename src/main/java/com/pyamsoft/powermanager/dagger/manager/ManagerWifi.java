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

package com.pyamsoft.powermanager.dagger.manager;

import android.app.Application;
import android.content.Context;
import android.net.wifi.WifiManager;
import android.support.annotation.NonNull;
import com.birbit.android.jobqueue.Params;
import com.pyamsoft.powermanager.PowerManager;
import com.pyamsoft.powermanager.PowerManagerPreferences;
import javax.inject.Inject;
import timber.log.Timber;

final class ManagerWifi extends ManagerBase {

  @NonNull private static final String TAG = "wifi_manager_job";
  @NonNull private final WifiManager androidWifiManager;
  @NonNull private final PowerManagerPreferences preferences;

  @Inject ManagerWifi(@NonNull WifiManager androidWifiManager,
      @NonNull PowerManagerPreferences preferences) {
    Timber.d("new ManagerWifi");
    this.androidWifiManager = androidWifiManager;
    this.preferences = preferences;
  }

  @Override public void enable(@NonNull Application application) {
    enable(application, 0);
  }

  @Override public void enable(@NonNull Application application, long time) {
    PowerManager.getJobManager(application)
        .addJobInBackground(new Job(application, time, DeviceJob.JOB_TYPE_ENABLE));
  }

  @Override public void disable(@NonNull Application application) {
    disable(application, preferences.getWifiDelay() * 1000L);
  }

  @Override public void disable(@NonNull Application application, long time) {
    PowerManager.getJobManager(application)
        .addJobInBackground(new Job(application, time, DeviceJob.JOB_TYPE_DISABLE));
  }

  @Override public boolean isEnabled() {
    return androidWifiManager.isWifiEnabled();
  }

  static final class Job extends DeviceJob {

    protected Job(@NonNull Context context, long delayTime, int jobType) {
      super(context, new Params(PRIORITY).setGroupId(ManagerWifi.TAG)
          .setDelayMs(delayTime)
          .setRequiresNetwork(false)
          .setSingleId(ManagerWifi.TAG)
          .singleInstanceBy(ManagerWifi.TAG), jobType);
    }

    @Override protected void enable() {
      Timber.d("Wifi job enable");

      final WifiManager wifiManager =
          (WifiManager) getContext().getSystemService(Context.WIFI_SERVICE);
      // Only turn wifi on if it is off
      if (!wifiManager.isWifiEnabled()) {
        Timber.d("Turn on WiFi");
        wifiManager.setWifiEnabled(true);
      } else {
        Timber.e("Wifi is already on");
      }
    }

    @Override protected void disable() {
      Timber.d("Wifi job disable");

      final WifiManager wifiManager =
          (WifiManager) getContext().getSystemService(Context.WIFI_SERVICE);
      // Only turn wifi on if it is off
      if (wifiManager.isWifiEnabled()) {
        Timber.d("Turn off WiFi");
        wifiManager.setWifiEnabled(false);
      } else {
        Timber.e("Wifi is already off");
      }
    }
  }
}
