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

package com.pyamsoft.powermanager.dagger.overview;

import android.support.annotation.NonNull;
import com.pyamsoft.powermanager.app.overview.OverviewPresenter;
import com.pyamsoft.pydroidrx.SchedulerPresenter;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;
import rx.Scheduler;
import rx.Subscription;
import rx.subscriptions.Subscriptions;
import timber.log.Timber;

class OverviewPresenterImpl extends SchedulerPresenter<OverviewPresenter.View>
    implements OverviewPresenter {

  @NonNull private final OverviewInteractor interactor;
  @NonNull private Subscription onboardingSubscription = Subscriptions.empty();

  @Inject OverviewPresenterImpl(@NonNull OverviewInteractor interactor,
      @NonNull Scheduler observeScheduler, @NonNull Scheduler subscribeScheduler) {
    super(observeScheduler, subscribeScheduler);
    this.interactor = interactor;
  }

  @Override protected void onUnbind() {
    super.onUnbind();
    unsubOnboarding();
  }

  @Override protected void onBind() {
    super.onBind();
    showOnBoarding();
  }

  @SuppressWarnings("WeakerAccess") void showOnBoarding() {
    unsubOnboarding();
    onboardingSubscription = interactor.hasShownOnboarding()
        .delay(1, TimeUnit.SECONDS)
        .subscribeOn(getSubscribeScheduler())
        .observeOn(getObserveScheduler())
        .subscribe(onboard -> {
          if (!onboard) {
            getView(View::showOnBoarding);
          }
        }, throwable -> Timber.e(throwable, "onError showOnBoarding"), this::unsubOnboarding);
  }

  @SuppressWarnings("WeakerAccess") void unsubOnboarding() {
    if (!onboardingSubscription.isUnsubscribed()) {
      onboardingSubscription.unsubscribe();
    }
  }

  @Override public void setShownOnBoarding() {
    interactor.setShownOnboarding();
  }
}
