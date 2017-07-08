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
import com.pyamsoft.pydroid.presenter.SchedulerViewPresenter
import io.reactivex.Scheduler
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Named

class TriggerPresenter @Inject internal constructor(@Named("obs") obsScheduler: Scheduler,
    @Named("sub") subScheduler: Scheduler, private val deleteBus: TriggerDeleteBus,
    private val createBus: TriggerCreateBus,
    private val interactor: TriggerInteractor) : SchedulerViewPresenter(obsScheduler,
    subScheduler) {

  fun registerOnBus(onAdd: (PowerTriggerEntry) -> Unit, onAddError: (Throwable) -> Unit,
      onCreateError: (Throwable) -> Unit, onTriggerDeleted: (Int) -> Unit,
      onTriggerDeleteError: (Throwable) -> Unit) {
    disposeOnStop {
      createBus.listen().subscribeOn(backgroundScheduler).observeOn(
          foregroundScheduler).flatMapSingle {
        interactor.createTrigger(it.entry).onErrorReturn {
          Timber.e(it, "createTrigger Error")
          if (it is SQLiteConstraintException) {
            Timber.e("Error inserting into DB")
            onAddError(it)
          } else {
            Timber.e("Issue creating trigger")
            onCreateError(it)
          }
          return@onErrorReturn PowerTriggerEntry.empty
        }
      }.subscribe({
        if (PowerTriggerEntry.isEmpty(it)) {
          Timber.w("Empty entry passed, do nothing")
        } else {
          onAdd(it)
        }
      }, { Timber.e(it, "onError create bus") })
    }

    disposeOnStop {
      deleteBus.listen().subscribeOn(backgroundScheduler).observeOn(
          foregroundScheduler).flatMapSingle {
        interactor.delete(it.percent).onErrorReturn {
          Timber.e(it, "Trigger Delete error")
          onTriggerDeleteError(it)
          return@onErrorReturn -1
        }
      }.subscribe({
        if (it < 0) {
          Timber.w("Trigger delete failed, do nothing")
        } else {
          onTriggerDeleted(it)
        }
      }, { Timber.e(it, "onError create bus") })
    }
  }

  fun loadTriggerView(forceRefresh: Boolean, onTriggerLoaded: (PowerTriggerEntry) -> Unit,
      onTriggerLoadError: (Throwable) -> Unit, onTriggerLoadFinished: () -> Unit) {
    disposeOnStop {
      interactor.queryAll(forceRefresh).subscribeOn(backgroundScheduler).observeOn(
          foregroundScheduler).doAfterTerminate({ onTriggerLoadFinished() }).subscribe(
          { onTriggerLoaded(it) }, {
        Timber.e(it, "onError")
        onTriggerLoadError(it)
      })
    }
  }
}
