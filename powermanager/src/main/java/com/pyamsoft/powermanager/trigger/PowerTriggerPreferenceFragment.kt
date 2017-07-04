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

package com.pyamsoft.powermanager.trigger

import android.os.Bundle
import android.support.v7.preference.Preference
import android.view.View
import com.pyamsoft.powermanager.Injector
import com.pyamsoft.powermanager.R
import com.pyamsoft.powermanager.service.ForegroundService
import com.pyamsoft.powermanager.uicore.WatchedPreferenceFragment
import javax.inject.Inject

class PowerTriggerPreferenceFragment : WatchedPreferenceFragment() {

  @field:Inject internal lateinit var presenter: TriggerPreferencePresenter
  private lateinit var triggerInterval: Preference

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    Injector.with(context) {
      it.inject(this)
    }
  }

  override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
    addPreferencesFromResource(R.xml.power_trigger_options)
  }

  override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    triggerInterval = findPreference(getString(R.string.trigger_period_key))
  }

  override fun onStart() {
    super.onStart()
    presenter.clickEvent(triggerInterval, {
      ForegroundService.restartTriggers(context)
    })
  }

  override fun onStop() {
    super.onStop()
    presenter.stop()
  }

  override fun onDestroy() {
    super.onDestroy()
    presenter.destroy()
  }

  companion object {
    const val TAG = "PowerTriggerPreferenceFragment"
  }
}
