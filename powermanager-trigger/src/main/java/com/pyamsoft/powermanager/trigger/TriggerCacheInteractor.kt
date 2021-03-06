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

import com.pyamsoft.powermanager.trigger.db.PowerTriggerEntry
import io.reactivex.Single
import javax.inject.Inject
import javax.inject.Singleton

@Singleton class TriggerCacheInteractor @Inject constructor() {

  private var cachedPowerTriggerEntryObservable: Single<List<PowerTriggerEntry>>? = null

  init {
    cachedPowerTriggerEntryObservable = null
  }

  /**
   * public
   */
  fun clearCache() {
    cachedPowerTriggerEntryObservable = null
  }

  /**
   * public
   */
  fun retrieve(): Single<List<PowerTriggerEntry>>? {
    return cachedPowerTriggerEntryObservable
  }

  /**
   * public
   */
  fun cache(cache: Single<List<PowerTriggerEntry>>) {
    cachedPowerTriggerEntryObservable = cache
  }
}
