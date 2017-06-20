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

package com.pyamsoft.powermanager.base.permission

import android.content.Context
import com.pyamsoft.powermanager.base.preference.RootPreferences
import com.pyamsoft.powermanager.base.shell.RootChecker
import timber.log.Timber
import javax.inject.Inject

internal open class RootPermissionObserver @Inject internal constructor(context: Context,
    private val preferences: RootPreferences, private val rootChecker: RootChecker,
    permission: String?) : PermissionObserverImpl(context, permission) {

  constructor(context: Context, preferences: RootPreferences, rootChecker: RootChecker) : this(
      context, preferences, rootChecker, null)

  override fun checkPermission(appContext: Context): Boolean {
    if (preferences.rootEnabled) {
      val permission = rootChecker.isSUAvailable
      Timber.d("Has root permission? %s", permission)
      return permission
    } else {
      Timber.w("Root is not enabled")
      return false
    }
  }
}
