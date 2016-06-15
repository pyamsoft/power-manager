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

import android.content.Context;
import android.net.wifi.WifiManager;
import android.support.annotation.NonNull;
import com.birbit.android.jobqueue.Params;
import com.pyamsoft.powermanager.PowerManagerPreferences;
import javax.inject.Inject;
import timber.log.Timber;

final class ManagerInteractorWifi extends WearableManagerInteractorImpl {

  @NonNull private static final String TAG = "wifi_manager_job";
  @NonNull private final WifiManager wifiManager;
  @NonNull private final PowerManagerPreferences preferences;
  @NonNull private final Context appContext;

  @Inject ManagerInteractorWifi(@NonNull PowerManagerPreferences preferences,
      @NonNull Context context, @NonNull WifiManager wifiManager) {
    super(context.getApplicationContext(), preferences);
    this.appContext = context.getApplicationContext();
    this.preferences = preferences;
    this.wifiManager = wifiManager;
  }

  @Override public void cancelJobs() {
    cancelJobs(TAG);
  }

  @Override public boolean isEnabled() {
    return wifiManager.isWifiEnabled();
  }

  @Override public boolean isManaged() {
    return preferences.isWifiManaged();
  }

  @Override public long getDelayTime() {
    return preferences.getWifiDelay();
  }

  @NonNull @Override public DeviceJob createEnableJob(long delayTime) {
    return new EnableJob(appContext, delayTime);
  }

  @NonNull @Override public DeviceJob createDisableJob(long delayTime) {
    return new DisableJob(appContext, delayTime);
  }

  static final class EnableJob extends WifiJob {

    protected EnableJob(@NonNull Context context, long delayTime) {
      super(context, new Params(PRIORITY).setGroupId(ManagerInteractorWifi.TAG)
          .setDelayMs(delayTime)
          .addTags(ManagerInteractorWifi.TAG)
          .setRequiresNetwork(false)
          .setSingleId(ManagerInteractorWifi.TAG)
          .singleInstanceBy(ManagerInteractorWifi.TAG), JOB_TYPE_ENABLE);
    }
  }

  static final class DisableJob extends WifiJob {

    protected DisableJob(@NonNull Context context, long delayTime) {
      super(context, new Params(PRIORITY).setGroupId(ManagerInteractorWifi.TAG)
          .setDelayMs(delayTime)
          .addTags(ManagerInteractorWifi.TAG)
          .setRequiresNetwork(false)
          .setSingleId(ManagerInteractorWifi.TAG)
          .singleInstanceBy(ManagerInteractorWifi.TAG), JOB_TYPE_DISABLE);
    }
  }

  static abstract class WifiJob extends DeviceJob {

    protected WifiJob(@NonNull Context context, @NonNull Params params, int jobType) {
      super(context, params, jobType);
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
