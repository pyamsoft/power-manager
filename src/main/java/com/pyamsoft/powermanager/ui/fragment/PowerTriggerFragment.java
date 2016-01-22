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
import com.pyamsoft.powermanager.ui.adapter.PowerTriggerAdapter;
import com.pyamsoft.pydroid.misc.DividerItemDecoration;
import com.pyamsoft.pydroid.util.AppUtil;
import com.pyamsoft.pydroid.util.StringUtil;

public final class PowerTriggerFragment extends ExplanationFragment {

  private RecyclerView recyclerView;
  private RecyclerView.ItemDecoration decor;

  @Nullable @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_recyclerview, container, false);
  }

  @Override public void onViewCreated(final View view, @Nullable final Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    setupExplanationString();
    final PowerTriggerAdapter adapter = new PowerTriggerAdapter(this);
    adapter.refreshDataSet();
    decor = new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST);
    recyclerView = (RecyclerView) view.findViewById(R.id.recyclerview);
    recyclerView.setLayoutManager(
        new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL));
    recyclerView.setHasFixedSize(false);
    recyclerView.addItemDecoration(decor);
    recyclerView.setAdapter(adapter);
  }

  @Override public void onDestroyView() {
    super.onDestroyView();
    if (recyclerView != null) {
      recyclerView.setLayoutManager(null);
      recyclerView.setAdapter(null);
      recyclerView.removeItemDecoration(decor);
    }
  }

  @Override Spannable setupExplanationString() {
    final String[] strings = {
        "Power Triggers" + "\n\n",

        "Power Triggers are special,", " user configured", " commands that will be run ",
        "automatically",
        " by Power Manager when the battery drops to a cetrain percentage." + "\n\n",

        "Triggers can change the current configuration of Power Manager"
            + "in realtime, or toggle various", " device interfaces",
        " either on or off automatically." + "\n\n",

        "New triggers can be created by selecting the", "plus icon", " or deleted by performing a ",
        "long press", " on the trigger entry in the list and confirming the ",
        "deletion prompt." + "\n\n"
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
    return AppUtil.androidVersionLessThan(Build.VERSION_CODES.LOLLIPOP) ? R.color.yellow500
        : R.color.scrim45_yellow500;
  }
}
