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
import android.os.Build
import com.pyamsoft.powermanager.base.preference.RootPreferences
import com.pyamsoft.powermanager.base.shell.RootChecker
import timber.log.Timber
import javax.inject.Inject

internal open class DataSaverPermissionObserver @Inject internal constructor(context: Context,
    preferences: RootPreferences, rootChecker: RootChecker) : RootPermissionObserver(context,
    preferences, rootChecker) {

  override fun checkPermission(appContext: Context): Boolean {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
      Timber.w("Data Saver did not exist on < Nougat")
      return false
    } else {
      Timber.d("Data Saver requires root")
      return super.checkPermission(appContext)
    }
  }
}
