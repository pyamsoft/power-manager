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
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.os.Build;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import com.birbit.android.jobqueue.Params;
import com.pyamsoft.powermanager.PowerManager;
import com.pyamsoft.powermanager.PowerManagerPreferences;
import javax.inject.Inject;
import timber.log.Timber;

final class ManagerBluetooth extends ManagerBase {

  @NonNull private static final String TAG = "bluetooth_manager_job";
  @NonNull private final BluetoothAdapter androidBluetooth;
  @NonNull private final PowerManagerPreferences preferences;

  @Inject ManagerBluetooth(@NonNull BluetoothAdapter androidBluetooth,
      @NonNull PowerManagerPreferences preferences) {
    Timber.d("new ManagerBluetooth");
    this.androidBluetooth = androidBluetooth;
    this.preferences = preferences;
  }

  @Override public void enable(@NonNull Application application) {
    enable(application, 0);
  }

  @Override public void enable(@NonNull Application application, long time) {
    if (preferences.isBluetoothManaged()) {
      Timber.d("Queue Bluetooth enable");
      cancelJobs(application, TAG);
      PowerManager.getJobManager(application).addJobInBackground(new EnableJob(application, time));
    } else {
      Timber.w("Bluetooth is not managed");
    }
  }

  @Override public void disable(@NonNull Application application) {
    disable(application, preferences.getBluetoothDelay() * 1000L);
  }

  @Override public void disable(@NonNull Application application, long time) {
    if (preferences.isBluetoothManaged()) {
      Timber.d("Queue Bluetooth disable");
      cancelJobs(application, TAG);
      PowerManager.getJobManager(application).addJobInBackground(new DisableJob(application, time));
    } else {
      Timber.w("Bluetooth is not managed");
    }
  }

  @Override public boolean isEnabled() {
    return androidBluetooth.isEnabled();
  }

  static final class EnableJob extends Job {

    protected EnableJob(@NonNull Context context, long delayTime) {
      super(context, new Params(PRIORITY).setGroupId(ManagerBluetooth.TAG)
          .setDelayMs(delayTime)
          .setRequiresNetwork(false)
          .setSingleId(ManagerBluetooth.TAG)
          .singleInstanceBy(ManagerBluetooth.TAG), JOB_TYPE_ENABLE);
    }
  }

  static final class DisableJob extends Job {

    protected DisableJob(@NonNull Context context, long delayTime) {
      super(context, new Params(PRIORITY).setGroupId(ManagerBluetooth.TAG)
          .setDelayMs(delayTime)
          .setRequiresNetwork(false)
          .setSingleId(ManagerBluetooth.TAG)
          .singleInstanceBy(ManagerBluetooth.TAG), JOB_TYPE_DISABLE);
    }
  }

  static abstract class Job extends DeviceJob {

    protected Job(@NonNull Context context, @NonNull Params params, int jobType) {
      super(context, params, jobType);
    }

    @CheckResult @NonNull BluetoothAdapter getBluetoothAdapter() {
      BluetoothAdapter adapter;
      if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR2) {
        adapter = BluetoothAdapter.getDefaultAdapter();
      } else {
        final BluetoothManager bluetoothManager =
            (BluetoothManager) getContext().getApplicationContext()
                .getSystemService(Context.BLUETOOTH_SERVICE);
        adapter = bluetoothManager.getAdapter();
      }
      return adapter;
    }

    @Override protected void enable() {
      Timber.d("Bluetooth job enable");
      final BluetoothAdapter adapter = getBluetoothAdapter();
      if (!adapter.isEnabled()) {
        Timber.d("Turn on Bluetooth");
        adapter.enable();
      } else {
        Timber.e("Bluetooth is already on");
      }
    }

    @Override protected void disable() {
      Timber.d("Bluetooth job disable");
      final BluetoothAdapter adapter = getBluetoothAdapter();
      if (adapter.isEnabled()) {
        Timber.d("Turn off Bluetooth");
        adapter.disable();
      } else {
        Timber.e("Bluetooth is already off");
      }
    }
  }
}
