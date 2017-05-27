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
import android.widget.CompoundButton
import android.widget.Toast
import com.pyamsoft.powermanager.Injector
import com.pyamsoft.powermanager.R
import com.pyamsoft.powermanager.manage.ExceptionPresenter.BusCallback
import kotlinx.android.synthetic.main.adapter_item_simple.view.simple_expander
import kotlinx.android.synthetic.main.layout_container_exception.view.exception_charging_airplane
import kotlinx.android.synthetic.main.layout_container_exception.view.exception_charging_bluetooth
import kotlinx.android.synthetic.main.layout_container_exception.view.exception_charging_data
import kotlinx.android.synthetic.main.layout_container_exception.view.exception_charging_doze
import kotlinx.android.synthetic.main.layout_container_exception.view.exception_charging_sync
import kotlinx.android.synthetic.main.layout_container_exception.view.exception_charging_wifi
import kotlinx.android.synthetic.main.layout_container_exception.view.exception_wear_airplane
import kotlinx.android.synthetic.main.layout_container_exception.view.exception_wear_bluetooth
import kotlinx.android.synthetic.main.layout_container_exception.view.exception_wear_data
import kotlinx.android.synthetic.main.layout_container_exception.view.exception_wear_doze
import kotlinx.android.synthetic.main.layout_container_exception.view.exception_wear_sync
import kotlinx.android.synthetic.main.layout_container_exception.view.exception_wear_wifi
import javax.inject.Inject
import javax.inject.Named

class ExceptionItem internal constructor() : BaseItem<ExceptionItem, ExceptionItem.ViewHolder>(
    ExceptionItem.TAG) {
  @field:[Inject Named("exception_wifi")] lateinit internal var presenterWifi: ExceptionPresenter
  @field:[Inject Named("exception_data")] lateinit internal var presenterData: ExceptionPresenter
  @field:[Inject Named(
      "exception_bluetooth")] lateinit internal var presenterBluetooth: ExceptionPresenter
  @field:[Inject Named("exception_sync")] lateinit internal var presenterSync: ExceptionPresenter
  @field:[Inject Named(
      "exception_airplane")] lateinit internal var presenterAirplane: ExceptionPresenter
  @field:[Inject Named("exception_doze")] lateinit internal var presenterDoze: ExceptionPresenter

  init {
    Injector.get().provideComponent().plusManageComponent().inject(this)
  }

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
    bind(holder.chargingContainer.exception_charging_wifi,
        holder.chargingContainer.exception_wear_wifi, "Wifi", presenterWifi)
    bind(holder.chargingContainer.exception_charging_data,
        holder.chargingContainer.exception_wear_data, "Data", presenterData)
    bind(holder.chargingContainer.exception_charging_bluetooth,
        holder.chargingContainer.exception_wear_bluetooth, "Bluetooth", presenterBluetooth)
    bind(holder.chargingContainer.exception_charging_sync,
        holder.chargingContainer.exception_wear_sync, "Sync", presenterSync)
    bind(holder.chargingContainer.exception_charging_airplane,
        holder.chargingContainer.exception_wear_airplane, "Airplane", presenterAirplane)
    bind(holder.chargingContainer.exception_charging_doze,
        holder.chargingContainer.exception_wear_doze, "Doze", presenterDoze)
  }

  override fun unbindView(holder: ViewHolder) {
    super.unbindView(holder)
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
  }

  override fun unbindItem() {
    presenterAirplane.stop()
    presenterAirplane.destroy()
    presenterWifi.stop()
    presenterWifi.destroy()
    presenterData.stop()
    presenterData.destroy()
    presenterBluetooth.stop()
    presenterBluetooth.destroy()
    presenterSync.stop()
    presenterSync.destroy()
    presenterDoze.stop()
    presenterDoze.destroy()
  }

  private fun bind(charging: CheckBox, wear: CheckBox, name: String,
      presenter: ExceptionPresenter) {
    bindChargingCheck(charging, name, presenter)
    bindWearCheck(wear, name, presenter)

    presenter.registerOnBus(object : BusCallback {
      override fun onManageChanged() {
        getIgnoreCharging(presenter, charging, name)
        getIgnoreWear(presenter, wear, name)
      }
    })
  }

  private fun bindChargingCheck(checkBox: CheckBox, name: String, presenter: ExceptionPresenter) {
    // Set enabled in case it failed last time
    checkBox.isEnabled = true

    // Get current state
    getIgnoreCharging(presenter, checkBox, name)
  }

  internal fun getIgnoreCharging(presenter: ExceptionPresenter, checkBox: CheckBox, name: String) {
    presenter.getIgnoreCharging(object : ExceptionPresenter.RetrieveCallback {
      override fun onEnableRetrieved(enabled: Boolean) {
        checkBox.isEnabled = enabled
      }

      override fun onStateRetrieved(enabled: Boolean) {
        // Make sure we don't trigger anything
        checkBox.setOnCheckedChangeListener(null)
        checkBox.isChecked = enabled
      }

      override fun onError(throwable: Throwable) {
        Toast.makeText(checkBox.context, "Failed to retrieve state: " + name,
            Toast.LENGTH_SHORT).show()

        // Mark switch as disabled
        checkBox.isEnabled = false
      }

      override fun onComplete() {
        checkBox.setOnCheckedChangeListener(object : CompoundButton.OnCheckedChangeListener {
          override fun onCheckedChanged(buttonView: CompoundButton, isChecked: Boolean) {
            // Make sure we don't trigger anything
            buttonView.setOnCheckedChangeListener(null)

            // Update backing
            val listener = this
            presenter.setIgnoreCharging(isChecked, object : ExceptionPresenter.ActionCallback {
              override fun onError(throwable: Throwable) {
                Toast.makeText(checkBox.context, "Failed to set state: " + name,
                    Toast.LENGTH_SHORT).show()

                // Roll back
                buttonView.isChecked = !isChecked
              }

              override fun onComplete() {
                // Re-apply listener
                buttonView.setOnCheckedChangeListener(listener)
              }
            })
          }
        })
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
    presenter.getIgnoreWear(object : ExceptionPresenter.RetrieveCallback {
      override fun onEnableRetrieved(enabled: Boolean) {
        checkBox.isEnabled = enabled
      }

      override fun onStateRetrieved(enabled: Boolean) {
        // Make sure we don't trigger anything
        checkBox.setOnCheckedChangeListener(null)
        checkBox.isChecked = enabled
      }

      override fun onError(throwable: Throwable) {
        Toast.makeText(checkBox.context, "Failed to retrieve state: " + name,
            Toast.LENGTH_SHORT).show()

        // Mark switch as disabled
        checkBox.isEnabled = false
      }

      override fun onComplete() {
        checkBox.setOnCheckedChangeListener(object : CompoundButton.OnCheckedChangeListener {
          override fun onCheckedChanged(buttonView: CompoundButton, isChecked: Boolean) {
            // Make sure we don't trigger anything
            buttonView.setOnCheckedChangeListener(null)

            // Update backing
            val listener = this
            presenter.setIgnoreWear(isChecked, object : ExceptionPresenter.ActionCallback {
              override fun onError(throwable: Throwable) {
                Toast.makeText(checkBox.context, "Failed to set state: " + name,
                    Toast.LENGTH_SHORT).show()

                // Roll back
                buttonView.isChecked = !isChecked
              }

              override fun onComplete() {
                // Re-apply listener
                buttonView.setOnCheckedChangeListener(listener)
              }
            })
          }
        })
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
    internal var chargingContainer: View = LayoutInflater.from(itemView.context).inflate(
        R.layout.layout_container_exception, itemView as ViewGroup, false)

    init {
      itemView.simple_expander.setTitle(R.string.exceptions_title)
      itemView.simple_expander.setDescription(R.string.exceptions_desc)
      itemView.simple_expander.setExpandingContent(chargingContainer)
    }
  }

  companion object {
    const internal val TAG = "ExceptionItem"
  }
}
