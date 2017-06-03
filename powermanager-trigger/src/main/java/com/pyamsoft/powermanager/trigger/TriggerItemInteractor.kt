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

import android.support.annotation.CheckResult
import com.pyamsoft.powermanager.trigger.db.PowerTriggerDB
import com.pyamsoft.powermanager.trigger.db.PowerTriggerEntry
import io.reactivex.Completable
import io.reactivex.Single
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton internal class TriggerItemInteractor @Inject constructor(powerTriggerDB: PowerTriggerDB,
    private val cacheInteractor: TriggerCacheInteractor) : TriggerBaseInteractor(powerTriggerDB) {

  /**
   * public
   */
  @CheckResult fun update(entry: PowerTriggerEntry, enabled: Boolean): Single<PowerTriggerEntry> {
    return Completable.fromCallable {
      val percent = entry.percent()
      Timber.d("Update enabled state with percent: %d", percent)
      Timber.d("Update entry to enabled state: %s", enabled)
      powerTriggerDB.updateEnabled(enabled, percent)
    }.andThen(Completable.fromAction { cacheInteractor.clearCache() }).andThen(get(entry.percent()))
  }
}
