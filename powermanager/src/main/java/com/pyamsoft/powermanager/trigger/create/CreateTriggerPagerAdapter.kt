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

package com.pyamsoft.powermanager.trigger.create

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentStatePagerAdapter
import android.support.v4.view.ViewPager
import com.pyamsoft.powermanager.trigger.bus.TriggerCreateEvent
import com.pyamsoft.powermanager.trigger.db.PowerTriggerEntry
import com.pyamsoft.pydroid.bus.EventBus
import timber.log.Timber

internal class CreateTriggerPagerAdapter(fragment: Fragment) : FragmentStatePagerAdapter(
    fragment.childFragmentManager) {

  override fun getItem(position: Int): Fragment {
    val fragment: Fragment
    when (position) {
      POSITION_WIFI -> fragment = CreateTriggerManageFragment.newInstance(
          CreateTriggerManageFragment.TYPE_WIFI)
      POSITION_DATA -> fragment = CreateTriggerManageFragment.newInstance(
          CreateTriggerManageFragment.TYPE_DATA)
      POSITION_BLUETOOTH -> fragment = CreateTriggerManageFragment.newInstance(
          CreateTriggerManageFragment.TYPE_BLUETOOTH)
      POSITION_SYNC -> fragment = CreateTriggerManageFragment.newInstance(
          CreateTriggerManageFragment.TYPE_SYNC)
      else -> fragment = CreateTriggerBasicFragment()
    }
    return fragment
  }

  override fun getCount(): Int {
    return TOTAL_COUNT
  }

  fun collect(viewPager: ViewPager) {
    val basicFragment = instantiateItem(viewPager, POSITION_BASIC) as CreateTriggerBasicFragment
    val wifiFragment = instantiateItem(viewPager, POSITION_WIFI) as CreateTriggerManageFragment
    val dataFragment = instantiateItem(viewPager, POSITION_DATA) as CreateTriggerManageFragment
    val bluetoothFragment = instantiateItem(viewPager,
        POSITION_BLUETOOTH) as CreateTriggerManageFragment
    val syncFragment = instantiateItem(viewPager, POSITION_SYNC) as CreateTriggerManageFragment

    val name = basicFragment.triggerName
    val percent = basicFragment.triggerPercent
    val wifiToggle = wifiFragment.triggerToggle
    val wifiEnable = wifiFragment.triggerEnable
    val dataToggle = dataFragment.triggerToggle
    val dataEnable = dataFragment.triggerEnable
    val bluetoothToggle = bluetoothFragment.triggerToggle
    val bluetoothEnable = bluetoothFragment.triggerEnable
    val syncToggle = syncFragment.triggerToggle
    val syncEnable = syncFragment.triggerEnable

    Timber.d("Post content values to bus")
    val entry = PowerTriggerEntry.creator().create(percent, name, true, true, wifiToggle,
        dataToggle, bluetoothToggle, syncToggle, wifiEnable, dataEnable, bluetoothEnable,
        syncEnable)
    EventBus.get().publish(TriggerCreateEvent(entry))
  }

  companion object {

    const val TOTAL_COUNT = 5
    private const val POSITION_BASIC = 0
    private const val POSITION_WIFI = 1
    private const val POSITION_DATA = 2
    private const val POSITION_BLUETOOTH = 3
    private const val POSITION_SYNC = 4
  }
}

