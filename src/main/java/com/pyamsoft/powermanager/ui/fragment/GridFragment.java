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

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.Spannable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.pyamsoft.powermanager.R;
import com.pyamsoft.powermanager.backend.service.MonitorService;
import com.pyamsoft.powermanager.backend.util.GlobalPreferenceUtil;
import com.pyamsoft.powermanager.ui.activity.MainActivity;
import com.pyamsoft.powermanager.ui.adapter.GridContentAdapter;
import com.pyamsoft.powermanager.ui.helper.ItemTouchHelperCallback;
import com.pyamsoft.pydroid.base.FABBase;
import com.pyamsoft.pydroid.base.PreferenceBase;
import com.pyamsoft.pydroid.util.AnimUtil;
import com.pyamsoft.pydroid.util.AppUtil;
import com.pyamsoft.pydroid.util.StringUtil;
import java.lang.ref.WeakReference;

public final class GridFragment extends ExplanationFragment {

  /* Views */
  private FloatingActionButton fab;
  private FABBase fabBase;
  /* Listeners */
  private final PreferenceBase.OnSharedPreferenceChangeListener listener =
      new PreferenceBase.OnSharedPreferenceChangeListener(
          GlobalPreferenceUtil.PowerManagerMonitor.ENABLED) {

        @Override protected void preferenceChanged(final SharedPreferences sharedPreferences,
            final String key) {
          if (fabBase != null) {
            fabBase.setChecked(fabBase.isChecked());
          }
        }
      };
  private RecyclerView recyclerView;
  private ItemTouchHelper helper;

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    setupFAB(view);
    setupRecyclerView(view);
  }

  @Override Spannable setupExplanationString() {
    final String[] strings = {
        "Overview" + "\n\n",

        "The Overview is the main screen of Power Manager. Here one can ", "configure controls",
        " and ", "view options", " related to the operation of the application." + "\n\n",

        "One can, for example, configure how long Power Manager will wait after "
            + "the before screen turns off before disabling the device's ", "WiFi",
        " connection." + "\n\n",

        "Or, one can visit the ", "Settings",
        " page and enable Power Manager to start when the device ", "starts up",
        " each time automatically, or disable the ", "Notification." + "\n\n"
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

    // Bold Wifi
    length += strings[4].length() + strings[5].length() + strings[6].length();
    StringUtil.boldSpan(explanation, length, length + strings[7].length());

    length += strings[7].length() + strings[8].length() + strings[9].length();
    StringUtil.boldSpan(explanation, length, length + strings[10].length());

    length += strings[10].length() + strings[11].length();
    StringUtil.boldSpan(explanation, length, length + strings[12].length());

    length += strings[12].length() + strings[13].length();
    StringUtil.boldSpan(explanation, length, length + strings[14].length());

    return explanation;
  }

  @Override int getBackgroundColor() {
    return AppUtil.androidVersionLessThan(Build.VERSION_CODES.LOLLIPOP) ? R.color.amber500
        : R.color.amber700;
  }

  @Override public void onDestroyView() {
    super.onDestroyView();
    // null fab callback
    if (fab != null) {
      AppUtil.nullifyCallback(fab);
      fab.setOnClickListener(null);
    }
    if (helper != null) {
      helper.attachToRecyclerView(null);
    }
    if (recyclerView != null) {
      recyclerView.setOnClickListener(null);
      recyclerView.setLayoutManager(null);
      recyclerView.setAdapter(null);
    }
  }

  private void showFAB() {
    if (fab != null) {
      AnimUtil.pop(fab, 500, 200).start();
    }
  }

  private void setupFAB(final View v) {
    fab = (FloatingActionButton) v.findViewById(R.id.fab);
    fabBase = new FABBase(fab, R.color.lightblueA200) {

      private final WeakReference<GridFragment> wA = new WeakReference<>(GridFragment.this);

      @Override public boolean isChecked() {
        final GlobalPreferenceUtil p = GlobalPreferenceUtil.with(getContext());
        return p.powerManagerMonitor().isEnabled();
      }

      @Override public void setChecked(boolean checked) {
        final int d =
            checked ? R.drawable.ic_pause_white_24dp : R.drawable.ic_play_arrow_white_24dp;
        setFabImage(d);
      }

      @Override public void startService() {
        final GridFragment a = wA.get();
        if (a != null) {
          MonitorService.powerManagerService(a.getContext());
        }
      }
    };

    fab.setOnClickListener(new View.OnClickListener() {

      private final WeakReference<GridFragment> wA = new WeakReference<>(GridFragment.this);

      @Override public void onClick(View v) {
        final GridFragment a = wA.get();
        if (a != null) {
          final FABBase f = a.fabBase;
          f.setChecked(!f.isChecked());
          f.startService();
        }
      }
    });
    fab.setOnLongClickListener(new View.OnLongClickListener() {
      @Override public boolean onLongClick(View v) {
        return true;
      }
    });

    fabBase.setChecked(fabBase.isChecked());
  }

  private void registerListener() {
    listener.register(GlobalPreferenceUtil.with(getContext()).powerManagerMonitor());
  }

  private void unregisterListener() {
    listener.unregister(GlobalPreferenceUtil.with(getContext()).powerManagerMonitor());
  }

  @Override public void onResume() {
    super.onResume();
    registerListener();
    showFAB();
    final MainActivity a = ((MainActivity) getActivity());
    if (a != null) {
      a.setActionBarUp(false);
      a.colorizeActionBarToolbar(true);
      a.colorizeStatusBar(R.color.amber700);
    }
  }

  @Override public void onPause() {
    super.onPause();
    unregisterListener();
  }

  @Nullable @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_grid, container, false);
  }

  private void setupRecyclerView(final View v) {
    final GridContentAdapter mainAdapter = new GridContentAdapter(this);
    recyclerView = (RecyclerView) v.findViewById(R.id.recyclerview);
    final StaggeredGridLayoutManager staggeredGridLayoutManager =
        new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
    recyclerView.setLayoutManager(staggeredGridLayoutManager);
    recyclerView.setHasFixedSize(true);
    recyclerView.setAdapter(mainAdapter);
    helper = new ItemTouchHelper(new ItemTouchHelperCallback(mainAdapter));
    helper.attachToRecyclerView(recyclerView);
  }
}
