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

import android.support.annotation.CheckResult
import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mikepenz.fastadapter.items.GenericAbstractItem
import com.pyamsoft.powermanager.R
import com.pyamsoft.pydroid.ui.helper.Toasty
import kotlinx.android.synthetic.main.adapter_item_simple.view.simple_expander
import kotlinx.android.synthetic.main.layout_container_delay.view.delay_input_custom
import kotlinx.android.synthetic.main.layout_container_delay.view.delay_radio_custom
import kotlinx.android.synthetic.main.layout_container_delay.view.delay_radio_group
import timber.log.Timber

abstract class TimeItem<VH : TimeItem.ViewHolder> internal constructor(
    tag: String) : GenericAbstractItem<String, TimeItem<VH>, VH>(tag) {

  internal var customTimeWatcher: TextWatcher? = null
  override fun getLayoutRes(): Int {
    return R.layout.adapter_item_simple
  }

  override fun bindView(holder: VH, payloads: List<Any>?) {
    super.bindView(holder, payloads)
    val context = holder.itemView.context
    providePresenter(holder).getTime(object : TimePresenter.TimeCallback {
      override fun onCustomTime(time: Long) {
        holder.containerDelay.delay_radio_group.clearCheck()
        holder.containerDelay.delay_input_custom.setText(time.toString())
        enableCustomInput(holder)
      }

      override fun onPresetTime(time: Long) {
        disableCustomInput(holder)
        val index: Int
        if (time == getTimeRadioOne()) {
          index = 0
        } else if (time == getTimeRadioTwo()) {
          index = 1
        } else if (time == getTimeRadioThree()) {
          index = 2
        } else if (time == getTimeRadioFour()) {
          index = 3
        } else if (time == getTimeRadioFive()) {
          index = 4
        } else if (time == getTimeRadioSix()) {
          index = 5
        } else if (time == getTimeRadioSeven()) {
          index = 6
        } else if (time == getTimeRadioEight()) {
          index = 7
        } else {
          throw IllegalStateException("No preset delay with time: " + time)
        }

        holder.containerDelay.delay_radio_group.check(
            holder.containerDelay.delay_radio_group.getChildAt(index).id)
        holder.containerDelay.delay_input_custom.setText(time.toString())
      }

      override fun onError(throwable: Throwable) {
        Toasty.makeText(context, "Error getting delay time", Toasty.LENGTH_SHORT).show()
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
          R.id.delay_radio_one -> time = getTimeRadioOne()
          R.id.delay_radio_two -> time = getTimeRadioTwo()
          R.id.delay_radio_three -> time = getTimeRadioThree()
          R.id.delay_radio_four -> time = getTimeRadioFour()
          R.id.delay_radio_five -> time = getTimeRadioFive()
          R.id.delay_radio_six -> time = getTimeRadioSix()
          R.id.delay_radio_seven -> time = getTimeRadioSeven()
          R.id.delay_radio_eight -> time = getTimeRadioEight()
          else -> throw IllegalArgumentException("Could not find RadioButton with id: " + checkedId)
        }

        providePresenter(holder).setPresetTime(time, object : TimePresenter.ActionCallback {
          override fun onError(throwable: Throwable) {
            Toasty.makeText(context, "Failed to set delay time",
                Toasty.LENGTH_SHORT).show()
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

    providePresenter(holder).listenForTimeChanges(object : TimePresenter.OnTimeChangedCallback {
      override fun onTimeChanged(time: Long) {
        // Remove watcher
        holder.containerDelay.delay_input_custom.setText(time.toString())
        holder.containerDelay.delay_input_custom.setSelection(
            holder.containerDelay.delay_input_custom.text.length - 1)
      }

      override fun onError(throwable: Throwable) {
        Toasty.makeText(context, "Error while listening for time changes",
            Toasty.LENGTH_SHORT).show()
        disableCustomInput(holder)
      }
    })
  }

  internal fun disableCustomInput(holder: VH) {
    holder.containerDelay.delay_radio_custom.isChecked = false
    holder.containerDelay.delay_input_custom.isEnabled = false

    if (customTimeWatcher != null) {
      holder.containerDelay.delay_input_custom.removeTextChangedListener(customTimeWatcher)
      customTimeWatcher = null
    }
    providePresenter(holder).stopListeningCustomTimeChanges()
  }

  internal fun enableCustomInput(holder: VH) {
    holder.containerDelay.delay_radio_custom.isChecked = true
    holder.containerDelay.delay_input_custom.isEnabled = true

    customTimeWatcher = object : TextWatcher {
      override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
      }

      override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
      }

      override fun afterTextChanged(s: Editable) {
        providePresenter(holder).submitCustomTimeChange(s.toString(), false)
      }
    }

    if (customTimeWatcher != null) {
      holder.containerDelay.delay_input_custom.addTextChangedListener(customTimeWatcher)
    }
    providePresenter(holder).listenForCustomTimeChanges(object : TimePresenter.CustomTimeChangedCallback {
      override fun onCustomTimeChanged(time: Long) {
        holder.containerDelay.delay_input_custom.setText(time.toString())
      }

      override fun onCustomTimeInputError(error: String?) {
        holder.containerDelay.delay_input_custom.error = if (error == null) null else "Invalid number: " + error
      }

      override fun onError(throwable: Throwable) {
        Toasty.makeText(holder.itemView.context, "Error while listening for custom changes",
            Toasty.LENGTH_SHORT).show()
        disableCustomInput(holder)
      }
    })
  }

  override fun unbindView(holder: VH) {
    super.unbindView(holder)
    if (holder.containerDelay.delay_input_custom.isEnabled) {
      providePresenter(holder).submitCustomTimeChange(
          holder.containerDelay.delay_input_custom.text.toString(), true)
    }
    holder.containerDelay.delay_input_custom.removeTextChangedListener(customTimeWatcher)
    holder.containerDelay.delay_radio_group.setOnCheckedChangeListener(null)
    providePresenter(holder).stop()
    providePresenter(holder).destroy()
  }

  @CheckResult abstract fun providePresenter(holder: VH): TimePresenter
  @CheckResult abstract fun getTimeRadioOne(): Long
  @CheckResult abstract fun getTimeRadioTwo(): Long
  @CheckResult abstract fun getTimeRadioThree(): Long
  @CheckResult abstract fun getTimeRadioFour(): Long
  @CheckResult abstract fun getTimeRadioFive(): Long
  @CheckResult abstract fun getTimeRadioSix(): Long
  @CheckResult abstract fun getTimeRadioSeven(): Long
  @CheckResult abstract fun getTimeRadioEight(): Long

  abstract class ViewHolder protected constructor(itemView: View) : RecyclerView.ViewHolder(
      itemView) {
    internal var containerDelay: View = LayoutInflater.from(itemView.context).inflate(
        R.layout.layout_container_delay, itemView as ViewGroup, false)

    init {
      itemView.simple_expander.setExpandingContent(containerDelay)
    }
  }
}

