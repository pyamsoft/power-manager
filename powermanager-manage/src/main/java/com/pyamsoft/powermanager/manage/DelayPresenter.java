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
import com.pyamsoft.pydroid.presenter.SchedulerPresenter;
import io.reactivex.Scheduler;
import javax.inject.Inject;
import javax.inject.Named;
import timber.log.Timber;

class DelayPresenter extends SchedulerPresenter {

  @NonNull private final DelayInteractor interactor;

  @Inject DelayPresenter(@NonNull @Named("obs") Scheduler observeScheduler,
      @NonNull @Named("sub") Scheduler subscribeScheduler, @NonNull DelayInteractor interactor) {
    super(observeScheduler, subscribeScheduler);
    this.interactor = interactor;
  }

  /**
   * public
   */
  void submitCustomTimeChange(@NonNull String text, boolean instant) {
    interactor.acceptCustomTimeChange(text, instant);
  }

  /**
   * public
   */
  void listenForCustomTimeChanges(@NonNull CustomTimeChangedCallback callback) {
    disposeOnDestroy(interactor.listenCustomTimeChanges()
        .subscribeOn(getSubscribeScheduler())
        .observeOn(getObserveScheduler())
        .subscribe(pair -> {
          callback.onCustomTimeInputError(pair.first);
          callback.onCustomTimeChanged(pair.second);
        }, throwable -> {
          Timber.e(throwable, "Error listen custom time change");
          callback.onError(throwable);
        }));
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
            final int index;
            if (delayTime == 5) {
              index = 0;
            } else if (delayTime == 10) {
              index = 1;
            } else if (delayTime == 15) {
              index = 2;
            } else if (delayTime == 30) {
              index = 3;
            } else if (delayTime == 45) {
              index = 4;
            } else if (delayTime == 60) {
              index = 5;
            } else if (delayTime == 90) {
              index = 6;
            } else if (delayTime == 120) {
              index = 7;
            } else {
              throw new IllegalStateException("No preset delay with time: " + delayTime);
            }

            callback.onPresetDelay(index, delayTime);
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

    void onPresetDelay(int index, long time);

    void onError(@NonNull Throwable throwable);

    void onComplete();
  }
}
