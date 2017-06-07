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
import android.support.annotation.CheckResult
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter
import com.pyamsoft.powermanager.Injector
import com.pyamsoft.powermanager.PowerManager
import com.pyamsoft.powermanager.R
import com.pyamsoft.powermanager.trigger.create.CreateTriggerDialog
import com.pyamsoft.powermanager.trigger.db.PowerTriggerEntry
import com.pyamsoft.powermanager.uicore.WatchedFragment
import com.pyamsoft.pydroid.loader.ImageLoader
import com.pyamsoft.pydroid.loader.LoaderMap
import com.pyamsoft.pydroid.ui.helper.Toasty
import com.pyamsoft.pydroid.ui.util.DialogUtil
import kotlinx.android.synthetic.main.fragment_powertrigger.power_trigger_empty
import kotlinx.android.synthetic.main.fragment_powertrigger.power_trigger_fab
import kotlinx.android.synthetic.main.fragment_powertrigger.power_trigger_list
import timber.log.Timber
import javax.inject.Inject

class PowerTriggerListFragment : WatchedFragment() {
  private val drawableMap = LoaderMap()
  @field:Inject lateinit internal var presenter: TriggerPresenter
  internal var adapter: FastItemAdapter<PowerTriggerListItem>? = null
  internal var listIsRefreshed: Boolean = false
  private lateinit var dividerDecoration: RecyclerView.ItemDecoration

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    Injector.with(context) {
      it.inject(this)
    }
  }

  override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
      savedInstanceState: Bundle?): View? {
    listIsRefreshed = false
    dividerDecoration = DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
    return inflater?.inflate(R.layout.fragment_powertrigger, container, false)
  }

  override fun onDestroyView() {
    super.onDestroyView()
    setActionBarUpEnabled(false)

    drawableMap.clear()
    power_trigger_list.removeItemDecoration(dividerDecoration)
  }

  override fun onStart() {
    super.onStart()
    if (adapter == null) {
      adapter = FastItemAdapter<PowerTriggerListItem>()

      with(adapter as FastItemAdapter<PowerTriggerListItem>) {
        withSelectable(true)
        withSelectOnLongClick(true)
        withOnLongClickListener { _, _, item, _ ->
          DialogUtil.guaranteeSingleDialogFragment(activity,
              DeleteTriggerDialog.newInstance(item.model), "delete_trigger")
          return@withOnLongClickListener true
        }

        itemAdapter.withComparator({ item1, item2 ->
          val item1Percent = item1.model.percent()
          val item2PPercent = item2.model.percent()
          if (item1Percent == item2PPercent) {
            throw IllegalStateException("Cannot have two triggers with same percent")
          } else if (item1Percent < item2PPercent) {
            return@withComparator -1
          } else {
            return@withComparator 1
          }
        }, true)
      }
    }

    if (!listIsRefreshed) {
      // Because we may already have an Adapter with entries, we clear it first so that there are no doubles.
      adapter?.clear()
      presenter.loadTriggerView(object : TriggerPresenter.TriggerLoadCallback {
        override fun onTriggerLoaded(entry: PowerTriggerEntry) {
          adapter?.add(createNewPowerTriggerListItem(entry))
        }

        override fun onTriggerLoadFinished() {
          if (adapter?.itemCount == 0) {
            loadEmptyView()
          } else {
            listIsRefreshed = true
            loadListView()
          }
        }
      }, false)
    }

    presenter.registerOnBus(object : TriggerPresenter.BusCallback {
      override fun onNewTriggerAdded(entry: PowerTriggerEntry) {
        Timber.d("Added new trigger with percent: %d", entry.percent())

        adapter?.add(createNewPowerTriggerListItem(entry))
        if (power_trigger_list.adapter == null) {
          Timber.d("First trigger, show list")
          loadListView()
        }
      }

      override fun onNewTriggerCreateError() {
        Toasty.makeText(context, "ERROR: Trigger must have a name and unique percent",
            Toasty.LENGTH_LONG).show()
      }

      override fun onNewTriggerInsertError() {
        Toasty.makeText(context, "ERROR: Two triggers cannot have the same percent",
            Toasty.LENGTH_LONG).show()
      }

      override fun onTriggerDeleted(position: Int) {
        adapter?.remove(position)
        if (adapter?.itemCount == 0) {
          Timber.d("Last trigger, hide list")
          loadEmptyView()
        }
      }
    })
  }

  override fun onResume() {
    super.onResume()
    setActionBarUpEnabled(true)
  }

  override fun onStop() {
    super.onStop()
    presenter.stop()
  }

  override fun onDestroy() {
    super.onDestroy()
    presenter.destroy()
    PowerManager.getRefWatcher(this).watch(this)
  }

  override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    setupRecyclerView()
    setupFab()
  }

  private fun setupFab() {
    val subscription = ImageLoader.fromResource(activity, R.drawable.ic_add_24dp).tint(
        android.R.color.white).into(power_trigger_fab)
    drawableMap.put("fab", subscription)

    power_trigger_fab.setOnClickListener {
      Timber.d("Show new trigger dialog")
      DialogUtil.guaranteeSingleDialogFragment(activity, CreateTriggerDialog(), "create_trigger")
    }
  }

  private fun setupRecyclerView() {
    val manager = LinearLayoutManager(context)
    manager.isItemPrefetchEnabled = true
    manager.initialPrefetchItemCount = 3
    power_trigger_list.layoutManager = manager
    power_trigger_list.setHasFixedSize(true)
    power_trigger_list.clipToPadding = false
    power_trigger_list.addItemDecoration(dividerDecoration)
  }

  internal fun loadEmptyView() {
    Timber.d("Load empty view")
    power_trigger_list.visibility = View.GONE
    power_trigger_list.adapter = null
    power_trigger_empty.visibility = View.VISIBLE
  }

  internal fun loadListView() {
    Timber.d("Load list view")
    power_trigger_empty.visibility = View.GONE
    power_trigger_list.adapter = adapter
    power_trigger_list.visibility = View.VISIBLE
  }

  @CheckResult internal fun createNewPowerTriggerListItem(
      entry: PowerTriggerEntry): PowerTriggerListItem {
    return PowerTriggerListItem(entry)
  }

  companion object {
    const val TAG = "PowerTriggerListFragment"
  }
}
