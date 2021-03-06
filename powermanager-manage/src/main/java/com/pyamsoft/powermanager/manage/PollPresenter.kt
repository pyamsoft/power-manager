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

import android.widget.CompoundButton
import com.pyamsoft.pydroid.util.rx.RxViews
import io.reactivex.Scheduler
import timber.log.Timber
import javax.inject.Inject

class PollPresenter @Inject internal constructor(private val interactor: PollInteractor,
    obsScheduler: Scheduler, subScheduler: Scheduler) : TimePresenter(obsScheduler, subScheduler,
    interactor) {

  fun getCurrentPeriodic(onStateRetrieved: (Boolean) -> Unit, onError: (Throwable) -> Unit,
      onCompleted: () -> Unit) {
    disposeOnDestroy {
      interactor.getCurrentState().subscribeOn(backgroundScheduler).observeOn(
          foregroundScheduler).doAfterTerminate { onCompleted() }.subscribe(
          { onStateRetrieved(it) }, {
        Timber.e(it, "Error getting polling state")
        onError(it)
      })
    }
  }

  fun toggleAll(view: CompoundButton, onError: (Throwable) -> Unit, onCompleted: () -> Unit) {
    disposeOnDestroy {
      RxViews.onCheckChanged(view).observeOn(
          backgroundScheduler).flatMapSingle { interactor.toggleAll(it.checked) }.observeOn(
          foregroundScheduler).subscribe({ onCompleted() }, {
        Timber.e(it, "Error toggle all polling")
        onError(it)
      })
    }
  }
}
