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
import com.pyamsoft.pydroidrx.SubscriptionHelper;
import java.util.concurrent.TimeUnit;
import rx.Observable;
import rx.Scheduler;
import rx.Subscription;

abstract class QueuerImpl implements Queuer {

  private static final long LARGEST_TIME_WITHOUT_ALARM = 60L;
  @NonNull final Logger logger;
  @NonNull final BooleanInterestObserver stateObserver;
  @NonNull final BooleanInterestModifier stateModifier;
  @NonNull final BooleanInterestObserver chargingObserver;
  @NonNull final JobQueuerWrapper jobQueuerWrapper;
  @NonNull private final Scheduler handlerScheduler;
  @SuppressWarnings("WeakerAccess") @Nullable Subscription smallTimeQueuedSubscription;
  @SuppressWarnings("WeakerAccess") @Nullable QueuerType type;
  @SuppressWarnings("WeakerAccess") int ignoreCharging;
  @SuppressWarnings("WeakerAccess") @NonNull Context appContext;
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
    jobQueuerWrapper.cancel(new Intent(appContext, getEnableServiceClass()));
    jobQueuerWrapper.cancel(new Intent(appContext, getDisableServiceClass()));
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
    smallTimeQueuedSubscription = Observable.defer(() -> Observable.just(true))
        .delay(delayTime, TimeUnit.MILLISECONDS)
        .subscribeOn(handlerScheduler)
        .observeOn(handlerScheduler)
        .subscribe(ignore -> {
              if (type == null) {
                throw new IllegalStateException("Type is unset");
              }

              logger.d("Run short queue job");
              QueueRunner.builder(appContext)
                  .setJobQueueWrapper(jobQueuerWrapper)
                  .setType(type)
                  .setObserver(stateObserver)
                  .setModifier(stateModifier)
                  .setCharging(chargingObserver)
                  .setIgnoreCharging(ignoreCharging)
                  .setLogger(logger)
                  .setPeriodic(periodic)
                  .setPeriodicEnableTime(periodicEnableTime)
                  .setPeriodicDisableTime(periodicDisableTime)
                  .setEnableService(getEnableServiceClass())
                  .setDisableService(getDisableServiceClass())
                  .build()
                  .run();
            }, throwable -> logger.e("%s onError Queuer queueShort", throwable.toString()),
            () -> SubscriptionHelper.unsubscribe(smallTimeQueuedSubscription));
  }

  private void queueLong() {
    final Class<? extends BaseLongTermService> serviceClass;
    if (type == null) {
      throw new IllegalStateException("QueueType is unset");
    } else if (type == QueuerType.ENABLE) {
      serviceClass = getEnableServiceClass();
    } else {
      serviceClass = getDisableServiceClass();
    }

    final Intent intent =
        BaseLongTermService.buildIntent(appContext, serviceClass, type, ignoreCharging, periodic,
            periodicEnableTime, periodicDisableTime);
    jobQueuerWrapper.cancel(intent);

    logger.d("Queue long term job with delay: %d", delayTime);
    final long triggerAtTime = System.currentTimeMillis() + delayTime;
    jobQueuerWrapper.set(intent, triggerAtTime);
  }

  @CheckResult @NonNull abstract Class<? extends BaseLongTermService> getEnableServiceClass();

  @CheckResult @NonNull abstract Class<? extends BaseLongTermService> getDisableServiceClass();
}
