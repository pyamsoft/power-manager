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
import kotlinx.android.synthetic.main.adapter_item_simple.view.simple_expander
import kotlinx.android.synthetic.main.layout_container_delay.view.delay_radio_eight
import kotlinx.android.synthetic.main.layout_container_delay.view.delay_radio_five
import kotlinx.android.synthetic.main.layout_container_delay.view.delay_radio_four
import kotlinx.android.synthetic.main.layout_container_delay.view.delay_radio_one
import kotlinx.android.synthetic.main.layout_container_delay.view.delay_radio_seven
import kotlinx.android.synthetic.main.layout_container_delay.view.delay_radio_six
import kotlinx.android.synthetic.main.layout_container_delay.view.delay_radio_three
import kotlinx.android.synthetic.main.layout_container_delay.view.delay_radio_two
import timber.log.Timber
import javax.inject.Inject

class DelayItem : TimeItem<TimePresenter, DelayItem.ViewHolder>(TAG) {

  override fun getType(): Int {
    return R.id.adapter_delay_card_item
  }

  override fun getTimeRadioOne(): Long {
    return 5L
  }

  override fun getTimeRadioTwo(): Long {
    return 10L
  }

  override fun getTimeRadioThree(): Long {
    return 15L
  }

  override fun getTimeRadioFour(): Long {
    return 30L
  }

  override fun getTimeRadioFive(): Long {
    return 45L
  }

  override fun getTimeRadioSix(): Long {
    return 60L
  }

  override fun getTimeRadioSeven(): Long {
    return 90L
  }

  override fun getTimeRadioEight(): Long {
    return 120L
  }

  override fun getViewHolder(view: View): ViewHolder {
    return ViewHolder(view)
  }

  override fun bindView(holder: ViewHolder, payloads: List<Any>?) {
    super.bindView(holder, payloads)
    Timber.d("Bind Delay item")
  }

  override fun unbindView(holder: ViewHolder) {
    super.unbindView(holder)
    Timber.d("Unbind Delay item")
  }

  class ViewHolder internal constructor(itemView: View) : TimeItem.ViewHolder<TimePresenter>(
      itemView) {

    @field:Inject override lateinit var presenter: TimePresenter

    init {
      Injector.with(itemView.context) {
        it.plusManageComponent().inject(this)
      }
      itemView.simple_expander.setTitle("Active Delay")
      itemView.simple_expander.setDescription(
          "Power Manager will wait for the specified amount of time before automatically managing certain device functions")
      containerDelay.delay_radio_one.text = "5 Seconds"
      containerDelay.delay_radio_two.text = "10 Seconds"
      containerDelay.delay_radio_three.text = "15 Seconds"
      containerDelay.delay_radio_four.text = "30 Seconds"
      containerDelay.delay_radio_five.text = "45 Seconds"
      containerDelay.delay_radio_six.text = "1 Minute"
      containerDelay.delay_radio_seven.text = "1 Minute 30 Seconds"
      containerDelay.delay_radio_eight.text = "2 Minutes"
    }
  }

  companion object {
    const internal val TAG = "DelayItem"
  }
}

