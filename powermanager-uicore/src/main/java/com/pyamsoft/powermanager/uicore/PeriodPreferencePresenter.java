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

package com.pyamsoft.powermanager.uicore;

import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import com.pyamsoft.powermanager.model.InterestObserver;
import com.pyamsoft.pydroid.helper.SubscriptionHelper;
import com.pyamsoft.pydroid.presenter.Presenter;
import com.pyamsoft.pydroid.presenter.SchedulerPresenter;
import javax.inject.Inject;
import rx.Scheduler;
import rx.Subscription;
import rx.subscriptions.Subscriptions;
import timber.log.Timber;

public class PeriodPreferencePresenter extends SchedulerPresenter<Presenter.Empty>
    implements OnboardingPresenter {

  @NonNull private static final String OBS_TAG = "PeriodPreferencePresenter";
  @NonNull private final InterestObserver observer;
  @NonNull private final PeriodPreferenceInteractor interactor;
  @NonNull private Subscription onboardingSubscription = Subscriptions.empty();

  @Inject public PeriodPreferencePresenter(@NonNull PeriodPreferenceInteractor interactor,
      @NonNull Scheduler observeScheduler, @NonNull Scheduler subscribeScheduler,
      @NonNull InterestObserver periodObserver) {
    super(observeScheduler, subscribeScheduler);
    this.interactor = interactor;
    this.observer = periodObserver;
  }

  @CallSuper @Override protected void onUnbind() {
    super.onUnbind();
    observer.unregister(OBS_TAG);
    onboardingSubscription = SubscriptionHelper.unsubscribe(onboardingSubscription);
  }

  public void registerObserver(@NonNull PeriodicCallback callback) {
    observer.register(OBS_TAG, callback::onPeriodicSet, callback::onPeriodicUnset);
  }

  @Override public void setShownOnBoarding() {
    interactor.setOnboarding();
  }

  @Override public void showOnboardingIfNeeded(@NonNull OnboardingCallback callback) {
    onboardingSubscription = SubscriptionHelper.unsubscribe(onboardingSubscription);
    onboardingSubscription = interactor.hasShownOnboarding()
        .subscribeOn(getSubscribeScheduler())
        .observeOn(getObserveScheduler())
        .subscribe(onboard -> {
          if (!onboard) {
            callback.onShowOnboarding();
          }
        }, throwable -> Timber.e(throwable, "onError onShowOnboarding"));
  }

  @Override public void dismissOnboarding(@NonNull OnboardingDismissCallback callback) {
    onboardingSubscription = SubscriptionHelper.unsubscribe(onboardingSubscription);
    callback.onDismissOnboarding();
  }

  interface PeriodicCallback {

    void onPeriodicSet();

    void onPeriodicUnset();
  }
}
