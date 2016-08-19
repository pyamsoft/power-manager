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

package com.pyamsoft.powermanager.dagger.manager.jobs;

import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import com.birbit.android.jobqueue.Params;
import com.pyamsoft.powermanager.Singleton;
import com.pyamsoft.powermanager.dagger.base.BaseJob;
import timber.log.Timber;

public abstract class DeviceJob extends BaseJob {

  public static final long MINIMUM_ALLOWED_PERIOD = 60L;
  protected static final int JOB_TYPE_ENABLE = 1;
  protected static final int JOB_TYPE_DISABLE = 2;
  protected static final int PRIORITY = 1;

  private final int jobType;
  private final boolean periodic;
  private final long periodicDisableTime;
  private final long periodicEnableTime;

  protected DeviceJob(@NonNull Params params, int jobType, boolean periodic,
      long periodicDisableTime, long periodicEnableTime) {
    super(params);
    this.periodicDisableTime = periodicDisableTime;
    this.periodicEnableTime = periodicEnableTime;
    this.jobType = jobType;
    this.periodic = periodic;
  }

  @CheckResult private boolean isPeriodic() {
    return periodic;
  }

  @CheckResult protected final long getPeriodicDisableTime() {
    return periodicDisableTime;
  }

  @CheckResult protected final long getPeriodicEnableTime() {
    return periodicEnableTime;
  }

  @Override public void onRun() throws Throwable {
    Timber.d("Run job");
    switch (jobType) {
      case JOB_TYPE_ENABLE:
        Timber.d("Enable job: %d", jobType);
        enable();
        break;
      case JOB_TYPE_DISABLE:
        Timber.d("Disable job: %d", jobType);
        disable();
        break;
      default:
        Timber.e("No job specified: %d", jobType);
    }
  }

  private void enable() {
    // Only turn wifi on if it is off
    if (!isEnabled()) {
      callEnable();
      if (isPeriodic()) {
        Timber.d("Periodic job");
        if (getPeriodicDisableTime() < MINIMUM_ALLOWED_PERIOD) {
          Timber.e("Not queuing period disable job with interval less than 1 minute");
        } else {
          Singleton.Jobs.with(getApplicationContext()).addJobInBackground(periodicDisableJob());
        }
      }
    } else {
      Timber.e("Radio is already on");
    }
  }

  private void disable() {
    // Only turn wifi on if it is off
    if (isEnabled()) {
      callDisable();
      if (isPeriodic()) {
        Timber.d("Periodic job");
        if (getPeriodicEnableTime() < MINIMUM_ALLOWED_PERIOD) {
          Timber.e("Not queuing period enable job with interval less than 1 minute");
        } else {
          Singleton.Jobs.with(getApplicationContext()).addJobInBackground(periodicEnableJob());
        }
      }
    } else {
      Timber.e("Radio is already off");
    }
  }

  protected abstract void callEnable();

  protected abstract void callDisable();

  @CheckResult protected abstract boolean isEnabled();

  @CheckResult protected abstract DeviceJob periodicDisableJob();

  @CheckResult protected abstract DeviceJob periodicEnableJob();
}