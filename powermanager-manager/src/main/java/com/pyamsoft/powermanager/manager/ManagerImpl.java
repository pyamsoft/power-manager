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
import com.pyamsoft.powermanager.model.Manager;
import com.pyamsoft.pydroid.rx.SchedulerHelper;
import com.pyamsoft.pydroid.rx.SubscriptionHelper;
import rx.Observable;
import rx.Scheduler;
import rx.Subscription;
import rx.functions.Func1;
import timber.log.Timber;

abstract class ManagerImpl implements Manager {

  @SuppressWarnings("WeakerAccess") @NonNull final ManagerInteractor interactor;
  @NonNull private final Scheduler scheduler;
  @SuppressWarnings("WeakerAccess") @Nullable Subscription cancelSubscription;
  @SuppressWarnings("WeakerAccess") @Nullable Subscription setSubscription;
  @SuppressWarnings("WeakerAccess") @Nullable Subscription unsetSubscription;

  ManagerImpl(@NonNull ManagerInteractor interactor, @NonNull Scheduler scheduler) {
    this.interactor = interactor;
    this.scheduler = scheduler;
    SchedulerHelper.enforceSubscribeScheduler(scheduler);
  }

  @NonNull @CheckResult Scheduler getScheduler() {
    return scheduler;
  }

  @Override public void cancel(@NonNull Runnable onCancel) {
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

  @Override public void queueSet(@Nullable Runnable onSet) {
    SubscriptionHelper.unsubscribe(setSubscription);
    setSubscription = interactor.isManaged().flatMap(managed -> {
      if (managed) {
        Timber.d("%s: Is original state enabled?", interactor.getJobTag());
        return interactor.isOriginalStateEnabled();
      } else {
        Timber.w("%s: Is not managed, return empty", interactor.getJobTag());
        return Observable.empty();
      }
    }).subscribeOn(scheduler).observeOn(scheduler).subscribe(originalState -> {
      // Technically can ignore this as if we are here we are non-empty
      // If we are non empty it means we pass the test
      if (originalState) {
        Timber.d("%s: Queued up a new enable job", interactor.getJobTag());
        interactor.queueEnableJob();
        Timber.d("%s: Unset original state", interactor.getJobTag());
        interactor.setOriginalStateEnabled(false);
      }
    }, throwable -> Timber.e(throwable, "%s: onError queueSet", interactor.getJobTag()), () -> {
      if (onSet != null) {
        onSet.run();
      }
      SubscriptionHelper.unsubscribe(setSubscription);
    });
  }

  @Override public void queueUnset(@Nullable Runnable onUnset) {
    SubscriptionHelper.unsubscribe(unsetSubscription);
    unsetSubscription = interactor.isManaged()
        .map(baseResult -> {
          Timber.d("%s: Unset original state", interactor.getJobTag());
          interactor.setOriginalStateEnabled(false);
          return baseResult;
        })
        .flatMap(managed -> {
          if (managed) {
            Timber.d("%s: Is original state enabled?", interactor.getJobTag());
            return interactor.isEnabled();
          } else {
            Timber.w("%s: Is not managed, return empty", interactor.getJobTag());
            return Observable.empty();
          }
        })
        .map(enabled -> {
          Timber.d("%s: Set original state enabled: %s", interactor.getJobTag(), enabled);
          interactor.setOriginalStateEnabled(enabled);
          return enabled;
        })
        .flatMap(accountForWearableBeforeDisable())
        .subscribeOn(scheduler)
        .observeOn(scheduler)
        .subscribe(shouldQueue -> {
              // Only queue a disable job if the radio is not ignored
              if (shouldQueue) {
                Timber.d("%s: Queued up a new disable job", interactor.getJobTag());
                interactor.queueDisableJob();
              }
            }, throwable -> Timber.e(throwable, "%s: onError queueUnset", interactor.getJobTag()),
            () -> {
              if (onUnset != null) {
                onUnset.run();
              }
              SubscriptionHelper.unsubscribe(unsetSubscription);
            });
  }

  @CallSuper @Override public void cleanup() {
    interactor.destroy();
    SubscriptionHelper.unsubscribe(cancelSubscription, setSubscription, unsetSubscription);

    // Reset the device back to its original state when the Service is cleaned up
    queueSet(null);
  }

  @CheckResult @NonNull
  abstract Func1<Boolean, Observable<Boolean>> accountForWearableBeforeDisable();
}
