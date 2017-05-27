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

package com.pyamsoft.powermanager.service

import android.support.annotation.CheckResult
import com.pyamsoft.powermanager.base.preference.ServicePreferences
import io.reactivex.Single
import javax.inject.Inject
import javax.inject.Singleton

@Singleton internal class ActionToggleInteractor @Inject constructor(
    preferences: ServicePreferences) : ServiceInteractor(preferences) {
  /**
   * public
   */
  @CheckResult fun toggleEnabledState(): Single<Boolean> {
    return isServiceEnabled.map {
      val newState = !it
      setServiceEnabled(newState)
      return@map newState
    }
  }
}
