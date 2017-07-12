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

  fun bindSwitch(button: SwitchCompat, name: String, presenter: ManagePresenter) {
    // Set enabled in case it failed last time
    button.isEnabled = true

    // Set title
    button.text = name

    // Get current state
    presenter.getState(onEnableRetrieved = {
      button.isEnabled = it
    }, onStateRetrieved = {
      button.isChecked = it
    }, onError = {
      Toasty.makeText(button.context, "Failed to retrieve state: " + name,
          Toasty.LENGTH_SHORT).show()

      // Mark button as disabled
      button.isEnabled = false
    }, onComplete = {
      presenter.setManaged(button, {
        Toasty.makeText(button.context, "Failed to set state: " + name, Toasty.LENGTH_SHORT).show()

        // Mark button as disabled
        button.isEnabled = false
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

  fun unbindSwitch(button: SwitchCompat) {
    button.text = null
  }

  class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    @field:[Inject Named("manage_wifi")] lateinit var presenterWifi: ManagePresenter
    @field:[Inject Named("manage_data")] lateinit var presenterData: ManagePresenter
    @field:[Inject Named("manage_bluetooth")] lateinit var presenterBluetooth: ManagePresenter
    @field:[Inject Named("manage_sync")] lateinit var presenterSync: ManagePresenter
    @field:[Inject Named("manage_airplane")] lateinit var presenterAirplane: ManagePresenter
    @field:[Inject Named("manage_doze")] lateinit var presenterDoze: ManagePresenter
    @field:[Inject Named("manage_data_saver")] lateinit var presenterDataSaver: ManagePresenter

    internal val binding: AdapterItemManageBinding = AdapterItemManageBinding.bind(itemView)

    init {
      Injector.with(itemView.context) {
        it.plusManageComponent().injectManage(this)
      }
    }
  }

  companion object {
    const val TAG = "ManageItem"
  }
}
