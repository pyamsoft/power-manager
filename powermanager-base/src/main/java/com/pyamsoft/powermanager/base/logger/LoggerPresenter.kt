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

package com.pyamsoft.powermanager.base.logger

import com.pyamsoft.pydroid.presenter.SchedulerPresenter
import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.disposables.CompositeDisposable
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class LoggerPresenter @Inject internal constructor(internal val interactor: LoggerInteractor,
    observeScheduler: Scheduler, subscribeScheduler: Scheduler) : SchedulerPresenter(
    observeScheduler, subscribeScheduler) {

  private val logDisposables: CompositeDisposable = CompositeDisposable()

  fun retrieveLogContents(callback: LogCallback) {
    logDisposables.add(interactor.logContents.subscribeOn(subscribeScheduler).observeOn(
        observeScheduler).doAfterTerminate(
        { callback.onAllLogContentsRetrieved() }).doOnSubscribe { callback.onPrepareLogContentRetrieval() }.subscribe(
        { callback.onLogContentRetrieved(it) }, {
      Timber.e(it, "onError: Failed to retrieve log contents: %s", interactor.logId)
    }))
  }

  override fun onStop() {
    super.onStop()
    clearLogs()
  }

  internal fun log(logType: LogType, fmt: String, vararg args: Any) {
    logDisposables.add(
        interactor.log(logType, fmt, *args).subscribeOn(subscribeScheduler).observeOn(
            observeScheduler).subscribe({
          // TODO anything else?
        }) { throwable ->
          Timber.e(throwable, "onError: Unable to successfully log message to log file")
          // TODO Any other error handling?
        })

    queueClearLogDisposable()
  }

  private fun queueClearLogDisposable() {
    logDisposables.add(
        Observable.just(true).delay(1, TimeUnit.MINUTES).subscribeOn(subscribeScheduler).observeOn(
            observeScheduler).subscribe({ logDisposables.clear() },
            { throwable -> Timber.e(throwable, "onError clearing composite subscription") }))
  }

  fun deleteLog(callback: DeleteCallback) {
    // Stop everything before we delete the log
    clearLogs()
    logDisposables.add(interactor.deleteLog().subscribeOn(subscribeScheduler).observeOn(
        observeScheduler).subscribe({
      if (it) {
        callback.onLogDeleted(interactor.logId)
      }
      clearLogs()
    }, { Timber.e(it, "onError deleteLog") }))
  }

  internal fun clearLogs() {
    logDisposables.clear()
  }

  interface DeleteCallback {
    fun onLogDeleted(logId: String)
  }

  interface LogCallback {
    fun onPrepareLogContentRetrieval()

    fun onLogContentRetrieved(logLine: String)

    fun onAllLogContentsRetrieved()
  }
}
