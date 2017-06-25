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

package com.pyamsoft.powermanager.main

import android.os.Bundle
import android.support.annotation.CheckResult
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.pyamsoft.powermanager.Injector
import com.pyamsoft.powermanager.R
import com.pyamsoft.powermanager.databinding.FragmentMainBinding
import com.pyamsoft.powermanager.manage.ManageFragment
import com.pyamsoft.powermanager.settings.SettingsFragment
import com.pyamsoft.powermanager.trigger.PowerTriggerFragment
import com.pyamsoft.powermanager.uicore.WatchedFragment
import com.pyamsoft.powermanager.workaround.WorkaroundFragment
import javax.inject.Inject

class MainFragment : WatchedFragment() {

  @field:Inject internal lateinit var presenter: MainRouterPresenter
  private lateinit var binding: FragmentMainBinding

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    Injector.with(context) {
      it.inject(this)
    }
  }

  override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
      savedInstanceState: Bundle?): View? {
    binding = FragmentMainBinding.inflate(inflater, container, false)
    return binding.root
  }

  private fun setupBottomBar() {
    presenter.clickBottomNavigation(binding.bottomtabs, {
      val handled: Boolean
      when (it.itemId) {
        R.id.menu_manage -> {
          handled = replaceFragment(ManageFragment.newInstance(), ManageFragment.TAG)
        }
        R.id.menu_workarounds -> {
          handled = replaceFragment(WorkaroundFragment(), WorkaroundFragment.TAG)
        }
        R.id.menu_triggers -> {
          handled = replaceFragment(ManageFragment.newInstance(), ManageFragment.TAG)
        }
        R.id.menu_settings -> {
          handled = replaceFragment(SettingsFragment(), SettingsFragment.TAG)
        }
        else -> handled = false
      }

      if (handled) {
        it.isChecked = !it.isChecked
      }
    })
  }

  @CheckResult private fun replaceFragment(fragment: Fragment, tag: String): Boolean {
    val fragmentManager = childFragmentManager
    if (fragmentManager.findFragmentByTag(tag) == null) {
      fragmentManager.beginTransaction().replace(R.id.main_container, fragment, tag).commit()
      return true
    } else {
      return false
    }
  }

  override fun onStart() {
    super.onStart()
    setupBottomBar()
    if (hasNoActiveFragment()) {
      binding.bottomtabs.menu.performIdentifierAction(R.id.menu_manage, 0)
    }
  }

  override fun onStop() {
    super.onStop()
    presenter.stop()
  }

  override fun onDestroy() {
    super.onDestroy()
    presenter.destroy()
  }

  @CheckResult private fun hasNoActiveFragment(): Boolean {
    return childFragmentManager.findFragmentByTag(
        SettingsFragment.TAG) == null && childFragmentManager.findFragmentByTag(
        WorkaroundFragment.TAG) == null && childFragmentManager.findFragmentByTag(
        PowerTriggerFragment.TAG) == null && childFragmentManager.findFragmentByTag(
        ManageFragment.TAG) == null
  }

  companion object {
    const val TAG = "MainFragment"
  }

}

