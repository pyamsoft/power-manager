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

import com.pyamsoft.powermanager.manage.bus.ManageBus
import io.reactivex.Scheduler
import timber.log.Timber

abstract class ExceptionPresenter internal constructor(private val interactor: ExceptionInteractor,
    private val bus: ManageBus, foregroundScheduler: Scheduler,
    backgroundScheduler: Scheduler) : TargetPresenter(foregroundScheduler, backgroundScheduler) {
  /**
   * public
   */
  fun setIgnoreCharging(state: Boolean, onError: (Throwable) -> Unit, onComplete: () -> Unit) {
    disposeOnDestroy {
      interactor.setIgnoreCharging(state).subscribeOn(backgroundScheduler).observeOn(
          foregroundScheduler).doAfterTerminate { onComplete() }.subscribe(
          { Timber.d("Set ignore charging state successfully: %s", state) }, {
        Timber.e(it, "Error setting ignore charging")
        onError(it)
      })
    }
  }

  /**
   * public
   */
  fun getIgnoreCharging(onEnableRetrieved: (Boolean) -> Unit, onStateRetrieved: (Boolean) -> Unit,
      onError: (Throwable) -> Unit, onComplete: () -> Unit) {
    disposeOnDestroy {
      interactor.isIgnoreCharging.subscribeOn(backgroundScheduler).observeOn(
          foregroundScheduler).doOnSuccess { (first) ->
        onEnableRetrieved(first)
      }.doAfterTerminate { onComplete() }.map { (_, second) -> second }.subscribe(
          { onStateRetrieved(it) }, {
        Timber.e(it, "Error getting ignore charging")
        onError(it)
      })
    }
  }

  /**
   * public
   */
  fun setIgnoreWear(state: Boolean, onError: (Throwable) -> Unit, onComplete: () -> Unit) {
    disposeOnDestroy {
      interactor.setIgnoreWear(state).subscribeOn(backgroundScheduler).observeOn(
          foregroundScheduler).doAfterTerminate { onComplete() }.subscribe(
          { Timber.d("Set ignore wear state successfully: %s", state) }, {
        Timber.e(it, "Error setting ignore wear")
        onError(it)
      })
    }
  }

  /**
   * public
   */
  fun getIgnoreWear(onEnableRetrieved: (Boolean) -> Unit, onStateRetrieved: (Boolean) -> Unit,
      onError: (Throwable) -> Unit, onComplete: () -> Unit) {
    disposeOnDestroy {
      interactor.isIgnoreWear.subscribeOn(backgroundScheduler).observeOn(
          foregroundScheduler).doOnSuccess { (first) ->
        onEnableRetrieved(first)
      }.doAfterTerminate { onComplete() }.map { (_, second) -> second }.subscribe(
          { onStateRetrieved(it) }, {
        Timber.e(it, "Error getting ignore wear")
        onError(it)
      })
    }
  }

  /**
   * public
   */
  fun registerOnBus(onManageChanged: () -> Unit) {
    disposeOnDestroy {
      bus.listen().filter { it.target === target }.subscribeOn(backgroundScheduler).observeOn(
          foregroundScheduler).subscribe({ onManageChanged() }, {
        Timber.e(it, "Error on manage change bus for target: %s", target)
      })
    }
  }
}
