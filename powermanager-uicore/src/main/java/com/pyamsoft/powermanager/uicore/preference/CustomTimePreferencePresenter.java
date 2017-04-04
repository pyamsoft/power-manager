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

package com.pyamsoft.powermanager.uicore.preference;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.pyamsoft.pydroid.helper.DisposableHelper;
import com.pyamsoft.pydroid.presenter.SchedulerPresenter;
import io.reactivex.Scheduler;
import io.reactivex.disposables.Disposable;
import io.reactivex.disposables.Disposables;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;
import timber.log.Timber;

public class CustomTimePreferencePresenter extends SchedulerPresenter {

  // Max time 30 minutes
  private static final long MAX_TIME_SECONDS = TimeUnit.MINUTES.toSeconds(30);
  private static final int MAX_CUSTOM_LENGTH = 6;

  @Nullable private final CustomTimePreferenceInteractor interactor;
  @NonNull private Disposable customTimeDisposable = Disposables.empty();

  @Inject public CustomTimePreferencePresenter(@Nullable CustomTimePreferenceInteractor interactor,
      @NonNull Scheduler observeScheduler, @NonNull Scheduler subscribeScheduler) {
    super(observeScheduler, subscribeScheduler);
    this.interactor = interactor;
  }

  @Override protected void onStop() {
    super.onStop();
    customTimeDisposable = DisposableHelper.dispose(customTimeDisposable);
  }

  public void updateCustomTime(@NonNull String time, @NonNull OnCustomTimeUpdateCallback callback) {
    updateCustomTime(time, true, callback);
  }

  public void updateCustomTime(@NonNull String time, long delay,
      @NonNull OnCustomTimeUpdateCallback callback) {
    updateCustomTime(time, delay, true, callback);
  }

  public void updateCustomTime(@NonNull String time, boolean updateView,
      @NonNull OnCustomTimeUpdateCallback callback) {
    updateCustomTime(time, 600L, updateView, callback);
  }

  public void updateCustomTime(@NonNull String time, long delay, boolean updateView,
      @NonNull OnCustomTimeUpdateCallback callback) {
    if (interactor != null) {
      long longTime;
      if (time.isEmpty()) {
        longTime = 0;
      } else if (time.length() > MAX_CUSTOM_LENGTH) {
        longTime = Long.parseLong(time.substring(0, MAX_CUSTOM_LENGTH + 1));
      } else {
        longTime = Long.parseLong(time);
      }

      // Make sure time is not too high or too low
      longTime = Math.min(MAX_TIME_SECONDS, longTime);

      callback.onClearFocus();
      customTimeDisposable = DisposableHelper.dispose(customTimeDisposable);
      customTimeDisposable = interactor.saveTime(longTime, delay)
          .subscribeOn(getSubscribeScheduler())
          .observeOn(getObserveScheduler())
          .subscribe(customTime -> {
            if (updateView) {
              callback.onCustomTimeUpdate(customTime);
            }
          }, throwable -> {
            Timber.e(throwable, "onError updateCustomTime");
            if (updateView) {
              callback.onCustomTimeError();
            }
          });
    } else {
      Timber.e("NULL interactor");
    }
  }

  public void initializeCustomTime(@NonNull OnCustomTimeUpdateCallback callback) {
    if (interactor != null) {
      customTimeDisposable = DisposableHelper.dispose(customTimeDisposable);
      customTimeDisposable = interactor.getTime()
          .subscribeOn(getSubscribeScheduler())
          .observeOn(getObserveScheduler())
          .subscribe(callback::onCustomTimeUpdate, throwable -> {
            Timber.e(throwable, "onError updateCustomTime");
            callback.onCustomTimeError();
          });
    } else {
      Timber.e("NULL interactor");
    }
  }

  interface OnCustomTimeUpdateCallback {

    void onClearFocus();

    void onCustomTimeUpdate(long time);

    void onCustomTimeError();
  }
}
