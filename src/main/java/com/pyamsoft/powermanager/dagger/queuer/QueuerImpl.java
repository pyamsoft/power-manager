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

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.pyamsoft.powermanager.app.logger.Logger;
import com.pyamsoft.powermanager.app.modifier.BooleanInterestModifier;
import com.pyamsoft.powermanager.app.observer.BooleanInterestObserver;
import com.pyamsoft.pydroidrx.SubscriptionHelper;
import java.util.concurrent.TimeUnit;
import rx.Observable;
import rx.Scheduler;
import rx.Subscription;

abstract class QueuerImpl implements Queuer {

  @NonNull static final String EXTRA_JOB_TYPE = "extra_job_queue_type";
  private static final long LARGEST_TIME_WITHOUT_ALARM = 120L;
  @SuppressWarnings("WeakerAccess") @NonNull final BooleanInterestObserver stateObserver;
  @SuppressWarnings("WeakerAccess") @NonNull final BooleanInterestModifier stateModifier;
  @NonNull private final AlarmManager alarmManager;
  @NonNull private final Scheduler handlerScheduler;
  @NonNull private final String jobTag;
  @NonNull private final Logger logger;
  @SuppressWarnings("WeakerAccess") long delayTime;
  @SuppressWarnings("WeakerAccess") @Nullable Subscription smallTimeQueuedSubscription;
  @SuppressWarnings("WeakerAccess") @Nullable QueuerType type;
  @NonNull private Context appContext;
  private long periodicEnableTime;
  private long periodicDisableTime;
  private int periodic;
  private int ignoreCharging;
  private boolean set;
  private boolean cancelRunning;

  QueuerImpl(@NonNull String jobTag, @NonNull Context context, @NonNull AlarmManager alarmManager,
      @NonNull Scheduler handlerScheduler, @NonNull BooleanInterestObserver stateObserver,
      @NonNull BooleanInterestModifier stateModifier, @NonNull Logger logger) {
    this.jobTag = jobTag;
    this.appContext = context.getApplicationContext();
    this.alarmManager = alarmManager;
    this.handlerScheduler = handlerScheduler;
    this.stateObserver = stateObserver;
    this.stateModifier = stateModifier;
    this.logger = logger;
    reset();
  }

  private void reset() {
    set = false;
    type = null;
    delayTime = -1L;
    periodicDisableTime = -1L;
    periodicEnableTime = -1L;
    periodic = -1;
    ignoreCharging = -1;
    cancelRunning = false;
  }

  private void checkAll() {
    if (type == null) {
      throw new IllegalStateException("Type is NULL1");
    }

    if (delayTime < 0) {
      throw new IllegalStateException("Delay time is less than 0");
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

    if (set) {
      throw new IllegalStateException("Must be reset before we can queue");
    }
  }

  @NonNull @Override public Queuer cancel() {
    reset();
    cancelRunning = true;
    return this;
  }

  @NonNull @Override public Queuer setType(@NonNull QueuerType queuerType) {
    type = queuerType;
    return this;
  }

  @NonNull @Override public Queuer setDelayTime(long time) {
    delayTime = time;
    return this;
  }

  @NonNull @Override public Queuer setPeriodic(boolean periodic) {
    this.periodic = (periodic ? 1 : 0);
    return this;
  }

  @NonNull @Override public Queuer setIgnoreCharging(boolean ignore) {
    ignoreCharging = (ignore ? 1 : 0);
    return this;
  }

  @NonNull @Override public Queuer setPeriodicEnableTime(long time) {
    periodicEnableTime = time;
    return this;
  }

  @NonNull @Override public Queuer setPeriodicDisableTime(long time) {
    periodicDisableTime = time;
    return this;
  }

  @Override public void queue() {
    checkAll();
    set = true;

    if (cancelRunning) {
      logger.d("Cancel any previous jobs for %s", jobTag);
      alarmManager.cancel(PendingIntent.getService(appContext, 0, getLongTermIntent(appContext), 0));
      SubscriptionHelper.unsubscribe(smallTimeQueuedSubscription);
    }

    if (delayTime <= LARGEST_TIME_WITHOUT_ALARM) {
      queueShort();
    } else {
      queueLong();
    }
  }

  private void queueShort() {
    logger.d("Queue short term job with delay: %d (%s)", delayTime, jobTag);

    SubscriptionHelper.unsubscribe(smallTimeQueuedSubscription);
    smallTimeQueuedSubscription = Observable.defer(() -> {
      logger.d("Prepare a short queue job %s with delay: %d seconds", jobTag, delayTime);
      return Observable.just(true);
    })
        .delay(delayTime, TimeUnit.SECONDS)
        .subscribeOn(handlerScheduler)
        .observeOn(handlerScheduler)
        .subscribe(ignore -> {
              if (type == null) {
                throw new IllegalStateException("QueueType is NULL");
              }

              logger.d("Run short queue job: %s", jobTag);
              if (type == QueuerType.ENABLE) {
                logger.i("Enable job: %s", jobTag);
                if (!stateObserver.is()) {
                  logger.w("RUN: ENABLE %s", jobTag);
                  stateModifier.set();
                }
              } else if (type == QueuerType.DISABLE) {
                logger.i("Disable job: %s", jobTag);
                if (stateObserver.is()) {
                  logger.w("RUN: DISABLE %s", jobTag);
                  stateModifier.unset();
                }
              } else if (type == QueuerType.TOGGLE_ENABLE) {
                logger.i("Toggle Enable job: %s", jobTag);
                if (stateObserver.is()) {
                  logger.w("RUN: TOGGLE_ENABLE %s", jobTag);
                  stateModifier.unset();
                }
              } else if (type == QueuerType.TOGGLE_DISABLE) {
                logger.i("Toggle Disable job: %s", jobTag);
                if (!stateObserver.is()) {
                  logger.w("RUN: TOGGLE_DISABLE %s", jobTag);
                  stateModifier.set();
                }
              } else {
                throw new IllegalStateException("QueueType is Invalid");
              }
            }, throwable -> logger.e("%s onError Queuer queueShort", throwable.toString()),
            () -> SubscriptionHelper.unsubscribe(smallTimeQueuedSubscription));
  }

  private void queueLong() {
    if (type == null) {
      throw new IllegalStateException("QueueType is NULL");
    }

    final Intent intent = getLongTermIntent(appContext);
    intent.putExtra(EXTRA_JOB_TYPE, type.name());
    logger.d("Queue long term job with delay: %d (%s)", delayTime, jobTag);

    alarmManager.cancel(PendingIntent.getService(appContext, 0, intent, 0));

    final long triggerAtTime = System.currentTimeMillis() + (delayTime * 1000L);
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
      alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAtTime,
          PendingIntent.getService(appContext, 0, intent, 0));
    } else {
      alarmManager.set(AlarmManager.RTC_WAKEUP, triggerAtTime,
          PendingIntent.getService(appContext, 0, intent, 0));
    }
  }

  @CheckResult @NonNull abstract Intent getLongTermIntent(@NonNull Context context);
}
