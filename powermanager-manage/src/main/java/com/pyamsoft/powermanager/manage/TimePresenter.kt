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

import com.pyamsoft.pydroid.helper.DisposableHelper
import com.pyamsoft.pydroid.presenter.SchedulerPresenter
import io.reactivex.Scheduler
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Named

open class TimePresenter @Inject internal constructor(@Named("obs") observeScheduler: Scheduler,
    @Named("sub") subscribeScheduler: Scheduler,
    private val interactor: TimeInteractor) : SchedulerPresenter(observeScheduler,
    subscribeScheduler) {

  private var customTimeChangeDisposable = DisposableHelper.dispose(null)

  override fun onStop() {
    super.onStop()
    stopListeningCustomTimeChanges()
  }

  /**
   * public
   */
  fun submitCustomTimeChange(text: String, instant: Boolean) {
    Timber.d("Custom time changed: %s instant? %s", text, instant)
    interactor.acceptCustomTimeChange(text, instant)
  }

  /**
   * public
   */
  fun stopListeningCustomTimeChanges() {
    customTimeChangeDisposable = DisposableHelper.dispose(customTimeChangeDisposable)
  }

  /**
   * public
   */
  fun listenForCustomTimeChanges(callback: CustomTimeChangedCallback) {
    stopListeningCustomTimeChanges()
    customTimeChangeDisposable = interactor.listenCustomTimeChanges().subscribeOn(
        subscribeScheduler).observeOn(observeScheduler).subscribe({ (first, second) ->
      callback.onCustomTimeInputError(first)
      callback.onCustomTimeChanged(second)
    }, {
      Timber.e(it, "Error listen custom time change")
      callback.onError(it)
    })
  }

  /**
   * public
   */
  fun setPresetTime(time: Long, callback: ActionCallback) {
    disposeOnDestroy(interactor.setTime(time).subscribeOn(subscribeScheduler).observeOn(
        observeScheduler).subscribe({ Timber.d("Set delay time successfully: %s", time) }, {
      Timber.e(it, "Error setting managed")
      callback.onError(it)
    }))
  }

  /**
   * public
   */
  fun getTime(callback: TimeCallback) {
    disposeOnDestroy(interactor.time.subscribeOn(subscribeScheduler).observeOn(
        observeScheduler).doAfterTerminate { callback.onComplete() }.subscribe(
        { (isCustom, delayTime) ->
          if (isCustom) {
            callback.onCustomTime(delayTime)
          } else {
            callback.onPresetTime(delayTime)
          }
        }, {
      Timber.e(it, "Error getting delay time")
      callback.onError(it)
    }))
  }

  /**
   * public
   */
  fun listenForTimeChanges(callback: OnTimeChangedCallback) {
    Timber.w("LISTEN FOR TIME CHANGES")
    disposeOnDestroy(interactor.listenTimeChanges().subscribeOn(subscribeScheduler).observeOn(
        observeScheduler).subscribe({ callback.onTimeChanged(it) }, {
      Timber.e(it, "Error on delay changed event")
      callback.onError(it)
    }))
  }

  interface ActionCallback {
    fun onError(throwable: Throwable)
  }

  interface OnTimeChangedCallback {
    fun onTimeChanged(time: Long)

    fun onError(throwable: Throwable)
  }

  interface CustomTimeChangedCallback {
    fun onCustomTimeChanged(time: Long)

    fun onCustomTimeInputError(error: String?)

    fun onError(throwable: Throwable)
  }

  interface TimeCallback {
    fun onCustomTime(time: Long)

    fun onPresetTime(time: Long)

    fun onError(throwable: Throwable)

    fun onComplete()
  }
}
