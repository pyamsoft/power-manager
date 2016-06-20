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
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.SwitchPreferenceCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.pyamsoft.powermanager.PowerManager;
import com.pyamsoft.powermanager.R;
import com.pyamsoft.powermanager.app.manager.custom.ManagerDelayPreference;
import com.pyamsoft.powermanager.dagger.manager.DaggerManagerSettingsComponent;
import javax.inject.Inject;
import timber.log.Timber;

public class ManagerSettingsFragment extends PreferenceFragmentCompat
    implements ManagerSettingsPresenter.ManagerSettingsView {

  @NonNull public static final String TYPE_WIFI = "wifi";
  @NonNull public static final String TYPE_DATA = "data";
  @NonNull public static final String TYPE_BLUETOOTH = "bluetooth";
  @NonNull public static final String TYPE_SYNC = "sync";
  @NonNull private static final String FRAGMENT_TYPE = "fragment_type";
  @Nullable @Inject ManagerSettingsPresenter presenter;
  @Nullable private SwitchPreferenceCompat managePreference;
  @Nullable private ManagerDelayPreference delayPreference;
  @Nullable private ListPreference presetDelayPreference;

  @XmlRes private int xmlResId;
  @StringRes private int manageKeyResId;
  @StringRes private int timeKeyResId;
  @StringRes private int presetTimeKeyResId;

  @CheckResult @NonNull public static ManagerSettingsFragment newInstance(@NonNull String type) {
    final Bundle args = new Bundle();
    final ManagerSettingsFragment fragment = new ManagerSettingsFragment();
    args.putString(FRAGMENT_TYPE, type);
    fragment.setArguments(args);
    return fragment;
  }

  @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    assert presenter != null;
    presenter.bindView(this);
    assert delayPreference != null;
    delayPreference.bindView();
    return super.onCreateView(inflater, container, savedInstanceState);
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    assert managePreference != null;
    managePreference.setOnPreferenceChangeListener((preference, o) -> {
      assert presenter != null;
      presenter.updateCustomTime((boolean) o);
      return true;
    });

    assert presenter != null;
    presenter.setCustomTimeStateFromPreference(getString(manageKeyResId));
  }

  @Override public void onDestroyView() {
    super.onDestroyView();

    assert delayPreference != null;
    delayPreference.unbindView();

    assert presenter != null;
    presenter.unbindView();
  }

  private void findCorrectPreferences() {
    final String fragmentType = getArguments().getString(FRAGMENT_TYPE, null);
    switch (fragmentType) {
      case TYPE_WIFI:
        Timber.d("Manage fragment for Wifi");
        xmlResId = R.xml.manage_wifi;
        manageKeyResId = R.string.manage_wifi_key;
        timeKeyResId = R.string.wifi_time_key;
        presetTimeKeyResId = R.string.preset_delay_wifi_key;
        break;
      case TYPE_DATA:
        Timber.d("Manage fragment for Data");
        xmlResId = R.xml.manage_data;
        manageKeyResId = R.string.manage_data_key;
        timeKeyResId = R.string.data_time_key;
        presetTimeKeyResId = R.string.preset_delay_data_key;
        break;
      case TYPE_BLUETOOTH:
        Timber.d("Manage fragment for Bluetooth");
        xmlResId = R.xml.manage_bluetooth;
        manageKeyResId = R.string.manage_bluetooth_key;
        timeKeyResId = R.string.bluetooth_time_key;
        presetTimeKeyResId = R.string.preset_delay_bluetooth_key;
        break;
      case TYPE_SYNC:
        Timber.d("Manage fragment for Sync");
        xmlResId = R.xml.manage_sync;
        manageKeyResId = R.string.manage_sync_key;
        timeKeyResId = R.string.sync_time_key;
        presetTimeKeyResId = R.string.preset_delay_sync_key;
        break;
      default:
        throw new IllegalStateException("Invalid fragment type requested: " + fragmentType);
    }
  }

  @Override public void onCreatePreferences(Bundle bundle, String s) {
    DaggerManagerSettingsComponent.builder()
        .powerManagerComponent(PowerManager.getInstance().getPowerManagerComponent())
        .build()
        .inject(this);

    findCorrectPreferences();
    addPreferencesFromResource(xmlResId);

    resolvePreferences();
  }

  private void resolvePreferences() {
    managePreference = (SwitchPreferenceCompat) findPreference(getString(manageKeyResId));
    delayPreference = (ManagerDelayPreference) findPreference(getString(timeKeyResId));
    presetDelayPreference = (ListPreference) findPreference(getString(presetTimeKeyResId));
  }

  @Override public void enableCustomTime() {
    Timber.d("Enable custom");
    assert delayPreference != null;
    delayPreference.setEnabled(true);
  }

  @Override public void disableCustomTime() {
    Timber.d("Disable custom");
    assert delayPreference != null;
    delayPreference.setEnabled(false);
  }
}
