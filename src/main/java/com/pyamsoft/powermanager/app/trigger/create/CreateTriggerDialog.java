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
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import com.pyamsoft.powermanager.R;
import com.pyamsoft.pydroid.model.AsyncDrawable;
import com.pyamsoft.pydroid.tool.AsyncVectorDrawableTask;
import timber.log.Timber;

public class CreateTriggerDialog extends DialogFragment {

  private static final String CURRENT_PAGE = "current_page";
  @BindView(R.id.new_trigger_back) ImageView backButton;
  @BindView(R.id.new_trigger_close) ImageView closeButton;
  @BindView(R.id.new_trigger_continue) ImageView continueButton;
  @BindView(R.id.new_trigger_pager) ViewPager viewPager;
  private CreateTriggerPagerAdapter adapter;
  private Unbinder unbinder;
  private AsyncVectorDrawableTask backButtonTask;
  private AsyncVectorDrawableTask closeButtonTask;
  private AsyncVectorDrawableTask continueButtonTask;
  private ViewPager.OnPageChangeListener pageChangeListener;

  @NonNull @Override public Dialog onCreateDialog(Bundle savedInstanceState) {
    final Dialog dialog = super.onCreateDialog(savedInstanceState);
    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
    dialog.getWindow()
        .setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
    return dialog;
  }

  @Nullable @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    final View view = inflater.inflate(R.layout.dialog_new_trigger, container, false);
    unbinder = ButterKnife.bind(this, view);
    return view;
  }

  @Override public void onDestroyView() {
    super.onDestroyView();
    cancelTask(backButtonTask);
    cancelTask(closeButtonTask);
    cancelTask(continueButtonTask);

    viewPager.removeOnPageChangeListener(pageChangeListener);
    unbinder.unbind();
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    setupToolbarButtons();
    setupContinueButton();
    setupViewPager(savedInstanceState);
  }

  private void cancelTask(@Nullable AsyncVectorDrawableTask task) {
    if (task != null && !task.isCancelled()) {
      Timber.d("Cancel running AsyncTask");
      task.cancel(true);
    }
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
          backButton.setVisibility(View.GONE);
        } else {
          Timber.d("Show back button");
          backButton.setVisibility(View.VISIBLE);
        }
      }

      @Override public void onPageScrollStateChanged(int state) {

      }
    };
    viewPager.addOnPageChangeListener(pageChangeListener);

    // Hold all the pages in memory so we can retrieve their content
    viewPager.setOffscreenPageLimit(4);

    // KLUDGE Child fragments are ugly.
    adapter = new CreateTriggerPagerAdapter(getChildFragmentManager());
    viewPager.setAdapter(adapter);

    int currentPage;
    if (bundle == null) {
      currentPage = 0;
    } else {
      currentPage = bundle.getInt(CURRENT_PAGE, 0);
    }
    if (currentPage == 0) {
      // Hide the back button at first
      Timber.d("Show first page");
      backButton.setVisibility(View.GONE);
    } else {
      Timber.d("Show saved page: %d", currentPage);
      backButton.setVisibility(View.VISIBLE);
      viewPager.setCurrentItem(currentPage);
    }
  }

  private void setupContinueButton() {
    continueButtonTask = new AsyncVectorDrawableTask(continueButton);

    continueButton.setOnClickListener(view -> {
      final int currentItem = viewPager.getCurrentItem();
      if (currentItem + 1 == CreateTriggerPagerAdapter.TOTAL_COUNT) {
        Timber.d("Final item continue clicked, process dialog and close");
        dismiss();
        // TODO process
      } else {
        Timber.d("Continue clicked, progress 1 item");
        viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
      }
    });

    continueButtonTask.execute(new AsyncDrawable(getContext(), R.drawable.ic_arrow_forward_24dp));
  }

  private void setupToolbarButtons() {
    backButtonTask = new AsyncVectorDrawableTask(backButton);
    closeButtonTask = new AsyncVectorDrawableTask(closeButton);

    backButton.setOnClickListener(view -> {
      Timber.d("Go back one item");
      viewPager.setCurrentItem(viewPager.getCurrentItem() - 1);
    });

    closeButton.setOnClickListener(view -> {
      Timber.d("Close clicked, dismiss dialog");
      dismiss();
    });

    backButtonTask.execute(
        new AsyncDrawable(getContext().getApplicationContext(), R.drawable.ic_arrow_back_24dp));
    closeButtonTask.execute(
        new AsyncDrawable(getContext().getApplicationContext(), R.drawable.ic_close_24dp));
  }

  @Override public void onSaveInstanceState(Bundle outState) {
    outState.putInt(CURRENT_PAGE, viewPager.getCurrentItem());
    super.onSaveInstanceState(outState);
  }
}
