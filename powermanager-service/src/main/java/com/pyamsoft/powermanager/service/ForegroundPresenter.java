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

package com.pyamsoft.powermanager.service;

import android.app.Notification;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.pyamsoft.pydroid.helper.DisposableHelper;
import com.pyamsoft.pydroid.presenter.Presenter;
import com.pyamsoft.pydroid.presenter.SchedulerPresenter;
import io.reactivex.Observable;
import io.reactivex.Scheduler;
import io.reactivex.disposables.Disposable;
import io.reactivex.disposables.Disposables;
import javax.inject.Inject;
import javax.inject.Named;
import timber.log.Timber;

class ForegroundPresenter extends SchedulerPresenter<Presenter.Empty> {

  @SuppressWarnings("WeakerAccess") @NonNull final ForegroundInteractor interactor;
  @NonNull private Disposable notificationDisposable = Disposables.empty();
  @NonNull private Disposable createDisposable = Disposables.empty();

  @Inject ForegroundPresenter(@NonNull ForegroundInteractor interactor,
      @Named("obs") Scheduler obsScheduler, @Named("sub") Scheduler subScheduler) {
    super(obsScheduler, subScheduler);
    this.interactor = interactor;
  }

  @Override protected void onBind(@Nullable Empty view) {
    super.onBind(view);
    createDisposable = DisposableHelper.unsubscribe(createDisposable);
    createDisposable = Observable.fromCallable(() -> {
      interactor.create();
      return Boolean.TRUE;
    })
        .subscribeOn(getSubscribeScheduler())
        .observeOn(getObserveScheduler())
        .subscribe(success -> Timber.d("Interactor was created"),
            throwable -> Timber.e(throwable, "Error creating interactor"));
  }

  @Override protected void onUnbind() {
    super.onUnbind();
    interactor.destroy();
    notificationDisposable = DisposableHelper.unsubscribe(notificationDisposable);
    createDisposable = DisposableHelper.unsubscribe(createDisposable);
  }

  public void startNotification(@NonNull NotificationCallback callback) {
    notificationDisposable = DisposableHelper.unsubscribe(notificationDisposable);
    notificationDisposable = interactor.createNotification()
        .subscribeOn(getSubscribeScheduler())
        .observeOn(getObserveScheduler())
        .subscribe(callback::onStartNotificationInForeground, throwable -> {
          Timber.e(throwable, "onError");
          // TODO handle error
        });
  }

  /**
   * Trigger interval is only read on interactor.create()
   *
   * Restart it by destroying and then re-creating the interactor
   */
  public void restartTriggerAlarm() {
    interactor.destroy();
    interactor.create();
  }

  public void setForegroundState(boolean enable) {
    interactor.setServiceEnabled(enable);
  }

  interface NotificationCallback {

    void onStartNotificationInForeground(@NonNull Notification notification);
  }
}
