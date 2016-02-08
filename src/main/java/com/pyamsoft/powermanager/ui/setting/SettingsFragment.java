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

package com.pyamsoft.powermanager.ui.setting;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.Spannable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.pyamsoft.powermanager.R;
import com.pyamsoft.powermanager.backend.util.GlobalPreferenceUtil;
import com.pyamsoft.powermanager.ui.ExplanationFragment;
import com.pyamsoft.pydroid.base.PreferenceBase;
import com.pyamsoft.pydroid.util.StringUtil;

public final class SettingsFragment extends ExplanationFragment {

  private RecyclerView recyclerView;
  private SettingsContentAdapter adapter;
  private PreferenceBase.OnSharedPreferenceChangeListener listener =
      new PreferenceBase.OnSharedPreferenceChangeListener(
          GlobalPreferenceUtil.PowerManagerMonitor.ENABLED,
          GlobalPreferenceUtil.PowerManagerMonitor.NOTIFICATION) {
        @Override
        protected void preferenceChanged(SharedPreferences sharedPreferences, String key) {
          if (adapter != null) {
            adapter.onForegroundAffected();
          }
        }
      };

  @Nullable @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_recyclerview, container, false);
  }

  @Override public void onResume() {
    super.onResume();
    listener.register(GlobalPreferenceUtil.with(getContext()).powerManagerMonitor());
  }

  @Override public void onPause() {
    super.onPause();
    listener.unregister(GlobalPreferenceUtil.with(getContext()).powerManagerMonitor());
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    adapter = new SettingsContentAdapter(getContext());
    recyclerView = (RecyclerView) view.findViewById(R.id.recyclerview);
    recyclerView.setLayoutManager(
        new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL));
    recyclerView.setHasFixedSize(true);
    recyclerView.setAdapter(adapter);

    setupExplanationString();
  }

  @Override public void onDestroyView() {
    super.onDestroyView();
    if (recyclerView != null) {
      recyclerView.setLayoutManager(null);
      recyclerView.setAdapter(null);
    }
    if (adapter != null) {
      adapter.destroy();
    }
  }

  @Override public Spannable setupExplanationString() {
    final String[] strings = {
        "Settings" + "\n\n",

        "Various settings can be configured for Power Manager. Here one can ",
        "disable or enable Advertisements", " as well as ",
        "start the application when the device starts",
        " and control how and when the application takes action." + "\n\n",

        "One can, for example, configure Power Manager to not "
            + "turn off any device interfaces as long as the device ", "is charging",
        " and has an active connection to some kind of source of power." + "\n\n",

        "Or, one can change whether the application shows a ",
        "notification in the foreground or even at all, ", "and can control whether Power Manager ",
        "starts up", " each time the device is successfully ", "turned on." + "\n\n"
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

  @Override public int getBackgroundColor() {
    return Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP ? R.color.lightgreen500
        : R.color.scrim45_lightgreen500;
  }
}
