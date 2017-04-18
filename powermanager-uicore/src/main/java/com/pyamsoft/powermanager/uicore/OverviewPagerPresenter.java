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
import com.pyamsoft.powermanager.model.StateModifier;
import com.pyamsoft.pydroid.presenter.SchedulerPresenter;
import io.reactivex.Observable;
import io.reactivex.Scheduler;
import javax.inject.Inject;
import timber.log.Timber;

public class OverviewPagerPresenter extends SchedulerPresenter {

  @SuppressWarnings("WeakerAccess") @NonNull final StateModifier modifier;

  @Inject public OverviewPagerPresenter(@NonNull Scheduler observeScheduler,
      @NonNull Scheduler subscribeScheduler, @NonNull StateModifier modifier) {
    super(observeScheduler, subscribeScheduler);
    this.modifier = modifier;
  }

  /**
   * public
   */
  void wrapSet() {
    disposeOnStop(Observable.fromCallable(() -> Boolean.TRUE)
        .subscribeOn(getSubscribeScheduler())
        .observeOn(getSubscribeScheduler())
        .subscribe(ignore -> modifier.set(), throwable -> Timber.e(throwable, "onError wrapSet")));
  }

  /**
   * public
   */
  void wrapUnset() {
    disposeOnStop(Observable.fromCallable(() -> Boolean.TRUE)
        .subscribeOn(getSubscribeScheduler())
        .observeOn(getSubscribeScheduler())
        .subscribe(ignore -> modifier.unset(),
            throwable -> Timber.e(throwable, "onError wrapUnset")));
  }
}
