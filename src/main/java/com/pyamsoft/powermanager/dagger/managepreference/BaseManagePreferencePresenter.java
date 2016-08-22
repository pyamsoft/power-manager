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

package com.pyamsoft.powermanager.dagger.managepreference;

import android.support.annotation.NonNull;
import com.pyamsoft.powermanager.app.observer.InterestObserver;
import com.pyamsoft.pydroid.base.presenter.SchedulerPresenter;
import rx.Scheduler;

public abstract class BaseManagePreferencePresenter
    extends SchedulerPresenter<BaseManagePreferencePresenter.ManagePreferenceView> {

  @NonNull private static final String OBS_TAG = "BaseManagePreferencePresenter";
  @NonNull private final InterestObserver manageObserver;
  @NonNull private final BaseManagePreferenceInteractor interactor;

  protected BaseManagePreferencePresenter(@NonNull BaseManagePreferenceInteractor manageInteractor,
      @NonNull Scheduler observeScheduler, @NonNull Scheduler subscribeScheduler,
      @NonNull InterestObserver manageObserver) {
    super(observeScheduler, subscribeScheduler);
    this.interactor = manageInteractor;
    this.manageObserver = manageObserver;
  }

  @Override protected void onStart(@NonNull ManagePreferenceView view) {
    super.onStart(view);
    manageObserver.register(OBS_TAG, view::onManageSet, view::onManageUnset);
  }

  @Override protected void onStop(@NonNull ManagePreferenceView view) {
    super.onStop(view);
    manageObserver.unregister(OBS_TAG);
  }

  public void updateManage(boolean state) {
    interactor.updateManage(state);
  }

  public interface ManagePreferenceView {

    void onManageSet();

    void onManageUnset();
  }
}