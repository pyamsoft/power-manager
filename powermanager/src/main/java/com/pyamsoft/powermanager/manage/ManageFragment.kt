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

import android.os.Bundle
import android.support.annotation.CheckResult
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.adapters.GenericItemAdapter
import com.pyamsoft.powermanager.R
import com.pyamsoft.powermanager.main.MainActivity
import com.pyamsoft.powermanager.uicore.WatchedFragment
import com.pyamsoft.pydroid.ui.util.ActionBarUtil
import io.reactivex.Observable
import kotlinx.android.synthetic.main.activity_main.bottomtabs
import kotlinx.android.synthetic.main.fragment_manage.recycler
import timber.log.Timber

class ManageFragment : WatchedFragment() {
  lateinit internal var adapter: GenericItemAdapter<String, BaseItem<*, *>>

  override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
      savedInstanceState: Bundle?): View? {
    return inflater?.inflate(R.layout.fragment_manage, container, false)
  }

  override fun onDestroyView() {
    super.onDestroyView()

    // Explicit unbind list item observables
    Observable.just(adapter).map { it.adapterItems }.flatMap {
      Observable.fromIterable(it)
    }.blockingForEach { it.unbindItem() }
  }

  override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    setupRecyclerView()
  }

  private fun setupRecyclerView() {
    adapter = GenericItemAdapter<String, BaseItem<*, *>> { s ->
      val item: BaseItem<*, *>?
      when (s) {
        ManageItem.TAG -> {
          Timber.d("Inflate ManageItem for TAG: %s", s)
          item = ManageItem()
        }
        ExceptionItem.TAG -> {
          Timber.d("Inflate ExceptionItem for TAG: %s", s)
          item = ExceptionItem()
        }
        DelayItem.TAG -> {
          Timber.d("Inflate DelayItem for TAG: %s", s)
          item = DelayItem()
        }
        PollItem.TAG -> {
          Timber.d("Inflate PollItem for TAG: %s", s)
          item = PollItem()
        }
        else -> {
          Timber.e("Cannot inflate item for TAG: %s", s)
          item = null
        }
      }
      return@GenericItemAdapter item
    }
    val manager = LinearLayoutManager(activity)
    manager.isItemPrefetchEnabled = true
    manager.initialPrefetchItemCount = 3
    recycler.layoutManager = manager
    recycler.clipToPadding = false
    recycler.setHasFixedSize(true)
    recycler.adapter = adapter.wrap(FastAdapter())

    adapter.add(ManageItem())
    adapter.add(DelayItem())
    adapter.add(PollItem())
    adapter.add(ExceptionItem())
  }

  override fun onResume() {
    super.onResume()
    ActionBarUtil.setActionBarUpEnabled(activity, false)
    ActionBarUtil.setActionBarTitle(activity, R.string.app_name)

    if (activity is MainActivity) {
      val main = activity as MainActivity
      main.bottomtabs.visibility = View.VISIBLE
      main.setOverlapTop(56F)
    }
  }

  companion object {
    const val TAG = "ManageFragment"

    @JvmStatic @CheckResult fun newInstance(): ManageFragment {
      val args = Bundle()
      val fragment = ManageFragment()
      fragment.arguments = args
      return fragment
    }
  }
}
