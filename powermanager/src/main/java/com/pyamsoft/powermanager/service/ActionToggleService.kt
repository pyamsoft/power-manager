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

import android.app.IntentService
import android.content.Intent
import com.pyamsoft.powermanager.Injector
import timber.log.Timber
import javax.inject.Inject

class ActionToggleService : IntentService(ActionToggleService::class.java.simpleName) {

  @field:Inject lateinit internal var presenter: ActionTogglePresenter

  override fun onCreate() {
    super.onCreate()
    Injector.get().provideComponent().inject(this)
  }

  override fun onDestroy() {
    super.onDestroy()
    presenter.stop()
    presenter.destroy()
  }

  override fun onHandleIntent(intent: Intent?) {
    presenter.toggleForegroundState(object : ActionTogglePresenter.ForegroundStateCallback {
      override fun onForegroundStateToggled(state: Boolean) {
        Timber.d("Foreground state toggled: %s", state)
        if (state) {
          ForegroundService.start(applicationContext)
        } else {
          ForegroundService.stop(applicationContext)
        }
      }
    })
  }
}
