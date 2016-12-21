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

package com.pyamsoft.powermanager.base;

import android.os.Bundle;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.preference.DialogPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.PreferenceGroup;
import android.support.v7.preference.PreferenceViewHolder;
import android.support.v7.preference.TwoStatePreference;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import com.getkeepsafe.taptargetview.TapTargetView;
import timber.log.Timber;

abstract class FormatterPreferenceFragment extends PreferenceFragmentCompat {

  final void dismissOnboarding(@Nullable TapTargetView targetView) {
    if (targetView == null) {
      Timber.w("NULL TargetView");
      return;
    }

    if (targetView.isVisible()) {
      targetView.dismiss(false);
    }
  }

  final void applyFormattedStrings(@NonNull Preference preference, @NonNull String name) {
    final String replaceString = "REPLACE_ME";
    String title = preference.getTitle().toString();
    title = title.replace(replaceString, name);
    preference.setTitle(title);

    if (preference instanceof DialogPreference) {
      final DialogPreference dialogPreference = (DialogPreference) preference;
      dialogPreference.setDialogTitle(title);
    }

    if (preference instanceof TwoStatePreference) {
      final TwoStatePreference twoStatePreference = (TwoStatePreference) preference;
      String summaryOn = twoStatePreference.getSummaryOn().toString();
      summaryOn = summaryOn.replace(replaceString, name);
      twoStatePreference.setSummaryOn(summaryOn);

      String summaryOff = twoStatePreference.getSummaryOff().toString();
      summaryOff = summaryOff.replace(replaceString, name);
      twoStatePreference.setSummaryOff(summaryOff);
    } else {
      String summary = preference.getSummary().toString();
      summary = summary.replace(replaceString, name);
      preference.setSummary(summary);
    }
  }

  @CheckResult @Nullable
  final PreferenceViewHolder findViewForPreference(@Nullable String preferenceKey) {
    if (preferenceKey == null) {
      Timber.w("NULL Preference Key");
      return null;
    }

    final PreferenceGroup.PreferencePositionCallback callback =
        (PreferenceGroup.PreferencePositionCallback) getListView().getAdapter();
    final int position = callback.getPreferenceAdapterPosition(preferenceKey);
    if (position == RecyclerView.NO_POSITION) {
      Timber.w("No position for key: %s", preferenceKey);
      return null;
    }

    return (PreferenceViewHolder) getListView().findViewHolderForAdapterPosition(position);
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    resolvePreferences();
    applyFormattedStrings(getModuleName());
  }

  @Override public void onDestroyView() {
    super.onDestroyView();
    dismissOnboarding();
  }

  abstract void dismissOnboarding();

  abstract void resolvePreferences();

  abstract void applyFormattedStrings(@NonNull String name);

  @CheckResult @NonNull protected abstract String getModuleName();
}
