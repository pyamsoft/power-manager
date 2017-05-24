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

package com.pyamsoft.powermanager.manager

import android.support.annotation.CallSuper
import com.pyamsoft.pydroid.helper.SchedulerHelper
import io.reactivex.Scheduler
import io.reactivex.disposables.CompositeDisposable
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Named

class Manager @Inject internal constructor(internal val interactor: ManagerInteractor,
    @param:Named("io") private val scheduler: Scheduler) {
  internal val compositeDisposable: CompositeDisposable = CompositeDisposable()

  init {
    SchedulerHelper.enforceSubscribeScheduler(scheduler)
  }

  fun enable(onEnabled: (() -> Unit)?) {
    compositeDisposable.add(
        interactor.queueEnable().subscribeOn(scheduler).observeOn(scheduler).subscribe({ tag ->
          Timber.d("%s: Queued up a new enable job", tag)
          onEnabled?.invoke()
        }) { throwable -> Timber.e(throwable, "%s: onError enable") })
  }

  fun disable(onDisabled: (() -> Unit)?) {
    compositeDisposable.add(
        interactor.queueDisable().subscribeOn(scheduler).observeOn(scheduler).subscribe({ tag ->
          Timber.d("%s: Queued up a new disable job", tag)
          onDisabled?.invoke()
        }) { throwable -> Timber.e(throwable, "%s: onError disable") })
  }

  @CallSuper fun cleanup() {
    compositeDisposable.clear()
    interactor.destroy()

    // Reset the device back to its original state when the Service is cleaned up
    enable({ compositeDisposable.clear() })
  }
}
