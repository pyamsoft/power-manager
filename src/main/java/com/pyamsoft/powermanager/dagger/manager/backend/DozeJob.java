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

import android.support.annotation.NonNull;
import com.birbit.android.jobqueue.Params;
import com.pyamsoft.powermanager.app.manager.backend.ManagerDoze;
import com.pyamsoft.powermanager.app.receiver.DozeReceiver;
import com.pyamsoft.powermanager.dagger.base.BaseJob;
import timber.log.Timber;

public abstract class DozeJob extends BaseJob {

  @NonNull public static final String DOZE_TAG = "doze_tag";
  private static final int PRIORITY = 5;

  private final boolean doze;

  DozeJob(long delay, boolean enable) {
    super(new Params(PRIORITY).setRequiresNetwork(false).addTags(DOZE_TAG).setDelayMs(delay));
    this.doze = enable;
  }

  @Override public void onRun() throws Throwable {
    Timber.d("Run DozeJob");
    final boolean isDoze = DozeReceiver.isDozeMode(getApplicationContext());
    if (doze) {
      if (!isDoze) {
        Timber.d("Do doze startDoze");
        ManagerDoze.executeDumpsys(getApplicationContext(), ManagerDoze.DUMPSYS_DOZE_START);
        ManagerDoze.executeDumpsys(getApplicationContext(), ManagerDoze.DUMPSYS_SENSOR_RESTRICT);
      } else {
        Timber.e("Doze already running");
      }
    } else {
      if (isDoze) {
        Timber.d("Do doze stopDoze");
        ManagerDoze.executeDumpsys(getApplicationContext(), ManagerDoze.DUMPSYS_DOZE_END);
        ManagerDoze.executeDumpsys(getApplicationContext(), ManagerDoze.DUMPSYS_SENSOR_ENABLE);
      } else {
        Timber.e("Doze already not running");
      }
    }
  }

  public static final class EnableJob extends DozeJob {

    public EnableJob() {
      super(100, false);
    }
  }

  public static final class DisableJob extends DozeJob {

    public DisableJob(long delay) {
      super(delay, true);
    }
  }
}
