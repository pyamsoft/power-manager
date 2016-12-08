/*
 * Copyright 2016 Peter Kenji Yamanaka
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

package com.pyamsoft.powermanager.app.trigger.create;

import android.app.Dialog;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import com.pyamsoft.powermanager.R;
import com.pyamsoft.powermanager.databinding.DialogNewTriggerBinding;
import com.pyamsoft.pydroid.tool.AsyncDrawable;
import com.pyamsoft.pydroid.tool.AsyncMap;
import timber.log.Timber;

public class CreateTriggerDialog extends DialogFragment {

  private static final String CURRENT_PAGE = "current_page";
  @NonNull private final AsyncDrawable.Mapper taskMap = new AsyncDrawable.Mapper();
  @SuppressWarnings("WeakerAccess") DialogNewTriggerBinding binding;
  private CreateTriggerPagerAdapter adapter;
  private ViewPager.OnPageChangeListener pageChangeListener;

  @NonNull @Override public Dialog onCreateDialog(Bundle savedInstanceState) {
    final Dialog dialog = super.onCreateDialog(savedInstanceState);
    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
    return dialog;
  }

  @Nullable @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    binding = DataBindingUtil.inflate(inflater, R.layout.dialog_new_trigger, container, false);
    return binding.getRoot();
  }

  @Override public void onDestroyView() {
    super.onDestroyView();
    taskMap.clear();

    binding.newTriggerPager.removeOnPageChangeListener(pageChangeListener);
    binding.unbind();
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    setupToolbarButtons();
    setupContinueButton();
    setupViewPager(savedInstanceState);
  }

  private void setupViewPager(@Nullable Bundle bundle) {
    pageChangeListener = new ViewPager.OnPageChangeListener() {
      @Override
      public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

      }

      @Override public void onPageSelected(int position) {
        Timber.d("Page selected: %d", position);
        if (position == 0) {
          Timber.d("Hide back button");
          binding.newTriggerBack.setVisibility(View.GONE);
        } else {
          Timber.d("Show back button");
          binding.newTriggerBack.setVisibility(View.VISIBLE);
        }
      }

      @Override public void onPageScrollStateChanged(int state) {

      }
    };
    binding.newTriggerPager.addOnPageChangeListener(pageChangeListener);

    // Hold all the pages in memory so we can retrieve their content
    binding.newTriggerPager.setOffscreenPageLimit(4);

    adapter = new CreateTriggerPagerAdapter(this);
    binding.newTriggerPager.setAdapter(adapter);

    int currentPage;
    if (bundle == null) {
      currentPage = 0;
    } else {
      currentPage = bundle.getInt(CURRENT_PAGE, 0);
    }
    if (currentPage == 0) {
      // Hide the back button at first
      Timber.d("Show first page");
      binding.newTriggerBack.setVisibility(View.GONE);
    } else {
      Timber.d("Show saved page: %d", currentPage);
      binding.newTriggerBack.setVisibility(View.VISIBLE);
      binding.newTriggerPager.setCurrentItem(currentPage);
    }
  }

  private void setupContinueButton() {
    binding.newTriggerContinue.setOnClickListener(view -> {
      final int currentItem = binding.newTriggerPager.getCurrentItem();
      if (currentItem + 1 == CreateTriggerPagerAdapter.TOTAL_COUNT) {
        Timber.d("Final item continue clicked, process dialog and close");
        dismiss();
        adapter.collect(binding.newTriggerPager);
      } else {
        Timber.d("Continue clicked, progress 1 item");
        binding.newTriggerPager.setCurrentItem(binding.newTriggerPager.getCurrentItem() + 1);
      }
    });

    final AsyncMap.Entry continueTask =
        AsyncDrawable.load(R.drawable.ic_arrow_forward_24dp).into(binding.newTriggerContinue);
    taskMap.put("continue", continueTask);
  }

  private void setupToolbarButtons() {
    binding.newTriggerBack.setOnClickListener(view -> {
      Timber.d("Go back one item");
      binding.newTriggerPager.setCurrentItem(binding.newTriggerPager.getCurrentItem() - 1);
    });

    binding.newTriggerClose.setOnClickListener(view -> {
      Timber.d("Close clicked, dismiss dialog");
      dismiss();
    });

    final AsyncMap.Entry backTask =
        AsyncDrawable.load(R.drawable.ic_arrow_back_24dp).into(binding.newTriggerBack);
    taskMap.put("back", backTask);

    final AsyncMap.Entry closeTask =
        AsyncDrawable.load(R.drawable.ic_close_24dp).into(binding.newTriggerClose);
    taskMap.put("close", closeTask);
  }

  @Override public void onSaveInstanceState(Bundle outState) {
    outState.putInt(CURRENT_PAGE, binding.newTriggerPager.getCurrentItem());
    super.onSaveInstanceState(outState);
  }

  @Override public void onResume() {
    super.onResume();

    final Window window = getDialog().getWindow();
    if (window != null) {
      window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
    }

  }
}
