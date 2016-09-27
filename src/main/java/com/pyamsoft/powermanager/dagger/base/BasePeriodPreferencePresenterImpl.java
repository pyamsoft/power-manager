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
import com.pyamsoft.powermanager.app.base.BasePeriodPreferencePresenter;
import com.pyamsoft.powermanager.app.observer.InterestObserver;
import com.pyamsoft.pydroidrx.SchedulerPresenter;
import rx.Scheduler;

public abstract class BasePeriodPreferencePresenterImpl
    extends SchedulerPresenter<BasePeriodPreferencePresenter.PeriodPreferenceView>
    implements BasePeriodPreferencePresenter {

  @SuppressWarnings("WeakerAccess") @NonNull static final String OBS_TAG =
      "BasePeriodPreferencePresenter";
  @SuppressWarnings("WeakerAccess") @NonNull final InterestObserver observer;
  @NonNull private final BasePeriodPreferenceInteractor interactor;

  protected BasePeriodPreferencePresenterImpl(@NonNull BasePeriodPreferenceInteractor interactor,
      @NonNull Scheduler observeScheduler, @NonNull Scheduler subscribeScheduler,
      @NonNull InterestObserver periodObserver) {
    super(observeScheduler, subscribeScheduler);
    this.interactor = interactor;
    this.observer = periodObserver;
  }

  @Override protected void onBind() {
    super.onBind();
    getView(periodPreferenceView -> observer.register(OBS_TAG, periodPreferenceView::onPeriodicSet,
        periodPreferenceView::onPeriodicUnset));
  }

  @Override protected void onUnbind() {
    super.onUnbind();
    observer.unregister(OBS_TAG);
  }

  @Override public void updatePeriodic(boolean state) {
    interactor.updatePeriodic(state);
  }
}
