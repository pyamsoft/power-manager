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

package com.pyamsoft.powermanager.app.manager;

import android.os.Bundle;
import android.support.annotation.BoolRes;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v7.preference.CheckBoxPreference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.PreferenceScreen;
import com.pyamsoft.powermanager.R;
import timber.log.Timber;

public class ManagerFragment extends PreferenceFragmentCompat {

  @NonNull private static final String FRAGMENT_TYPE = "fragment_type";
  @NonNull public static final String TYPE_WIFI = "wifi";
  @NonNull public static final String TYPE_DATA = "data";
  @NonNull public static final String TYPE_BLUETOOTH = "bluetooth";
  @NonNull public static final String TYPE_SYNC = "sync";
  @StringRes private int manageKey;
  @StringRes private int manageTitle;
  @StringRes private int manageSummary;
  @BoolRes private int manageDefault;

  @CheckResult @NonNull public static ManagerFragment newInstance(@NonNull String type) {
    final Bundle args = new Bundle();
    final ManagerFragment fragment = new ManagerFragment();
    args.putString(FRAGMENT_TYPE, type);
    fragment.setArguments(args);
    return fragment;
  }

  private void findCorrectPreferences() {
    final String fragmentType = getArguments().getString(FRAGMENT_TYPE, null);
    switch (fragmentType) {
      case TYPE_WIFI:
        Timber.d("Manage fragment for Wifi");
        manageKey = R.string.manage_wifi_key;
        manageTitle = R.string.manage_wifi_title;
        manageSummary = R.string.manage_wifi_summary;
        manageDefault = R.bool.manage_wifi_default;
        break;
      case TYPE_DATA:
        Timber.d("Manage fragment for Data");
        manageKey = R.string.manage_data_key;
        manageTitle = R.string.manage_data_title;
        manageSummary = R.string.manage_data_summary;
        manageDefault = R.bool.manage_data_default;
        break;
      case TYPE_BLUETOOTH:
        Timber.d("Manage fragment for Bluetooth");
        manageKey = R.string.manage_bluetooth_key;
        manageTitle = R.string.manage_bluetooth_title;
        manageSummary = R.string.manage_bluetooth_summary;
        manageDefault = R.bool.manage_bluetooth_default;
        break;
      case TYPE_SYNC:
        Timber.d("Manage fragment for Sync");
        manageKey = R.string.manage_sync_key;
        manageTitle = R.string.manage_sync_title;
        manageSummary = R.string.manage_sync_summary;
        manageDefault = R.bool.manage_sync_default;
        break;
      default:
        throw new IllegalStateException("Invalid fragment type requested: " + fragmentType);
    }
  }

  @Override public void onCreatePreferences(Bundle bundle, String s) {
    findCorrectPreferences();
    final PreferenceScreen preferenceScreen =
        getPreferenceManager().createPreferenceScreen(getActivity());

    final CheckBoxPreference managed = new CheckBoxPreference(getActivity());
    managed.setKey(getString(manageKey));
    managed.setTitle(getString(manageTitle));
    managed.setSummary(getString(manageSummary));
    managed.setDefaultValue(getResources().getBoolean(manageDefault));
    preferenceScreen.addPreference(managed);

    setPreferenceScreen(preferenceScreen);
  }
}
