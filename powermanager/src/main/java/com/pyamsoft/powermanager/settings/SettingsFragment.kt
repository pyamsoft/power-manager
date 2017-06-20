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

package com.pyamsoft.powermanager.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.pyamsoft.powermanager.R
import com.pyamsoft.powermanager.main.MainActivity
import com.pyamsoft.powermanager.uicore.WatchedFragment
import com.pyamsoft.pydroid.ui.util.ActionBarUtil

class SettingsFragment : WatchedFragment() {
  override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
      savedInstanceState: Bundle?): View? {
    return inflater?.inflate(R.layout.fragment_settings, container, false)
  }

  override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    if (childFragmentManager.findFragmentByTag(SettingsPreferenceFragment.TAG) == null) {
      childFragmentManager.beginTransaction().add(R.id.settings_child_here,
          SettingsPreferenceFragment(), SettingsPreferenceFragment.TAG).commit()
    }
  }

  override fun onResume() {
    super.onResume()
    ActionBarUtil.setActionBarUpEnabled(activity, false)
    ActionBarUtil.setActionBarTitle(activity, R.string.app_name)

    if (activity is MainActivity) {
      val main = activity as MainActivity
      main.binding.bottomtabs.visibility = View.VISIBLE
      main.setOverlapTop(0F)
    }
  }

  companion object {
    const val TAG = "SettingsFragment"
  }
}

