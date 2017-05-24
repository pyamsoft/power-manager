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

import android.app.Dialog
import android.os.Bundle
import android.support.annotation.CheckResult
import android.support.v7.app.AlertDialog
import com.pyamsoft.powermanager.trigger.bus.TriggerDeleteEvent
import com.pyamsoft.powermanager.trigger.db.PowerTriggerEntry
import com.pyamsoft.powermanager.uicore.WatchedDialog
import com.pyamsoft.pydroid.bus.EventBus
import timber.log.Timber

class DeleteTriggerDialog : WatchedDialog() {
  internal var percent: Int = 0
  private var name: String? = null

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    name = arguments.getString(TRIGGER_NAME, null)
    percent = arguments.getInt(TRIGGER_PERCENT, -1)

    if (percent == -1) {
      Timber.e("Invalid percent for DeleteTriggerDialog. Dismiss dialog")
      dismiss()
    }
  }

  override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
    return AlertDialog.Builder(activity).setTitle("Delete Trigger").setMessage(
        "Really delete trigger for: $name [$percent%] ?").setNegativeButton(
        "Cancel") { _, _ -> dismiss() }.setPositiveButton("Okay") { _, _ ->
      sendDeleteEvent(percent)
      dismiss()
    }.create()
  }

  internal fun sendDeleteEvent(percent: Int) {
    EventBus.get().publish(TriggerDeleteEvent(percent))
  }

  companion object {

    private const val TRIGGER_NAME = "trigger_name"
    private const val TRIGGER_PERCENT = "trigger_percent"

    @JvmStatic @CheckResult fun newInstance(trigger: PowerTriggerEntry): DeleteTriggerDialog {
      val args = Bundle()
      val fragment = DeleteTriggerDialog()
      args.putString(TRIGGER_NAME, trigger.name())
      args.putInt(TRIGGER_PERCENT, trigger.percent())
      fragment.arguments = args
      return fragment
    }
  }
}
