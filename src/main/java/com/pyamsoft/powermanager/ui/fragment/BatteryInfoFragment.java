/*
 * Copyright 2013 - 2016 Peter Kenji Yamanaka
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
package com.pyamsoft.powermanager.ui.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.Spannable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.pyamsoft.powermanager.R;
import com.pyamsoft.powermanager.ui.adapter.BatteryInfoAdapter;
import com.pyamsoft.pydroid.misc.DividerItemDecoration;
import com.pyamsoft.pydroid.util.AppUtil;
import com.pyamsoft.pydroid.util.StringUtil;

public final class BatteryInfoFragment extends ExplanationFragment {

  private final IntentFilter filter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
  private final BatteryInfoAdapter adapter = new BatteryInfoAdapter();
  private final Handler handler = new Handler();
  private final BroadcastReceiver batteryReceiver = new BroadcastReceiver() {

    @Override public void onReceive(Context context, Intent intent) {
      if (intent != null && adapter != null) {
        adapter.notifyDataSetChanged();
      }
    }
  };
  private SwipeRefreshLayout swipeRefreshLayout;
  private final Runnable runnable = new Runnable() {

    @Override public void run() {
      if (adapter != null) {
        adapter.notifyDataSetChanged();
      }
      if (swipeRefreshLayout != null) {
        swipeRefreshLayout.setRefreshing(false);
      }
    }
  };
  private RecyclerView recyclerView;
  private RecyclerView.ItemDecoration decor;

  @Nullable @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_battery_info, container, false);
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    filter.addAction(Intent.ACTION_POWER_CONNECTED);
    filter.addAction(Intent.ACTION_POWER_DISCONNECTED);
    swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swiperefresh);
    swipeRefreshLayout.setColorSchemeResources(R.color.amber500, R.color.lightblueA200,
        R.color.amber700, R.color.cyan500);
    setupSwipeRefresh();
    decor = new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST);
    recyclerView = (RecyclerView) view.findViewById(R.id.recyclerview);
    recyclerView.setLayoutManager(
        new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL));
    recyclerView.setHasFixedSize(true);
    recyclerView.addItemDecoration(decor);
    recyclerView.setAdapter(adapter);

    setupExplanationString();
  }

  private void setupSwipeRefresh() {
    swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

      @Override public void onRefresh() {
        handler.postDelayed(runnable, 1000);
      }
    });
  }

  @Override public void onResume() {
    super.onResume();
    if (adapter != null) {
      adapter.notifyDataSetChanged();
    }
    getContext().registerReceiver(batteryReceiver, filter);
  }

  @Override public void onPause() {
    super.onPause();
    getContext().unregisterReceiver(batteryReceiver);
    handler.removeCallbacksAndMessages(null);
  }

  @Override public void onDestroyView() {
    super.onDestroyView();
    if (recyclerView != null) {
      recyclerView.setLayoutManager(null);
      recyclerView.removeItemDecoration(decor);
      recyclerView.setAdapter(null);
    }
    if (swipeRefreshLayout != null) {
      swipeRefreshLayout.setOnClickListener(null);
      swipeRefreshLayout.setOnRefreshListener(null);
    }
  }

  @Override Spannable setupExplanationString() {
    final String[] strings = {
        "Battery Info" + "\n\n",

        "This screen displays various information about the current state of the ",
        "device's battery. ", "The screen includes information about the current battery ",
        "percent, charging status, and temperature ",
        "and can be updated by swiping downwards on the list of items." + "\n\n",
    };

    final int largeSize =
        StringUtil.getTextSizeFromAppearance(getContext(), android.R.attr.textAppearanceLarge);
    final int smallSize =
        StringUtil.getTextSizeFromAppearance(getContext(), android.R.attr.textAppearanceMedium);

    int length = strings[0].length();

    // Color all text white
    final Spannable explanation = StringUtil.createBuilder(strings);
    StringUtil.colorSpan(explanation, 0, explanation.length(), Color.WHITE);

    // Bold title
    StringUtil.boldSpan(explanation, 0, length);
    if (largeSize != -1) {
      // Large title
      StringUtil.sizeSpan(explanation, 0, length, largeSize);
    }

    if (smallSize != -1) {
      // Small everything else
      StringUtil.sizeSpan(explanation, length, explanation.length(), smallSize);
    }

    // Bold configure
    length += strings[1].length();
    StringUtil.boldSpan(explanation, length, length + strings[2].length());

    // Bold view
    length += strings[2].length() + strings[3].length();
    StringUtil.boldSpan(explanation, length, length + strings[4].length());
    return explanation;
  }

  @Override int getBackgroundColor() {
    return AppUtil.androidVersionLessThan(Build.VERSION_CODES.LOLLIPOP) ? R.color.pink500
        : R.color.scrim45_pink500;
  }
}
