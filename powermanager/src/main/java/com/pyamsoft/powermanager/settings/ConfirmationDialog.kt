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

import android.app.Dialog
import android.os.Bundle
import android.support.annotation.CheckResult
import android.support.v7.app.AlertDialog
import com.pyamsoft.powermanager.settings.bus.ConfirmEvent
import com.pyamsoft.powermanager.uicore.WatchedDialog
import com.pyamsoft.pydroid.bus.EventBus

class ConfirmationDialog : WatchedDialog() {

  lateinit internal var clearType: ConfirmEvent.Type

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    val code = arguments.getString(WHICH) ?: throw RuntimeException(
        "Cannot show dialog without ClearCode")

    clearType = ConfirmEvent.Type.valueOf(code)
  }

  override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
    return AlertDialog.Builder(activity).setMessage(
        if (clearType === ConfirmEvent.Type.DATABASE) "Really clear entire database?\n\nYou will have to re-configure all triggers again"
        else "Really clear all application settings?").setPositiveButton("Yes") { _, _ ->
      EventBus.get().publish(ConfirmEvent(clearType))
    }.setNegativeButton("No") { _, _ -> dismiss() }.create()
  }

  companion object {
    private const val WHICH = "which_type"

    @JvmStatic @CheckResult fun newInstance(codes: ConfirmEvent.Type): ConfirmationDialog {
      val fragment = ConfirmationDialog()
      val args = Bundle()
      args.putString(WHICH, codes.name)
      fragment.arguments = args
      return fragment
    }
  }
}
