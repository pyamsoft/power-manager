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

import com.pyamsoft.powermanager.settings.bus.ConfirmEvent.Type.ALL
import com.pyamsoft.powermanager.settings.bus.ConfirmEvent.Type.DATABASE
import com.pyamsoft.powermanager.settings.bus.SettingsBus
import com.pyamsoft.pydroid.presenter.SchedulerPreferencePresenter
import io.reactivex.Scheduler
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Named

class SettingsPreferencePresenter @Inject internal constructor(private val bus: SettingsBus,
    private val interactor: SettingsPreferenceInteractor, @Named("obs") obsScheduler: Scheduler,
    @Named("sub") subScheduler: Scheduler) : SchedulerPreferencePresenter(obsScheduler,
    subScheduler) {

  /**
   * public
   *
   * Gets confirm events from ConfirmationDialog
   */
  fun registerOnBus(onClearDatabase: () -> Unit, onClearAll: () -> Unit) {
    disposeOnStop {
      bus.listen().subscribeOn(backgroundScheduler).observeOn(foregroundScheduler).subscribe(
          { (type) ->
            when (type) {
              DATABASE -> clearDatabase(onClearDatabase)
              ALL -> clearAll(onClearAll)
              else -> throw IllegalStateException(
                  "Received invalid confirmation event type: " + type)
            }
          }, { Timber.e(it, "confirm bus error") })
    }
  }

  private fun clearAll(onClearAll: () -> Unit) {
    disposeOnStop {
      interactor.clearAll().subscribeOn(backgroundScheduler).observeOn(
          foregroundScheduler).subscribe({ onClearAll() }, { Timber.e(it, "onError") })
    }
  }

  private fun clearDatabase(onClearDatabase: () -> Unit) {
    disposeOnStop {
      interactor.clearDatabase().subscribeOn(backgroundScheduler).observeOn(
          foregroundScheduler).subscribe({ onClearDatabase() }, { Timber.e(it, "onError") })
    }
  }
}
