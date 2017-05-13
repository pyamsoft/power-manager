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

class ExceptionPresenter extends SchedulerPresenter {

  @NonNull private final ExceptionInteractor interactor;

  ExceptionPresenter(@NonNull ExceptionInteractor interactor, @NonNull Scheduler observeScheduler,
      @NonNull Scheduler subscribeScheduler) {
    super(observeScheduler, subscribeScheduler);
    this.interactor = interactor;
  }

  /**
   * public
   */
  void setIgnoreCharging(boolean state, @NonNull ActionCallback callback) {
    disposeOnDestroy(interactor.setIgnoreCharging(state)
        .subscribeOn(getSubscribeScheduler())
        .observeOn(getObserveScheduler())
        .doAfterTerminate(callback::onComplete)
        .subscribe(() -> Timber.d("Set ignore charging state successfully: %s", state),
            throwable -> {
              Timber.e(throwable, "Error setting ignore charging");
              callback.onError(throwable);
            }));
  }

  /**
   * public
   */
  void getIgnoreCharging(@NonNull RetrieveCallback callback) {
    disposeOnDestroy(interactor.isIgnoreCharging()
        .subscribeOn(getSubscribeScheduler())
        .observeOn(getObserveScheduler())
        .doOnSuccess(booleanBooleanPair -> callback.onEnableRetrieved(booleanBooleanPair.first))
        .doAfterTerminate(callback::onComplete)
        .map(booleanBooleanPair -> booleanBooleanPair.second)
        .subscribe(callback::onStateRetrieved, throwable -> {
          Timber.e(throwable, "Error getting ignore charging");
          callback.onError(throwable);
        }));
  }

  /**
   * public
   */
  void setIgnoreWear(boolean state, @NonNull ActionCallback callback) {
    disposeOnDestroy(interactor.setIgnoreWear(state)
        .subscribeOn(getSubscribeScheduler())
        .observeOn(getObserveScheduler())
        .doAfterTerminate(callback::onComplete)
        .subscribe(() -> Timber.d("Set ignore wear state successfully: %s", state), throwable -> {
          Timber.e(throwable, "Error setting ignore wear");
          callback.onError(throwable);
        }));
  }

  /**
   * public
   */
  void getIgnoreWear(@NonNull RetrieveCallback callback) {
    disposeOnDestroy(interactor.isIgnoreWear()
        .subscribeOn(getSubscribeScheduler())
        .observeOn(getObserveScheduler())
        .doOnSuccess(booleanBooleanPair -> callback.onEnableRetrieved(booleanBooleanPair.first))
        .doAfterTerminate(callback::onComplete)
        .map(booleanBooleanPair -> booleanBooleanPair.second)
        .subscribe(callback::onStateRetrieved, throwable -> {
          Timber.e(throwable, "Error getting ignore wear");
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
