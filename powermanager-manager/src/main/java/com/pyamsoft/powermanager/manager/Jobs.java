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

package com.pyamsoft.powermanager.manager;

import android.support.annotation.CallSuper;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.birbit.android.jobqueue.Job;
import com.birbit.android.jobqueue.Params;
import com.birbit.android.jobqueue.RetryConstraint;
import com.pyamsoft.powermanager.model.BooleanInterestModifier;
import com.pyamsoft.powermanager.model.BooleanInterestObserver;
import com.pyamsoft.powermanager.model.QueuerType;
import java.util.Arrays;
import java.util.Set;
import timber.log.Timber;

final class Jobs {

  private Jobs() {
    throw new RuntimeException("No instances");
  }

  @CheckResult @NonNull
  static Job createNonRepeating(@NonNull String tag, long delay, boolean ignoreWhenCharging,
      @NonNull BooleanInterestObserver observer, @NonNull BooleanInterestModifier modifier,
      @NonNull BooleanInterestObserver chargingObserver, @NonNull QueuerType type) {
    return new NoRepeatJob(tag, delay, observer, modifier, chargingObserver, type,
        ignoreWhenCharging);
  }

  private static abstract class BasicJob extends Job {

    @NonNull private final BooleanInterestObserver observer;
    @NonNull private final BooleanInterestModifier modifier;
    @NonNull private final BooleanInterestObserver chargingObserver;
    @NonNull private final QueuerType type;
    private final boolean ignoreWhenCharging;

    BasicJob(@NonNull String tag, long delayTime, @NonNull BooleanInterestObserver observer,
        @NonNull BooleanInterestModifier modifier,
        @NonNull BooleanInterestObserver chargingObserver, @NonNull QueuerType type,
        boolean ignoreWhenCharging) {
      super(new Params(1).addTags(tag, ManagerInteractor.ALL_JOB_TAG)
          .setDelayMs(delayTime)
          .setRequiresNetwork(false)
          .setRequiresUnmeteredNetwork(false));

      this.observer = observer;
      this.modifier = modifier;
      this.chargingObserver = chargingObserver;
      this.type = type;
      this.ignoreWhenCharging = ignoreWhenCharging;
    }

    @NonNull @CheckResult BooleanInterestObserver getObserver() {
      return observer;
    }

    @NonNull @CheckResult BooleanInterestModifier getModifier() {
      return modifier;
    }

    @NonNull @CheckResult QueuerType getType() {
      return type;
    }

    @CheckResult boolean isIgnoreWhenCharging() {
      return ignoreWhenCharging;
    }

    @NonNull @CheckResult BooleanInterestObserver getChargingObserver() {
      return chargingObserver;
    }

    @CheckResult @NonNull private String getJobTagString() {
      final String tagString;
      final Set<String> tags = getTags();
      if (tags == null) {
        tagString = "[NO TAGS]";
      } else {
        tagString = Arrays.toString(tags.toArray());
      }
      return tagString;
    }

    @CallSuper @Override public void onAdded() {
      Timber.d("Added job with tags: %s, delay: %d, type: %s", getJobTagString(), getDelayInMs(),
          type);
    }

    @CallSuper @Override protected void onCancel(int cancelReason, @Nullable Throwable throwable) {
      Timber.w("Cancelled job with tags: %s, type: %s", getJobTagString(), type);
    }

    @Override
    protected RetryConstraint shouldReRunOnThrowable(@NonNull Throwable throwable, int runCount,
        int maxRunCount) {
      return RetryConstraint.CANCEL;
    }
  }

  private static class NoRepeatJob extends BasicJob {

    NoRepeatJob(@NonNull String tag, long delay, @NonNull BooleanInterestObserver observer,
        @NonNull BooleanInterestModifier modifier,
        @NonNull BooleanInterestObserver chargingObserver, @NonNull QueuerType type,
        boolean ignoreWhenCharging) {
      super(tag, delay, observer, modifier, chargingObserver, type, ignoreWhenCharging);
    }

    @CallSuper @Override public void onRun() throws Throwable {
      final QueuerType type = getType();
      if (type == QueuerType.SCREEN_OFF_DISABLE || type == QueuerType.SCREEN_OFF_ENABLE) {
        if (isIgnoreWhenCharging()) {
          if (getChargingObserver().is()) {
            return;
          }
        }
      }

      if (type == QueuerType.SCREEN_ON_ENABLE || type == QueuerType.SCREEN_OFF_ENABLE) {
        set();
      } else {
        unset();
      }
    }

    private void set() {
      if (!getObserver().is()) {
        getModifier().set();
      }
    }

    private void unset() {
      if (getObserver().is()) {
        getModifier().unset();
      }
    }
  }

  private static class RepeatingJob extends NoRepeatJob {

    private final long onWindowTime;
    private final long offWindowTime;

    private RepeatingJob(@NonNull String tag, long delay, @NonNull BooleanInterestObserver observer,
        @NonNull BooleanInterestModifier modifier,
        @NonNull BooleanInterestObserver chargingObserver, @NonNull QueuerType type,
        boolean ignoreWhenCharging, long onWindowTime, long offWindowTime) {
      super(tag, delay, observer, modifier, chargingObserver, type, ignoreWhenCharging);
      this.onWindowTime = onWindowTime;
      this.offWindowTime = offWindowTime;
    }

    @Override public void onRun() throws Throwable {
      super.onRun();

      // TODO switch the type and then re-queue the job
    }
  }
}
