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
import com.pyamsoft.pydroid.helper.DisposableHelper;
import com.pyamsoft.pydroid.presenter.SchedulerPresenter;
import io.reactivex.Observable;
import io.reactivex.Scheduler;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.disposables.Disposables;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;
import timber.log.Timber;

public class LoggerPresenter extends SchedulerPresenter {

  @SuppressWarnings("WeakerAccess") @NonNull final LoggerInteractor interactor;
  @SuppressWarnings("WeakerAccess") @NonNull final CompositeDisposable logDisposables =
      new CompositeDisposable();
  @NonNull private Disposable logContenDisposable = Disposables.empty();
  @NonNull private Disposable clearLogDisposable = Disposables.empty();
  @NonNull private Disposable deleteLogDisposable = Disposables.empty();

  @Inject LoggerPresenter(@NonNull LoggerInteractor interactor, @NonNull Scheduler observeScheduler,
      @NonNull Scheduler subscribeScheduler) {
    super(observeScheduler, subscribeScheduler);
    this.interactor = interactor;
  }

  public void retrieveLogContents(@NonNull LogCallback callback) {
    callback.onPrepareLogContentRetrieval();
    logContenDisposable = DisposableHelper.dispose(logContenDisposable);
    logContenDisposable = interactor.getLogContents()
        .subscribeOn(getSubscribeScheduler())
        .observeOn(getObserveScheduler())
        .doAfterTerminate(callback::onAllLogContentsRetrieved)
        .subscribe(callback::onLogContentRetrieved,
            throwable -> Timber.e(throwable, "onError: Failed to retrieve log contents: %s",
                interactor.getLogId()));
  }

  @Override protected void onStop() {
    super.onStop();
    clearLogs();
  }

  public void log(@NonNull LogType logType, @NonNull String fmt, @Nullable Object... args) {
    final Disposable logDisposable = interactor.log(logType, fmt, args)
        .subscribeOn(getSubscribeScheduler())
        .observeOn(getObserveScheduler())
        .subscribe(() -> {
          // TODO anything else?
        }, throwable -> {
          Timber.e(throwable, "onError: Unable to successfully log message to log file");
          // TODO Any other error handling?
        });

    queueClearLogDisposable();
    logDisposables.add(logDisposable);
  }

  private void queueClearLogDisposable() {
    clearLogDisposable = DisposableHelper.dispose(clearLogDisposable);
    clearLogDisposable = Observable.just(Boolean.TRUE)
        .delay(1, TimeUnit.MINUTES)
        .subscribeOn(getSubscribeScheduler())
        .observeOn(getObserveScheduler())
        .subscribe(aBoolean -> logDisposables.clear(),
            throwable -> Timber.e(throwable, "onError clearing composite subscription"));
  }

  public void deleteLog(@NonNull DeleteCallback callback) {
    // Stop everything before we delete the log
    clearLogs();
    deleteLogDisposable = interactor.deleteLog()
        .subscribeOn(getSubscribeScheduler())
        .observeOn(getObserveScheduler())
        .subscribe(deleted -> {
          if (deleted) {
            callback.onLogDeleted(interactor.getLogId());
          }
          clearLogs();
        }, throwable -> Timber.e(throwable, "onError deleteLog"));
  }

  @SuppressWarnings("WeakerAccess") void clearLogs() {
    logDisposables.clear();
    logContenDisposable = DisposableHelper.dispose(logContenDisposable);
    clearLogDisposable = DisposableHelper.dispose(clearLogDisposable);
    deleteLogDisposable = DisposableHelper.dispose(deleteLogDisposable);
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
