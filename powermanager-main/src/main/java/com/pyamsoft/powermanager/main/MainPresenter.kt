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

package com.pyamsoft.powermanager.main

import com.pyamsoft.pydroid.presenter.SchedulerPresenter
import io.reactivex.Scheduler
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Named

class MainPresenter @Inject internal constructor(val interactor: MainInteractor,
    @Named("obs") obsScheduler: Scheduler,
    @Named("sub") subScheduler: Scheduler) : SchedulerPresenter(obsScheduler, subScheduler) {

  /**
   * public
   */
  fun runStartupHooks(callback: StartupCallback) {
    startServiceWhenOpen(callback)
    checkForRoot(callback)
  }

  private fun startServiceWhenOpen(callback: StartupCallback) {
    disposeOnStop(interactor.isStartWhenOpen.subscribeOn(subscribeScheduler).observeOn(
        observeScheduler).subscribe({
      if (it) {
        callback.onServiceEnabledWhenOpen()
      }
    }, { Timber.e(it, "onError isStartWhenOpen") }))
  }

  private fun checkForRoot(callback: StartupCallback) {
    disposeOnStop(interactor.hasRootPermission().subscribeOn(subscribeScheduler).observeOn(
        observeScheduler).subscribe({
      if (!it) {
        interactor.missingRootPermission()
        callback.explainRootRequirement()
      }
    }, { Timber.e(it, "onError checking root permission") }))
  }

  interface StartupCallback {

    fun onServiceEnabledWhenOpen()

    fun explainRootRequirement()
  }
}
