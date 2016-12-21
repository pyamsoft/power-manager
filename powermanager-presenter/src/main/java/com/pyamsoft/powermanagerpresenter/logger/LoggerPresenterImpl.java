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

package com.pyamsoft.powermanagerpresenter.logger;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.pyamsoft.powermanagermodel.LogType;
import com.pyamsoft.pydroidrx.SchedulerPresenter;
import com.pyamsoft.pydroidrx.SubscriptionHelper;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;
import rx.Observable;
import rx.Scheduler;
import rx.Subscription;
import rx.subscriptions.CompositeSubscription;
import rx.subscriptions.Subscriptions;
import timber.log.Timber;

class LoggerPresenterImpl extends SchedulerPresenter<LoggerPresenter.Provider>
    implements LoggerPresenter {

  @SuppressWarnings("WeakerAccess") @NonNull final LoggerInteractor interactor;
  @SuppressWarnings("WeakerAccess") @NonNull final CompositeSubscription logSubscriptions =
      new CompositeSubscription();
  @SuppressWarnings("WeakerAccess") @NonNull Subscription logContenSubscription =
      Subscriptions.empty();
  @SuppressWarnings("WeakerAccess") @NonNull Subscription clearLogSubscription =
      Subscriptions.empty();
  @SuppressWarnings("WeakerAccess") @NonNull Subscription deleteLogSubscription =
      Subscriptions.empty();

  @Inject LoggerPresenterImpl(@NonNull LoggerInteractor interactor,
      @NonNull Scheduler observeScheduler, @NonNull Scheduler subscribeScheduler) {
    super(observeScheduler, subscribeScheduler);
    this.interactor = interactor;
  }

  @Override protected void onBind() {
    super.onBind();
    retrieveLogContents();
  }

  private void retrieveLogContents() {
    getView(Provider::onPrepareLogContentRetrieval);
    SubscriptionHelper.unsubscribe(logContenSubscription);
    logContenSubscription = interactor.getLogContents()
        .subscribeOn(getSubscribeScheduler())
        .observeOn(getObserveScheduler())
        .subscribe(logLine -> getView(view -> view.onLogContentRetrieved(logLine)),
            throwable -> Timber.e(throwable, "onError: Failed to retrieve log contents: %s",
                interactor.getLogId()), () -> {
              getView(Provider::onAllLogContentsRetrieved);
              SubscriptionHelper.unsubscribe(logContenSubscription);
            });
  }

  @Override protected void onUnbind() {
    super.onUnbind();
    logSubscriptions.clear();
    SubscriptionHelper.unsubscribe(logContenSubscription, clearLogSubscription,
        deleteLogSubscription);
  }

  @Override
  public void log(@NonNull LogType logType, @NonNull String fmt, @Nullable Object... args) {
    logWithTimber(logType, fmt, args);
    final Subscription logSubscription =
        interactor.isLoggingEnabled()
            .filter(enabled -> enabled)
            .flatMap(loggingEnabled -> {
              final String message = String.format(Locale.getDefault(), fmt, args);
              final String logMessage =
                  String.format(Locale.getDefault(), "%s: %s", logType.name(), message);

              final Observable<Boolean> writeAppendResult;
              if (loggingEnabled) {
                writeAppendResult = interactor.appendToLog(logMessage);
              } else {
                writeAppendResult = Observable.just(false);
              }
              return writeAppendResult;
            })
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

  @SuppressWarnings("WeakerAccess") void logWithTimber(@NonNull LogType logType,
      @NonNull String fmt, @Nullable Object... args) {
    switch (logType) {
      case DEBUG:
        Timber.d(fmt, args);
        break;
      case INFO:
        Timber.i(fmt, args);
        break;
      case WARNING:
        Timber.w(fmt, args);
        break;
      case ERROR:
        Timber.e(fmt, args);
        break;
      default:
        throw new IllegalStateException("Invalid LogType: " + logType.name());
    }
  }

  private void queueClearLogSubscription() {
    SubscriptionHelper.unsubscribe(clearLogSubscription);
    clearLogSubscription = Observable.just(true)
        .delay(1, TimeUnit.MINUTES)
        .subscribeOn(getSubscribeScheduler())
        .observeOn(getObserveScheduler())
        .subscribe(aBoolean -> logSubscriptions.clear(),
            throwable -> Timber.e(throwable, "onError clearing composite subscription"),
            () -> SubscriptionHelper.unsubscribe(clearLogSubscription));
  }

  @Override public void deleteLog() {
    // Stop everything before we delete the log
    logSubscriptions.clear();
    SubscriptionHelper.unsubscribe(logContenSubscription, clearLogSubscription,
        deleteLogSubscription);

    deleteLogSubscription = interactor.deleteLog()
        .subscribeOn(getSubscribeScheduler())
        .observeOn(getObserveScheduler())
        .subscribe(deleted -> {
              if (deleted) {
                getView(view -> view.onLogDeleted(interactor.getLogId()));
              }
            }, throwable -> Timber.e(throwable, "onError deleteLog"),
            () -> SubscriptionHelper.unsubscribe(deleteLogSubscription));
  }
}
