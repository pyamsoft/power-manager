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
package com.pyamsoft.powermanager.ui.plan;

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
import com.pyamsoft.powermanager.ui.ExplanationFragment;
import com.pyamsoft.pydroid.util.StringUtil;

public final class PowerPlanFragment extends ExplanationFragment {

  private RecyclerView recyclerView;
  private PowerPlanAdapter adapter;

  @Nullable @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_recyclerview, container, false);
  }

  @Override public void onViewCreated(final View view, @Nullable final Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    adapter = new PowerPlanAdapter(getContext());
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
        "Power Plans" + "\n\n",

        "Power Plans are ", "preset application configurations ",
        "which provide a quick way to switch to specific application profiles which are ",
        "either stricter or more lax with the control over device interfaces. ",
        "A user defined custom plan is offered as a way to allow fine grained tuning" + "\n\n",
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

  @Override public int getBackgroundColor() {
    return Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP ? R.color.red500
        : R.color.scrim45_red500;
  }
}
