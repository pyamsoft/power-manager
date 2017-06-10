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
import android.support.annotation.CheckResult
import com.pyamsoft.pydroid.presenter.SchedulerPresenter
import io.reactivex.Scheduler
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Named

class ForegroundPresenter @Inject internal constructor(private val interactor: ForegroundInteractor,
    @Named("obs") obsScheduler: Scheduler,
    @Named("sub") subScheduler: Scheduler) : SchedulerPresenter(obsScheduler, subScheduler) {
  /**
   * public
   */
  fun queueRepeatingTriggerJob() {
    interactor.queueRepeatingTriggerJob()
  }

  override fun onDestroy() {
    super.onDestroy()
    interactor.destroy()
  }

  /**
   * public
   */
  @CheckResult fun hangNotification(): Notification {
    return interactor.createNotification(false).blockingGet()
  }

  /**
   * public
   */
  fun startNotification(onStartNotificationInForeground: (Notification) -> Unit) {
    disposeOnStop {
      interactor.createNotification(true).subscribeOn(subscribeScheduler).observeOn(
          observeScheduler).subscribe({ onStartNotificationInForeground(it) },
          { Timber.e(it, "onError") })
    }
  }

  /**
   * public

   * Trigger interval is only read on interactor.queueRepeatingTriggerJob()
   * Restart it by destroying and then re-creating the interactor
   */
  fun restartTriggerAlarm() {
    interactor.destroy()
    interactor.queueRepeatingTriggerJob()
  }

  fun setForegroundState(enable: Boolean) {
    interactor.setServiceEnabled(enable)
  }
}
