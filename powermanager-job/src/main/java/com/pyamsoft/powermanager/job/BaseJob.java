/*
 * Copyright 2017 Peter Kenji Yamanaka
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
import com.pyamsoft.powermanager.base.logger.Logger;
import com.pyamsoft.powermanager.model.StateModifier;
import com.pyamsoft.powermanager.model.StateObserver;
import javax.inject.Inject;
import javax.inject.Named;

public abstract class BaseJob {

  @SuppressWarnings("WeakerAccess") @Inject JobQueuer jobQueuer;
  @SuppressWarnings("WeakerAccess") @Inject @Named("obs_charging_state") StateObserver
      chargingObserver;
  private QueuerType type;
  private boolean ignoreWhenCharging;
  private long windowOnTime;
  private long windowOffTime;
  private boolean repeat;

  private void initialize(@NonNull PersistableBundleCompat extras) {
    ignoreWhenCharging = extras.getBoolean(JobQueuerImpl.KEY_IGNORE_CHARGING, false);
    type = QueuerType.valueOf(extras.getString(JobQueuerImpl.KEY_QUEUE_TYPE, null));
    windowOnTime = extras.getLong(JobQueuerImpl.KEY_ON_WINDOW, 0);
    windowOffTime = extras.getLong(JobQueuerImpl.KEY_OFF_WINDOW, 0);
    repeat = extras.getBoolean(JobQueuerImpl.KEY_PERIODIC, false);
    inject();
  }

  @CheckResult
  private boolean runJob(@NonNull String tag, @NonNull PersistableBundleCompat extras) {
    if (isStopped()) {
      getLogger().w("Stop job early: " + tag);
      return false;
    }

    initialize(extras);
    if (type == QueuerType.SCREEN_OFF_DISABLE || type == QueuerType.SCREEN_OFF_ENABLE) {
      if (ignoreWhenCharging) {
        if (chargingObserver.enabled()) {
          getLogger().w("Do not run job because device is charging: " + tag);
          return true;
        }
      }
    }

    if (isStopped()) {
      getLogger().w("Stop job early: " + tag);
      return false;
    }

    getLogger().i("Run job: %s [%s]", type, tag);
    if (type == QueuerType.SCREEN_ON_ENABLE || type == QueuerType.SCREEN_OFF_ENABLE) {
      getModifier().set();
    } else {
      getModifier().unset();
    }

    return true;
  }

  private void repeatIfRequired(@NonNull String tag) {
    if (!repeat) {
      getLogger().w("Job is not repeating. Do not re-queue: " + tag);
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

    final JobQueuerEntry entry = JobQueuerEntry.builder(tag)
        .ignoreIfCharging(ignoreWhenCharging)
        .delay(newDelayTime)
        .repeating(true)
        .repeatingOffWindow(windowOffTime)
        .repeatingOnWindow(windowOnTime)
        .type(newType)
        .build();

    getLogger().d("Requeue job with new type: %s [%s]", newType, tag);
    jobQueuer.cancel(tag);
    jobQueuer.queue(entry);
  }

  /**
   * Runs the Job. Called either by managed jobs or directly by the JobQueuer
   */
  void run(@NonNull String tag, @NonNull PersistableBundleCompat extras) {
    if (runJob(tag, extras)) {
      repeatIfRequired(tag);
    }
  }

  /**
   * Override in the actual ManagedJobs to call Job.isCancelled();
   *
   * If it is not a managed job it never isStopped, always run to completion
   */
  @CheckResult boolean isStopped() {
    return false;
  }

  abstract void inject();

  @CheckResult @NonNull abstract Logger getLogger();

  @CheckResult @NonNull abstract StateObserver getObserver();

  @CheckResult @NonNull abstract StateModifier getModifier();
}
