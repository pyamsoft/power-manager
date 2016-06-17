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

package com.pyamsoft.powermanager.app.settings;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import com.pyamsoft.powermanager.R;
import com.pyamsoft.powermanager.app.receiver.BootReceiver;

public final class SettingsFragment extends PreferenceFragmentCompat {

  @NonNull public static final String TAG = "settings";

  @Override public void onCreatePreferences(Bundle bundle, String s) {
    addPreferencesFromResource(R.xml.preferences);

    final Preference boot = getPreferenceScreen().findPreference(getString(R.string.boot_key));
    boot.setDefaultValue(BootReceiver.isBootEnabled(getContext()));
    boot.setOnPreferenceClickListener(preference -> {
      BootReceiver.setBootEnabled(getContext(), !BootReceiver.isBootEnabled(getContext()));
      return false;
    });
  }
}
