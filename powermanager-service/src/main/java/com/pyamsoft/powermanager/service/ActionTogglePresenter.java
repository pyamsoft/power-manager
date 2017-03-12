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

package com.pyamsoft.powermanager.service;

import android.support.annotation.NonNull;
import com.pyamsoft.pydroid.helper.DisposableHelper;
import com.pyamsoft.pydroid.presenter.Presenter;
import com.pyamsoft.pydroid.presenter.SchedulerPresenter;
import io.reactivex.Scheduler;
import io.reactivex.disposables.Disposable;
import io.reactivex.disposables.Disposables;
import javax.inject.Inject;
import javax.inject.Named;
import timber.log.Timber;

class ActionTogglePresenter extends SchedulerPresenter<Presenter.Empty> {

  @NonNull private final ActionToggleInteractor interactor;
  @NonNull private Disposable subscription = Disposables.empty();

  @Inject ActionTogglePresenter(@NonNull ActionToggleInteractor interactor,
      @Named("obs") Scheduler obsScheduler, @Named("sub") Scheduler subScheduler) {
    super(obsScheduler, subScheduler);
    this.interactor = interactor;
  }

  @Override protected void onUnbind() {
    super.onUnbind();
    subscription = DisposableHelper.unsubscribe(subscription);
  }

  public void toggleForegroundState(@NonNull ForegroundStateCallback callback) {
    subscription = DisposableHelper.unsubscribe(subscription);
    subscription = interactor.toggleEnabledState()
        .subscribeOn(getSubscribeScheduler())
        .observeOn(getObserveScheduler())
        .subscribe(callback::onForegroundStateToggled,
            throwable -> Timber.e(throwable, "onError toggleForegroundState"));
  }

  public interface ForegroundStateCallback {

    void onForegroundStateToggled(boolean state);
  }
}
