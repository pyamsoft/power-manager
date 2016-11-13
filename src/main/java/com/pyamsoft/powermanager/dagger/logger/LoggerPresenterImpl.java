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

package com.pyamsoft.powermanager.dagger.logger;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.pyamsoft.powermanager.app.logger.LogType;
import com.pyamsoft.powermanager.app.logger.LoggerPresenter;
import com.pyamsoft.pydroidrx.SchedulerPresenter;
import com.pyamsoft.pydroidrx.SubscriptionHelper;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;
import rx.Observable;
import rx.Scheduler;
import rx.Subscription;
import rx.functions.Func1;
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
    SubscriptionHelper.unsubscribe(logContenSubscription);
    getView(Provider::onPrepareLogContentRetrieval);
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
    SubscriptionHelper.unsubscribe(logContenSubscription, clearLogSubscription);
  }

  @Override
  public void log(@NonNull LogType logType, @NonNull String fmt, @Nullable Object... args) {
    final Subscription logSubscription = interactor.isLoggingEnabled().filter(enabled -> {
      Timber.d("Filter out logging not enabled ");
      return enabled;
    }).flatMap(new Func1<Boolean, Observable<Boolean>>() {
      @Override public Observable<Boolean> call(Boolean loggingEnabled) {
        final Observable<Boolean> writeAppendResult;
        if (loggingEnabled) {
          Timber.d("Format message");
          final String message = String.format(Locale.getDefault(), fmt, args);
          final String logMessage =
              String.format(Locale.getDefault(), "%s: %s", logType.name(), message);

          Timber.d("Attempt to append to log");
          writeAppendResult = interactor.appendToLog(logMessage);
          logWithTimber(logType, message);
        } else {
          Timber.w("Logging disabled, return false");
          writeAppendResult = Observable.just(false);
        }
        return writeAppendResult;
      }
    }).subscribeOn(getSubscribeScheduler()).observeOn(getObserveScheduler()).subscribe(success -> {
      Timber.d("Successfully logged message to log file: %s", success);
      // TODO anything else?
    }, throwable -> {
      Timber.e(throwable, "onError: Unable to successfully log message to log file");
      // TODO Any other error handling?
    });

    queueClearLogSubscription();
    logSubscriptions.add(logSubscription);
  }

  @SuppressWarnings("WeakerAccess") void logWithTimber(@NonNull LogType logType,
      @NonNull String message) {
    switch (logType) {
      case DEBUG:
        Timber.d(message);
        break;
      case INFO:
        Timber.i(message);
        break;
      case WARNING:
        Timber.w(message);
        break;
      case ERROR:
        Timber.e(message);
        break;
      default:
        throw new IllegalStateException("Invalid LogType: " + logType.name());
    }
  }

  @Override public void d(@NonNull String fmt, @Nullable Object... args) {
    log(LogType.DEBUG, fmt, args);
  }

  @Override public void i(@NonNull String fmt, @Nullable Object... args) {
    log(LogType.INFO, fmt, args);
  }

  @Override public void w(@NonNull String fmt, @Nullable Object... args) {
    log(LogType.WARNING, fmt, args);
  }

  @Override public void e(@NonNull String fmt, @Nullable Object... args) {
    log(LogType.ERROR, fmt, args);
  }

  private void queueClearLogSubscription() {
    Timber.d("Queue to clear the Log CompositeSubscription in 1 minute");
    SubscriptionHelper.unsubscribe(clearLogSubscription);
    clearLogSubscription = Observable.just(true)
        .delay(1, TimeUnit.MINUTES)
        .subscribeOn(getSubscribeScheduler())
        .observeOn(getObserveScheduler())
        .subscribe(aBoolean -> {
              Timber.d("Clear composite Log subscriptions");
              logSubscriptions.clear();
            }, throwable -> Timber.e(throwable, "onError clearing composite subscription"),
            () -> SubscriptionHelper.unsubscribe(clearLogSubscription));
  }

  @Override public void deleteLog() {
    Timber.d("Delete log file for %s", interactor.getLogId());
    Timber.w("TODO");
  }
}
