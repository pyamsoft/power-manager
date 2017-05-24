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
import android.support.annotation.CheckResult
import com.pyamsoft.powermanager.trigger.db.PowerTriggerDB
import com.pyamsoft.powermanager.trigger.db.PowerTriggerEntry
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import timber.log.Timber
import java.util.Collections
import javax.inject.Inject
import javax.inject.Singleton

@Singleton internal class TriggerInteractor @Inject internal constructor(
    powerTriggerDB: PowerTriggerDB,
    internal val cacheInteractor: TriggerCacheInteractor) : TriggerBaseInteractor(powerTriggerDB) {

  fun clearCached() {
    cacheInteractor.clearCache()
  }

  /**
   * public
   */
  @CheckResult internal fun queryAll(forceRefresh: Boolean): Observable<PowerTriggerEntry> {
    // TODO
    return Observable.empty()
  }

  /**
   * public
   */
  @CheckResult internal fun put(entry: PowerTriggerEntry): Single<PowerTriggerEntry> {
    return powerTriggerDB.queryWithPercent(entry.percent())
        .flatMapCompletable { triggerEntry ->
          if (!PowerTriggerEntry.isEmpty(triggerEntry)) {
            Timber.e("Entry already exists, throw")
            throw SQLiteConstraintException(
                "Entry already exists with percent: " + entry.percent())
          }

          if (PowerTriggerEntry.isEmpty(entry)) {
            Timber.e("Trigger is EMPTY")
            throw IllegalStateException("Trigger is EMPTY")
          } else if (entry.percent() > 100 || entry.percent() <= 0) {
            Timber.e("Percent too high")
            throw IllegalStateException("Percent is too high")
          } else {
            Timber.d("Insert new Trigger into DB")
            return@flatMapCompletable powerTriggerDB.queryWithPercent(entry.percent())
                .flatMapCompletable { powerTriggerDB.insert(it) }
          }
        }
        .andThen(Completable.fromAction { cacheInteractor.clearCache() })
        .andThen(get(entry.percent()))
  }

  /**
   * public
   */
  @CheckResult internal fun delete(percent: Int): Single<Int> {
    return powerTriggerDB.queryAll().map {

      // Sort first
      Collections.sort(it) { entry, entry2 ->
        if (entry.percent() < entry2.percent()) {
          // This is less, goes first
          return@sort -1
        } else if (entry.percent() > entry2.percent()) {
          // This is greater, goes second
          return@sort 1
        } else {
          // Same percent. This is impossible technically due to DB rules
          throw IllegalStateException("Cannot have two entries with the same percent")
        }
      }

      var foundEntry = -1
      for (i in it.indices) {
        val entry = it[i]
        if (entry.percent() == percent) {
          foundEntry = i
          break
        }
      }

      if (foundEntry == -1) {
        throw IllegalStateException("Could not find entry with percent: " + percent)
      }

      return@map foundEntry
    }.flatMap { position ->
      Timber.d("Delete trigger with percent: %d", percent)
      powerTriggerDB.deleteWithPercent(percent).andThen(Single.fromCallable {
        Timber.d("Return the position")
        cacheInteractor.clearCache()
        return@fromCallable position
      })
    }
  }
}
