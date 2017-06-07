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

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.CompoundButton
import com.mikepenz.fastadapter.items.GenericAbstractItem
import com.pyamsoft.powermanager.Injector
import com.pyamsoft.powermanager.R
import com.pyamsoft.powermanager.trigger.TriggerItemPresenter.TriggerToggleCallback
import com.pyamsoft.powermanager.trigger.db.PowerTriggerEntry
import kotlinx.android.synthetic.main.adapter_item_trigger.view.trigger_enabled_switch
import kotlinx.android.synthetic.main.adapter_item_trigger.view.trigger_name
import kotlinx.android.synthetic.main.adapter_item_trigger.view.trigger_percent
import java.util.Locale
import javax.inject.Inject

class PowerTriggerListItem internal constructor(
    trigger: PowerTriggerEntry) : GenericAbstractItem<PowerTriggerEntry, PowerTriggerListItem, PowerTriggerListItem.ViewHolder>(
    trigger) {

  override fun getType(): Int {
    return R.id.adapter_trigger_item
  }

  override fun getLayoutRes(): Int {
    return R.layout.adapter_item_trigger
  }

  override fun bindView(holder: ViewHolder, payloads: List<Any>?) {
    super.bindView(holder, payloads)
    bindModelToHolder(holder)

    holder.itemView.trigger_enabled_switch.setOnCheckedChangeListener(
        object : CompoundButton.OnCheckedChangeListener {
          override fun onCheckedChanged(buttonView: CompoundButton, isChecked: Boolean) {
            buttonView.setOnCheckedChangeListener(null)
            buttonView.isChecked = !isChecked
            val listener = this
            holder.presenter.toggleEnabledState(model, isChecked, object : TriggerToggleCallback {
              override fun updateViewHolder(entry: PowerTriggerEntry) {
                withModel(entry)
                bindModelToHolder(holder)
                buttonView.setOnCheckedChangeListener(listener)
              }
            })
          }
        })
  }

  internal fun bindModelToHolder(holder: ViewHolder) {
    holder.itemView.trigger_name.text = model.name()
    holder.itemView.trigger_percent.text = String.format(Locale.getDefault(), "Percent: %s",
        model.percent())
    holder.itemView.trigger_enabled_switch.setOnCheckedChangeListener(null)
    holder.itemView.trigger_enabled_switch.isChecked = model.enabled()
  }

  override fun unbindView(holder: ViewHolder) {
    super.unbindView(holder)
    holder.itemView.trigger_name.text = null
    holder.itemView.trigger_percent.text = null
    holder.itemView.trigger_enabled_switch.setOnCheckedChangeListener(null)
    holder.presenter.stop()
    holder.presenter.destroy()
  }

  override fun getViewHolder(view: View): ViewHolder {
    return ViewHolder(view)
  }

  class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    @field:Inject lateinit internal var presenter: TriggerItemPresenter

    init {
      Injector.with(itemView.context) {
        it.inject(this)
      }
    }
  }
}
