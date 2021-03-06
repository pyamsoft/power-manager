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

package com.pyamsoft.powermanager.base.states

import android.content.ContentResolver
import android.content.Context
import android.net.ConnectivityManager
import android.os.Build
import android.provider.Settings
import android.support.annotation.CheckResult
import com.pyamsoft.powermanager.base.logger.Logger
import com.pyamsoft.powermanager.base.preference.RootPreferences
import com.pyamsoft.powermanager.base.preference.WorkaroundPreferences
import com.pyamsoft.powermanager.base.shell.ShellHelper
import com.pyamsoft.powermanager.model.PermissionObserver
import com.pyamsoft.powermanager.model.States
import timber.log.Timber
import java.lang.reflect.Method
import javax.inject.Inject

internal class DataWrapperImpl @Inject internal constructor(context: Context,
    private val shellHelper: ShellHelper, private val logger: Logger,
    private val rootPreferences: RootPreferences, private val dataUri: String,
    private val workaroundPreferences: WorkaroundPreferences,
    private val workaroundPermissionObserver: PermissionObserver) : DeviceFunctionWrapper {
  private val connectivityManager: ConnectivityManager = context.applicationContext.getSystemService(
      Context.CONNECTIVITY_SERVICE) as ConnectivityManager
  private val contentResolver: ContentResolver = context.applicationContext.contentResolver

  private fun setMobileDataEnabledReflection(enabled: Boolean) {
    if (SET_MOBILE_DATA_ENABLED_METHOD != null) {
      try {
        SET_MOBILE_DATA_ENABLED_METHOD.invoke(connectivityManager, enabled)
      } catch (e: Exception) {
        logger.e("ManagerData setMobileDataEnabled ERROR")
      }
    }
  }

  /**
   * Requires ROOT to work properly
   *
   * Will exit with a failed 137 code or otherwise if ROOT is not allowed
   */
  private fun setMobileDataEnabledRoot(enabled: Boolean) {
    // Just check preferences since it is faster than opening an SU session to check success
    // Will fail with undetermined consequence if root is not granted, be sure to guard with
    // an external check for root
    if (rootPreferences.rootEnabled) {
      val command = "svc data " + if (enabled) "enable" else "disable"
      shellHelper.runSUCommand(command)
    } else {
      logger.w("Root not enabled, cannot toggle Data")
    }
  }

  private fun setMobileDataEnabledWorkaround(enabled: Boolean) {
    // Will call expensive root check if workAroundPreferences is not enabled for data
    // Undetermined failure if this is the case, be sure to guard with external check for
    // preference enabled. This will safely guard a lack of permissions error
    if (workaroundPermissionObserver.hasPermission()) {
      Settings.Global.putInt(contentResolver, dataUri, if (enabled) 1 else 0)
    } else {
      logger.w("Workaround permission not granted, cannot toggle Data")
    }
  }

  private fun setMobileDataEnabled(enabled: Boolean) {
    logger.i("Data: %s", if (enabled) "enable" else "disable")
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      if (workaroundPreferences.isDataWorkaroundEnabled()) {
        setMobileDataEnabledWorkaround(enabled)
      } else {
        setMobileDataEnabledRoot(enabled)
      }
    } else {
      setMobileDataEnabledReflection(enabled)
    }
  }

  override fun enable() {
    setMobileDataEnabled(true)
  }

  override fun disable() {
    setMobileDataEnabled(false)
  }

  private val mobileDataEnabledSettings: States
    @CheckResult get() = if (Settings.Global.getInt(contentResolver, dataUri,
        0) == 1) States.ENABLED
    else States.DISABLED

  private val mobileDataEnabledReflection: States
    @CheckResult get() {
      if (GET_MOBILE_DATA_ENABLED_METHOD != null) {
        try {
          return if (GET_MOBILE_DATA_ENABLED_METHOD.invoke(
              connectivityManager) as Boolean) States.ENABLED
          else States.DISABLED
        } catch (e: Exception) {
          logger.e("ManagerData getMobileDataEnabled ERROR")
        }
      }

      return mobileDataEnabledSettings
    }

  override val state: States
    get() {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        return mobileDataEnabledSettings
      } else {
        return mobileDataEnabledReflection
      }
    }

  companion object {
    private val GET_METHOD_NAME = "getMobileDataEnabled"
    private val SET_METHOD_NAME = "setMobileDataEnabled"
    private val GET_MOBILE_DATA_ENABLED_METHOD: Method?
    private val SET_MOBILE_DATA_ENABLED_METHOD: Method?

    init {
      GET_MOBILE_DATA_ENABLED_METHOD = reflectGetMethod()
      SET_MOBILE_DATA_ENABLED_METHOD = reflectSetMethod()
    }

    @CheckResult private fun reflectGetMethod(): Method? {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        Timber.e("Reflection method %s does not exist on Lollipop+", GET_METHOD_NAME)
        return null
      }

      try {
        val method = ConnectivityManager::class.java.getDeclaredMethod(GET_METHOD_NAME)
        method.isAccessible = true
        return method
      } catch (e: Exception) {
        Timber.e(e, "ManagerData reflectGetMethod ERROR")
      }

      return null
    }

    @CheckResult private fun reflectSetMethod(): Method? {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        Timber.e("Reflection method %s does not exist on Lollipop+", SET_METHOD_NAME)
        return null
      }

      try {
        val method = ConnectivityManager::class.java.getDeclaredMethod(SET_METHOD_NAME,
            java.lang.Boolean.TYPE)
        method.isAccessible = true
        return method
      } catch (e: Exception) {
        Timber.e(e, "ManagerData reflectSetMethod ERROR")
      }

      return null
    }
  }
}
