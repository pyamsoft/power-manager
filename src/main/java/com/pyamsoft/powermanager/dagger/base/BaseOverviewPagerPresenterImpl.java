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
import com.pyamsoft.powermanager.app.base.BaseOverviewPagerPresenter;
import com.pyamsoft.powermanager.app.modifier.BooleanInterestModifier;
import com.pyamsoft.pydroid.presenter.SchedulerPresenter;
import rx.Observable;
import rx.Scheduler;
import rx.Subscription;
import rx.subscriptions.Subscriptions;
import timber.log.Timber;

public abstract class BaseOverviewPagerPresenterImpl
    extends SchedulerPresenter<BaseOverviewPagerPresenter.View>
    implements BaseOverviewPagerPresenter {

  @SuppressWarnings("WeakerAccess") @NonNull final BooleanInterestModifier modifier;
  @NonNull private Subscription subscription = Subscriptions.empty();

  protected BaseOverviewPagerPresenterImpl(@NonNull Scheduler observeScheduler,
      @NonNull Scheduler subscribeScheduler, @NonNull BooleanInterestModifier modifier) {
    super(observeScheduler, subscribeScheduler);
    this.modifier = modifier;
  }

  @Override protected void onUnbind() {
    super.onUnbind();
    unsubscribe();
  }

  @SuppressWarnings("WeakerAccess") void unsubscribe() {
    if (!subscription.isUnsubscribed()) {
      subscription.unsubscribe();
    }
  }

  @Override public void wrapSet() {
    unsubscribe();
    subscription = Observable.defer(() -> {
      modifier.set();
      return Observable.just(true);
    })
        .subscribeOn(getSubscribeScheduler())
        .observeOn(getObserveScheduler())
        .subscribe(aBoolean -> Timber.d("Finished wrapped set call"),
            throwable -> Timber.e(throwable, "onError wrapSet"), this::unsubscribe);
  }

  @Override public void wrapUnset() {
    unsubscribe();
    subscription = Observable.defer(() -> {
      modifier.unset();
      return Observable.just(true);
    })
        .subscribeOn(getSubscribeScheduler())
        .observeOn(getObserveScheduler())
        .subscribe(aBoolean -> Timber.d("Finished wrapped unset call"),
            throwable -> Timber.e(throwable, "onError wrapUnset"), this::unsubscribe);
  }
}
