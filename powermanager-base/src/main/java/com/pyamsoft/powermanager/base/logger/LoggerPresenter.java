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

package com.pyamsoft.powermanager.base.logger;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.pyamsoft.powermanager.model.LogType;
import com.pyamsoft.pydroid.helper.SubscriptionHelper;
import com.pyamsoft.pydroid.presenter.Presenter;
import com.pyamsoft.pydroid.presenter.SchedulerPresenter;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;
import rx.Observable;
import rx.Scheduler;
import rx.Subscription;
import rx.subscriptions.CompositeSubscription;
import rx.subscriptions.Subscriptions;
import timber.log.Timber;

public class LoggerPresenter extends SchedulerPresenter<Presenter.Empty> {

  @SuppressWarnings("WeakerAccess") @NonNull final LoggerInteractor interactor;
  @SuppressWarnings("WeakerAccess") @NonNull final CompositeSubscription logSubscriptions =
      new CompositeSubscription();
  @SuppressWarnings("WeakerAccess") @NonNull Subscription logContenSubscription =
      Subscriptions.empty();
  @SuppressWarnings("WeakerAccess") @NonNull Subscription clearLogSubscription =
      Subscriptions.empty();
  @SuppressWarnings("WeakerAccess") @NonNull Subscription deleteLogSubscription =
      Subscriptions.empty();

  @Inject LoggerPresenter(@NonNull LoggerInteractor interactor, @NonNull Scheduler observeScheduler,
      @NonNull Scheduler subscribeScheduler) {
    super(observeScheduler, subscribeScheduler);
    this.interactor = interactor;
  }

  public void retrieveLogContents(@NonNull LogCallback callback) {
    callback.onPrepareLogContentRetrieval();
    SubscriptionHelper.unsubscribe(logContenSubscription);
    logContenSubscription = interactor.getLogContents()
        .subscribeOn(getSubscribeScheduler())
        .observeOn(getObserveScheduler())
        .doAfterTerminate(callback::onAllLogContentsRetrieved)
        .subscribe(callback::onLogContentRetrieved,
            throwable -> Timber.e(throwable, "onError: Failed to retrieve log contents: %s",
                interactor.getLogId()),
            () -> SubscriptionHelper.unsubscribe(logContenSubscription));
  }

  @Override protected void onUnbind() {
    super.onUnbind();
    logSubscriptions.clear();
    SubscriptionHelper.unsubscribe(logContenSubscription, clearLogSubscription,
        deleteLogSubscription);
  }

  public void log(@NonNull LogType logType, @NonNull String fmt, @Nullable Object... args) {
    final Subscription logSubscription = interactor.log(logType, fmt, args)
        .subscribeOn(getSubscribeScheduler())
        .observeOn(getObserveScheduler())
        .subscribe(success -> {
          // TODO anything else?
        }, throwable -> {
          Timber.e(throwable, "onError: Unable to successfully log message to log file");
          // TODO Any other error handling?
        });

    queueClearLogSubscription();
    logSubscriptions.add(logSubscription);
  }

  private void queueClearLogSubscription() {
    SubscriptionHelper.unsubscribe(clearLogSubscription);
    clearLogSubscription = Observable.just(Boolean.TRUE)
        .delay(1, TimeUnit.MINUTES)
        .subscribeOn(getSubscribeScheduler())
        .observeOn(getObserveScheduler())
        .subscribe(aBoolean -> logSubscriptions.clear(),
            throwable -> Timber.e(throwable, "onError clearing composite subscription"),
            () -> SubscriptionHelper.unsubscribe(clearLogSubscription));
  }

  public void deleteLog(@NonNull DeleteCallback callback) {
    // Stop everything before we delete the log
    logSubscriptions.clear();
    SubscriptionHelper.unsubscribe(logContenSubscription, clearLogSubscription,
        deleteLogSubscription);

    deleteLogSubscription = interactor.deleteLog()
        .subscribeOn(getSubscribeScheduler())
        .observeOn(getObserveScheduler())
        .subscribe(deleted -> {
              if (deleted) {
                callback.onLogDeleted(interactor.getLogId());
              }
            }, throwable -> Timber.e(throwable, "onError deleteLog"),
            () -> SubscriptionHelper.unsubscribe(deleteLogSubscription));
  }

  public interface DeleteCallback {

    void onLogDeleted(@NonNull String logId);
  }

  public interface LogCallback {

    void onPrepareLogContentRetrieval();

    void onLogContentRetrieved(@NonNull String logLine);

    void onAllLogContentsRetrieved();
  }
}
