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

import android.support.annotation.NonNull;
import com.pyamsoft.powermanager.model.InterestObserver;
import com.pyamsoft.pydroid.presenter.Presenter;
import com.pyamsoft.pydroid.rx.SchedulerPresenter;
import com.pyamsoft.pydroid.rx.SubscriptionHelper;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;
import rx.Scheduler;
import rx.Subscription;
import rx.subscriptions.Subscriptions;
import timber.log.Timber;

public class ManagePreferencePresenterImpl extends SchedulerPresenter<Presenter.Empty>
    implements ManagePreferencePresenter {

  @SuppressWarnings("WeakerAccess") @NonNull static final String OBS_TAG =
      "BaseManagePreferencePresenter";
  @SuppressWarnings("WeakerAccess") @NonNull final InterestObserver manageObserver;
  @NonNull private final ManagePreferenceInteractor interactor;
  @SuppressWarnings("WeakerAccess") @NonNull Subscription onboardingSubscription =
      Subscriptions.empty();

  @Inject public ManagePreferencePresenterImpl(@NonNull ManagePreferenceInteractor manageInteractor,
      @NonNull Scheduler observeScheduler, @NonNull Scheduler subscribeScheduler,
      @NonNull InterestObserver manageObserver) {
    super(observeScheduler, subscribeScheduler);
    this.interactor = manageInteractor;
    this.manageObserver = manageObserver;
  }

  @Override public void registerObserver(@NonNull ManageCallback callback) {
    manageObserver.register(OBS_TAG, callback::onManageSet, callback::onManageUnset);
  }

  @Override protected void onUnbind() {
    super.onUnbind();
    manageObserver.unregister(OBS_TAG);
    SubscriptionHelper.unsubscribe(onboardingSubscription);
  }

  @Override public void setShownOnBoarding() {
    interactor.setOnboarding();
  }

  @Override public void showOnboardingIfNeeded(@NonNull OnboardingCallback callback) {
    SubscriptionHelper.unsubscribe(onboardingSubscription);
    onboardingSubscription = interactor.hasShownOnboarding()
        .delay(1, TimeUnit.SECONDS)
        .subscribeOn(getSubscribeScheduler())
        .observeOn(getObserveScheduler())
        .subscribe(onboard -> {
              if (!onboard) {
                callback.onShowOnboarding();
              }
            }, throwable -> Timber.e(throwable, "onError onShowOnboarding"),
            () -> SubscriptionHelper.unsubscribe(onboardingSubscription));
  }

  @Override public void dismissOnboarding(@NonNull OnboardingDismissCallback callback) {
    SubscriptionHelper.unsubscribe(onboardingSubscription);
    callback.onDismissOnboarding();
  }

  @Override public void checkManagePermission(@NonNull ManagePermissionCallback callback) {
    // Override if you need to check permissions
  }
}
