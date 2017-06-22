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

open class TimePresenter @Inject internal constructor(@Named("obs") foregroundScheduler: Scheduler,
    @Named("sub") backgroundScheduler: Scheduler,
    private val interactor: TimeInteractor) : SchedulerPresenter(foregroundScheduler,
    backgroundScheduler) {

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
  fun listenForCustomTimeChanges(onTimeChanged: (Long) -> Unit, onTimeError: (String?) -> Unit,
      onError: (Throwable) -> Unit) {
    stopListeningCustomTimeChanges()
    customTimeChangeDisposable = interactor.listenCustomTimeChanges().subscribeOn(
        backgroundScheduler).observeOn(foregroundScheduler).subscribe({ (first, second) ->
      onTimeError(first)
      onTimeChanged(second)
    }, {
      Timber.e(it, "Error listen custom time change")
      onError(it)
    })
  }

  /**
   * public
   */
  fun setPresetTime(time: Long, onError: (Throwable) -> Unit) {
    disposeOnDestroy {
      interactor.setTime(time).subscribeOn(backgroundScheduler).observeOn(
          foregroundScheduler).subscribe({ Timber.d("Set delay time successfully: %s", time) }, {
        Timber.e(it, "Error setting managed")
        onError(it)
      })
    }
  }

  /**
   * public
   */
  fun getTime(onCustom: (Long) -> Unit, onPreset: (Long) -> Unit, onError: (Throwable) -> Unit,
      onCompleted: () -> Unit) {
    disposeOnDestroy {
      interactor.time.subscribeOn(backgroundScheduler).observeOn(
          foregroundScheduler).doAfterTerminate { onCompleted() }.subscribe({ (isCustom, delayTime) ->
        if (isCustom) {
          onCustom(delayTime)
        } else {
          onPreset(delayTime)
        }
      }, {
        Timber.e(it, "Error getting delay time")
        onError(it)
      })
    }
  }

  /**
   * public
   */
  fun listenForTimeChanges(onTimeChanged: (Long) -> Unit, onError: (Throwable) -> Unit) {
    Timber.w("LISTEN FOR TIME CHANGES")
    disposeOnDestroy {
      interactor.listenTimeChanges().subscribeOn(backgroundScheduler).observeOn(
          foregroundScheduler).subscribe({ onTimeChanged(it) }, {
        Timber.e(it, "Error on delay changed event")
        onError(it)
      })
    }
  }
}
