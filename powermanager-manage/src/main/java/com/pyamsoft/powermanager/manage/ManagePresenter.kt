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
import com.pyamsoft.powermanager.manage.bus.ManageChangeEvent
import io.reactivex.Scheduler
import timber.log.Timber

abstract class ManagePresenter internal constructor(private val interactor: ManageInteractor,
    private val bus: ManageBus, observeScheduler: Scheduler,
    subscribeScheduler: Scheduler) : TargetPresenter(observeScheduler, subscribeScheduler) {
  /**
   * public
   */
  fun setManaged(state: Boolean, onError: (Throwable) -> Unit, onComplete: () -> Unit) {
    disposeOnDestroy {
      interactor.setManaged(state).subscribeOn(subscribeScheduler).observeOn(
          observeScheduler).doAfterTerminate {
        bus.publish(ManageChangeEvent(target))
      }.doAfterTerminate { onComplete() }.subscribe(
          { Timber.d("Set managed state successfully: %s", state) }, {
        Timber.e(it, "Error setting managed")
        onError(it)
      })
    }
  }

  /**
   * public
   */
  fun getState(onEnableRetrieved: (Boolean) -> Unit, onStateRetrieved: (Boolean) -> Unit,
      onError: (Throwable) -> Unit, onComplete: () -> Unit) {
    disposeOnDestroy {
      interactor.isManaged.subscribeOn(subscribeScheduler).observeOn(
          observeScheduler).doOnSuccess { (first) ->
        onEnableRetrieved(first)
      }.doAfterTerminate { onComplete() }.map { (_, second) -> second }.subscribe(
          { onStateRetrieved(it) }, {
        Timber.e(it, "Error getting managed")
        onError(it)
      })
    }
  }
}
