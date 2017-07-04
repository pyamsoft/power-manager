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

import android.support.annotation.CheckResult
import com.pyamsoft.powermanager.base.preference.ClearPreferences
import com.pyamsoft.powermanager.base.preference.RootPreferences
import com.pyamsoft.powermanager.base.shell.RootChecker
import com.pyamsoft.powermanager.trigger.TriggerCacheInteractor
import com.pyamsoft.powermanager.trigger.db.PowerTriggerDB
import io.reactivex.Completable
import io.reactivex.Single
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton internal class SettingsPreferenceInteractor @Inject constructor(
    private val powerTriggerDB: PowerTriggerDB, private val preferences: RootPreferences,
    private val clearPreferences: ClearPreferences, private val rootChecker: RootChecker,
    private val triggerInteractor: TriggerCacheInteractor) {

  /**
   * public
   */
  @CheckResult fun clearDatabase(): Single<Boolean> {
    return powerTriggerDB.deleteAll().andThen(powerTriggerDB.deleteDatabase()).andThen(
        Completable.fromAction { triggerInteractor.clearCache() }).andThen(Single.just(true))
  }

  /**
   * public
   */
  @CheckResult fun clearAll(): Single<Boolean> {
    return clearDatabase().map {
      Timber.d("Clear all preferences")
      clearPreferences.clearAll()
      return@map true
    }
  }
}
