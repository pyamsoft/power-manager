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

package com.pyamsoft.powermanager.trigger

import android.database.sqlite.SQLiteConstraintException
import com.pyamsoft.powermanager.trigger.bus.TriggerCreateEvent
import com.pyamsoft.powermanager.trigger.bus.TriggerDeleteEvent
import com.pyamsoft.powermanager.trigger.db.PowerTriggerEntry
import com.pyamsoft.pydroid.bus.EventBus
import com.pyamsoft.pydroid.presenter.SchedulerPresenter
import io.reactivex.Scheduler
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Named

internal class TriggerPresenter @Inject constructor(@Named("obs") obsScheduler: Scheduler,
    @Named("sub") subScheduler: Scheduler,
    private val interactor: TriggerInteractor) : SchedulerPresenter(obsScheduler, subScheduler) {

  /**
   * public
   */
  fun registerOnBus(callback: BusCallback) {
    disposeOnStop(EventBus.get()
        .listen(TriggerCreateEvent::class.java)
        .subscribeOn(subscribeScheduler)
        .observeOn(observeScheduler)
        .subscribe({ createPowerTrigger(it.entry, callback) },
            { Timber.e(it, "onError create bus") }))

    disposeOnStop(EventBus.get()
        .listen(TriggerDeleteEvent::class.java)
        .subscribeOn(subscribeScheduler)
        .observeOn(observeScheduler)
        .subscribe({ deleteTrigger(it.percent, callback) },
            { Timber.e(it, "onError create bus") }))
  }

  fun createPowerTrigger(entry: PowerTriggerEntry,
      callback: TriggerCreateCallback) {
    Timber.d("Create new power trigger")
    disposeOnStop(interactor.put(entry)
        .subscribeOn(subscribeScheduler)
        .observeOn(observeScheduler)
        .subscribe({ callback.onNewTriggerAdded(it) }, {
          Timber.e(it, "onError")
          if (it is SQLiteConstraintException) {
            Timber.e("Error inserting into DB")
            callback.onNewTriggerInsertError()
          } else {
            Timber.e("Issue creating trigger")
            callback.onNewTriggerCreateError()
          }
        }))
  }

  fun deleteTrigger(percent: Int,
      callback: TriggerDeleteCallback) {
    disposeOnStop(interactor.delete(percent)
        .subscribeOn(subscribeScheduler)
        .observeOn(observeScheduler)
        .subscribe({ callback.onTriggerDeleted(it) }, {
          Timber.e(it, "onError")
        }))
  }

  /**
   * public
   */
  fun loadTriggerView(callback: TriggerLoadCallback, forceRefresh: Boolean) {
    disposeOnStop(interactor.queryAll(forceRefresh)
        .subscribeOn(subscribeScheduler)
        .observeOn(observeScheduler)
        .doAfterTerminate({ callback.onTriggerLoadFinished() })
        .subscribe({ callback.onTriggerLoaded(it) }, { Timber.e(it, "onError") }))
  }

  internal interface TriggerLoadCallback {

    fun onTriggerLoaded(entry: PowerTriggerEntry)

    fun onTriggerLoadFinished()
  }

  internal interface BusCallback : TriggerDeleteCallback, TriggerCreateCallback

  internal interface TriggerDeleteCallback {

    fun onTriggerDeleted(position: Int)
  }

  internal interface TriggerCreateCallback {

    fun onNewTriggerAdded(entry: PowerTriggerEntry)

    fun onNewTriggerCreateError()

    fun onNewTriggerInsertError()
  }
}
