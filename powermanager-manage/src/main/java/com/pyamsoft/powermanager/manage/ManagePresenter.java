/*
 * Copyright 2017 Peter Kenji Yamanaka
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

package com.pyamsoft.powermanager.manage;

import android.support.annotation.NonNull;
import com.pyamsoft.pydroid.presenter.SchedulerPresenter;
import io.reactivex.Scheduler;
import timber.log.Timber;

class ManagePresenter extends SchedulerPresenter {

  @NonNull private final ManageInteractor interactor;

  ManagePresenter(@NonNull ManageInteractor interactor, @NonNull Scheduler observeScheduler,
      @NonNull Scheduler subscribeScheduler) {
    super(observeScheduler, subscribeScheduler);
    this.interactor = interactor;
  }

  /**
   * public
   */
  void setManaged(@NonNull ActionCallback callback) {
    disposeOnDestroy(interactor.setManaged()
        .subscribeOn(getSubscribeScheduler())
        .observeOn(getObserveScheduler())
        .doAfterTerminate(callback::onComplete)
        .subscribe(callback::onSuccess, throwable -> {
          Timber.e(throwable, "Error setting managed");
          callback.onError(throwable);
        }));
  }

  /**
   * public
   */
  void setUnManaged(@NonNull ActionCallback callback) {
    disposeOnDestroy(interactor.setUnManaged()
        .subscribeOn(getSubscribeScheduler())
        .observeOn(getObserveScheduler())
        .doAfterTerminate(callback::onComplete)
        .subscribe(callback::onSuccess, throwable -> {
          Timber.e(throwable, "Error setting unmanaged");
          callback.onError(throwable);
        }));
  }

  /**
   * public
   */
  void getState(@NonNull RetrieveCallback callback) {
    disposeOnDestroy(interactor.isManaged()
        .subscribeOn(getSubscribeScheduler())
        .observeOn(getObserveScheduler())
        .doAfterTerminate(callback::onComplete)
        .subscribe(callback::onRetrieved, throwable -> {
          Timber.e(throwable, "Error setting unmanaged");
          callback.onError(throwable);
        }));
  }

  interface ActionCallback {

    void onSuccess();

    void onError(@NonNull Throwable throwable);

    void onComplete();
  }

  interface RetrieveCallback {

    void onRetrieved(boolean managed);

    void onError(@NonNull Throwable throwable);

    void onComplete();
  }
}
