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

class LoggerPresenter @Inject internal constructor(private val interactor: LoggerInteractor,
    foregroundScheduler: Scheduler, backgroundScheduler: Scheduler) : SchedulerPresenter(
    foregroundScheduler, backgroundScheduler) {

  private val logDisposables: CompositeDisposable = CompositeDisposable()

  fun retrieveLogContents(onPrepareLogContentRetrieval: () -> Unit,
      onLogContentRetrieved: (String) -> Unit, onAllLogContentsRetrieved: () -> Unit) {
    logDisposables.add(interactor.getLogContents().subscribeOn(backgroundScheduler).observeOn(
        foregroundScheduler).doAfterTerminate(
        { onAllLogContentsRetrieved() }).doOnSubscribe { onPrepareLogContentRetrieval() }.subscribe(
        { onLogContentRetrieved(it) }, {
      Timber.e(it, "onError: Failed to retrieve log contents: %s", interactor.logId)
    }))
  }

  override fun onStop() {
    super.onStop()
    clearLogs()
  }

  internal fun log(logType: LogType, fmt: String, vararg args: Any) {
    logDisposables.add(
        interactor.log(logType, fmt, *args).subscribeOn(backgroundScheduler).observeOn(
            foregroundScheduler).subscribe({
          // TODO anything else?
        }) { throwable ->
          Timber.e(throwable, "onError: Unable to successfully log message to log file")
          // TODO Any other error handling?
        })

    queueClearLogDisposable()
  }

  private fun queueClearLogDisposable() {
    logDisposables.add(
        Observable.just(true).delay(1, TimeUnit.MINUTES).subscribeOn(backgroundScheduler).observeOn(
            foregroundScheduler).subscribe({ logDisposables.clear() },
            { throwable -> Timber.e(throwable, "onError clearing composite subscription") }))
  }

  fun deleteLog(onLogDeleted: (String) -> Unit) {
    // Stop everything before we delete the log
    clearLogs()
    logDisposables.add(interactor.deleteLog().subscribeOn(backgroundScheduler).observeOn(
        foregroundScheduler).subscribe({
      if (it) {
        onLogDeleted(interactor.logId)
      }
      clearLogs()
    }, { Timber.e(it, "onError deleteLog") }))
  }

  internal fun clearLogs() {
    logDisposables.clear()
  }
}
