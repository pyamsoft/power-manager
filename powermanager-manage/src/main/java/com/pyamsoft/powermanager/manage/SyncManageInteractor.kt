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

package com.pyamsoft.powermanager.manage

import com.pyamsoft.powermanager.base.preference.SyncPreferences
import io.reactivex.Completable
import io.reactivex.Single
import javax.inject.Inject

internal class SyncManageInteractor @Inject constructor(
    val preferences: SyncPreferences) : ManageInteractor() {

  override fun setManaged(state: Boolean): Completable {
    return Completable.fromAction { preferences.syncManaged = state }
  }

  override val isManaged: Single<Pair<Boolean, Boolean>>
    get() = Single.fromCallable {
      Pair(true, preferences.syncManaged)
    }
}