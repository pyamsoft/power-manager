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
import android.view.View
import android.widget.Switch
import com.mikepenz.fastadapter.items.GenericAbstractItem
import com.pyamsoft.powermanager.Injector
import com.pyamsoft.powermanager.R
import com.pyamsoft.pydroid.ui.helper.Toasty
import kotlinx.android.synthetic.main.adapter_item_manage.view.manage_airplane
import kotlinx.android.synthetic.main.adapter_item_manage.view.manage_bluetooth
import kotlinx.android.synthetic.main.adapter_item_manage.view.manage_data
import kotlinx.android.synthetic.main.adapter_item_manage.view.manage_data_saver
import kotlinx.android.synthetic.main.adapter_item_manage.view.manage_doze
import kotlinx.android.synthetic.main.adapter_item_manage.view.manage_sync
import kotlinx.android.synthetic.main.adapter_item_manage.view.manage_wifi
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
    bindSwitch(holder.itemView.manage_wifi, "WiFi", holder.presenterWifi)
    bindSwitch(holder.itemView.manage_data, "Cellular Data", holder.presenterData)
    bindSwitch(holder.itemView.manage_bluetooth, "Bluetooth", holder.presenterBluetooth)
    bindSwitch(holder.itemView.manage_sync, "Auto Sync", holder.presenterSync)
    bindSwitch(holder.itemView.manage_airplane, "Airplane Mode", holder.presenterAirplane)
    bindSwitch(holder.itemView.manage_doze, "Doze Mode", holder.presenterDoze)
    bindSwitch(holder.itemView.manage_data_saver, "Data Saver", holder.presenterDataSaver)
  }

  private fun bindSwitch(switch: Switch, name: String, presenter: ManagePresenter) {
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
      switch.setOnCheckedChangeListener { buttonView, isChecked ->
        presenter.setManaged(isChecked, {
          Toasty.makeText(switch.context, "Failed to set state: " + name,
              Toasty.LENGTH_SHORT).show()

          // Roll back
          buttonView.isChecked = !isChecked
        }, {})
      }
    })
  }

  override fun unbindView(holder: ViewHolder) {
    super.unbindView(holder)
    Timber.d("Unbind manage item")
    unbindSwitch(holder.itemView.manage_wifi)
    unbindSwitch(holder.itemView.manage_data)
    unbindSwitch(holder.itemView.manage_bluetooth)
    unbindSwitch(holder.itemView.manage_sync)
    unbindSwitch(holder.itemView.manage_airplane)
    unbindSwitch(holder.itemView.manage_doze)
    unbindSwitch(holder.itemView.manage_data_saver)

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

  private fun unbindSwitch(switch: Switch) {
    switch.text = null
    switch.setOnCheckedChangeListener(null)
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
