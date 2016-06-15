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

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.os.Build;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import com.birbit.android.jobqueue.Params;
import com.pyamsoft.powermanager.PowerManagerPreferences;
import javax.inject.Inject;
import timber.log.Timber;

final class ManagerInteractorBluetooth extends ManagerInteractorBase {

  @NonNull private static final String TAG = "bluetooth_manager_job";
  @NonNull private final BluetoothAdapter androidBluetooth;
  @NonNull private final PowerManagerPreferences preferences;
  @NonNull private final Context appContext;

  @Inject ManagerInteractorBluetooth(@NonNull PowerManagerPreferences preferences,
      @NonNull Context context, @NonNull BluetoothAdapter bluetoothAdapter) {
    this.appContext = context.getApplicationContext();
    this.preferences = preferences;
    this.androidBluetooth = bluetoothAdapter;
  }

  @Override public void cancelJobs() {
    cancelJobs(TAG);
  }

  @Override public boolean isEnabled() {
    return androidBluetooth.isEnabled();
  }

  @Override public boolean isManaged() {
    return preferences.isBluetoothManaged();
  }

  @Override public long getDelayTime() {
    return preferences.getBluetoothDelay();
  }

  @NonNull @Override public DeviceJob createEnableJob(long delayTime) {
    return new EnableJob(appContext, delayTime);
  }

  @NonNull @Override public DeviceJob createDisableJob(long delayTime) {
    return new DisableJob(appContext, delayTime);
  }

  static final class EnableJob extends Job {

    protected EnableJob(@NonNull Context context, long delayTime) {
      super(context, new Params(PRIORITY).setGroupId(ManagerInteractorBluetooth.TAG)
          .setDelayMs(delayTime)
          .setRequiresNetwork(false)
          .setSingleId(ManagerInteractorBluetooth.TAG)
          .singleInstanceBy(ManagerInteractorBluetooth.TAG), JOB_TYPE_ENABLE);
    }
  }

  static final class DisableJob extends Job {

    protected DisableJob(@NonNull Context context, long delayTime) {
      super(context, new Params(PRIORITY).setGroupId(ManagerInteractorBluetooth.TAG)
          .setDelayMs(delayTime)
          .setRequiresNetwork(false)
          .setSingleId(ManagerInteractorBluetooth.TAG)
          .singleInstanceBy(ManagerInteractorBluetooth.TAG), JOB_TYPE_DISABLE);
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
