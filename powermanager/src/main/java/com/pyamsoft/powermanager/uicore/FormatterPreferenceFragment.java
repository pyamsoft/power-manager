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

package com.pyamsoft.powermanager.uicore;

import android.os.Bundle;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.preference.DialogPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.TwoStatePreference;
import android.view.View;

abstract class FormatterPreferenceFragment extends PreferenceFragmentCompat {

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
      CharSequence nullableSummary = preference.getSummary();
      if (nullableSummary != null) {
        String summary = nullableSummary.toString();
        summary = summary.replace(replaceString, name);
        preference.setSummary(summary);
      }
    }
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    applyFormattedStrings(getModuleName());
  }

  abstract void applyFormattedStrings(@NonNull String name);

  @CheckResult @NonNull protected abstract String getModuleName();
}
