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
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import com.mikepenz.fastadapter.items.GenericAbstractItem
import com.pyamsoft.powermanager.Injector
import com.pyamsoft.powermanager.R
import com.pyamsoft.pydroid.ui.helper.Toasty
import kotlinx.android.synthetic.main.adapter_item_simple.view.simple_expander
import kotlinx.android.synthetic.main.layout_container_exception.view.exception_charging_airplane
import kotlinx.android.synthetic.main.layout_container_exception.view.exception_charging_bluetooth
import kotlinx.android.synthetic.main.layout_container_exception.view.exception_charging_data
import kotlinx.android.synthetic.main.layout_container_exception.view.exception_charging_data_saver
import kotlinx.android.synthetic.main.layout_container_exception.view.exception_charging_doze
import kotlinx.android.synthetic.main.layout_container_exception.view.exception_charging_sync
import kotlinx.android.synthetic.main.layout_container_exception.view.exception_charging_wifi
import kotlinx.android.synthetic.main.layout_container_exception.view.exception_wear_airplane
import kotlinx.android.synthetic.main.layout_container_exception.view.exception_wear_bluetooth
import kotlinx.android.synthetic.main.layout_container_exception.view.exception_wear_data
import kotlinx.android.synthetic.main.layout_container_exception.view.exception_wear_data_saver
import kotlinx.android.synthetic.main.layout_container_exception.view.exception_wear_doze
import kotlinx.android.synthetic.main.layout_container_exception.view.exception_wear_sync
import kotlinx.android.synthetic.main.layout_container_exception.view.exception_wear_wifi
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Named

class ExceptionItem internal constructor() : GenericAbstractItem<String, ExceptionItem, ExceptionItem.ViewHolder>(
    ExceptionItem.TAG) {

  override fun getViewHolder(view: View): ViewHolder {
    return ViewHolder(view)
  }

  override fun getType(): Int {
    return R.id.adapter_exception_card_item
  }

  override fun getLayoutRes(): Int {
    return R.layout.adapter_item_simple
  }

  override fun bindView(holder: ViewHolder, payloads: List<Any>?) {
    super.bindView(holder, payloads)
    Timber.d("Bind exception item")
    bind(holder.chargingContainer.exception_charging_wifi,
        holder.chargingContainer.exception_wear_wifi, "Wifi", holder.presenterWifi)
    bind(holder.chargingContainer.exception_charging_data,
        holder.chargingContainer.exception_wear_data, "Data", holder.presenterData)
    bind(holder.chargingContainer.exception_charging_bluetooth,
        holder.chargingContainer.exception_wear_bluetooth, "Bluetooth", holder.presenterBluetooth)
    bind(holder.chargingContainer.exception_charging_sync,
        holder.chargingContainer.exception_wear_sync, "Sync", holder.presenterSync)
    bind(holder.chargingContainer.exception_charging_airplane,
        holder.chargingContainer.exception_wear_airplane, "Airplane Mode", holder.presenterAirplane)
    bind(holder.chargingContainer.exception_charging_doze,
        holder.chargingContainer.exception_wear_doze, "Doze Mode", holder.presenterDoze)
    bind(holder.chargingContainer.exception_charging_data_saver,
        holder.chargingContainer.exception_wear_data_saver, "Data Saver", holder.presenterDataSaver)
  }

  override fun unbindView(holder: ViewHolder) {
    super.unbindView(holder)
    Timber.d("Unbind exception item")
    unbind(holder.chargingContainer.exception_charging_airplane,
        holder.chargingContainer.exception_wear_airplane)
    unbind(holder.chargingContainer.exception_charging_wifi,
        holder.chargingContainer.exception_wear_wifi)
    unbind(holder.chargingContainer.exception_charging_data,
        holder.chargingContainer.exception_wear_data)
    unbind(holder.chargingContainer.exception_charging_bluetooth,
        holder.chargingContainer.exception_wear_bluetooth)
    unbind(holder.chargingContainer.exception_charging_sync,
        holder.chargingContainer.exception_wear_sync)
    unbind(holder.chargingContainer.exception_charging_doze,
        holder.chargingContainer.exception_wear_doze)
    unbind(holder.chargingContainer.exception_charging_data_saver,
        holder.chargingContainer.exception_wear_data_saver)

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

  private fun bind(charging: CheckBox, wear: CheckBox, name: String,
      presenter: ExceptionPresenter) {
    bindChargingCheck(charging, name, presenter)
    bindWearCheck(wear, name, presenter)

    presenter.registerOnBus {
      getIgnoreCharging(presenter, charging, name)
      getIgnoreWear(presenter, wear, name)
    }
  }

  private fun bindChargingCheck(checkBox: CheckBox, name: String, presenter: ExceptionPresenter) {
    // Set enabled in case it failed last time
    checkBox.isEnabled = true

    // Get current state
    getIgnoreCharging(presenter, checkBox, name)
  }

  internal fun getIgnoreCharging(presenter: ExceptionPresenter, checkBox: CheckBox, name: String) {
    presenter.getIgnoreCharging(onEnableRetrieved = {
      checkBox.isEnabled = it
    }, onStateRetrieved = {
      checkBox.isChecked = it
    }, onError = {
      Toasty.makeText(checkBox.context, "Failed to retrieve state: " + name,
          Toasty.LENGTH_SHORT).show()

      // Mark switch as disabled
      checkBox.isEnabled = false
    }, onComplete = {
      checkBox.setOnCheckedChangeListener { buttonView, isChecked ->
        presenter.setIgnoreCharging(isChecked, {
          Toasty.makeText(checkBox.context, "Failed to set state: " + name,
              Toasty.LENGTH_SHORT).show()

          // Roll back
          buttonView.isChecked = !isChecked
        }, {})
      }
    })
  }

  private fun bindWearCheck(checkBox: CheckBox, name: String, presenter: ExceptionPresenter) {
    // Set enabled in case it failed last time
    checkBox.isEnabled = true

    // Get current state
    getIgnoreWear(presenter, checkBox, name)
  }

  internal fun getIgnoreWear(presenter: ExceptionPresenter, checkBox: CheckBox, name: String) {
    presenter.getIgnoreWear(onEnableRetrieved = {
      checkBox.isEnabled = it
    }, onStateRetrieved = {
      checkBox.isChecked = it
    }, onError = {
      Toasty.makeText(checkBox.context, "Failed to retrieve state: " + name,
          Toasty.LENGTH_SHORT).show()

      // Mark switch as disabled
      checkBox.isEnabled = false
    }, onComplete = {
      checkBox.setOnCheckedChangeListener { buttonView, isChecked ->
        // Update backing
        presenter.setIgnoreWear(isChecked, {
          Toasty.makeText(checkBox.context, "Failed to set state: " + name,
              Toasty.LENGTH_SHORT).show()

          // Roll back
          buttonView.isChecked = !isChecked
        }, {})
      }
    })
  }

  private fun unbind(chargeCheckbox: CheckBox, wearCheckBox: CheckBox) {
    chargeCheckbox.text = null
    chargeCheckbox.setOnCheckedChangeListener(null)
    wearCheckBox.text = null
    wearCheckBox.setOnCheckedChangeListener(null)
  }

  class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    @field:[Inject Named("exception_wifi")] lateinit internal var presenterWifi: ExceptionPresenter
    @field:[Inject Named("exception_data")] lateinit internal var presenterData: ExceptionPresenter
    @field:[Inject Named(
        "exception_bluetooth")] lateinit internal var presenterBluetooth: ExceptionPresenter
    @field:[Inject Named("exception_sync")] lateinit internal var presenterSync: ExceptionPresenter
    @field:[Inject Named(
        "exception_airplane")] lateinit internal var presenterAirplane: ExceptionPresenter
    @field:[Inject Named("exception_doze")] lateinit internal var presenterDoze: ExceptionPresenter
    @field:[Inject Named(
        "exception_data_saver")] lateinit internal var presenterDataSaver: ExceptionPresenter

    internal var chargingContainer: View = LayoutInflater.from(itemView.context).inflate(
        R.layout.layout_container_exception, itemView as ViewGroup, false)

    init {
      itemView.simple_expander.setTitle(R.string.exceptions_title)
      itemView.simple_expander.setDescription(R.string.exceptions_desc)
      itemView.simple_expander.setExpandingContent(chargingContainer)

      Injector.with(itemView.context) {
        it.plusManageComponent().inject(this)
      }
    }
  }

  companion object {
    const internal val TAG = "ExceptionItem"
  }
}
