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
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Named

class PollItem : TimeItem<PollItem.ViewHolder>(TAG) {

  @field:[Inject Named("manage_disable")] lateinit internal var presenter: TimePresenter

  init {
    Injector.get().provideComponent().inject(this)
  }

  override fun getType(): Int {
    return R.id.adapter_poll_card_item
  }

  override fun providePresenter(): TimePresenter {
    return presenter
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

  class ViewHolder internal constructor(itemView: View) : TimeItem.ViewHolder(itemView) {

    init {
      itemView.simple_expander.setTitle("Smart Poll")
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
    }
  }

  companion object {

    const internal val TAG = "PollItem"
  }
}

