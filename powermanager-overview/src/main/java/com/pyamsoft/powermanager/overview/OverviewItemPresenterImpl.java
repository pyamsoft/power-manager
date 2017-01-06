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

package com.pyamsoft.powermanager.overview;

import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.pyamsoft.powermanager.model.BooleanInterestObserver;
import com.pyamsoft.pydroid.rx.SchedulerPresenter;
import com.pyamsoft.pydroid.rx.SubscriptionHelper;
import javax.inject.Inject;
import rx.Observable;
import rx.Scheduler;
import rx.Subscription;
import timber.log.Timber;

class OverviewItemPresenterImpl extends SchedulerPresenter<OverviewItemPresenter.View>
    implements OverviewItemPresenter {

  @SuppressWarnings("WeakerAccess") @Nullable Subscription iconSubscription;

  @Inject OverviewItemPresenterImpl(@NonNull Scheduler observeScheduler,
      @NonNull Scheduler subscribeScheduler) {
    super(observeScheduler, subscribeScheduler);
  }

  @Override protected void onUnbind() {
    super.onUnbind();
    SubscriptionHelper.unsubscribe(iconSubscription);
  }

  @Override public void decideManageState(@Nullable BooleanInterestObserver observer) {
    SubscriptionHelper.unsubscribe(iconSubscription);
    iconSubscription = Observable.fromCallable(() -> {
      @DrawableRes final int icon;
      if (observer == null) {
        icon = 0;
      } else {
        if (observer.is()) {
          icon = R.drawable.ic_check_box_24dp;
        } else {
          icon = R.drawable.ic_check_box_outline_24dp;
        }
      }
      return icon;
    })
        .subscribeOn(getSubscribeScheduler())
        .observeOn(getObserveScheduler())
        .subscribe(icon -> getView(view -> {
              if (icon == 0) {
                view.onManageStateNone();
              } else {
                view.onManageStateDecided(icon);
              }
            }), throwable -> Timber.e(throwable, "onError"),
            () -> SubscriptionHelper.unsubscribe(iconSubscription));
  }
}
