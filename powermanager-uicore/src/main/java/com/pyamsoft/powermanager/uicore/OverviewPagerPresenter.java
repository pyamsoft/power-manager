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
import com.pyamsoft.powermanager.model.BooleanInterestModifier;
import com.pyamsoft.pydroid.helper.DisposableHelper;
import com.pyamsoft.pydroid.presenter.Presenter;
import com.pyamsoft.pydroid.presenter.SchedulerPresenter;
import io.reactivex.Observable;
import io.reactivex.Scheduler;
import io.reactivex.disposables.Disposable;
import io.reactivex.disposables.Disposables;
import javax.inject.Inject;
import timber.log.Timber;

public class OverviewPagerPresenter extends SchedulerPresenter<Presenter.Empty> {

  @SuppressWarnings("WeakerAccess") @NonNull final BooleanInterestModifier modifier;
  @NonNull private Disposable subscription = Disposables.empty();

  @Inject public OverviewPagerPresenter(@NonNull Scheduler observeScheduler,
      @NonNull Scheduler subscribeScheduler, @NonNull BooleanInterestModifier modifier) {
    super(observeScheduler, subscribeScheduler);
    this.modifier = modifier;
  }

  @CallSuper @Override protected void onUnbind() {
    super.onUnbind();
    subscription = DisposableHelper.unsubscribe(subscription);
  }

  public void wrapSet() {
    subscription = DisposableHelper.unsubscribe(subscription);
    subscription = Observable.fromCallable(() -> Boolean.TRUE)
        .subscribeOn(getSubscribeScheduler())
        .observeOn(getSubscribeScheduler())
        .subscribe(ignore -> modifier.set(), throwable -> Timber.e(throwable, "onError wrapSet"));
  }

  public void wrapUnset() {
    subscription = DisposableHelper.unsubscribe(subscription);
    subscription = Observable.fromCallable(() -> Boolean.TRUE)
        .subscribeOn(getSubscribeScheduler())
        .observeOn(getSubscribeScheduler())
        .subscribe(ignore -> modifier.unset(),
            throwable -> Timber.e(throwable, "onError wrapUnset"));
  }
}
