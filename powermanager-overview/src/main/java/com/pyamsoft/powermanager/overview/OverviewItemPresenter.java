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

package com.pyamsoft.powermanager.overview;

import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import com.pyamsoft.powermanager.model.states.States;
import com.pyamsoft.pydroid.helper.DisposableHelper;
import com.pyamsoft.pydroid.presenter.SchedulerPresenter;
import io.reactivex.Observable;
import io.reactivex.Scheduler;
import io.reactivex.disposables.Disposable;
import io.reactivex.disposables.Disposables;
import javax.inject.Inject;
import javax.inject.Named;
import timber.log.Timber;

class OverviewItemPresenter extends SchedulerPresenter {

  @NonNull private Disposable iconDisposable = Disposables.empty();

  @Inject OverviewItemPresenter(@Named("obs") Scheduler obsScheduler,
      @Named("sub") Scheduler subScheduler) {
    super(obsScheduler, subScheduler);
  }

  @Override protected void onStop() {
    super.onStop();
    iconDisposable = DisposableHelper.dispose(iconDisposable);
  }

  public void decideManageState(@NonNull States states, @NonNull ManageStateCallback callback) {
    iconDisposable = DisposableHelper.dispose(iconDisposable);
    iconDisposable = Observable.fromCallable(() -> {
      @DrawableRes final int icon;
      if (states == States.UNKNOWN) {
        icon = 0;
      } else {
        if (states == States.ENABLED) {
          icon = R.drawable.ic_check_box_24dp;
        } else {
          icon = R.drawable.ic_check_box_outline_24dp;
        }
      }
      return icon;
    }).subscribeOn(getSubscribeScheduler()).observeOn(getObserveScheduler()).subscribe(icon -> {
      if (icon == 0) {
        callback.onManageStateNone();
      } else {
        callback.onManageStateDecided(icon);
      }
    }, throwable -> Timber.e(throwable, "onError"));
  }

  interface ManageStateCallback {

    void onManageStateDecided(@DrawableRes int icon);

    void onManageStateNone();
  }
}
