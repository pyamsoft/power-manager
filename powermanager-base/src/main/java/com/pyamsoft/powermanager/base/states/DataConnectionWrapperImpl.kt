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
import com.pyamsoft.powermanager.base.shell.ShellCommandHelper
import com.pyamsoft.powermanager.model.States
import timber.log.Timber
import java.lang.reflect.Method
import javax.inject.Inject

internal class DataConnectionWrapperImpl @Inject constructor(context: Context,
    private val shellCommandHelper: ShellCommandHelper, private val logger: Logger,
    private val preferences: RootPreferences, private val dataUri: String) : DeviceFunctionWrapper {

  private val connectivityManager: ConnectivityManager = context.applicationContext.getSystemService(
      Context.CONNECTIVITY_SERVICE) as ConnectivityManager
  private val contentResolver: ContentResolver = context.applicationContext.contentResolver

  private val mobileDataEnabledReflection: States
    @CheckResult get() {
      if (GET_MOBILE_DATA_ENABLED_METHOD != null) {
        try {
          return if (GET_MOBILE_DATA_ENABLED_METHOD.invoke(connectivityManager) as Boolean)
            States.ENABLED
          else
            States.DISABLED
        } catch (e: Exception) {
          logger.e("ManagerData getMobileDataEnabled ERROR")
        }

      }

      return mobileDataEnabledSettings
    }

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

   * Will exit with a failed 137 code or otherwise if ROOT is not allowed
   */
  private fun setMobileDataEnabledRoot(enabled: Boolean) {
    if (preferences.rootEnabled) {
      val command = "svc data " + if (enabled) "enable" else "disable"
      shellCommandHelper.runSUCommand(command)
    } else {
      logger.w("Root not enabled, cannot toggle Data")
    }
  }

  private val mobileDataEnabledSettings: States
    @CheckResult get() = if (Settings.Global.getInt(contentResolver, dataUri, 0) == 1)
      States.ENABLED
    else
      States.DISABLED

  private fun setMobileDataEnabled(enabled: Boolean) {
    logger.i("Data: %s", if (enabled) "enable" else "disable")
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
      setMobileDataEnabledReflection(enabled)
    } else {
      setMobileDataEnabledRoot(enabled)
    }
  }

  override fun enable() {
    setMobileDataEnabled(true)
  }

  override fun disable() {
    setMobileDataEnabled(false)
  }

  override val state: States
    get() {
      if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
        return mobileDataEnabledReflection
      } else {
        return mobileDataEnabledSettings
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
