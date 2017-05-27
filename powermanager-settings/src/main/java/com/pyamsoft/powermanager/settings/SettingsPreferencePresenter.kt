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

package com.pyamsoft.powermanager.settings

import com.pyamsoft.powermanager.settings.bus.ConfirmEvent
import com.pyamsoft.powermanager.settings.bus.ConfirmEvent.Type.ALL
import com.pyamsoft.powermanager.settings.bus.ConfirmEvent.Type.DATABASE
import com.pyamsoft.pydroid.bus.EventBus
import com.pyamsoft.pydroid.presenter.SchedulerPresenter
import io.reactivex.Scheduler
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Named

internal class SettingsPreferencePresenter @Inject constructor(
    private val interactor: SettingsPreferenceInteractor, @Named("obs") obsScheduler: Scheduler,
    @Named("sub") subScheduler: Scheduler) : SchedulerPresenter(obsScheduler, subScheduler) {
  /**
   * public
   *
   * Gets confirm events from ConfirmationDialog
   */
  fun registerOnBus(callback: BusCallback) {
    disposeOnStop(
        EventBus.get().listen(ConfirmEvent::class.java).subscribeOn(subscribeScheduler).observeOn(
            observeScheduler).subscribe({ (type) ->
          when (type) {
            DATABASE -> clearDatabase(callback)
            ALL -> clearAll(callback)
            else -> throw IllegalStateException("Received invalid confirmation event type: " + type)
          }
        }, { Timber.e(it, "confirm bus error") }))
  }

  fun clearAll(callback: ClearRequestCallback) {
    disposeOnStop(
        interactor.clearAll().subscribeOn(subscribeScheduler).observeOn(observeScheduler).subscribe(
            { callback.onClearAll() }, { Timber.e(it, "onError") }))
  }

  fun clearDatabase(callback: ClearRequestCallback) {
    disposeOnStop(interactor.clearDatabase().subscribeOn(subscribeScheduler).observeOn(
        observeScheduler).subscribe({ callback.onClearDatabase() }, { Timber.e(it, "onError") }))
  }

  /**
   * public
   */
  fun checkRootEnabled(callback: RootCallback) {
    disposeOnStop(interactor.isRootEnabled.subscribeOn(subscribeScheduler).observeOn(
        observeScheduler).doAfterTerminate { callback.onComplete() }.doOnSubscribe { callback.onBegin() }.subscribe(
        { checkRoot(false, it, callback) }, { Timber.e(it, "onError bindCheckRoot") }))
  }

  /**
   * public
   */
  fun checkRoot(causedByUser: Boolean, rootEnable: Boolean, callback: RootCallback) {
    disposeOnStop(interactor.checkRoot(rootEnable).subscribeOn(subscribeScheduler).observeOn(
        observeScheduler).doAfterTerminate { callback.onComplete() }.doOnSubscribe { callback.onBegin() }.subscribe(
        { callback.onRootCallback(causedByUser, it, rootEnable) }, {
      Timber.e(it, "onError checking root")
      callback.onRootCallback(causedByUser, false, rootEnable)
    }))
  }

  internal interface BusCallback : ClearRequestCallback

  internal interface RootCallback {
    fun onBegin()

    fun onRootCallback(causedByUser: Boolean, hasPermission: Boolean, rootEnable: Boolean)

    fun onComplete()
  }

  internal interface ClearRequestCallback {
    fun onClearAll()

    fun onClearDatabase()
  }
}
