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

import com.pyamsoft.powermanager.manage.bus.ManageChangeEvent
import com.pyamsoft.pydroid.bus.EventBus
import io.reactivex.Scheduler
import timber.log.Timber

abstract class ExceptionPresenter internal constructor(private val interactor: ExceptionInteractor,
    observeScheduler: Scheduler, subscribeScheduler: Scheduler) : TargetPresenter(observeScheduler,
    subscribeScheduler) {
  /**
   * public
   */
  fun setIgnoreCharging(state: Boolean, onError: (Throwable) -> Unit, onComplete: () -> Unit) {
    disposeOnDestroy {
      interactor.setIgnoreCharging(state).subscribeOn(subscribeScheduler).observeOn(
          observeScheduler).doAfterTerminate { onComplete() }.subscribe(
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
      interactor.isIgnoreCharging.subscribeOn(subscribeScheduler).observeOn(
          observeScheduler).doOnSuccess { (first) ->
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
      interactor.setIgnoreWear(state).subscribeOn(subscribeScheduler).observeOn(
          observeScheduler).doAfterTerminate { onComplete() }.subscribe(
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
      interactor.isIgnoreWear.subscribeOn(subscribeScheduler).observeOn(
          observeScheduler).doOnSuccess { (first) ->
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
      EventBus.get().listen(
          ManageChangeEvent::class.java).filter { it.target === target }.subscribeOn(
          subscribeScheduler).observeOn(observeScheduler).subscribe({ onManageChanged() }, {
        Timber.e(it, "Error on manage change bus for target: %s", target)
      })
    }
  }
}
