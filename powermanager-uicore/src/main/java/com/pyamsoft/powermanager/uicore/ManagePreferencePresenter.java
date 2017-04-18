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
import com.pyamsoft.pydroid.presenter.SchedulerPresenter;
import io.reactivex.Scheduler;
import javax.inject.Inject;
import timber.log.Timber;

public class ManagePreferencePresenter extends SchedulerPresenter implements OnboardingPresenter {

  @NonNull private final ManagePreferenceInteractor interactor;

  @Inject public ManagePreferencePresenter(@NonNull ManagePreferenceInteractor manageInteractor,
      @NonNull Scheduler observeScheduler, @NonNull Scheduler subscribeScheduler) {
    super(observeScheduler, subscribeScheduler);
    this.interactor = manageInteractor;
  }

  public void checkManagePermission(@NonNull ManagePermissionCallback callback) {
    // Override if you need to check permissions
  }

  @Override public void showOnboardingIfNeeded(@NonNull OnboardingCallback callback) {
    disposeOnStop(interactor.hasShownOnboarding()
        .subscribeOn(getSubscribeScheduler())
        .observeOn(getObserveScheduler())
        .subscribe(onboard -> {
          if (!onboard) {
            callback.onShowOnboarding();
          }
        }, throwable -> Timber.e(throwable, "onError onShowOnboarding")));
  }

  interface ManagePermissionCallback {

    void onBegin();

    void onManagePermissionCallback(boolean hasPermission);

    void onComplete();
  }
}
