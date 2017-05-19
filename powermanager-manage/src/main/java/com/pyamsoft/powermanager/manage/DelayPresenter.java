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
import android.support.annotation.Nullable;
import com.pyamsoft.pydroid.helper.DisposableHelper;
import com.pyamsoft.pydroid.presenter.SchedulerPresenter;
import io.reactivex.Scheduler;
import io.reactivex.disposables.Disposable;
import javax.inject.Inject;
import javax.inject.Named;
import timber.log.Timber;

class DelayPresenter extends SchedulerPresenter {

  @NonNull private final DelayInteractor interactor;
  @NonNull private Disposable customTimeChangeDisposable = DisposableHelper.dispose(null);

  @Inject DelayPresenter(@NonNull @Named("obs") Scheduler observeScheduler,
      @NonNull @Named("sub") Scheduler subscribeScheduler, @NonNull DelayInteractor interactor) {
    super(observeScheduler, subscribeScheduler);
    this.interactor = interactor;
  }

  @Override protected void onStop() {
    super.onStop();
    stopListeningCustomTimeChanges();
  }

  /**
   * public
   */
  void submitCustomTimeChange(@NonNull String text, boolean instant) {
    Timber.d("Custom time changed: %s instant? %s", text, instant);
    interactor.acceptCustomTimeChange(text, instant);
  }

  /**
   * public
   */
  void stopListeningCustomTimeChanges() {
    customTimeChangeDisposable = DisposableHelper.dispose(customTimeChangeDisposable);
  }

  /**
   * public
   */
  void listenForCustomTimeChanges(@NonNull CustomTimeChangedCallback callback) {
    stopListeningCustomTimeChanges();
    customTimeChangeDisposable = interactor.listenCustomTimeChanges()
        .subscribeOn(getSubscribeScheduler())
        .observeOn(getObserveScheduler())
        .subscribe(pair -> {
          callback.onCustomTimeInputError(pair.first);
          callback.onCustomTimeChanged(pair.second);
        }, throwable -> {
          Timber.e(throwable, "Error listen custom time change");
          callback.onError(throwable);
        });
  }

  /**
   * public
   */
  void setPresetDelayTime(long time, @NonNull ActionCallback callback) {
    disposeOnDestroy(interactor.setDelayTime(time)
        .subscribeOn(getSubscribeScheduler())
        .observeOn(getObserveScheduler())
        .subscribe(() -> Timber.d("Set delay time successfully: %s", time), throwable -> {
          Timber.e(throwable, "Error setting managed");
          callback.onError(throwable);
        }));
  }

  /**
   * public
   */
  void getDelayTime(@NonNull DelayCallback callback) {
    disposeOnDestroy(interactor.getDelayTime()
        .subscribeOn(getSubscribeScheduler())
        .observeOn(getObserveScheduler())
        .doAfterTerminate(callback::onComplete)
        .subscribe(booleanLongPair -> {
          boolean isCustom = booleanLongPair.first;
          long delayTime = booleanLongPair.second;
          if (isCustom) {
            callback.onCustomDelay(delayTime);
          } else {
            callback.onPresetDelay(delayTime);
          }
        }, throwable -> {
          Timber.e(throwable, "Error getting delay time");
          callback.onError(throwable);
        }));
  }

  /**
   * public
   */
  void listenForDelayTimeChanges(@NonNull OnDelayChangedCallback callback) {
    disposeOnDestroy(interactor.listenTimeChanges()
        .subscribeOn(getSubscribeScheduler())
        .observeOn(getObserveScheduler())
        .subscribe(callback::onDelayTimeChanged, throwable -> {
          Timber.e(throwable, "Error on delay changed event");
          callback.onError(throwable);
        }));
  }

  interface ActionCallback {

    void onError(@NonNull Throwable throwable);
  }

  interface OnDelayChangedCallback {

    void onDelayTimeChanged(long time);

    void onError(@NonNull Throwable throwable);
  }

  interface CustomTimeChangedCallback {

    void onCustomTimeChanged(long time);

    void onCustomTimeInputError(@Nullable String error);

    void onError(@NonNull Throwable throwable);
  }

  interface DelayCallback {

    void onCustomDelay(long time);

    void onPresetDelay(long time);

    void onError(@NonNull Throwable throwable);

    void onComplete();
  }
}
