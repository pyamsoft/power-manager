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
import com.mikepenz.fastadapter.items.GenericAbstractItem
import com.pyamsoft.powermanager.Injector
import com.pyamsoft.powermanager.R
import com.pyamsoft.powermanager.databinding.FragmentPowertriggerBinding
import com.pyamsoft.powermanager.uicore.WatchedFragment
import com.pyamsoft.pydroid.loader.ImageLoader
import timber.log.Timber
import javax.inject.Inject

class PowerTriggerFragment : WatchedFragment() {

  private lateinit var binding: FragmentPowertriggerBinding
  @field:Inject internal lateinit var presenter: TriggerPresenter
  private lateinit var adapter: FastItemAdapter<GenericAbstractItem<Any, *, *>>

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
      TODO()
    }, onAddError = {
      TODO()
    }, onCreateError = {
      TODO()
    }, onTriggerDeleted = {
      TODO()
    }, onTriggerDeleteError = {
      TODO()
    })

    presenter.loadTriggerView(false, onTriggerLoaded = {
      Timber.d("TODO: Add trigger: %s", it)
    }, onTriggerLoadError = { TODO() }, onTriggerLoadFinished = { TODO() })

    presenter.clickEvent(binding.powerTriggerFab, {
      Timber.d("TODO: Show trigger creation dialog")
    })
  }

  override fun onStop() {
    super.onStop()
    presenter.stop()
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
