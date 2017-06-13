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

import android.Manifest
import android.content.Context
import android.os.Build
import com.pyamsoft.powermanager.base.preference.RootPreferences
import com.pyamsoft.powermanager.base.preference.WorkaroundPreferences
import com.pyamsoft.powermanager.base.shell.RootChecker
import timber.log.Timber
import javax.inject.Inject

internal open class DataPermissionObserver @Inject internal constructor(context: Context,
    preferences: RootPreferences, rootChecker: RootChecker,
    private val workaroundPreferences: WorkaroundPreferences) : RootPermissionObserver(context,
    preferences, rootChecker, Manifest.permission.WRITE_SECURE_SETTINGS) {

  override fun checkPermission(appContext: Context): Boolean {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      if (workaroundPreferences.isDataWorkaroundEnabled()) {
        Timber.i("DATA WORKAROUND: Enabled")
        return hasRuntimePermission()
      } else {
        Timber.w("Lollipop and up requires root for Data")
        return super.checkPermission(appContext)
      }
    } else {
      Timber.d("Kitkat has reflection based Data method")
      return true
    }
  }
}
