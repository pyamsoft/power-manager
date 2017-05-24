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

package com.pyamsoft.powermanager.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.hardware.display.DisplayManager
import android.os.Build
import android.support.annotation.CheckResult
import android.view.Display
import android.widget.Toast
import com.pyamsoft.powermanager.Injector
import com.pyamsoft.powermanager.base.logger.Logger
import com.pyamsoft.powermanager.manager.Manager
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Named

class ScreenOnOffReceiver(context: Context) : BroadcastReceiver() {

  private val appContext: Context = context.applicationContext
  private val displayManager: DisplayManager

  @field: Inject lateinit internal var manager: Manager
  @field:[Inject Named("logger_manager")] lateinit internal var logger: Logger
  private var isRegistered: Boolean = false

  init {
    isRegistered = false
    displayManager = appContext.getSystemService(Context.DISPLAY_SERVICE) as DisplayManager
    Injector.get().provideComponent().inject(this)
  }

  /**
   * Checks the display state on API's which have finer tuned states.

   * Returns true if the display is in the state we assume it to be.
   */
  @CheckResult private fun checkDisplayState(displayOn: Boolean): Boolean {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT_WATCH) {
      Timber.w("Old API, always trust the broadcast")
      return true
    } else {
      val allDisplays = displayManager.displays
      val checkState = if (displayOn) Display.STATE_OFF else Display.STATE_ON
      var allInState = true
      for (display in allDisplays) {
        Timber.d("Check that display: %s is %s", display.name, if (displayOn) "ON" else "OFF")
        if (display.state == checkState) {
          Timber.w("Display: %s is %s", display.name, if (displayOn) "OFF" else "ON")
          allInState = false
          break
        }
      }
      return allInState
    }
  }

  override fun onReceive(context: Context, intent: Intent?) {
    if (null != intent) {
      val action = intent.action
      when (action) {
        Intent.ACTION_SCREEN_OFF -> {
          logger.d("Some screen off action")
          if (checkDisplayState(false)) {
            logger.i("Screen off event")
            disableManagers()
          }
        }
        Intent.ACTION_SCREEN_ON -> {
          logger.d("Some screen on action")
          if (checkDisplayState(true)) {
            logger.i("Screen on event")
            enableManagers()
          }
        }
        else -> Timber.e("Invalid event: %s", action)
      }
    }
  }

  private fun enableManagers() {
    manager.enable(null)
  }

  private fun disableManagers() {
    manager.disable(null)
  }

  fun register() {
    if (!isRegistered) {
      cleanup()
      appContext.registerReceiver(this, SCREEN_FILTER)
      isRegistered = true

      Toast.makeText(appContext, "Power Manager started", Toast.LENGTH_SHORT).show()
    } else {
      Timber.w("Already registered")
    }
  }

  private fun cleanup() {
    manager.cleanup()
  }

  fun unregister() {
    if (isRegistered) {
      appContext.unregisterReceiver(this)
      cleanup()
      isRegistered = false

      Toast.makeText(appContext, "Power Manager suspended", Toast.LENGTH_SHORT).show()
    } else {
      Timber.w("Already unregistered")
    }
  }

  companion object {

    @JvmStatic private val SCREEN_FILTER: IntentFilter = IntentFilter()

    init {
      SCREEN_FILTER.addAction(Intent.ACTION_SCREEN_OFF)
      SCREEN_FILTER.addAction(Intent.ACTION_SCREEN_ON)
    }
  }
}

