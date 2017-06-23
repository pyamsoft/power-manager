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

package com.pyamsoft.powermanager.manage

import android.support.v7.widget.RecyclerView
import android.support.v7.widget.SwitchCompat
import android.view.View
import com.mikepenz.fastadapter.items.GenericAbstractItem
import com.pyamsoft.powermanager.Injector
import com.pyamsoft.powermanager.R
import com.pyamsoft.powermanager.databinding.AdapterItemManageBinding
import com.pyamsoft.pydroid.ui.helper.Toasty
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Named

class ManageItem internal constructor() : GenericAbstractItem<String, ManageItem, ManageItem.ViewHolder>(
    ManageItem.TAG) {

  override fun getViewHolder(view: View): ViewHolder {
    return ViewHolder(view)
  }

  override fun getType(): Int {
    return R.id.adapter_manage_card_item
  }

  override fun getLayoutRes(): Int {
    return R.layout.adapter_item_manage
  }

  override fun bindView(holder: ViewHolder, payloads: List<Any>?) {
    super.bindView(holder, payloads)
    Timber.d("Bind manage item")
    bindSwitch(holder.binding.manageWifi, "WiFi", holder.presenterWifi)
    bindSwitch(holder.binding.manageData, "Cellular Data", holder.presenterData)
    bindSwitch(holder.binding.manageBluetooth, "Bluetooth", holder.presenterBluetooth)
    bindSwitch(holder.binding.manageSync, "Auto Sync", holder.presenterSync)
    bindSwitch(holder.binding.manageAirplane, "Airplane Mode", holder.presenterAirplane)
    bindSwitch(holder.binding.manageDoze, "Doze Mode", holder.presenterDoze)
    bindSwitch(holder.binding.manageDataSaver, "Data Saver", holder.presenterDataSaver)
  }

  private fun bindSwitch(switch: SwitchCompat, name: String, presenter: ManagePresenter) {
    // Set enabled in case it failed last time
    switch.isEnabled = true

    // Set title
    switch.text = name

    // Get current state
    presenter.getState(onEnableRetrieved = {
      switch.isEnabled = it
    }, onStateRetrieved = {
      switch.isChecked = it
    }, onError = {
      Toasty.makeText(switch.context, "Failed to retrieve state: " + name,
          Toasty.LENGTH_SHORT).show()

      // Mark switch as disabled
      switch.isEnabled = false
    }, onComplete = {
      presenter.setManaged(switch, {
        Toasty.makeText(switch.context, "Failed to set state: " + name, Toasty.LENGTH_SHORT).show()

        // Mark switch as disabled
        switch.isEnabled = false
      }, {})
    })
  }

  override fun unbindView(holder: ViewHolder) {
    super.unbindView(holder)
    Timber.d("Unbind manage item")
    unbindSwitch(holder.binding.manageWifi)
    unbindSwitch(holder.binding.manageData)
    unbindSwitch(holder.binding.manageBluetooth)
    unbindSwitch(holder.binding.manageSync)
    unbindSwitch(holder.binding.manageAirplane)
    unbindSwitch(holder.binding.manageDoze)
    unbindSwitch(holder.binding.manageDataSaver)

    holder.presenterAirplane.stop()
    holder.presenterAirplane.destroy()
    holder.presenterWifi.stop()
    holder.presenterWifi.destroy()
    holder.presenterData.stop()
    holder.presenterData.destroy()
    holder.presenterBluetooth.stop()
    holder.presenterBluetooth.destroy()
    holder.presenterSync.stop()
    holder.presenterSync.destroy()
    holder.presenterDoze.stop()
    holder.presenterDoze.destroy()
    holder.presenterDataSaver.stop()
    holder.presenterDataSaver.destroy()
  }

  private fun unbindSwitch(switch: SwitchCompat) {
    switch.text = null
  }

  class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    @field:[Inject Named("manage_wifi")] lateinit internal var presenterWifi: ManagePresenter
    @field:[Inject Named("manage_data")] lateinit internal var presenterData: ManagePresenter
    @field:[Inject Named(
        "manage_bluetooth")] lateinit internal var presenterBluetooth: ManagePresenter
    @field:[Inject Named("manage_sync")] lateinit internal var presenterSync: ManagePresenter
    @field:[Inject Named(
        "manage_airplane")] lateinit internal var presenterAirplane: ManagePresenter
    @field:[Inject Named("manage_doze")] lateinit internal var presenterDoze: ManagePresenter
    @field:[Inject Named(
        "manage_data_saver")] lateinit internal var presenterDataSaver: ManagePresenter

    internal val binding = AdapterItemManageBinding.bind(itemView)

    init {
      Injector.with(itemView.context) {
        it.plusManageComponent().inject(this)
      }
    }
  }

  companion object {
    const internal val TAG = "ManageItem"
  }
}
