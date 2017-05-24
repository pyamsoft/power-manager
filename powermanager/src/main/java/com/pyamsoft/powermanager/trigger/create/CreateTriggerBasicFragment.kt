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
import com.pyamsoft.powermanager.trigger.db.PowerTriggerEntry
import com.pyamsoft.powermanager.uicore.WatchedFragment
import kotlinx.android.synthetic.main.fragment_trigger_basic.create_trigger_basic_name_layout
import kotlinx.android.synthetic.main.fragment_trigger_basic.create_trigger_basic_percent_layout
import timber.log.Timber

class CreateTriggerBasicFragment : WatchedFragment() {

  override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
      savedInstanceState: Bundle?): View? {
    return inflater?.inflate(R.layout.fragment_trigger_basic, container, false)
  }

  val triggerName: String
    @CheckResult get() {
      val name: String
      if (create_trigger_basic_name_layout == null) {
        Timber.e("Name layout is empty!")
        name = PowerTriggerEntry.EMPTY_NAME
      } else {
        val editText = create_trigger_basic_name_layout.editText
        if (editText == null || editText.text.toString().isEmpty()) {
          Timber.e("Name edit is empty!")
          name = PowerTriggerEntry.EMPTY_NAME
        } else {
          Timber.d("Get name")
          name = editText.text.toString()
        }
      }
      return name
    }

  val triggerPercent: Int
    @CheckResult get() {
      var percent: Int
      if (create_trigger_basic_percent_layout == null) {
        Timber.e("Percent layout is empty!")
        percent = PowerTriggerEntry.EMPTY_PERCENT
      } else {
        val editText = create_trigger_basic_percent_layout.editText
        if (editText == null) {
          Timber.e("Percent edit is empty!")
          percent = PowerTriggerEntry.EMPTY_PERCENT
        } else {
          Timber.d("Get percent")
          try {
            percent = Integer.parseInt(editText.text.toString())
          } catch (e: NumberFormatException) {
            Timber.e("Percent is not a Number")
            percent = PowerTriggerEntry.EMPTY_PERCENT
          }

        }
      }
      return percent
    }
}
