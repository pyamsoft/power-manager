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

package com.pyamsoft.powermanager.app.base;

import android.os.Bundle;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.TwoStatePreference;
import android.view.View;
import java.util.Locale;

abstract class FormatterPreferenceFragment extends PreferenceFragmentCompat {

  final void applyFormattedStrings(@NonNull Preference preference, @NonNull String name) {
    String title = preference.getTitle().toString();
    title = String.format(Locale.getDefault(), title, name);
    preference.setTitle(title);

    if (preference instanceof TwoStatePreference) {
      final TwoStatePreference twoStatePreference = (TwoStatePreference) preference;
      String summaryOn = twoStatePreference.getSummaryOn().toString();
      summaryOn = String.format(Locale.getDefault(), summaryOn, name);
      twoStatePreference.setSummaryOn(summaryOn);

      String summaryOff = twoStatePreference.getSummaryOff().toString();
      summaryOff = String.format(Locale.getDefault(), summaryOff, name);
      twoStatePreference.setSummaryOff(summaryOff);
    } else {
      String summary = preference.getSummary().toString();
      summary = String.format(Locale.getDefault(), summary, name);
      preference.setSummary(summary);
    }
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    resolvePreferences();
    applyFormattedStrings(getModuleName());
  }

  abstract void resolvePreferences();

  abstract void applyFormattedStrings(@NonNull String name);

  @CheckResult @NonNull protected abstract String getModuleName();
}
