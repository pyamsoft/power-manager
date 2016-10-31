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

package com.pyamsoft.powermanager.dagger.preference;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.pyamsoft.powermanager.app.preference.CustomTimeInputPreferencePresenter;
import com.pyamsoft.pydroidrx.SchedulerPresenter;
import com.pyamsoft.pydroidrx.SubscriptionHelper;
import java.util.concurrent.TimeUnit;
import rx.Scheduler;
import rx.Subscription;
import rx.subscriptions.Subscriptions;
import timber.log.Timber;

public abstract class CustomTimeInputPreferencePresenterImpl
    extends SchedulerPresenter<CustomTimeInputPreferencePresenter.View>
    implements CustomTimeInputPreferencePresenter {

  private static final int MAX_CUSTOM_LENGTH = 10;

  @Nullable private final CustomTimeInputPreferenceInteractor interactor;
  @SuppressWarnings("WeakerAccess") @NonNull Subscription customTimeSubscription =
      Subscriptions.empty();

  protected CustomTimeInputPreferencePresenterImpl(
      @Nullable CustomTimeInputPreferenceInteractor interactor, @NonNull Scheduler observeScheduler,
      @NonNull Scheduler subscribeScheduler) {
    super(observeScheduler, subscribeScheduler);
    this.interactor = interactor;
  }

  @Override protected void onUnbind() {
    super.onUnbind();
    SubscriptionHelper.unsubscribe(customTimeSubscription);
  }

  @Override public void updateCustomTime(@NonNull String time) {
    updateCustomTime(time, true);
  }

  @Override public void updateCustomTime(@NonNull String time, long delay) {
    updateCustomTime(time, delay, true);
  }

  @Override public void updateCustomTime(@NonNull String time, boolean updateView) {
    updateCustomTime(time, 600L, updateView);
  }

  @Override public void updateCustomTime(@NonNull String time, long delay, boolean updateView) {
    if (interactor != null) {
      final long longTime;
      if (time.isEmpty()) {
        longTime = 0;
      } else if (time.length() > MAX_CUSTOM_LENGTH) {
        longTime = Long.parseLong(time.substring(0, MAX_CUSTOM_LENGTH + 1));
      } else {
        longTime = Long.parseLong(time);
      }

      SubscriptionHelper.unsubscribe(customTimeSubscription);
      customTimeSubscription = interactor.saveTime(longTime)
          .delay(delay, TimeUnit.MILLISECONDS)
          .subscribeOn(getSubscribeScheduler())
          .observeOn(getObserveScheduler())
          .subscribe(customTime -> getView(view -> {
            if (updateView) {
              view.onCustomTimeUpdate(customTime);
            }
          }), throwable -> {
            Timber.e(throwable, "onError updateCustomTime");
            getView(view -> {
              if (updateView) {
                view.onCustomTimeError();
              }
            });
          }, () -> SubscriptionHelper.unsubscribe(customTimeSubscription));
    } else {
      Timber.e("NULL interactor");
    }
  }

  @Override public void initializeCustomTime() {
    if (interactor != null) {
      SubscriptionHelper.unsubscribe(customTimeSubscription);
      customTimeSubscription = interactor.getTime()
          .subscribeOn(getSubscribeScheduler())
          .observeOn(getObserveScheduler())
          .subscribe(customTime -> getView(view -> view.onCustomTimeUpdate(customTime)),
              throwable -> {
                Timber.e(throwable, "onError updateCustomTime");
                getView(View::onCustomTimeError);
              }, () -> SubscriptionHelper.unsubscribe(customTimeSubscription));
    } else {
      Timber.e("NULL interactor");
    }
  }
}
