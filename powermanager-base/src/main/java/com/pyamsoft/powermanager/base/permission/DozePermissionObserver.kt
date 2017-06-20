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

internal class DozePermissionObserver @Inject internal constructor(context: Context,
    private val workaroundPreferences: WorkaroundPreferences, preferences: RootPreferences,
    rootChecker: RootChecker) : RootPermissionObserver(context, preferences, rootChecker) {

  private val dumpPermissionObserver = object : PermissionObserverImpl(context,
      Manifest.permission.DUMP) {

    override fun checkPermission(appContext: Context): Boolean {
      return hasRuntimePermission()
    }
  }

  private val secureSettingPermissionObserver = object : PermissionObserverImpl(context,
      Manifest.permission.WRITE_SECURE_SETTINGS) {

    override fun checkPermission(appContext: Context): Boolean {
      return hasRuntimePermission()
    }
  }

  override fun checkPermission(appContext: Context): Boolean {
    if (workaroundPreferences.isDozeWorkaroundEnabled()) {
      if (Build.VERSION.SDK_INT == Build.VERSION_CODES.M) {
        return dumpPermissionObserver.hasPermission()
      } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        return secureSettingPermissionObserver.hasPermission()
      } else {
        Timber.w("Doze workaround is not supported on this API level")
        return false
      }
    } else {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        return super.checkPermission(appContext)
      } else {
        Timber.w("Doze workaround is not supported on this API level")
        return false
      }
    }
  }

}
