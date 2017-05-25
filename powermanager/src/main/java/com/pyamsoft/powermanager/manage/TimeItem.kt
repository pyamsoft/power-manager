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
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.pyamsoft.powermanager.Injector
import com.pyamsoft.powermanager.R
import kotlinx.android.synthetic.main.adapter_item_simple.view.simple_expander
import kotlinx.android.synthetic.main.layout_container_delay.view.delay_input_custom
import kotlinx.android.synthetic.main.layout_container_delay.view.delay_radio_custom
import kotlinx.android.synthetic.main.layout_container_delay.view.delay_radio_eight
import kotlinx.android.synthetic.main.layout_container_delay.view.delay_radio_five
import kotlinx.android.synthetic.main.layout_container_delay.view.delay_radio_four
import kotlinx.android.synthetic.main.layout_container_delay.view.delay_radio_group
import kotlinx.android.synthetic.main.layout_container_delay.view.delay_radio_one
import kotlinx.android.synthetic.main.layout_container_delay.view.delay_radio_seven
import kotlinx.android.synthetic.main.layout_container_delay.view.delay_radio_six
import kotlinx.android.synthetic.main.layout_container_delay.view.delay_radio_three
import kotlinx.android.synthetic.main.layout_container_delay.view.delay_radio_two
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Named

class TimeItem internal constructor() : BaseItem<TimeItem, TimeItem.ViewHolder>(TimeItem.TAG) {
  @field:[Inject Named("manage_delay")] lateinit internal var presenter: TimePresenter
  internal val customTimeWatcher: TextWatcher = object : TextWatcher {
    override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

    }

    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

    }

    override fun afterTextChanged(s: Editable) {
      presenter.submitCustomTimeChange(s.toString(), false)
    }
  }

  init {
    Injector.get().provideComponent().inject(this)
  }

  override fun getViewHolder(view: View): ViewHolder {
    return ViewHolder(view)
  }

  override fun getType(): Int {
    return R.id.adapter_delay_card_item
  }

  override fun getLayoutRes(): Int {
    return R.layout.adapter_item_simple
  }

  override fun bindView(holder: ViewHolder, payloads: List<Any>?) {
    super.bindView(holder, payloads)

    val context = holder.itemView.context
    presenter.getTime(object : TimePresenter.TimeCallback {
      override fun onCustomTime(time: Long) {
        holder.containerDelay.delay_radio_group.clearCheck()
        holder.containerDelay.delay_input_custom.setText(time.toString())
        enableCustomInput(holder)
      }

      override fun onPresetTime(time: Long) {
        disableCustomInput(holder)

        val index: Int
        if (time == 5L) {
          index = 0
        } else if (time == 10L) {
          index = 1
        } else if (time == 15L) {
          index = 2
        } else if (time == 30L) {
          index = 3
        } else if (time == 45L) {
          index = 4
        } else if (time == 60L) {
          index = 5
        } else if (time == 90L) {
          index = 6
        } else if (time == 120L) {
          index = 7
        } else {
          throw IllegalStateException("No preset delay with time: " + time)
        }

        holder.containerDelay.delay_radio_group.check(
            holder.containerDelay.delay_radio_group.getChildAt(index).id)
        holder.containerDelay.delay_input_custom.setText(time.toString())
      }

      override fun onError(throwable: Throwable) {
        Toast.makeText(context, "Error getting delay time", Toast.LENGTH_SHORT).show()
      }

      override fun onComplete() {

      }
    })

    holder.containerDelay.delay_radio_group.setOnCheckedChangeListener { group, checkedId ->
      if (checkedId == -1) {
        Timber.d("Custom is checked")
        enableCustomInput(holder)
      } else {
        Timber.d("Preset is checked")
        disableCustomInput(holder)

        val time: Long
        when (checkedId) {
          R.id.delay_radio_one -> time = 5
          R.id.delay_radio_two -> time = 10
          R.id.delay_radio_three -> time = 15
          R.id.delay_radio_four -> time = 30
          R.id.delay_radio_five -> time = 45
          R.id.delay_radio_six -> time = 60
          R.id.delay_radio_seven -> time = 90
          R.id.delay_radio_eight -> time = 120
          else -> throw IllegalArgumentException("Could not find RadioButton with id: " + checkedId)
        }

        presenter.setPresetTime(time, object : TimePresenter.ActionCallback {
          override fun onError(throwable: Throwable) {
            Toast.makeText(context, "Failed to set delay time", Toast.LENGTH_SHORT).show()
            group.isEnabled = false
          }
        })
      }
    }

    holder.containerDelay.delay_radio_custom.setOnCheckedChangeListener { _, isChecked ->
      if (isChecked) {
        holder.containerDelay.delay_radio_group.clearCheck()
        enableCustomInput(holder)
      } else {
        disableCustomInput(holder)
      }
    }

    presenter.listenForTimeChanges(object : TimePresenter.OnTimeChangedCallback {
      override fun onTimeChanged(time: Long) {
        // Remove watcher
        holder.containerDelay.delay_input_custom.setText(time.toString())
        holder.containerDelay.delay_input_custom.setSelection(
            holder.containerDelay.delay_input_custom.text.length - 1)
      }

      override fun onError(throwable: Throwable) {
        Toast.makeText(context, "Error while listening for delay changes",
            Toast.LENGTH_SHORT).show()
        disableCustomInput(holder)
      }
    })
  }

  internal fun disableCustomInput(holder: ViewHolder) {
    holder.containerDelay.delay_radio_custom.isChecked = false
    holder.containerDelay.delay_input_custom.isEnabled = false

    holder.containerDelay.delay_input_custom.removeTextChangedListener(customTimeWatcher)
    presenter.stopListeningCustomTimeChanges()
  }

  internal fun enableCustomInput(holder: ViewHolder) {
    holder.containerDelay.delay_radio_custom.isChecked = true
    holder.containerDelay.delay_input_custom.isEnabled = true

    holder.containerDelay.delay_input_custom.addTextChangedListener(customTimeWatcher)
    presenter.listenForCustomTimeChanges(object : TimePresenter.CustomTimeChangedCallback {
      override fun onCustomTimeChanged(time: Long) {
        holder.containerDelay.delay_input_custom.setText(time.toString())
      }

      override fun onCustomTimeInputError(error: String?) {
        holder.containerDelay.delay_input_custom.error = if (error == null) null else "Invalid number: " + error
      }

      override fun onError(throwable: Throwable) {
        Toast.makeText(holder.itemView.context, "Error while listening for custom changes",
            Toast.LENGTH_SHORT).show()
        disableCustomInput(holder)
      }
    })
  }

  override fun unbindView(holder: ViewHolder?) {
    super.unbindView(holder)
    if (holder != null) {
      if (holder.containerDelay.delay_input_custom.isEnabled) {
        presenter.submitCustomTimeChange(holder.containerDelay.delay_input_custom.text.toString(),
            true)
      }
      holder.containerDelay.delay_input_custom.removeTextChangedListener(customTimeWatcher)
    }
  }

  override fun unbindItem() {
    presenter.stop()
    presenter.destroy()
  }

  class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    internal var containerDelay: View = LayoutInflater.from(itemView.context).inflate(
        R.layout.layout_container_delay, itemView as ViewGroup, false)

    init {
      itemView.simple_expander.setTitle("Active Delay")
      itemView.simple_expander.setDescription(
          "Power Manager will wait for the specified amount of time before automatically managing certain device functions")
      itemView.simple_expander.setExpandingContent(containerDelay)
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

    const internal val TAG = "TimeItem"
  }
}

