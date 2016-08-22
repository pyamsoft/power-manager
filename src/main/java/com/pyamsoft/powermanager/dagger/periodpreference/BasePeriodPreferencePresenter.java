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

package com.pyamsoft.powermanager.dagger.periodpreference;

import android.support.annotation.NonNull;
import com.pyamsoft.powermanager.app.observer.InterestObserver;
import com.pyamsoft.pydroid.base.presenter.SchedulerPresenter;
import rx.Scheduler;

public abstract class BasePeriodPreferencePresenter
    extends SchedulerPresenter<BasePeriodPreferencePresenter.PeriodPreferenceView> {

  @NonNull private static final String OBS_TAG = "BasePeriodPreferencePresenter";
  @NonNull private final InterestObserver observer;
  @NonNull private final BasePeriodPreferenceInteractor interactor;

  protected BasePeriodPreferencePresenter(@NonNull BasePeriodPreferenceInteractor interactor,
      @NonNull Scheduler observeScheduler, @NonNull Scheduler subscribeScheduler,
      @NonNull InterestObserver periodObserver) {
    super(observeScheduler, subscribeScheduler);
    this.interactor = interactor;
    this.observer = periodObserver;
  }

  @Override protected void onStart(@NonNull PeriodPreferenceView view) {
    super.onStart(view);
    observer.register(OBS_TAG, view::onPeriodicSet, view::onPeriodicUnset);
  }

  @Override protected void onStop(@NonNull PeriodPreferenceView view) {
    super.onStop(view);
    observer.unregister(OBS_TAG);
  }

  public void updatePeriodic(boolean state) {
    interactor.updatePeriodic(state);
  }

  public interface PeriodPreferenceView {

    void onPeriodicSet();

    void onPeriodicUnset();
  }
}
