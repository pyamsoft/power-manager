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

package com.pyamsoft.powermanager.presenter.queuer;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.pyamsoft.powermanagermodel.BooleanInterestModifier;
import com.pyamsoft.powermanagermodel.BooleanInterestObserver;
import com.pyamsoft.powermanagermodel.QueuerType;
import com.pyamsoft.powermanager.presenter.logger.Logger;
import com.pyamsoft.powermanager.presenter.wrapper.JobQueuerWrapper;
import com.pyamsoft.pydroidrx.SubscriptionHelper;
import java.util.concurrent.TimeUnit;
import rx.Observable;
import rx.Scheduler;
import rx.Subscription;
import timber.log.Timber;

abstract class QueuerImpl implements Queuer {

  private static final long LARGEST_TIME_WITHOUT_ALARM = 60L;
  @SuppressWarnings("WeakerAccess") @NonNull final Logger logger;
  @SuppressWarnings("WeakerAccess") @NonNull final BooleanInterestObserver stateObserver;
  @SuppressWarnings("WeakerAccess") @NonNull final BooleanInterestModifier stateModifier;
  @SuppressWarnings("WeakerAccess") @NonNull final BooleanInterestObserver chargingObserver;
  @SuppressWarnings("WeakerAccess") @NonNull final JobQueuerWrapper jobQueuerWrapper;
  @SuppressWarnings("WeakerAccess") @NonNull final Scheduler handlerScheduler;
  @SuppressWarnings("WeakerAccess") @NonNull final Context appContext;
  @SuppressWarnings("WeakerAccess") @Nullable Subscription smallTimeQueuedSubscription;
  @SuppressWarnings("WeakerAccess") @Nullable QueuerType type;
  @SuppressWarnings("WeakerAccess") int ignoreCharging;
  @SuppressWarnings("WeakerAccess") long periodicEnableTime;
  @SuppressWarnings("WeakerAccess") long periodicDisableTime;
  @SuppressWarnings("WeakerAccess") int periodic;
  private long delayTime;

  QueuerImpl(@NonNull Context context, @NonNull JobQueuerWrapper jobQueuerWrapper,
      @NonNull Scheduler handlerScheduler, @NonNull BooleanInterestObserver stateObserver,
      @NonNull BooleanInterestModifier stateModifier,
      @NonNull BooleanInterestObserver chargingObserver, @NonNull Logger logger) {
    this.appContext = context.getApplicationContext();
    this.jobQueuerWrapper = jobQueuerWrapper;
    this.handlerScheduler = handlerScheduler;
    this.chargingObserver = chargingObserver;
    this.stateObserver = stateObserver;
    this.stateModifier = stateModifier;
    this.logger = logger;
    reset();
  }

  private void reset() {
    type = null;
    delayTime = -1L;
    periodicDisableTime = -1L;
    periodicEnableTime = -1L;
    periodic = -1;
    ignoreCharging = -1;
  }

  @Override public void cancel() {
    reset();
    internalCancel();
  }

  private void internalCancel() {
    logger.d("Cancel any previous jobs");
    jobQueuerWrapper.cancel(new Intent(appContext, getScreenOnServiceClass()));
    jobQueuerWrapper.cancel(new Intent(appContext, getScreenOffServiceClass()));
    SubscriptionHelper.unsubscribe(smallTimeQueuedSubscription);
  }

  private void checkAll() {
    if (type == null) {
      throw new IllegalStateException("Type is unset");
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

    internalCancel();
    if (delayTime <= LARGEST_TIME_WITHOUT_ALARM * 1000L) {
      queueShort();
    } else {
      queueLong();
    }
  }

  private void queueShort() {
    SubscriptionHelper.unsubscribe(smallTimeQueuedSubscription);

    logger.d("Queue short term job with delay: %d", delayTime);
    smallTimeQueuedSubscription =
        Observable.defer(() -> Observable.timer(delayTime, TimeUnit.MILLISECONDS, handlerScheduler))
            .subscribeOn(handlerScheduler)
            .observeOn(handlerScheduler)
            .subscribe(ignore -> {
                  if (type == null) {
                    throw new IllegalStateException("Type is unset");
                  }

                  logger.d("Run short queue job");
                  QueueRunner.builder()
                      .setType(type)
                      .setObserver(stateObserver)
                      .setModifier(stateModifier)
                      .setCharging(chargingObserver)
                      .setIgnoreCharging(ignoreCharging)
                      .setLogger(logger)
                      .build()
                      .run();

                  requeue();
                }, throwable -> logger.e("%s onError Queuer queueShort", throwable.toString()),
                () -> SubscriptionHelper.unsubscribe(smallTimeQueuedSubscription));
  }

  private void queueLong() {
    final Class<? extends BaseLongTermService> serviceClass;
    if (type == null) {
      throw new IllegalStateException("QueueType is unset");
    } else if (type == QueuerType.SCREEN_OFF_ENABLE || type == QueuerType.SCREEN_ON_ENABLE) {
      serviceClass = getScreenOnServiceClass();
    } else {
      serviceClass = getScreenOffServiceClass();
    }

    final Intent intent =
        BaseLongTermService.buildIntent(appContext, serviceClass, type, ignoreCharging, periodic,
            periodicEnableTime, periodicDisableTime);
    logger.d("Queue long term job with delay: %d", delayTime);
    final long triggerAtTime = System.currentTimeMillis() + delayTime;
    jobQueuerWrapper.set(intent, triggerAtTime);
  }

  /**
   * Switch the type of the job running (ENABLE <=> DISABLE) and set the delay time to the correct
   * constant, either periodicDisableTime or periodicEnableTime
   */
  @SuppressWarnings("WeakerAccess") void requeue() {
    if (periodic < 1) {
      Timber.e("Job is not periodic. Skip");
      return;
    }

    if (type == null) {
      throw new IllegalStateException("Type is unset");
    }

    final QueuerType newType = type.flip();
    final long newDelayTime;
    if (newType == QueuerType.SCREEN_ON_ENABLE || newType == QueuerType.SCREEN_ON_DISABLE) {
      newDelayTime = periodicDisableTime * 1000L;
    } else {
      newDelayTime = periodicEnableTime * 1000L;
    }

    logger.d("Requeue for periodic job. New type: %s, new delay time: %d", newType, newDelayTime);
    setType(newType);
    setDelayTime(newDelayTime);
    queue();
  }

  @CheckResult @NonNull abstract Class<? extends BaseLongTermService> getScreenOnServiceClass();

  @CheckResult @NonNull abstract Class<? extends BaseLongTermService> getScreenOffServiceClass();
}
