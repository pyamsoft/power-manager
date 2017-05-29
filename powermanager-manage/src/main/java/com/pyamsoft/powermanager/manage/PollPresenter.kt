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

package com.pyamsoft.powermanager.manage

import io.reactivex.Scheduler
import timber.log.Timber
import javax.inject.Inject

class PollPresenter @Inject internal constructor(private val interactor: PollInteractor,
    obsScheduler: Scheduler, subScheduler: Scheduler) : TimePresenter(obsScheduler, subScheduler,
    interactor) {

  fun getCurrentPeriodic(callback: StateCallback) {
    disposeOnDestroy(interactor.getCurrentState().subscribeOn(subscribeScheduler).observeOn(
        observeScheduler).doAfterTerminate { callback.onCompleted() }.subscribe(
        { callback.onStateRetrieved(it) }, {
      Timber.e(it, "Error getting polling state")
      callback.onError(it)
    }))
  }

  fun toggleAll(checked: Boolean, callback: ToggleAllCallback) {
    disposeOnDestroy(interactor.toggleAll(checked).subscribeOn(subscribeScheduler).observeOn(
        observeScheduler).subscribe({ callback.onCompleted() }, {
      Timber.e(it, "Error toggle all polling")
      callback.onError(it)
    }))
  }

  interface StateCallback {

    fun onStateRetrieved(checked: Boolean)

    fun onError(throwable: Throwable)

    fun onCompleted()
  }

  interface ToggleAllCallback {

    fun onError(throwable: Throwable)

    fun onCompleted()
  }
}
