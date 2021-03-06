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

import com.pyamsoft.powermanager.base.preference.DataSaverPreferences
import com.pyamsoft.powermanager.model.PermissionObserver
import io.reactivex.Completable
import io.reactivex.Single
import javax.inject.Inject

internal class DataSaverExceptionInteractor @Inject internal constructor(
    private val permissionObserver: PermissionObserver,
    private val preferences: DataSaverPreferences) : ExceptionInteractor() {

  override fun setIgnoreCharging(state: Boolean): Single<Boolean> {
    return Completable.fromAction { preferences.ignoreChargingDataSaver = state }.andThen(
        Single.just(state))
  }

  override fun setIgnoreWear(state: Boolean): Single<Boolean> {
    return Completable.fromAction { preferences.ignoreWearDataSaver = state }.andThen(
        Single.just(state))
  }

  override val isIgnoreCharging: Single<Pair<Boolean, Boolean>>
    get() = Single.fromCallable {
      Pair(preferences.dataSaverManaged && permissionObserver.hasPermission(),
          preferences.ignoreChargingDataSaver)
    }

  override val isIgnoreWear: Single<Pair<Boolean, Boolean>>
    get() = Single.fromCallable {
      Pair(preferences.dataSaverManaged && permissionObserver.hasPermission(),
          preferences.ignoreWearDataSaver)
    }
}
