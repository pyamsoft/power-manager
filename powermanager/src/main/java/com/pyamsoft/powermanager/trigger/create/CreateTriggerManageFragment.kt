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

import android.os.Bundle
import android.support.annotation.CheckResult
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.pyamsoft.powermanager.R
import com.pyamsoft.powermanager.uicore.WatchedFragment
import kotlinx.android.synthetic.main.fragment_trigger_manage.create_trigger_manage_enable
import kotlinx.android.synthetic.main.fragment_trigger_manage.create_trigger_manage_enable_explanation
import kotlinx.android.synthetic.main.fragment_trigger_manage.create_trigger_manage_toggle
import kotlinx.android.synthetic.main.fragment_trigger_manage.create_trigger_manage_toggle_explanation
import timber.log.Timber

class CreateTriggerManageFragment : WatchedFragment() {
  private var type: Int = 0

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    type = arguments.getInt(FRAGMENT_TYPE, -1)
  }

  override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
      savedInstanceState: Bundle?): View? {
    return inflater?.inflate(R.layout.fragment_trigger_manage, container, false)
  }

  override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    setExplanation()
  }

  private fun setExplanation() {
    val radio: String
    when (type) {
      TYPE_WIFI -> radio = "Wifi"
      TYPE_DATA -> radio = "Data"
      TYPE_BLUETOOTH -> radio = "Bluetooth"
      TYPE_SYNC -> radio = "Sync"
      else -> throw IllegalStateException("Invalid type: $type")
    }
    val toggle = "Toggle $radio"
    val toggleExplainChecked = "Change state of $radio as specified"
    val toggleExplainUnchecked = "Do not change state of $radio"
    val enable = "Enable $radio"
    val enableExplainChecked = "$radio will be turned on"
    val enableExplainUnchecked = "$radio will be turned off"

    create_trigger_manage_toggle_explanation.text = toggleExplainUnchecked
    create_trigger_manage_toggle.text = toggle
    create_trigger_manage_toggle.setOnCheckedChangeListener { _, b ->
      create_trigger_manage_toggle_explanation.text = if (b) toggleExplainChecked else toggleExplainUnchecked
    }

    create_trigger_manage_enable_explanation.text = enableExplainUnchecked
    create_trigger_manage_enable.text = enable
    create_trigger_manage_enable.setOnCheckedChangeListener { _, b ->
      create_trigger_manage_enable_explanation.text = if (b) enableExplainChecked else enableExplainUnchecked
    }
  }

  val triggerToggle: Boolean
    @CheckResult get() {
      val toggle: Boolean
      if (create_trigger_manage_toggle == null) {
        Timber.e("Toggle is NULL")
        toggle = false
      } else {
        Timber.d("Get toggle")
        toggle = create_trigger_manage_toggle.isChecked
      }
      return toggle
    }
  val triggerEnable: Boolean
    @CheckResult get() {
      val enable: Boolean
      if (create_trigger_manage_enable == null) {
        Timber.e("Enable is NULL")
        enable = false
      } else {
        Timber.d("Get enable")
        enable = create_trigger_manage_enable.isChecked
      }
      return enable
    }

  companion object {
    const val TYPE_WIFI = 0
    const val TYPE_DATA = 1
    const val TYPE_BLUETOOTH = 2
    const val TYPE_SYNC = 3
    private const val FRAGMENT_TYPE = "fragment_type"

    @JvmStatic @CheckResult fun newInstance(type: Int): CreateTriggerManageFragment {
      val args = Bundle()
      val fragment = CreateTriggerManageFragment()
      args.putInt(FRAGMENT_TYPE, type)
      fragment.arguments = args
      return fragment
    }
  }
}
