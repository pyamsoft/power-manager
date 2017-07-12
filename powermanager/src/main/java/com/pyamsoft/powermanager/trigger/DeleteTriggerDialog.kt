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

import android.os.Bundle
import android.support.annotation.CheckResult
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.pyamsoft.powermanager.Injector
import com.pyamsoft.powermanager.databinding.DialogTriggerDeleteBinding
import com.pyamsoft.powermanager.trigger.db.PowerTriggerEntry
import com.pyamsoft.powermanager.uicore.WatchedBottomSheet
import timber.log.Timber
import javax.inject.Inject

class DeleteTriggerDialog : WatchedBottomSheet() {

  private var percent: Int = 0
  private lateinit var name: String

  private lateinit var binding: DialogTriggerDeleteBinding
  @field:Inject internal lateinit var presenter: TriggerDeletePresenter

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    name = arguments.getString(TRIGGER_NAME, null)
    percent = arguments.getInt(TRIGGER_PERCENT, -1)

    if (percent < 0) {
      Timber.e("Invalid percent for DeleteTriggerDialog. Dismiss dialog")
      dismiss()
    }

    Injector.with(context) {
      it.inject(this)
    }
  }

  override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
      savedInstanceState: Bundle?): View? {
    binding = DialogTriggerDeleteBinding.inflate(inflater, container, false)
    return binding.root
  }

  override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    binding.triggerDeleteMsg.text = "This operation cannot be undone."
    binding.triggerDeleteTitle.text = "Really delete trigger for $percent% ?"
  }

  override fun onStart() {
    super.onStart()

    presenter.clickEvent(binding.triggerDeleteCancel, { dismiss() })
    presenter.deleteTrigger(binding.triggerDeleteConfirm, percent, { dismiss() })
  }

  override fun onStop() {
    super.onStop()
    presenter.stop()
  }

  override fun onDestroy() {
    super.onDestroy()
    presenter.destroy()
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
