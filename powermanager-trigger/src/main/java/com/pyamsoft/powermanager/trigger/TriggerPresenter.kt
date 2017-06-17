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
import com.pyamsoft.powermanager.trigger.bus.TriggerCreateBus
import com.pyamsoft.powermanager.trigger.bus.TriggerDeleteBus
import com.pyamsoft.powermanager.trigger.db.PowerTriggerEntry
import com.pyamsoft.pydroid.presenter.SchedulerPresenter
import io.reactivex.Scheduler
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Named

class TriggerPresenter @Inject internal constructor(@Named("obs") obsScheduler: Scheduler,
    @Named("sub") subScheduler: Scheduler, private val deleteBus: TriggerDeleteBus,
    private val createBus: TriggerCreateBus,
    private val interactor: TriggerInteractor) : SchedulerPresenter(obsScheduler, subScheduler) {

  /**
   * public
   */
  fun registerOnBus(onAdd: (PowerTriggerEntry) -> Unit, onAddError: (Throwable) -> Unit,
      onCreateError: (Throwable) -> Unit, onTriggerDeleted: (Int) -> Unit,
      onTriggerDeleteError: (Throwable) -> Unit) {
    disposeOnStop {
      createBus.listen().subscribeOn(subscribeScheduler).observeOn(observeScheduler).subscribe(
          { createPowerTrigger(it.entry, onAdd, onAddError, onCreateError) },
          { Timber.e(it, "onError create bus") })
    }

    disposeOnStop {
      deleteBus.listen().subscribeOn(subscribeScheduler).observeOn(observeScheduler).subscribe(
          { deleteTrigger(it.percent, onTriggerDeleted, onTriggerDeleteError) },
          { Timber.e(it, "onError create bus") })
    }
  }

  fun createPowerTrigger(entry: PowerTriggerEntry, onAdd: (PowerTriggerEntry) -> Unit,
      onAddError: (Throwable) -> Unit, onCreateError: (Throwable) -> Unit) {
    disposeOnStop {
      Timber.d("Create new power trigger")
      interactor.put(entry).subscribeOn(subscribeScheduler).observeOn(observeScheduler).subscribe(
          { onAdd(it) }, {
        Timber.e(it, "onError")
        if (it is SQLiteConstraintException) {
          Timber.e("Error inserting into DB")
          onAddError(it)
        } else {
          Timber.e("Issue creating trigger")
          onCreateError(it)
        }
      })
    }
  }

  fun deleteTrigger(percent: Int, onTriggerDeleted: (Int) -> Unit,
      onTriggerError: (Throwable) -> Unit) {
    disposeOnStop {
      interactor.delete(percent).subscribeOn(subscribeScheduler).observeOn(
          observeScheduler).subscribe({ onTriggerDeleted(it) }, {
        Timber.e(it, "onError")
        onTriggerError(it)
      })
    }
  }

  /**
   * public
   */
  fun loadTriggerView(forceRefresh: Boolean, onTriggerLoaded: (PowerTriggerEntry) -> Unit,
      onTriggerLoadError: (Throwable) -> Unit, onTriggerLoadFinished: () -> Unit) {
    disposeOnStop {
      interactor.queryAll(forceRefresh).subscribeOn(subscribeScheduler).observeOn(
          observeScheduler).doAfterTerminate({ onTriggerLoadFinished() }).subscribe(
          { onTriggerLoaded(it) }, {
        Timber.e(it, "onError")
        onTriggerLoadError(it)
      })
    }
  }
}
