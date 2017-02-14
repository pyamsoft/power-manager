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
import com.pyamsoft.pydroid.helper.SchedulerHelper;
import com.pyamsoft.pydroid.helper.SubscriptionHelper;
import rx.Scheduler;
import rx.Subscription;
import timber.log.Timber;

public class Manager {

  @SuppressWarnings("WeakerAccess") @NonNull final ManagerInteractor interactor;
  @NonNull private final Scheduler scheduler;
  @SuppressWarnings("WeakerAccess") @Nullable Subscription cancelSubscription;
  @SuppressWarnings("WeakerAccess") @Nullable Subscription setSubscription;
  @SuppressWarnings("WeakerAccess") @Nullable Subscription unsetSubscription;

  Manager(@NonNull ManagerInteractor interactor, @NonNull Scheduler scheduler) {
    this.interactor = interactor;
    this.scheduler = scheduler;
    SchedulerHelper.enforceSubscribeScheduler(scheduler);
  }

  @NonNull @CheckResult Scheduler getScheduler() {
    return scheduler;
  }

  public void cancel(@NonNull Runnable onCancel) {
    SubscriptionHelper.unsubscribe(cancelSubscription);
    cancelSubscription = interactor.cancelJobs()
        .subscribeOn(getScheduler())
        .observeOn(getScheduler())
        .subscribe(cancelled -> Timber.d("Job cancelled: %s", interactor.getJobTag()),
            throwable -> Timber.e(throwable, "onError cancelling manager"), () -> {
              onCancel.run();
              SubscriptionHelper.unsubscribe(cancelSubscription);
            });
  }

  public void queueSet(@Nullable Runnable onSet) {
    SubscriptionHelper.unsubscribe(setSubscription);
    setSubscription = interactor.queueSet()
        .subscribeOn(scheduler)
        .observeOn(scheduler)
        .subscribe(originalState -> {
          // Technically can ignore this as if we are here we are non-empty
          // If we are non empty it means we pass the test
          if (originalState) {
            Timber.d("%s: Queued up a new enable job", interactor.getJobTag());
          }
        }, throwable -> Timber.e(throwable, "%s: onError queueSet", interactor.getJobTag()), () -> {
          if (onSet != null) {
            onSet.run();
          }
          SubscriptionHelper.unsubscribe(setSubscription);
        });
  }

  public void queueUnset(@Nullable Runnable onUnset) {
    SubscriptionHelper.unsubscribe(unsetSubscription);
    unsetSubscription = interactor.queueUnset()
        .subscribeOn(scheduler)
        .observeOn(scheduler)
        .subscribe(shouldQueue -> {
              // Only queue a disable job if the radio is not ignored
              if (shouldQueue) {
                Timber.d("%s: Queued up a new disable job", interactor.getJobTag());
              }
            }, throwable -> Timber.e(throwable, "%s: onError queueUnset", interactor.getJobTag()),
            () -> {
              if (onUnset != null) {
                onUnset.run();
              }
              SubscriptionHelper.unsubscribe(unsetSubscription);
            });
  }

  @CallSuper public void cleanup() {
    interactor.destroy();
    SubscriptionHelper.unsubscribe(cancelSubscription, setSubscription, unsetSubscription);

    // Reset the device back to its original state when the Service is cleaned up
    queueSet(null);
  }
}
