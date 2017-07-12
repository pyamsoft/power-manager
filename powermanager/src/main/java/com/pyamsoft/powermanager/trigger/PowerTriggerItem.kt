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
import com.mikepenz.fastadapter.items.GenericAbstractItem
import com.pyamsoft.powermanager.R
import com.pyamsoft.powermanager.databinding.AdapterItemTriggerBinding
import com.pyamsoft.powermanager.trigger.db.PowerTriggerEntry

class PowerTriggerItem internal constructor(
    trigger: PowerTriggerEntry) : GenericAbstractItem<PowerTriggerEntry, PowerTriggerItem, PowerTriggerItem.ViewHolder>(
    trigger) {

  override fun getViewHolder(view: View): ViewHolder {
    return ViewHolder(view)
  }

  override fun getType(): Int {
    return R.id.adapter_trigger_item
  }

  override fun getLayoutRes(): Int {
    return R.layout.adapter_item_trigger
  }

  override fun bindView(holder: ViewHolder?, payloads: MutableList<Any>?) {
    super.bindView(holder, payloads)
    if (holder != null) {
      holder.binding.triggerTitle.text = model.name()
      holder.binding.triggerPercent.text = "Trigger at percent: ${model.percent()}"
    }
  }

  override fun unbindView(holder: ViewHolder?) {
    super.unbindView(holder)
    if (holder != null) {
      holder.binding.triggerTitle.text = null
      holder.binding.triggerPercent.text = null
    }
  }

  class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    internal val binding: AdapterItemTriggerBinding = AdapterItemTriggerBinding.bind(view)
  }
}

