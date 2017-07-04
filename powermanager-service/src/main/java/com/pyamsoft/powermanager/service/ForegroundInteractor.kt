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

import android.app.Activity
import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.support.annotation.CheckResult
import android.support.v4.app.NotificationCompat
import android.support.v4.content.ContextCompat
import com.pyamsoft.powermanager.base.preference.ServicePreferences
import com.pyamsoft.powermanager.base.preference.TriggerPreferences
import com.pyamsoft.powermanager.job.JobQueuer
import io.reactivex.Single
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

@Singleton internal class ForegroundInteractor @Inject constructor(
    @param:Named("delay") private val jobQueuer: JobQueuer, context: Context,
    preferences: ServicePreferences, private val triggerPreferences: TriggerPreferences,
    @Named("main") mainActivityClass: Class<out Activity>,
    @param:Named("toggle") private val toggleServiceClass: Class<out Service>) : ServiceInteractor(
    preferences) {
  private val builder: NotificationCompat.Builder
  private val appContext: Context = context.applicationContext

  init {
    val intent = Intent(appContext, mainActivityClass).setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
    val pendingIntent = PendingIntent.getActivity(appContext, PENDING_RC, intent, 0)

    builder = NotificationCompat.Builder(appContext).setContentTitle(
        context.getString(R.string.app_name)).setSmallIcon(R.drawable.ic_notification).setColor(
        ContextCompat.getColor(context, R.color.amber500)).setAutoCancel(false).setWhen(
        0).setNumber(0).setContentIntent(pendingIntent)
  }

  /**
   * public
   */
  fun queueRepeatingTriggerJob() {
    // TODO
    //    val delayTime = triggerPreferences.triggerPeriodTime
    //    val triggerPeriod = TimeUnit.MINUTES.toSeconds(delayTime)
    //jobQueuer.cancel(JobQueuer.TRIGGER_JOB_TAG);
    //jobQueuer.queueRepeating(JobQueuerEntry.builder(JobQueuer.TRIGGER_JOB_TAG)
    //    .repeatingOnWindow(0)
    //    .repeating(true)
    //    .repeatingOffWindow(0)
    //    .delay(triggerPeriod)
    //    .ignoreIfCharging(false)
    //    .type(QueuerType.POWER_TRIGGER)
    //    .build());
  }

  fun destroy() {
    Timber.d("Cancel all trigger jobs")
    // TODO
    //    jobQueuer.cancel(JobQueuer.TRIGGER_JOB_TAG);
  }

  @CheckResult fun getNotificationPriority(): Single<Int> {
    return Single.fromCallable { preferences.notificationPriority }
  }

  /**
   * public
   */
  fun createNotification(foreground: Boolean): Single<Notification> {
    return isServiceEnabled().flatMap { serviceEnabled ->
      val actionName = if (serviceEnabled) "Suspend" else "Start"
      val toggleService = Intent(appContext, toggleServiceClass)
      val actionToggleService = PendingIntent.getService(appContext, TOGGLE_RC, toggleService,
          PendingIntent.FLAG_UPDATE_CURRENT)
      val title = if (serviceEnabled) "Managing Device Power..." else "Power Management Suspended..."

      getNotificationPriority().map {
        // Clear all of the Actions
        builder.mActions.clear()
        builder.setPriority(it).setContentText(title).setOngoing(foreground).addAction(
            R.drawable.ic_notification, actionName, actionToggleService).build()
      }
    }
  }

  companion object {
    private const val PENDING_RC = 1004
    private const val TOGGLE_RC = 421
  }
}
