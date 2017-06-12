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

import android.app.ActivityManager
import android.content.Context
import android.os.Bundle
import android.support.v4.app.NotificationManagerCompat
import android.view.View
import com.pyamsoft.powermanager.Injector
import com.pyamsoft.powermanager.PowerManager
import com.pyamsoft.powermanager.R
import com.pyamsoft.powermanager.main.MainActivity
import com.pyamsoft.powermanager.service.ForegroundService
import com.pyamsoft.powermanager.settings.bus.ConfirmEvent
import com.pyamsoft.pydroid.ui.about.AboutLibrariesFragment.BackStackState
import com.pyamsoft.pydroid.ui.about.AboutLibrariesFragment.BackStackState.LAST
import com.pyamsoft.pydroid.ui.app.fragment.ActionBarSettingsPreferenceFragment
import com.pyamsoft.pydroid.ui.helper.Toasty
import com.pyamsoft.pydroid.ui.util.ActionBarUtil
import kotlinx.android.synthetic.main.activity_main.bottomtabs
import timber.log.Timber
import javax.inject.Inject

class SettingsPreferenceFragment : ActionBarSettingsPreferenceFragment() {

  @field:Inject internal lateinit var presenter: SettingsPreferencePresenter

  override val applicationName: String
    get() = getString(R.string.app_name)
  override val rootViewContainer: Int
    get() = R.id.main_container
  override val preferenceXmlResId: Int
    get() = R.xml.preferences
  override val isLastOnBackStack: BackStackState
    get() = LAST

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    Injector.with(context) {
      it.inject(this)
    }
  }

  override fun onLicenseItemClicked() {
    ActionBarUtil.setActionBarUpEnabled(activity, true)

    if (activity is MainActivity) {
      activity.bottomtabs.visibility = View.GONE
    }
    super.onLicenseItemClicked()
  }

  override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    findPreference(getString(R.string.clear_db_key))?.setOnPreferenceClickListener {
      ConfirmationDialog.newInstance(ConfirmEvent.Type.DATABASE)
      return@setOnPreferenceClickListener true
    }

    findPreference(getString(R.string.use_root_key))?.setOnPreferenceClickListener {
      Timber.d("Root clicked")
      return@setOnPreferenceClickListener true
    }
  }

  override fun onDestroyView() {
    super.onDestroyView()
    findPreference(getString(R.string.clear_db_key))?.onPreferenceClickListener = null
    findPreference(getString(R.string.clear_all_key))?.onPreferenceClickListener = null
    findPreference(getString(R.string.use_root_key))?.onPreferenceClickListener = null
  }

  override fun onClearAllClicked() {
    super.onClearAllClicked()
    ConfirmationDialog.newInstance(ConfirmEvent.Type.ALL)
  }

  override fun onStart() {
    super.onStart()
    presenter.registerOnBus(onClearDatabase = {
      Toasty.makeText(context, "Trigger Database cleared", Toasty.LENGTH_SHORT).show()
    }, onClearAll = {
      ForegroundService.stop(context)
      NotificationManagerCompat.from(context.applicationContext).cancel(
          ForegroundService.NOTIFICATION_ID)
      (context.applicationContext.getSystemService(
          Context.ACTIVITY_SERVICE) as ActivityManager).clearApplicationUserData()
    })
  }

  override fun onStop() {
    super.onStop()
    presenter.stop()
  }

  override fun onDestroy() {
    super.onDestroy()
    PowerManager.getRefWatcher(this).watch(this)
    presenter.destroy()
  }

  companion object {
    const val TAG = "SettingsPreferenceFragment"
  }
}
