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

package com.pyamsoft.powermanager.job;

import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import com.evernote.android.job.util.support.PersistableBundleCompat;
import com.pyamsoft.powermanager.model.BooleanInterestModifier;
import com.pyamsoft.powermanager.model.BooleanInterestObserver;
import com.pyamsoft.powermanager.model.JobQueuerEntry;
import com.pyamsoft.powermanager.model.Logger;
import com.pyamsoft.powermanager.model.QueuerType;
import javax.inject.Inject;
import javax.inject.Named;

abstract class BaseJob {

  @SuppressWarnings("WeakerAccess") @Inject JobQueuer jobQueuer;
  @SuppressWarnings("WeakerAccess") @Inject @Named("obs_charging_state") BooleanInterestObserver
      chargingObserver;
  private QueuerType type;
  private boolean ignoreWhenCharging;
  private long windowOnTime;
  private long windowOffTime;
  private boolean repeat;
  private String jobTag;

  private void initialize(@NonNull String tag, @NonNull PersistableBundleCompat extras) {
    jobTag = tag;
    ignoreWhenCharging = extras.getBoolean(JobQueuerImpl.KEY_IGNORE_CHARGING, false);
    type = QueuerType.valueOf(extras.getString(JobQueuerImpl.KEY_QUEUE_TYPE, null));
    windowOnTime = extras.getLong(JobQueuerImpl.KEY_ON_WINDOW, 0);
    windowOffTime = extras.getLong(JobQueuerImpl.KEY_OFF_WINDOW, 0);
    repeat = extras.getBoolean(JobQueuerImpl.KEY_PERIODIC, false);
    inject();
  }

  void run(@NonNull String tag, @NonNull PersistableBundleCompat extras) {
    if (isStopped()) {
      getLogger().w("Stop job early");
      return;
    }

    initialize(tag, extras);
    if (type == QueuerType.SCREEN_OFF_DISABLE || type == QueuerType.SCREEN_OFF_ENABLE) {
      if (ignoreWhenCharging) {
        if (chargingObserver.is()) {
          getLogger().w("Do not run job because device is charging");
          return;
        }
      }
    }

    if (isStopped()) {
      getLogger().w("Stop job early");
      return;
    }

    getLogger().i("Run job: %s [%s]", type, jobTag);
    if (type == QueuerType.SCREEN_ON_ENABLE || type == QueuerType.SCREEN_OFF_ENABLE) {
      set();
    } else {
      unset();
    }

    if (isStopped()) {
      getLogger().w("Stop job early");
      return;
    }
    repeatIfRequired();
  }

  private void repeatIfRequired() {
    if (!repeat) {
      getLogger().w("Job is not repeating. Do not re-queue");
      return;
    }

    final QueuerType newType = type.flip();
    final long newDelayTime;
    // Switch them
    if (newType == QueuerType.SCREEN_ON_ENABLE || newType == QueuerType.SCREEN_ON_DISABLE) {
      newDelayTime = windowOffTime * 1000L;
    } else {
      newDelayTime = windowOnTime * 1000L;
    }

    final JobQueuerEntry entry = JobQueuerEntry.builder(jobTag)
        .ignoreIfCharging(ignoreWhenCharging)
        .delay(newDelayTime)
        .repeating(true)
        .repeatingOffWindow(windowOffTime)
        .repeatingOnWindow(windowOnTime)
        .type(newType)
        .build();

    getLogger().d("Requeue job with new type: %s", newType);
    jobQueuer.cancel(jobTag);
    jobQueuer.queue(entry);
  }

  private void set() {
    if (!getObserver().is()) {
      if (isStopped()) {
        getLogger().w("Stop job early");
        return;
      }

      getModifier().set();
    }
  }

  private void unset() {
    if (getObserver().is()) {
      if (isStopped()) {
        getLogger().w("Stop job early");
        return;
      }

      getModifier().unset();
    }
  }

  @CheckResult abstract boolean isStopped();

  @CheckResult @NonNull abstract Logger getLogger();

  @CheckResult @NonNull abstract BooleanInterestObserver getObserver();

  @CheckResult @NonNull abstract BooleanInterestModifier getModifier();

  abstract void inject();
}
