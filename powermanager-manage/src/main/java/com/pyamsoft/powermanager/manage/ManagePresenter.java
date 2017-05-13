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
import com.pyamsoft.powermanager.manage.bus.ManageChangeEvent;
import com.pyamsoft.pydroid.bus.EventBus;
import io.reactivex.Scheduler;
import timber.log.Timber;

abstract class ManagePresenter extends TargetPresenter {

  @NonNull private final ManageInteractor interactor;

  ManagePresenter(@NonNull ManageInteractor interactor, @NonNull Scheduler observeScheduler,
      @NonNull Scheduler subscribeScheduler) {
    super(observeScheduler, subscribeScheduler);
    this.interactor = interactor;
  }

  /**
   * public
   */
  void setManaged(boolean state, @NonNull ActionCallback callback) {
    disposeOnDestroy(interactor.setManaged(state)
        .subscribeOn(getSubscribeScheduler())
        .observeOn(getObserveScheduler())
        .doAfterTerminate(() -> EventBus.get().publish(ManageChangeEvent.create(getTarget())))
        .doAfterTerminate(callback::onComplete)
        .subscribe(() -> Timber.d("Set managed state successfully: %s", state), throwable -> {
          Timber.e(throwable, "Error setting managed");
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
        .doOnSuccess(booleanBooleanPair -> callback.onEnableRetrieved(booleanBooleanPair.first))
        .doAfterTerminate(callback::onComplete)
        .map(booleanBooleanPair -> booleanBooleanPair.second)
        .subscribe(callback::onStateRetrieved, throwable -> {
          Timber.e(throwable, "Error getting managed");
          callback.onError(throwable);
        }));
  }

  interface ActionCallback {

    void onError(@NonNull Throwable throwable);

    void onComplete();
  }

  interface RetrieveCallback {

    void onEnableRetrieved(boolean enabled);

    void onStateRetrieved(boolean enabled);

    void onError(@NonNull Throwable throwable);

    void onComplete();
  }
}
