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

package com.pyamsoft.powermanager.trigger.create

import android.os.Bundle
import android.support.v4.view.ViewPager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import com.pyamsoft.powermanager.R
import com.pyamsoft.powermanager.databinding.DialogNewTriggerBinding
import com.pyamsoft.powermanager.uicore.WatchedDialog
import com.pyamsoft.pydroid.loader.ImageLoader
import com.pyamsoft.pydroid.loader.LoaderMap
import timber.log.Timber

class CreateTriggerDialog : WatchedDialog() {
  private val taskMap = LoaderMap()
  private lateinit var adapter: CreateTriggerPagerAdapter
  private lateinit var pageChangeListener: ViewPager.OnPageChangeListener
  private lateinit var binding: DialogNewTriggerBinding

  override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
      savedInstanceState: Bundle?): View? {
    binding = DialogNewTriggerBinding.inflate(inflater, container, false)
    return binding.root
  }

  override fun onDestroyView() {
    super.onDestroyView()
    taskMap.clear()
    binding.newTriggerPager.removeOnPageChangeListener(pageChangeListener)
  }

  override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    setupToolbarButtons()
    setupContinueButton()
    setupViewPager(savedInstanceState)
  }

  private fun setupViewPager(bundle: Bundle?) {
    pageChangeListener = object : ViewPager.OnPageChangeListener {
      override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
      }

      override fun onPageSelected(position: Int) {
        Timber.d("Page selected: %d", position)
        if (position == 0) {
          Timber.d("Hide back button")
          binding.newTriggerBack.visibility = View.GONE
        } else {
          Timber.d("Show back button")
          binding.newTriggerBack.visibility = View.VISIBLE
        }
      }

      override fun onPageScrollStateChanged(state: Int) {
      }
    }
    binding.newTriggerPager.addOnPageChangeListener(pageChangeListener)

    // Hold all the pages in memory so we can retrieve their content
    binding.newTriggerPager.offscreenPageLimit = 4

    adapter = CreateTriggerPagerAdapter(this)
    binding.newTriggerPager.adapter = adapter
    val currentPage: Int
    if (bundle == null) {
      currentPage = 0
    } else {
      currentPage = bundle.getInt(CURRENT_PAGE, 0)
    }
    if (currentPage == 0) {
      // Hide the back button at first
      Timber.d("Show first page")
      binding.newTriggerBack.visibility = View.GONE
    } else {
      Timber.d("Show saved page: %d", currentPage)
      binding.newTriggerBack.visibility = View.VISIBLE
      binding.newTriggerPager.currentItem = currentPage
    }
  }

  private fun setupContinueButton() {
    binding.newTriggerContinue.setOnClickListener {
      val currentItem = binding.newTriggerPager.currentItem
      if (currentItem + 1 == CreateTriggerPagerAdapter.TOTAL_COUNT) {
        Timber.d("Final item continue clicked, process dialog and close")
        dismiss()
        adapter.collect(binding.newTriggerPager)
      } else {
        Timber.d("Continue clicked, progress 1 item")
        binding.newTriggerPager.currentItem = binding.newTriggerPager.currentItem + 1
      }
    }
    val continueTask = ImageLoader.fromResource(activity, R.drawable.ic_arrow_forward_24dp).into(
        binding.newTriggerContinue)
    taskMap.put("continue", continueTask)
  }

  private fun setupToolbarButtons() {
    binding.newTriggerBack.setOnClickListener {
      Timber.d("Go back one item")
      binding.newTriggerPager.currentItem = binding.newTriggerPager.currentItem - 1
    }

    binding.newTriggerClose.setOnClickListener {
      Timber.d("Close clicked, dismiss dialog")
      dismiss()
    }
    val backTask = ImageLoader.fromResource(activity, R.drawable.ic_arrow_back_24dp).into(
        binding.newTriggerBack)
    taskMap.put("back", backTask)
    val closeTask = ImageLoader.fromResource(activity, R.drawable.ic_close_24dp).into(
        binding.newTriggerClose)
    taskMap.put("close", closeTask)
  }

  override fun onSaveInstanceState(outState: Bundle?) {
    outState?.putInt(CURRENT_PAGE, binding.newTriggerPager.currentItem)
    super.onSaveInstanceState(outState)
  }

  override fun onResume() {
    super.onResume()

    // The dialog is super small for some reason. We have to set the size manually, in onResume
    val window = dialog.window
    window?.setLayout(WindowManager.LayoutParams.MATCH_PARENT,
        WindowManager.LayoutParams.WRAP_CONTENT)
  }

  companion object {
    private const val CURRENT_PAGE = "current_page"
  }
}
