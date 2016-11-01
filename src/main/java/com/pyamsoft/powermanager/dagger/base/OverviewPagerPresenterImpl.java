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

package com.pyamsoft.powermanager.dagger.base;

import android.support.annotation.NonNull;
import com.pyamsoft.powermanager.app.base.OverviewPagerPresenter;
import com.pyamsoft.powermanager.app.modifier.BooleanInterestModifier;
import com.pyamsoft.pydroidrx.SchedulerPresenter;
import com.pyamsoft.pydroidrx.SubscriptionHelper;
import rx.Observable;
import rx.Scheduler;
import rx.Subscription;
import rx.subscriptions.Subscriptions;
import timber.log.Timber;

public abstract class OverviewPagerPresenterImpl
    extends SchedulerPresenter<OverviewPagerPresenter.View>
    implements OverviewPagerPresenter {

  @SuppressWarnings("WeakerAccess") @NonNull final BooleanInterestModifier modifier;
  @SuppressWarnings("WeakerAccess") @NonNull Subscription subscription = Subscriptions.empty();

  protected OverviewPagerPresenterImpl(@NonNull Scheduler observeScheduler,
      @NonNull Scheduler subscribeScheduler, @NonNull BooleanInterestModifier modifier) {
    super(observeScheduler, subscribeScheduler);
    this.modifier = modifier;
  }

  @Override protected void onUnbind() {
    super.onUnbind();
    SubscriptionHelper.unsubscribe(subscription);
  }

  @Override public void wrapSet() {
    SubscriptionHelper.unsubscribe(subscription);
    subscription = Observable.defer(() -> {
      modifier.set();
      return Observable.just(true);
    })
        .subscribeOn(getSubscribeScheduler())
        .observeOn(getObserveScheduler())
        .subscribe(aBoolean -> Timber.d("Finished wrapped set call"),
            throwable -> Timber.e(throwable, "onError wrapSet"),
            () -> SubscriptionHelper.unsubscribe(subscription));
  }

  @Override public void wrapUnset() {
    SubscriptionHelper.unsubscribe(subscription);
    subscription = Observable.defer(() -> {
      modifier.unset();
      return Observable.just(true);
    })
        .subscribeOn(getSubscribeScheduler())
        .observeOn(getObserveScheduler())
        .subscribe(aBoolean -> Timber.d("Finished wrapped unset call"),
            throwable -> Timber.e(throwable, "onError wrapUnset"),
            () -> SubscriptionHelper.unsubscribe(subscription));
  }
}
