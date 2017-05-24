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

package com.pyamsoft.powermanager.job

import android.support.annotation.CheckResult
import com.evernote.android.job.JobManager
import com.evernote.android.job.JobRequest
import com.evernote.android.job.util.support.PersistableBundleCompat
import timber.log.Timber
import java.util.concurrent.TimeUnit

internal abstract class BaseJobQueuer(private val jobManager: JobManager) : JobQueuer {

  override fun cancel(tag: String) {
    Timber.w("Cancel all jobs for tag: %s", tag)
    jobManager.cancelAllForTag(tag)
  }

  @CheckResult private fun createExtras(entry: JobQueuerEntry): PersistableBundleCompat {
    val extras = PersistableBundleCompat()
    extras.putBoolean(KEY_SCREEN, entry.screenOn())
    extras.putLong(KEY_ON_WINDOW, entry.repeatingOnWindow())
    extras.putLong(KEY_OFF_WINDOW, entry.repeatingOffWindow())
    extras.putBoolean(KEY_ONESHOT, entry.oneshot())
    extras.putBoolean(KEY_FIRST_RUN, entry.firstRun())
    return extras
  }

  override fun queue(entry: JobQueuerEntry) {
    val extras = createExtras(entry)
    if (entry.delay() == 0L) {
      runInstantJob(entry.tag(), extras)
    } else {
      scheduleJob(entry, extras)
    }
  }

  private fun scheduleJob(entry: JobQueuerEntry, extras: PersistableBundleCompat) {
    val startTime = TimeUnit.SECONDS.toMillis(entry.delay())
    JobRequest.Builder(entry.tag()).setExecutionWindow(startTime, startTime + FIVE_SECONDS)
        .setPersisted(false)
        .setExtras(extras)
        .setRequiresCharging(false)
        .setRequiresDeviceIdle(false)
        .build()
        .schedule()
  }

  override fun queueRepeating(entry: JobQueuerEntry) {
    val extras = createExtras(entry)
    JobRequest.Builder(entry.tag()).setPeriodic(TimeUnit.SECONDS.toMillis(entry.delay()))
        .setPersisted(false)
        .setExtras(extras)
        .setRequiresCharging(false)
        .setRequiresDeviceIdle(false)
        .build()
        .schedule()
  }

  internal abstract fun runInstantJob(tag: String, extras: PersistableBundleCompat)

  companion object {

    const val KEY_ON_WINDOW = "extra_key__on_window"
    const val KEY_OFF_WINDOW = "extra_key__off_window"
    const val KEY_SCREEN = "extra_key__screen"
    const val KEY_ONESHOT = "extra_key__once"
    const val KEY_FIRST_RUN = "extra_key__first"
    @JvmStatic
    private val FIVE_SECONDS = TimeUnit.SECONDS.toMillis(5)
  }
}
