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

package com.pyamsoft.powermanager.dagger.manager.backend;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.os.Build;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.birbit.android.jobqueue.Params;
import com.pyamsoft.powermanager.PowerManagerPreferences;
import javax.inject.Inject;
import timber.log.Timber;

final class ManagerInteractorBluetooth extends WearableManagerInteractorImpl {

  @NonNull private static final String TAG = "bluetooth_manager_job";
  @NonNull private final BluetoothAdapterWrapper androidBluetooth;
  @NonNull private final PowerManagerPreferences preferences;
  @NonNull private final Context appContext;

  @Inject ManagerInteractorBluetooth(@NonNull PowerManagerPreferences preferences,
      @NonNull Context context, @NonNull BluetoothAdapterWrapper bluetoothAdapter) {
    super(context.getApplicationContext(), preferences);
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

  @Override public boolean isPeriodic() {
    return preferences.isPeriodicBluetooth();
  }

  @Override public long getPeriodicEnableTime() {
    // TODO
    return 30;
  }

  @Override public long getPeriodicDisableTime() {
    return preferences.getPeriodicDisableTimeBluetooth();
  }

  @Override public long getDelayTime() {
    return preferences.getBluetoothDelay();
  }

  @NonNull @Override public DeviceJob createEnableJob(long delayTime, boolean periodic) {
    return new EnableJob(appContext, delayTime, isOriginalStateEnabled(), periodic,
        getPeriodicDisableTime(), getPeriodicEnableTime());
  }

  @NonNull @Override public DeviceJob createDisableJob(long delayTime, boolean periodic) {
    return new DisableJob(appContext, delayTime, isOriginalStateEnabled(), periodic,
        getPeriodicDisableTime(), getPeriodicEnableTime());
  }

  static final class EnableJob extends Job {

    protected EnableJob(@NonNull Context context, long delayTime, boolean originalState,
        boolean periodic, long periodicDisableTime, long periodicEnableTime) {
      super(context, new Params(PRIORITY).setDelayMs(delayTime), JOB_TYPE_ENABLE, originalState,
          periodic, periodicDisableTime, periodicEnableTime);
    }
  }

  static final class DisableJob extends Job {

    protected DisableJob(@NonNull Context context, long delayTime, boolean originalState,
        boolean periodic, long periodicDisableTime, long periodicEnableTime) {
      super(context, new Params(PRIORITY).setDelayMs(delayTime), JOB_TYPE_DISABLE, originalState,
          periodic, periodicDisableTime, periodicEnableTime);
    }
  }

  static abstract class Job extends DeviceJob {

    @NonNull private final BluetoothAdapterWrapper adapter;

    protected Job(@NonNull Context context, @NonNull Params params, int jobType,
        boolean originalState, boolean periodic, long periodicDisableTime,
        long periodicEnableTime) {
      super(context, params.addTags(ManagerInteractorBluetooth.TAG), jobType, originalState,
          periodic, periodicDisableTime, periodicEnableTime);
      adapter = new BluetoothAdapterWrapper(getBluetoothAdapter());
    }

    @CheckResult @Nullable final BluetoothAdapter getBluetoothAdapter() {
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

    @Override protected void callEnable() {
      Timber.d("Enable bluetooth");
      adapter.enable();
    }

    @Override protected void callDisable() {
      Timber.d("Disable bluetooth");
      adapter.disable();
    }

    @Override protected boolean isEnabled() {
      Timber.d("isBluetoothEnabled");
      return adapter.isEnabled();
    }

    @Override protected DeviceJob periodicDisableJob() {
      Timber.d("Periodic bluetooth disable job");
      return new DisableJob(getContext(), getPeriodicDisableTime() * 1000, isOriginalState(), true,
          getPeriodicDisableTime(), getPeriodicEnableTime());
    }

    @Override protected DeviceJob periodicEnableJob() {
      Timber.d("Periodic bluetooth enable job");
      return new EnableJob(getContext(), getPeriodicEnableTime() * 1000, isOriginalState(), true,
          getPeriodicDisableTime(), getPeriodicEnableTime());
    }
  }
}
