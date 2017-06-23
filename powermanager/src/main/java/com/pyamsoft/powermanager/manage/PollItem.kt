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

import android.view.View
import com.pyamsoft.powermanager.Injector
import com.pyamsoft.powermanager.R
import com.pyamsoft.powermanager.databinding.AdapterItemToggleBinding
import com.pyamsoft.pydroid.ui.helper.Toasty
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class PollItem : TimeItem<PollPresenter, PollItem.ViewHolder>(TAG) {

  override fun getType(): Int {
    return R.id.adapter_poll_card_item
  }

  override fun getLayoutRes(): Int {
    return R.layout.adapter_item_toggle
  }

  override fun getTimeRadioOne(): Long {
    return TimeUnit.MINUTES.toSeconds(1L)
  }

  override fun getTimeRadioTwo(): Long {
    return TimeUnit.MINUTES.toSeconds(5L)
  }

  override fun getTimeRadioThree(): Long {
    return TimeUnit.MINUTES.toSeconds(10L)
  }

  override fun getTimeRadioFour(): Long {
    return TimeUnit.MINUTES.toSeconds(15L)
  }

  override fun getTimeRadioFive(): Long {
    return TimeUnit.MINUTES.toSeconds(30L)
  }

  override fun getTimeRadioSix(): Long {
    return TimeUnit.MINUTES.toSeconds(45L)
  }

  override fun getTimeRadioSeven(): Long {
    return TimeUnit.MINUTES.toSeconds(60L)
  }

  override fun getTimeRadioEight(): Long {
    return TimeUnit.MINUTES.toSeconds(90L)
  }

  override fun getViewHolder(view: View): ViewHolder {
    return ViewHolder(view)
  }

  override fun bindView(holder: ViewHolder, payloads: List<Any>?) {
    super.bindView(holder, payloads)
    Timber.d("Bind poll item")

    holder.binding.toggleSwitch.setOnCheckedChangeListener(null)
    holder.presenter.getCurrentPeriodic(onStateRetrieved = {
      Timber.d("Poll state retrieved: %s", it)
      holder.binding.toggleSwitch.isChecked = it
    }, onError = {
      Toasty.makeText(holder.itemView.context, "Failed to retrieve polling state",
          Toasty.LENGTH_SHORT).show()

      // Mark switch as disabled
      holder.binding.toggleSwitch.isEnabled = false
    }, onCompleted = {
      holder.presenter.toggleAll(holder.binding.toggleSwitch, onError = {
        Toasty.makeText(holder.itemView.context, "Failed to set polling state",
            Toasty.LENGTH_SHORT).show()

        // Mark switch as disabled
        holder.binding.toggleSwitch.isEnabled = false
      }, onCompleted = {})
    })
  }

  override fun unbindView(holder: ViewHolder) {
    super.unbindView(holder)
    Timber.d("Unbind poll item")
  }

  class ViewHolder internal constructor(itemView: View) : TimeItem.ViewHolder<PollPresenter>(
      itemView) {

    @field:Inject lateinit override var presenter: PollPresenter
    internal val binding = AdapterItemToggleBinding.bind(itemView)

    init {
      binding.toggleSwitch.text = "Smart Polling"
      binding.simpleExpander.setTitle("Polling Delay")
      binding.simpleExpander.setTitleTextSize(16)
      binding.simpleExpander.setDescription(
          "Peter will create some good description here eventually")
      binding.simpleExpander.setExpandingContent(containerBinding.root)

      containerBinding.delayRadioOne.text = "1 Minute"
      containerBinding.delayRadioTwo.text = "5 Minutes"
      containerBinding.delayRadioThree.text = "10 Minutes"
      containerBinding.delayRadioFour.text = "15 Minutes"
      containerBinding.delayRadioFive.text = "30 Minutes"

      // Currently unused
      containerBinding.delayRadioSix.visibility = View.GONE
      containerBinding.delayRadioSeven.visibility = View.GONE
      containerBinding.delayRadioEight.visibility = View.GONE
      //      containerDelay.delay_radio_six.text = "45 Minutes"
      //      containerDelay.delay_radio_seven.text = "1 Hour"
      //      containerDelay.delay_radio_eight.text = "1 Hour 30 Minutes"

      Injector.with(itemView.context) {
        it.plusManageComponent().inject(this)
      }
    }
  }

  companion object {
    const internal val TAG = "PollItem"
  }
}

