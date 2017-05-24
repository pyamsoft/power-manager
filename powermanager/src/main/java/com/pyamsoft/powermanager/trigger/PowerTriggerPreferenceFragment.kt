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
import android.view.View
import com.pyamsoft.powermanager.R
import com.pyamsoft.powermanager.service.ForegroundService
import com.pyamsoft.powermanager.uicore.WatchedPreferenceFragment

class PowerTriggerPreferenceFragment : WatchedPreferenceFragment() {

  override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
    addPreferencesFromResource(R.xml.power_trigger_options)
  }

  override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    val triggerInterval = findPreference(getString(R.string.trigger_period_key))
    triggerInterval.setOnPreferenceChangeListener { _, _ ->
      ForegroundService.restartTriggers(context)
      true
    }
  }

  companion object {

    const val TAG = "PowerTriggerPreferenceFragment"
  }
}
