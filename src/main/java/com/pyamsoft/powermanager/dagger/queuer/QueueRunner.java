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

package com.pyamsoft.powermanager.dagger.queuer;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.pyamsoft.powermanager.app.logger.Logger;
import com.pyamsoft.powermanager.app.modifier.BooleanInterestModifier;
import com.pyamsoft.powermanager.app.observer.BooleanInterestObserver;
import com.pyamsoft.powermanager.dagger.wrapper.JobQueuerWrapper;
import java.util.Date;

class QueueRunner {

  @NonNull private final Context appContext;
  @NonNull private final JobQueuerWrapper jobQueuerWrapper;
  @NonNull private final Class<? extends BaseLongTermService> enableServiceClass;
  @NonNull private final Class<? extends BaseLongTermService> disableServiceClass;
  @NonNull private final QueuerType type;
  @NonNull private final BooleanInterestObserver observer;
  @NonNull private final BooleanInterestModifier modifier;
  @NonNull private final BooleanInterestObserver charging;
  @NonNull private final Logger logger;
  private final int periodic;
  private final int ignoreCharging;
  private final long periodicEnableTime;
  private final long periodicDisableTime;

  @SuppressWarnings("WeakerAccess") QueueRunner(@NonNull Context context,
      @NonNull JobQueuerWrapper jobQueuerWrapper,
      @NonNull Class<? extends BaseLongTermService> enableServiceClass,
      @NonNull Class<? extends BaseLongTermService> disableServiceClass, @NonNull QueuerType type,
      int periodic, long periodicEnableTime, long periodicDisableTime, int ignoreCharging,
      @NonNull BooleanInterestObserver stateObserver,
      @NonNull BooleanInterestModifier stateModifier,
      @NonNull BooleanInterestObserver chargingObserver, @NonNull Logger logger) {
    this.appContext = context.getApplicationContext();
    this.jobQueuerWrapper = jobQueuerWrapper;
    this.enableServiceClass = enableServiceClass;
    this.disableServiceClass = disableServiceClass;
    this.type = type;
    this.periodic = periodic;
    this.ignoreCharging = ignoreCharging;
    this.periodicEnableTime = periodicEnableTime;
    this.periodicDisableTime = periodicDisableTime;
    this.observer = stateObserver;
    this.modifier = stateModifier;
    this.charging = chargingObserver;
    this.logger = logger;
  }

  @CheckResult @NonNull static Builder builder(@NonNull Context context) {
    return new Builder(context);
  }

  void run() {
    immediateAction();
    scheduleFutureAction();
  }

  private void immediateAction() {
    if (type == QueuerType.DISABLE || type == QueuerType.TOGGLE_DISABLE) {
      if (ignoreCharging == 1) {
        if (charging.is()) {
          logger.w("Ignore disable job because we are charging");
          return;
        }
      }
    }

    if (type == QueuerType.ENABLE || type == QueuerType.TOGGLE_DISABLE) {
      set();
    } else {
      unset();
    }
  }

  private void scheduleFutureAction() {
    if (periodic < 1) {
      logger.i("This job is not periodic. Skip");
      return;
    }

    if (periodicEnableTime < 60L) {
      logger.w("Periodic Enable time is too low %d. Must be at least 60", periodicEnableTime);
      return;
    }

    if (periodicDisableTime < 60L) {
      logger.w("Periodic Disable time is too low %d. Must be at least 60", periodicDisableTime);
      return;
    }

    // Remember that the times are switched to make the logic work correctly even if naming is confusing
    final long intervalUntilReEnable = periodicDisableTime * 1000L;
    final long intervalUntilReDisable = periodicEnableTime * 1000L;

    final long reEnableTime = System.currentTimeMillis() + intervalUntilReEnable;
    final long reDisableTime = reEnableTime + intervalUntilReDisable;

    final QueuerType newType;
    if (type == QueuerType.ENABLE) {
      newType = QueuerType.DISABLE;
    } else if (type == QueuerType.DISABLE) {
      newType = QueuerType.ENABLE;
    } else if (type == QueuerType.TOGGLE_ENABLE) {
      newType = QueuerType.TOGGLE_DISABLE;
    } else if (type == QueuerType.TOGGLE_DISABLE) {
      newType = QueuerType.TOGGLE_ENABLE;
    } else {
      throw new IllegalStateException("Invalid queue type");
    }

    // Queue a constant re-enable job with the same Type as original
    final Date reEnableDate = new Date(reEnableTime);
    logger.i("Set periodic enable job starting at %s", reEnableDate);
    final Intent reEnableIntent =
        BaseLongTermService.buildIntent(appContext, enableServiceClass, type, ignoreCharging,
            periodic, periodicEnableTime, periodicDisableTime);
    jobQueuerWrapper.set(reEnableIntent, reEnableTime);

    // Queue a constant re-disable job with the opposite type
    final Date reDisableDate = new Date(reDisableTime);
    logger.i("Set periodic disable job starting at %s", reDisableDate);
    final Intent reDisableIntent =
        BaseLongTermService.buildIntent(appContext, disableServiceClass, newType, ignoreCharging,
            periodic, periodicEnableTime, periodicDisableTime);
    jobQueuerWrapper.set(reDisableIntent, reDisableTime);
  }

  private void set() {
    logger.i("Prereq %s job", type);
    if (!observer.is()) {
      logger.w("RUN: %s job", type);
      modifier.set();
    }
  }

  private void unset() {
    logger.i("Prereq %s job", type);
    if (observer.is()) {
      logger.w("RUN: %s job", type);
      modifier.unset();
    }
  }

  static final class Builder {

    @NonNull private final Context appContext;
    @Nullable private JobQueuerWrapper jobQueuerWrapper;
    @Nullable private Class<? extends BaseLongTermService> enableServiceClass;
    @Nullable private Class<? extends BaseLongTermService> disableServiceClass;
    @Nullable private QueuerType type;
    @Nullable private BooleanInterestObserver observer;
    @Nullable private BooleanInterestModifier modifier;
    @Nullable private BooleanInterestObserver charging;
    @Nullable private Logger logger;
    private int periodic;
    private int ignoreCharging;
    private long periodicEnableTime;
    private long periodicDisableTime;

    Builder(@NonNull Context context) {
      appContext = context.getApplicationContext();
      enableServiceClass = null;
      disableServiceClass = null;
      type = null;
      periodicDisableTime = -1L;
      periodicEnableTime = -1L;
      periodic = -1;
      ignoreCharging = -1;
    }

    @CheckResult @NonNull Builder setJobQueueWrapper(@NonNull JobQueuerWrapper jobQueueWrapper) {
      this.jobQueuerWrapper = jobQueueWrapper;
      return this;
    }

    @CheckResult @NonNull Builder setEnableService(
        @NonNull Class<? extends BaseLongTermService> serviceClass) {
      this.enableServiceClass = serviceClass;
      return this;
    }

    @CheckResult @NonNull Builder setDisableService(
        @NonNull Class<? extends BaseLongTermService> serviceClass) {
      this.disableServiceClass = serviceClass;
      return this;
    }

    @CheckResult @NonNull Builder setType(@NonNull QueuerType type) {
      this.type = type;
      return this;
    }

    @CheckResult @NonNull Builder setPeriodic(int periodic) {
      this.periodic = periodic;
      return this;
    }

    @CheckResult @NonNull Builder setIgnoreCharging(int ignore) {
      this.ignoreCharging = ignore;
      return this;
    }

    @CheckResult @NonNull Builder setPeriodicEnableTime(long time) {
      this.periodicEnableTime = time;
      return this;
    }

    @CheckResult @NonNull Builder setPeriodicDisableTime(long time) {
      this.periodicDisableTime = time;
      return this;
    }

    @CheckResult @NonNull Builder setObserver(@NonNull BooleanInterestObserver observer) {
      this.observer = observer;
      return this;
    }

    @CheckResult @NonNull Builder setModifier(@NonNull BooleanInterestModifier modifier) {
      this.modifier = modifier;
      return this;
    }

    @CheckResult @NonNull Builder setCharging(@NonNull BooleanInterestObserver charging) {
      this.charging = charging;
      return this;
    }

    @CheckResult @NonNull Builder setLogger(@NonNull Logger logger) {
      this.logger = logger;
      return this;
    }

    private void checkAll() {
      if (jobQueuerWrapper == null) {
        throw new IllegalStateException("JobQueuerWrapper is NULL");
      }

      if (enableServiceClass == null) {
        throw new IllegalStateException("Enable Service Class is NULL");
      }

      if (disableServiceClass == null) {
        throw new IllegalStateException("Disable Service Class is NULL");
      }

      if (type == null) {
        throw new IllegalStateException("Type is unset");
      }

      if (periodicDisableTime < 0) {
        throw new IllegalStateException("Periodic Disable time is less than 0");
      }

      if (periodicEnableTime < 0) {
        throw new IllegalStateException("Periodic Enable time is less than 0");
      }

      if (periodic < 0) {
        throw new IllegalStateException("Periodic is not set");
      }

      if (ignoreCharging < 0) {
        throw new IllegalStateException("Ignore Charging is not set");
      }

      if (observer == null) {
        throw new IllegalStateException("Observer is NULL");
      }

      if (modifier == null) {
        throw new IllegalStateException("Modifier is NULL");
      }

      if (charging == null) {
        throw new IllegalStateException("Charging Observer is NULL");
      }

      if (logger == null) {
        throw new IllegalStateException("Logger is NULL");
      }
    }

    @CheckResult @NonNull QueueRunner build() {
      checkAll();

      // We checked for nulls and such before this
      //noinspection ConstantConditions
      return new QueueRunner(appContext, jobQueuerWrapper, enableServiceClass, disableServiceClass,
          type, periodic, periodicEnableTime, periodicDisableTime, ignoreCharging, observer,
          modifier, charging, logger);
    }
  }
}
