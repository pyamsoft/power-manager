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

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter
import com.pyamsoft.powermanager.Injector
import com.pyamsoft.powermanager.R
import com.pyamsoft.powermanager.databinding.FragmentPowertriggerBinding
import com.pyamsoft.powermanager.uicore.WatchedFragment
import com.pyamsoft.pydroid.loader.ImageLoader
import com.pyamsoft.pydroid.ui.helper.Toasty
import com.pyamsoft.pydroid.ui.util.DialogUtil
import timber.log.Timber
import javax.inject.Inject

class PowerTriggerFragment : WatchedFragment() {

  private lateinit var binding: FragmentPowertriggerBinding
  @field:Inject internal lateinit var presenter: TriggerPresenter
  private lateinit var adapter: FastItemAdapter<PowerTriggerItem>

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    Injector.with(context) {
      it.inject(this)
    }
  }

  override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
      savedInstanceState: Bundle?): View? {
    binding = FragmentPowertriggerBinding.inflate(inflater, container, false)
    return binding.root
  }

  override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    addPreferenceFragment()
    setupFAB()
    setupList()
  }

  private fun setupList() {
    adapter = FastItemAdapter()
    val manager = LinearLayoutManager(context)
    manager.initialPrefetchItemCount = 3
    manager.isItemPrefetchEnabled = true
    binding.powerTriggerList.layoutManager = manager
    binding.powerTriggerList.adapter = adapter
  }

  private fun setupFAB() {
    ImageLoader.fromResource(context, R.drawable.ic_add_24dp).into(binding.powerTriggerFab)
  }

  private fun addPreferenceFragment() {
    val fragmentManager = childFragmentManager
    if (fragmentManager.findFragmentByTag(PowerTriggerPreferenceFragment.TAG) == null) {
      fragmentManager.beginTransaction().replace(R.id.power_trigger_preference_container,
          PowerTriggerPreferenceFragment(), PowerTriggerPreferenceFragment.TAG).commit()
    }
  }

  override fun onStart() {
    super.onStart()
    presenter.registerOnBus(onAdd = {
      if (adapter.adapterItems.isEmpty()) {
        // We are it, just add
        adapter.add(PowerTriggerItem(it))
      } else {
        // Go down the line until we are the big boy
        val items = adapter.adapterItems
        var i = 0
        while (i < items.size && items[i].model.percent() > it.percent()) {
          ++i
        }

        adapter.add(i, PowerTriggerItem(it))
      }
    }, onAddError = {
      Timber.e(it, "Error adding new power trigger")
      val msg = it.message
      if (msg != null) {
        Toasty.makeText(context, msg, Toasty.LENGTH_SHORT).show()
      }
    }, onTriggerDeleted = {
      val items = adapter.adapterItems
      var index = -1
      for (i in items.indices) {
        val item = items[i]
        if (item.model.percent() == it) {
          index = i
          break
        }
      }

      if (index >= 0) {
        adapter.remove(index)
      }
    }, onTriggerDeleteError = {
      Timber.e(it, "Error deleting power trigger")
      val msg = it.message
      if (msg != null) {
        Toasty.makeText(context, msg, Toasty.LENGTH_SHORT).show()
      }
    })

    presenter.loadTriggerView(false, onTriggerLoaded = { trigger ->
      if (adapter.adapterItems.filter {
        it.model.percent() == trigger.percent()
      }.isEmpty()) {
        adapter.add(PowerTriggerItem(trigger))
      }
    }, onTriggerLoadError = { Timber.e(it, "Failed to load trigger into list") },
        onTriggerLoadFinished = {
          if (adapter.itemCount == 0) {
            loadTriggerListEmpty()
          } else {
            loadTriggerList()
          }
        })

    presenter.clickEvent(binding.powerTriggerFab, {
      Timber.d("TODO: Show trigger creation dialog")
    })

    adapter.withOnLongClickListener { _, _, powerTriggerItem, _ ->
      DialogUtil.guaranteeSingleDialogFragment(activity,
          DeleteTriggerDialog.newInstance(powerTriggerItem.model), "delete_trigger")
      return@withOnLongClickListener true
    }
  }

  private fun loadTriggerListEmpty() {
    binding.powerTriggerList.visibility = View.GONE
    binding.powerTriggerEmpty.visibility = View.VISIBLE
  }

  private fun loadTriggerList() {
    binding.powerTriggerEmpty.visibility = View.GONE
    binding.powerTriggerList.visibility = View.VISIBLE
  }

  override fun onStop() {
    super.onStop()
    presenter.stop()

    adapter.withOnLongClickListener(null)
  }

  override fun onDestroyView() {
    super.onDestroyView()
    adapter.clear()
    binding.powerTriggerList.adapter = null
  }

  override fun onDestroy() {
    super.onDestroy()
    presenter.destroy()
  }

  companion object {
    const val TAG = "Power Triggers"
  }
}
