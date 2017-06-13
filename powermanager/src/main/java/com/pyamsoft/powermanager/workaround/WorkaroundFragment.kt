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

package com.pyamsoft.powermanager.workaround

import android.os.Bundle
import com.pyamsoft.powermanager.Injector
import com.pyamsoft.powermanager.R
import com.pyamsoft.powermanager.model.PermissionObserver
import com.pyamsoft.powermanager.uicore.WatchedPreferenceFragment
import com.pyamsoft.pydroid.ui.util.DialogUtil
import javax.inject.Inject
import javax.inject.Named

class WorkaroundFragment : WatchedPreferenceFragment() {

  @field:[Inject Named(
      "obs_data_permission")] internal lateinit var dataPermissionObserver: PermissionObserver

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    Injector.with(context) {
      it.inject(this)
    }
  }

  override fun onCreatePreferences(p0: Bundle?, p1: String?) {
    addPreferencesFromResource(R.xml.workarounds)
  }

  override fun onStart() {
    super.onStart()
    findPreference(getString(R.string.key_workaround_howto_data)).setOnPreferenceClickListener {
      DialogUtil.guaranteeSingleDialogFragment(activity, DataWorkaroundDialog(), "data_workaround")
      return@setOnPreferenceClickListener true
    }


    findPreference(getString(R.string.key_workaround_data)).setOnPreferenceChangeListener { _, _ ->
      dataPermissionObserver.hasPermission()
    }
  }

  override fun onResume() {
    super.onResume()
    findPreference(
        getString(R.string.key_workaround_data)).isEnabled = dataPermissionObserver.hasPermission()
  }

  override fun onStop() {
    super.onStop()
    findPreference(getString(R.string.key_workaround_data)).onPreferenceChangeListener = null
  }

  companion object {

    const val TAG = "WorkaroundFragment"
  }
}

