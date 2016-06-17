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
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.annotation.XmlRes;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.pyamsoft.powermanager.R;
import timber.log.Timber;

public class ManagerSettingsFragment extends PreferenceFragmentCompat {

  @NonNull public static final String TYPE_WIFI = "wifi";
  @NonNull public static final String TYPE_DATA = "data";
  @NonNull public static final String TYPE_BLUETOOTH = "bluetooth";
  @NonNull public static final String TYPE_SYNC = "sync";
  @NonNull private static final String FRAGMENT_TYPE = "fragment_type";
  @Nullable private ManageDelayPreference delayPreference;

  @XmlRes private int xmlResId;
  @StringRes private int timeKeyResId;

  @CheckResult @NonNull public static ManagerSettingsFragment newInstance(@NonNull String type) {
    final Bundle args = new Bundle();
    final ManagerSettingsFragment fragment = new ManagerSettingsFragment();
    args.putString(FRAGMENT_TYPE, type);
    fragment.setArguments(args);
    return fragment;
  }

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }

  @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {

    return super.onCreateView(inflater, container, savedInstanceState);
  }

  @Override public void onDestroyView() {
    super.onDestroyView();

    assert delayPreference != null;
    delayPreference.unbindView();
  }

  private void findCorrectPreferences() {
    final String fragmentType = getArguments().getString(FRAGMENT_TYPE, null);
    switch (fragmentType) {
      case TYPE_WIFI:
        Timber.d("Manage fragment for Wifi");
        xmlResId = R.xml.manage_wifi;
        timeKeyResId = R.string.wifi_time_key;
        break;
      case TYPE_DATA:
        Timber.d("Manage fragment for Data");
        xmlResId = R.xml.manage_data;
        timeKeyResId = R.string.data_time_key;
        //delayKeyResId = R.string.data_delay_key;
        break;
      case TYPE_BLUETOOTH:
        Timber.d("Manage fragment for Bluetooth");
        xmlResId = R.xml.manage_bluetooth;
        timeKeyResId = R.string.bluetooth_time_key;
        //delayKeyResId = R.string.bluetooth_delay_key;
        break;
      case TYPE_SYNC:
        Timber.d("Manage fragment for Sync");
        xmlResId = R.xml.manage_sync;
        timeKeyResId = R.string.sync_time_key;
        //delayKeyResId = R.string.sync_delay_key;
        break;
      default:
        throw new IllegalStateException("Invalid fragment type requested: " + fragmentType);
    }
  }

  @Override public void onCreatePreferences(Bundle bundle, String s) {
    findCorrectPreferences();
    addPreferencesFromResource(xmlResId);

    delayPreference = (ManageDelayPreference) findPreference(getString(timeKeyResId));
    delayPreference.bindView();
  }
}
