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
import com.pyamsoft.powermanager.databinding.AdapterItemSimpleBinding
import com.pyamsoft.powermanager.databinding.LayoutContainerExceptionBinding
import com.pyamsoft.pydroid.ui.helper.Toasty
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
    bind(holder.chargingBinding.exceptionChargingWifi, holder.chargingBinding.exceptionWearWifi,
        "Wifi", holder.presenterWifi)
    bind(holder.chargingBinding.exceptionChargingData, holder.chargingBinding.exceptionWearData,
        "Data", holder.presenterData)
    bind(holder.chargingBinding.exceptionChargingBluetooth,
        holder.chargingBinding.exceptionWearBluetooth, "Bluetooth", holder.presenterBluetooth)
    bind(holder.chargingBinding.exceptionChargingSync, holder.chargingBinding.exceptionWearSync,
        "Sync", holder.presenterSync)
    bind(holder.chargingBinding.exceptionChargingAirplane,
        holder.chargingBinding.exceptionWearAirplane, "Airplane Mode", holder.presenterAirplane)
    bind(holder.chargingBinding.exceptionChargingDoze, holder.chargingBinding.exceptionWearDoze,
        "Doze Mode", holder.presenterDoze)
    bind(holder.chargingBinding.exceptionChargingDataSaver,
        holder.chargingBinding.exceptionWearDataSaver, "Data Saver", holder.presenterDataSaver)
  }

  override fun unbindView(holder: ViewHolder) {
    super.unbindView(holder)
    Timber.d("Unbind exception item")
    unbind(holder.chargingBinding.exceptionChargingAirplane,
        holder.chargingBinding.exceptionWearAirplane)
    unbind(holder.chargingBinding.exceptionChargingWifi, holder.chargingBinding.exceptionWearWifi)
    unbind(holder.chargingBinding.exceptionChargingData, holder.chargingBinding.exceptionWearData)
    unbind(holder.chargingBinding.exceptionChargingBluetooth,
        holder.chargingBinding.exceptionWearBluetooth)
    unbind(holder.chargingBinding.exceptionChargingSync, holder.chargingBinding.exceptionWearSync)
    unbind(holder.chargingBinding.exceptionChargingDoze, holder.chargingBinding.exceptionWearDoze)
    unbind(holder.chargingBinding.exceptionChargingDataSaver,
        holder.chargingBinding.exceptionWearDataSaver)

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
      presenter.setIgnoreCharging(checkBox, {
        Toasty.makeText(checkBox.context, "Failed to set state: " + name,
            Toasty.LENGTH_SHORT).show()

        // Mark switch as disabled
        checkBox.isEnabled = false
      }, {})
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
      presenter.setIgnoreWear(checkBox, {
        Toasty.makeText(checkBox.context, "Failed to set state: " + name,
            Toasty.LENGTH_SHORT).show()

        // Mark switch as disabled
        checkBox.isEnabled = false
      }, {})
    })
  }

  private fun unbind(chargeCheckbox: CheckBox, wearCheckBox: CheckBox) {
    chargeCheckbox.text = null
    wearCheckBox.text = null
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

    internal val binding = AdapterItemSimpleBinding.bind(itemView)
    internal val chargingBinding: LayoutContainerExceptionBinding

    init {
      val chargingContainer = LayoutInflater.from(itemView.context).inflate(
          R.layout.layout_container_exception, itemView as ViewGroup, false)
      chargingBinding = LayoutContainerExceptionBinding.bind(chargingContainer)

      binding.simpleExpander.setTitle(R.string.exceptions_title)
      binding.simpleExpander.setDescription(R.string.exceptions_desc)
      binding.simpleExpander.setExpandingContent(chargingBinding.root)

      Injector.with(itemView.context) {
        it.plusManageComponent().injectException(this)
      }
    }
  }

  companion object {
    const internal val TAG = "ExceptionItem"
  }
}
