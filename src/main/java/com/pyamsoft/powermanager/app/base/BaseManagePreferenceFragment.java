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
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.annotation.XmlRes;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.view.View;
import timber.log.Timber;

public abstract class BaseManagePreferenceFragment extends PreferenceFragmentCompat {

  private String manageKey;
  private String presetTimeKey;
  private String timeKey;

  @Override public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
    addPreferencesFromResource(getPreferencesResId());
    manageKey = getString(getManageKeyResId());
    presetTimeKey = getString(getPresetTimeKeyResId());
    timeKey = getString(getTimeKeyResId());
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    final Preference managePreference = findPreference(manageKey);
    if (managePreference != null) {
      managePreference.setOnPreferenceChangeListener((preference, newValue) -> {
        if (newValue instanceof Boolean) {
          final boolean b = (boolean) newValue;
          Timber.d("onPreferenceChange for key: %s", preference.getKey());
          return onManagePreferenceChanged(b);
        }
        return false;
      });
    }

    final Preference customTimePreference = findPreference(timeKey);
    final Preference presetTimePreference = findPreference(presetTimeKey);
    if (presetTimePreference != null) {
      presetTimePreference.setOnPreferenceChangeListener((preference, newValue) -> {
        if (newValue instanceof String) {
          final String string = (String) newValue;
          final long time = Long.parseLong(string);
          Timber.d("onPreferenceChange for key: %s", preference.getKey());
          return onPresetTimePreferenceChanged(time, customTimePreference);
        }
        return false;
      });
    }
  }

  /**
   * Override if you implement any custom conditions for changing preferences
   */
  @CheckResult protected boolean onManagePreferenceChanged(boolean b) {
    return true;
  }

  /**
   * Override if you implement any custom conditions for changing preferences
   */
  @CheckResult protected boolean onPresetTimePreferenceChanged(long time,
      @Nullable Preference customTimePreference) {
    return true;
  }

  @StringRes protected abstract int getManageKeyResId();

  @StringRes protected abstract int getPresetTimeKeyResId();

  @StringRes protected abstract int getTimeKeyResId();

  @XmlRes protected abstract int getPreferencesResId();
}
