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
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.pyamsoft.powermanager.Injector
import com.pyamsoft.powermanager.databinding.DialogTriggerCreateBinding
import com.pyamsoft.powermanager.trigger.db.PowerTriggerEntry
import com.pyamsoft.powermanager.uicore.WatchedBottomSheet
import timber.log.Timber
import javax.inject.Inject

class CreateTriggerDialog : WatchedBottomSheet() {

  @field:Inject internal lateinit var publisher: TriggerPublisher
  @field:Inject internal lateinit var presenter: TriggerCreatePresenter
  private lateinit var binding: DialogTriggerCreateBinding

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    Injector.with(context) {
      it.inject(this)
    }
  }

  override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
      savedInstanceState: Bundle?): View? {
    binding = DialogTriggerCreateBinding.inflate(inflater, container, false)
    return binding.root
  }

  override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    // Views will autosave state for us, we just need to set them to none by default
    if (savedInstanceState == null) {
      binding.triggerCreateWifiNone.isChecked = true
      binding.triggerCreateDataNone.isChecked = true
      binding.triggerCreateBluetoothNone.isChecked = true
      binding.triggerCreateSyncNone.isChecked = true
    }
  }

  override fun onStart() {
    super.onStart()
    presenter.clickEvent(binding.triggerCreateCancel, {
      Timber.d("Cancel clicked")
      dismiss()
    })

    presenter.clickEvent(binding.triggerCreateConfirm, {
      Timber.d("Publish trigger info to publisher")
      val name: String = binding.triggerCreateName.text.toString()
      val percent: Int = binding.triggerCreatePercent.text.toString().toInt()

      val stateWifi: Int
      if (binding.triggerCreateWifiOn.isChecked) {
        stateWifi = PowerTriggerEntry.STATE_ENABLE
      } else if (binding.triggerCreateWifiOff.isChecked) {
        stateWifi = PowerTriggerEntry.STATE_DISABLE
      } else {
        stateWifi = PowerTriggerEntry.STATE_NONE
      }

      val stateData: Int
      if (binding.triggerCreateDataOn.isChecked) {
        stateData = PowerTriggerEntry.STATE_ENABLE
      } else if (binding.triggerCreateDataOff.isChecked) {
        stateData = PowerTriggerEntry.STATE_DISABLE
      } else {
        stateData = PowerTriggerEntry.STATE_NONE
      }

      val stateBluetooth: Int
      if (binding.triggerCreateBluetoothOn.isChecked) {
        stateBluetooth = PowerTriggerEntry.STATE_ENABLE
      } else if (binding.triggerCreateBluetoothOff.isChecked) {
        stateBluetooth = PowerTriggerEntry.STATE_DISABLE
      } else {
        stateBluetooth = PowerTriggerEntry.STATE_NONE
      }

      val stateSync: Int
      if (binding.triggerCreateSyncOn.isChecked) {
        stateSync = PowerTriggerEntry.STATE_ENABLE
      } else if (binding.triggerCreateSyncOff.isChecked) {
        stateSync = PowerTriggerEntry.STATE_DISABLE
      } else {
        stateSync = PowerTriggerEntry.STATE_NONE
      }

      publisher.publish(name, percent, stateWifi, stateData, stateBluetooth, stateSync)
      dismiss()
    })
  }

  override fun onStop() {
    super.onStop()
    presenter.stop()
  }

  override fun onDestroy() {
    super.onDestroy()
    presenter.destroy()
  }

}

