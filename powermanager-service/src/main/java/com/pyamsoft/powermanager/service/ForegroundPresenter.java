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
import com.pyamsoft.pydroid.helper.DisposableHelper;
import com.pyamsoft.pydroid.presenter.SchedulerPresenter;
import io.reactivex.Scheduler;
import io.reactivex.disposables.Disposable;
import io.reactivex.disposables.Disposables;
import javax.inject.Inject;
import javax.inject.Named;
import timber.log.Timber;

class ForegroundPresenter extends SchedulerPresenter {

  @SuppressWarnings("WeakerAccess") @NonNull final ForegroundInteractor interactor;
  @NonNull private Disposable notificationDisposable = Disposables.empty();

  @Inject ForegroundPresenter(@NonNull ForegroundInteractor interactor,
      @Named("obs") Scheduler obsScheduler, @Named("sub") Scheduler subScheduler) {
    super(obsScheduler, subScheduler);
    this.interactor = interactor;
  }

  public void queueRepeatingTriggerJob() {
    interactor.queueRepeatingTriggerJob();
  }

  @Override protected void onStop() {
    super.onStop();
    notificationDisposable = DisposableHelper.dispose(notificationDisposable);
  }

  @Override protected void onDestroy() {
    super.onDestroy();
    interactor.destroy();
  }

  public void startNotification(@NonNull NotificationCallback callback) {
    notificationDisposable = DisposableHelper.dispose(notificationDisposable);
    notificationDisposable = interactor.createNotification()
        .subscribeOn(getSubscribeScheduler())
        .observeOn(getObserveScheduler())
        .subscribe(callback::onStartNotificationInForeground, throwable -> {
          Timber.e(throwable, "onError");
          // TODO handle error
        });
  }

  /**
   * Trigger interval is only read on interactor.queueRepeatingTriggerJob()
   *
   * Restart it by destroying and then re-creating the interactor
   */
  public void restartTriggerAlarm() {
    interactor.destroy();
    interactor.queueRepeatingTriggerJob();
  }

  public void setForegroundState(boolean enable) {
    interactor.setServiceEnabled(enable);
  }

  interface NotificationCallback {

    void onStartNotificationInForeground(@NonNull Notification notification);
  }
}
