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

import android.view.View
import com.pyamsoft.powermanager.R
import com.pyamsoft.powermanager.main.MainActivity
import com.pyamsoft.pydroid.ui.about.AboutLibrariesFragment.BackStackState
import com.pyamsoft.pydroid.ui.about.AboutLibrariesFragment.BackStackState.LAST
import com.pyamsoft.pydroid.ui.app.fragment.ActionBarSettingsPreferenceFragment
import com.pyamsoft.pydroid.ui.util.ActionBarUtil
import kotlinx.android.synthetic.main.activity_main.bottomtabs

class SettingsFragment : ActionBarSettingsPreferenceFragment() {

  override val applicationName: String
    get() = getString(R.string.app_name)

  override val rootViewContainer: Int
    get() = R.id.main_container

  override val preferenceXmlResId: Int
    get() = R.xml.preferences

  override val isLastOnBackStack: BackStackState
    get() = LAST

  override fun onLicenseItemClicked() {
    ActionBarUtil.setActionBarUpEnabled(activity, true)

    if (activity is MainActivity) {
      activity.bottomtabs.visibility = View.GONE
    }
    super.onLicenseItemClicked()
  }

  override fun onResume() {
    super.onResume()
    ActionBarUtil.setActionBarUpEnabled(activity, false)
    ActionBarUtil.setActionBarTitle(activity, R.string.app_name)

    if (activity is MainActivity) {
      activity.bottomtabs.visibility = View.VISIBLE
    }
  }

  companion object {
    const val TAG = "SettingsFragment"
  }
}
