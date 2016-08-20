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
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import com.birbit.android.jobqueue.Job;
import com.pyamsoft.powermanager.PowerManagerPreferences;
import com.pyamsoft.powermanager.app.observer.InterestObserver;
import com.pyamsoft.powermanager.dagger.job.BluetoothManageJob;
import javax.inject.Inject;
import rx.Observable;

final class ManagerBluetoothInteractor extends ManagerBaseInteractor {

  @NonNull private final InterestObserver bluetoothObserver;

  @Inject ManagerBluetoothInteractor(@NonNull Context context,
      @NonNull PowerManagerPreferences preferences, @NonNull InterestObserver observer) {
    super(context, preferences);
    this.bluetoothObserver = observer;
  }

  @CheckResult long getDelayTime() {
    return getPreferences().getBluetoothDelay();
  }

  @CheckResult boolean isPeriodic() {
    return getPreferences().isPeriodicBluetooth();
  }

  @CheckResult long getPeriodicEnableTime() {
    return getPreferences().getPeriodicEnableTimeBluetooth();
  }

  @CheckResult long getPeriodicDisableTime() {
    return getPreferences().getPeriodicDisableTimeBluetooth();
  }

  @NonNull @Override protected Job createEnableJob() {
    return new BluetoothManageJob.EnableJob(100L, false, 0, 0);
  }

  @NonNull @Override protected Job createDisableJob() {
    return new BluetoothManageJob.DisableJob(getDelayTime() * 1000L, isPeriodic(),
        getPeriodicEnableTime(), getPeriodicDisableTime());
  }

  @Override public void destroy() {
    destroy(BluetoothManageJob.JOB_TAG);
  }

  @NonNull @Override public Observable<Boolean> cancelJobs() {
    return cancelJobs(BluetoothManageJob.JOB_TAG);
  }

  @NonNull @Override public Observable<Boolean> isManaged() {
    return Observable.defer(() -> Observable.just(getPreferences().isBluetoothManaged()));
  }

  @NonNull @Override public Observable<Boolean> isIgnoreWhileCharging() {
    return Observable.defer(() -> Observable.just(getPreferences().isIgnoreChargingBluetooth()));
  }

  @NonNull @Override public Observable<Boolean> isEnabled() {
    return Observable.defer(() -> Observable.just(bluetoothObserver.is()));
  }
}
