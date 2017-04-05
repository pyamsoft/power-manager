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

package com.pyamsoft.powermanager.trigger;

import android.support.annotation.NonNull;
import com.pyamsoft.powermanager.trigger.db.PowerTriggerEntry;
import com.pyamsoft.pydroid.helper.DisposableHelper;
import com.pyamsoft.pydroid.presenter.SchedulerPresenter;
import io.reactivex.Scheduler;
import io.reactivex.disposables.Disposable;
import io.reactivex.disposables.Disposables;
import javax.inject.Inject;
import javax.inject.Named;
import timber.log.Timber;

class TriggerItemPresenter extends SchedulerPresenter {

  @NonNull private final TriggerItemInteractor interactor;
  @NonNull private Disposable updateDisposable = Disposables.empty();

  @Inject TriggerItemPresenter(@NonNull @Named("obs") Scheduler obsScheduler,
      @NonNull @Named("sub") Scheduler subScheduler, @NonNull TriggerItemInteractor interactor) {
    super(obsScheduler, subScheduler);
    this.interactor = interactor;
  }

  @Override protected void onStop() {
    super.onStop();
    updateDisposable = DisposableHelper.dispose(updateDisposable);
  }

  /**
   * public
   */
  void toggleEnabledState(@NonNull PowerTriggerEntry entry, boolean enabled,
      @NonNull TriggerToggleCallback callback) {
    updateDisposable = DisposableHelper.dispose(updateDisposable);
    updateDisposable = interactor.update(entry, enabled)
        .subscribeOn(getSubscribeScheduler())
        .observeOn(getObserveScheduler())
        .subscribe(callback::updateViewHolder, throwable -> Timber.e(throwable, "onError"));
  }

  interface TriggerToggleCallback {

    void updateViewHolder(@NonNull PowerTriggerEntry entry);
  }
}
