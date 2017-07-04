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
import com.pyamsoft.powermanager.Injector
import com.pyamsoft.powermanager.trigger.bus.TriggerDeleteEvent
import com.pyamsoft.powermanager.trigger.db.PowerTriggerEntry
import com.pyamsoft.powermanager.uicore.WatchedDialog
import timber.log.Timber
import javax.inject.Inject

class DeleteTriggerDialog : WatchedDialog() {

  private var percent: Int = 0
  private lateinit var name: String

  @field:Inject internal lateinit var publisher: TriggerPublisher

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    name = arguments.getString(TRIGGER_NAME, null)
    percent = arguments.getInt(TRIGGER_PERCENT, -1)

    if (percent == -1) {
      Timber.e("Invalid percent for DeleteTriggerDialog. Dismiss dialog")
      dismiss()
    }

    Injector.with(context) {
      it.inject(this)
    }
  }

  override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
    return AlertDialog.Builder(activity).setTitle("Delete Trigger").setMessage(
        "Really delete trigger for: $name [$percent%] ?").setNegativeButton(
        "Cancel") { _, _ -> dismiss() }.setPositiveButton("Okay") { _, _ ->
      publisher.publish(TriggerDeleteEvent(percent))
      dismiss()
    }.create()
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
