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
import com.pyamsoft.pydroid.ui.helper.Toasty
import kotlinx.android.synthetic.main.adapter_item_simple.view.simple_expander
import kotlinx.android.synthetic.main.adapter_item_toggle.view.toggle_switch
import kotlinx.android.synthetic.main.layout_container_delay.view.delay_radio_eight
import kotlinx.android.synthetic.main.layout_container_delay.view.delay_radio_five
import kotlinx.android.synthetic.main.layout_container_delay.view.delay_radio_four
import kotlinx.android.synthetic.main.layout_container_delay.view.delay_radio_one
import kotlinx.android.synthetic.main.layout_container_delay.view.delay_radio_seven
import kotlinx.android.synthetic.main.layout_container_delay.view.delay_radio_six
import kotlinx.android.synthetic.main.layout_container_delay.view.delay_radio_three
import kotlinx.android.synthetic.main.layout_container_delay.view.delay_radio_two
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

    holder.itemView.toggle_switch.setOnCheckedChangeListener(null)
    holder.presenter.getCurrentPeriodic({
      Timber.d("Poll state retrieved: %s", it)
      holder.itemView.toggle_switch.isChecked = it
    }, {
      Toasty.makeText(holder.itemView.context, "Failed to retrieve polling state",
          Toasty.LENGTH_SHORT).show()

      // Mark switch as disabled
      holder.itemView.toggle_switch.isEnabled = false
    }, {
      holder.itemView.toggle_switch.setOnCheckedChangeListener { buttonView, isChecked ->
        if (buttonView != null) {
          Timber.d("Set polling enabled: %s", isChecked)
          holder.presenter.toggleAll(isChecked, {
            Toasty.makeText(buttonView.context, "Failed to set polling state",
                Toasty.LENGTH_SHORT).show()

            // Mark switch as disabled
            buttonView.isEnabled = false
          }, {})
        }
      }
    })
  }

  override fun unbindView(holder: ViewHolder) {
    super.unbindView(holder)
    Timber.d("Unbind poll item")
    holder.itemView.toggle_switch.setOnCheckedChangeListener(null)
  }

  class ViewHolder internal constructor(itemView: View) : TimeItem.ViewHolder<PollPresenter>(
      itemView) {

    @field:Inject lateinit override var presenter: PollPresenter

    init {
      itemView.toggle_switch.text = "Smart Polling"
      itemView.simple_expander.setTitle("Polling Delay")
      itemView.simple_expander.setTitleTextSize(16)
      itemView.simple_expander.setDescription(
          "Peter will create some good description here eventually")
      containerDelay.delay_radio_one.text = "1 Minute"
      containerDelay.delay_radio_two.text = "5 Minutes"
      containerDelay.delay_radio_three.text = "10 Minutes"
      containerDelay.delay_radio_four.text = "15 Minutes"
      containerDelay.delay_radio_five.text = "30 Minutes"

      // Currently unused
      containerDelay.delay_radio_six.visibility = View.GONE
      containerDelay.delay_radio_seven.visibility = View.GONE
      containerDelay.delay_radio_eight.visibility = View.GONE
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

