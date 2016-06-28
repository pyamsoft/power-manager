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
import android.support.annotation.NonNull;
import com.pyamsoft.powermanager.PowerManagerPreferences;
import com.pyamsoft.powermanager.R;
import javax.inject.Inject;
import rx.Observable;

final class ManagerPeriodicInteractorImpl extends ManagerSettingsInteractorImpl
    implements ManagerPeriodicInteractor {

  @NonNull private final String KEY_PERIODIC_WIFI;
  @NonNull private final String KEY_PERIODIC_DATA;
  @NonNull private final String KEY_PERIODIC_BLUETOOTH;
  @NonNull private final String KEY_PERIODIC_SYNC;

  @Inject ManagerPeriodicInteractorImpl(@NonNull Context context,
      @NonNull PowerManagerPreferences preferences) {
    super(context, preferences);
    KEY_PERIODIC_WIFI = appContext.getString(R.string.periodic_wifi_key);
    KEY_PERIODIC_DATA = appContext.getString(R.string.periodic_data_key);
    KEY_PERIODIC_BLUETOOTH = appContext.getString(R.string.periodic_bluetooth_key);
    KEY_PERIODIC_SYNC = appContext.getString(R.string.periodic_sync_key);
  }

  @Override @NonNull public Observable<Boolean> isCustomPeriodicDisableTime(@NonNull String key) {
    return Observable.defer(() -> {
      boolean custom;
      if (key.equals(KEY_PERIODIC_WIFI)) {
        custom = preferences.isCustomPeriodicDisableTimeWifi();
      } else if (key.equals(KEY_PERIODIC_DATA)) {
        custom = preferences.isCustomPeriodicDisableTimeData();
      } else if (key.equals(KEY_PERIODIC_BLUETOOTH)) {
        custom = preferences.isCustomPeriodicDisableTimeBluetooth();
      } else if (key.equals(KEY_PERIODIC_SYNC)) {
        custom = preferences.isCustomPeriodicDisableTimeSync();
      } else {
        throw new IllegalStateException("Invalid key");
      }

      return Observable.just(custom);
    });
  }

  @Override @NonNull public Observable<Boolean> isCustomPeriodicEnableTime(@NonNull String key) {
    return Observable.defer(() -> {
      boolean custom;
      if (key.equals(KEY_PERIODIC_WIFI)) {
        custom = preferences.isCustomPeriodicEnableTimeWifi();
      } else if (key.equals(KEY_PERIODIC_DATA)) {
        custom = preferences.isCustomPeriodicEnableTimeData();
      } else if (key.equals(KEY_PERIODIC_BLUETOOTH)) {
        custom = preferences.isCustomPeriodicEnableTimeBluetooth();
      } else if (key.equals(KEY_PERIODIC_SYNC)) {
        custom = preferences.isCustomPeriodicEnableTimeSync();
      } else {
        throw new IllegalStateException("Invalid key");
      }
      return Observable.just(custom);
    });
  }
}

