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

package com.pyamsoft.powermanager.ui.radio;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.Spannable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.pyamsoft.powermanager.R;
import com.pyamsoft.powermanager.ui.ExplanationFragment;
import com.pyamsoft.pydroid.util.StringUtil;

public abstract class BaseRadioFragment extends ExplanationFragment {

  private final StaggeredGridLayoutManager staggeredGridLayoutManager =
      new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);
  private RecyclerView recyclerView;
  private RadioContentAdapter adapter;

  protected abstract RadioContentAdapter.RadioInterface getRadio();

  @Nullable @Override
  public final View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_recyclerview, container, false);
  }

  @Override public final void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    adapter = new RadioContentAdapter(getRadio());
    recyclerView = (RecyclerView) view.findViewById(R.id.recyclerview);
    recyclerView.setLayoutManager(staggeredGridLayoutManager);
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
        "%s Interface" + "\n\n",

        "The %s interface allows users to configure how Power Manager,",
        " handles the device's %s power usage.", " This mainly revolves around controlling how to ",
        "automatically turn off", " the %s interface after a specified amount of time." + "\n\n",

        "The large Floating Action Button controls whether or not ",
        "Power Manager will automatically", " turn off the %s interface",
        " after the amount of time specified by Timeout Delay below." + "\n\n",

        "The small Floating Action Button controls whether or not the %s interface will ",
        "turn back on after being automatically turned off.",
        " This feature depends on the larger button for automatic control being ",
        "enabled and already completed.",
        " The %s interface can then be periodically toggled on and off at the duration ",
        "specified by Re-Open Duration." + "\n\n",

        "These control features are shared all of the device interfaces: ",
        "Wifi, Data, Bluetooth, and Sync." + "\n\n",
    };

    formatWithInterface(strings, 0);
    formatWithInterface(strings, 1);
    formatWithInterface(strings, 2);
    formatWithInterface(strings, 5);
    formatWithInterface(strings, 8);
    formatWithInterface(strings, 10);
    formatWithInterface(strings, 14);

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

    length += strings[7].length() + strings[8].length() + strings[9].length() +
        strings[10].length();
    StringUtil.boldSpan(explanation, length, length + strings[11].length());

    length += strings[11].length() + strings[12].length();
    StringUtil.boldSpan(explanation, length, length + strings[13].length());

    length += strings[13].length() + strings[14].length();
    StringUtil.boldSpan(explanation, length, length + strings[15].length());

    length += strings[15].length() + strings[16].length();
    StringUtil.boldSpan(explanation, length, length + strings[17].length());
    return explanation;
  }

  private void formatWithInterface(final String[] strings, final int index) {
    final String fmt = strings[index];
    final String replace = StringUtil.formatString(fmt, getRadio().getName());
    strings[index] = replace;
  }
}
