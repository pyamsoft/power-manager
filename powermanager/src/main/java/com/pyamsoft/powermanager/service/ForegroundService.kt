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

import android.app.Notification
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.support.v4.app.NotificationManagerCompat
import com.pyamsoft.powermanager.Injector
import com.pyamsoft.powermanager.receiver.ScreenOnOffReceiver
import com.pyamsoft.powermanager.service.ForegroundPresenter.NotificationCallback
import com.pyamsoft.pydroid.ui.app.AutoRestartService
import timber.log.Timber
import javax.inject.Inject

class ForegroundService : AutoRestartService() {
  @field:Inject lateinit internal var presenter: ForegroundPresenter
  private lateinit var screenOnOffReceiver: ScreenOnOffReceiver
  private lateinit var notificationManager: NotificationManagerCompat

  override fun onBind(intent: Intent): IBinder? {
    return null
  }

  override fun onCreate() {
    super.onCreate()
    Injector.get().provideComponent().inject(this)

    notificationManager = NotificationManagerCompat.from(applicationContext)
    notificationManager.cancel(NOTIFICATION_ID)

    presenter.setForegroundState(true)
    presenter.queueRepeatingTriggerJob()

    screenOnOffReceiver = ScreenOnOffReceiver(this)
    screenOnOffReceiver.register()
  }

  override fun onDestroy() {
    super.onDestroy()
    presenter.setForegroundState(false)
    screenOnOffReceiver.unregister()

    presenter.stop()
    presenter.destroy()

    stopForeground(true)

    notificationManager.notify(NOTIFICATION_ID, presenter.hangNotification())
  }

  override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
    if (intent != null) {
      if (intent.getBooleanExtra(EXTRA_RESTART_TRIGGERS, false)) {
        presenter.restartTriggerAlarm()
      }
    }
    presenter.startNotification(object : NotificationCallback {
      override fun onStartNotificationInForeground(notification: Notification) {
        startForeground(NOTIFICATION_ID, notification)
      }
    })
    return Service.START_STICKY
  }

  companion object {
    const internal val NOTIFICATION_ID = 1000
    private const val EXTRA_RESTART_TRIGGERS = "EXTRA_RESTART_TRIGGERS"

    /**
     * Force the service On
     */
    @JvmStatic fun start(context: Context) {
      context.applicationContext.startService(
          Intent(context.applicationContext, ForegroundService::class.java))
    }

    /**
     * Force the service Off
     */
    @JvmStatic fun stop(context: Context) {
      context.applicationContext.stopService(
          Intent(context.applicationContext, ForegroundService::class.java))
    }

    /**
     * Restart the triggers
     */
    @JvmStatic fun restartTriggers(context: Context) {
      val appContext = context.applicationContext
      val service = Intent(appContext, ForegroundService::class.java)
      service.putExtra(EXTRA_RESTART_TRIGGERS, true)
      Timber.d("Restart Power Triggers")
      appContext.startService(service)
    }
  }
}
