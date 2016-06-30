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

import android.content.SharedPreferences;
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
import com.pyamsoft.powermanager.app.manager.custom.ManagerPeriodicPreference;
import com.pyamsoft.powermanager.dagger.manager.DaggerManagerPeriodicComponent;
import javax.inject.Inject;
import timber.log.Timber;

public class ManagerPeriodicFragment extends PreferenceFragmentCompat
    implements ManagerPeriodicView {

  @Inject ManagerPeriodicPresenter presenter;

  private SwitchPreferenceCompat periodicPreference;
  private ListPreference presetPeriodicDisablePreference;
  private ManagerPeriodicPreference periodicDisablePreference;
  private ListPreference presetPeriodicEnablePreference;
  private ManagerPeriodicPreference periodicEnablePreference;

  @XmlRes private int xmlResId;
  @StringRes private int manageKeyResId;
  @StringRes private int periodicKeyResId;
  @StringRes private int periodicDisableKeyResId;
  @StringRes private int presetPeriodicDisableKeyResId;
  @StringRes private int periodicEnableKeyResId;
  @StringRes private int presetPeriodicEnableKeyResId;
  private SharedPreferences.OnSharedPreferenceChangeListener listener;

  @CheckResult @NonNull public static ManagerPeriodicFragment newInstance(@NonNull String type) {
    final Bundle args = new Bundle();
    final ManagerPeriodicFragment fragment = new ManagerPeriodicFragment();
    args.putString(ManagerSettingsPagerAdapter.FRAGMENT_TYPE, type);
    fragment.setArguments(args);
    return fragment;
  }

  @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    presenter.bindView(this);
    periodicDisablePreference.bindView();
    periodicEnablePreference.bindView();
    return super.onCreateView(inflater, container, savedInstanceState);
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    final String managedKey = getString(manageKeyResId);
    periodicPreference.setOnPreferenceChangeListener((preference, o) -> {
      if (o instanceof Boolean) {
        final boolean checked = (boolean) o;

        // Disable delay custom when unchecked
        // Enable delay custom when checked and custom delay time and periodic checked
        final String presetDisable = presetPeriodicDisablePreference.getValue();
        final long disableTime = Long.parseLong(presetDisable);
        if (!checked || disableTime == -1) {
          presenter.updateCustomPeriodicDisableTimeView(managedKey, checked);
        }

        final String presetEnable = presetPeriodicEnablePreference.getValue();
        final long enableTime = Long.parseLong(presetEnable);
        if (!checked || enableTime == -1) {
          presenter.updateCustomPeriodicEnableTimeView(managedKey, checked);
        }
        return true;
      }
      return false;
    });

    presetPeriodicDisablePreference.setOnPreferenceChangeListener((preference, o) -> {
      if (o instanceof String) {
        final String string = (String) o;
        final long time = Long.parseLong(string);

        if (time != -1) {
          periodicDisablePreference.updateTime(time);
        }

        presenter.updateCustomPeriodicDisableTimeView(managedKey,
            time == -1 && periodicPreference.isChecked());
        return true;
      }
      return false;
    });

    presetPeriodicEnablePreference.setOnPreferenceChangeListener((preference, o) -> {
      if (o instanceof String) {
        final String string = (String) o;
        final long time = Long.parseLong(string);

        if (time != -1) {
          periodicEnablePreference.updateTime(time);
        }

        presenter.updateCustomPeriodicEnableTimeView(managedKey,
            time == -1 && periodicPreference.isChecked());
        return true;
      }
      return false;
    });

    presenter.setCustomPeriodicDisableTimeStateFromPreference(managedKey,
        getString(periodicKeyResId), periodicPreference.isChecked());

    presenter.setCustomPeriodicEnableTimeStateFromPreference(managedKey,
        getString(periodicKeyResId), periodicPreference.isChecked());

    // We want to enable the periodic switch only based on managed state
    presenter.setPeriodicFromPreference(managedKey);
  }

  @Override public void onDestroyView() {
    super.onDestroyView();
    periodicDisablePreference.unbindView();
    periodicEnablePreference.unbindView();
    presenter.unbindView();
  }

  private void findCorrectPreferences() {
    final String fragmentType =
        getArguments().getString(ManagerSettingsPagerAdapter.FRAGMENT_TYPE, null);
    switch (fragmentType) {
      case ManagerSettingsPagerAdapter.TYPE_WIFI:
        Timber.d("Periodic fragment for Wifi");
        xmlResId = R.xml.periodic_wifi;
        manageKeyResId = R.string.manage_wifi_key;
        periodicKeyResId = R.string.periodic_wifi_key;
        periodicDisableKeyResId = R.string.periodic_wifi_disable_key;
        presetPeriodicDisableKeyResId = R.string.preset_periodic_wifi_disable_key;
        periodicEnableKeyResId = R.string.periodic_wifi_enable_key;
        presetPeriodicEnableKeyResId = R.string.preset_periodic_wifi_enable_key;
        break;
      case ManagerSettingsPagerAdapter.TYPE_DATA:
        Timber.d("Periodic fragment for Data");
        xmlResId = R.xml.periodic_data;
        manageKeyResId = R.string.manage_data_key;
        periodicKeyResId = R.string.periodic_data_key;
        periodicDisableKeyResId = R.string.periodic_data_disable_key;
        presetPeriodicDisableKeyResId = R.string.preset_periodic_data_disable_key;
        periodicEnableKeyResId = R.string.periodic_data_enable_key;
        presetPeriodicEnableKeyResId = R.string.preset_periodic_data_enable_key;
        break;
      case ManagerSettingsPagerAdapter.TYPE_BLUETOOTH:
        Timber.d("Periodic fragment for Bluetooth");
        xmlResId = R.xml.periodic_bluetooth;
        manageKeyResId = R.string.manage_bluetooth_key;
        periodicKeyResId = R.string.periodic_bluetooth_key;
        periodicDisableKeyResId = R.string.periodic_bluetooth_disable_key;
        presetPeriodicDisableKeyResId = R.string.preset_periodic_bluetooth_disable_key;
        periodicEnableKeyResId = R.string.periodic_bluetooth_enable_key;
        presetPeriodicEnableKeyResId = R.string.preset_periodic_bluetooth_enable_key;
        break;
      case ManagerSettingsPagerAdapter.TYPE_SYNC:
        Timber.d("Periodic fragment for Sync");
        xmlResId = R.xml.periodic_sync;
        manageKeyResId = R.string.manage_sync_key;
        periodicKeyResId = R.string.periodic_sync_key;
        periodicDisableKeyResId = R.string.periodic_sync_disable_key;
        presetPeriodicDisableKeyResId = R.string.preset_periodic_sync_disable_key;
        periodicEnableKeyResId = R.string.periodic_sync_enable_key;
        presetPeriodicEnableKeyResId = R.string.preset_periodic_sync_enable_key;
        break;
      default:
        throw new IllegalStateException("Invalid fragment type requested: " + fragmentType);
    }
  }

  @Override public void onCreatePreferences(Bundle bundle, String s) {
    DaggerManagerPeriodicComponent.builder()
        .powerManagerComponent(PowerManager.getInstance().getPowerManagerComponent())
        .build()
        .inject(this);

    findCorrectPreferences();
    addPreferencesFromResource(xmlResId);

    resolvePreferences();
    listener = (sharedPreferences, pref) -> {
      final String key = getString(manageKeyResId);
      if (pref.equals(key)) {
        // We want to enable the periodic switch only based on managed state
        presenter.setPeriodicFromPreference(key);
        presenter.setCustomPeriodicDisableTimeStateFromPreference(key, getString(periodicKeyResId),
            periodicPreference.isChecked());
        presenter.setCustomPeriodicEnableTimeStateFromPreference(key, getString(periodicKeyResId),
            periodicPreference.isChecked());
      }
    };
  }

  private void resolvePreferences() {
    periodicPreference = (SwitchPreferenceCompat) findPreference(getString(periodicKeyResId));
    presetPeriodicDisablePreference =
        (ListPreference) findPreference(getString(presetPeriodicDisableKeyResId));
    periodicDisablePreference =
        (ManagerPeriodicPreference) findPreference(getString(periodicDisableKeyResId));
    presetPeriodicEnablePreference =
        (ListPreference) findPreference(getString(presetPeriodicEnableKeyResId));
    periodicEnablePreference =
        (ManagerPeriodicPreference) findPreference(getString(periodicEnableKeyResId));
  }

  @Override public void enablePeriodicDisableTime() {
    Timber.d("Enable custom periodic disable");
    periodicDisablePreference.setEnabled(true);
  }

  @Override public void disablePeriodicDisableTime() {
    Timber.d("Disable custom periodic disable");
    periodicDisablePreference.setEnabled(false);
  }

  @Override public void enablePeriodicEnableTime() {
    Timber.d("Enable custom periodic enable");
    periodicEnablePreference.setEnabled(true);
  }

  @Override public void disablePeriodicEnableTime() {
    Timber.d("Disable custom periodic enable");
    periodicEnablePreference.setEnabled(false);
  }

  @Override public void enablePeriodic() {
    Timber.d("Enable periodic");
    periodicPreference.setEnabled(true);
  }

  @Override public void disablePeriodic() {
    Timber.d("Disable periodic");
    periodicPreference.setEnabled(false);
  }

  @Override public void onResume() {
    super.onResume();
    presenter.onResume();
    presenter.registerSharedPreferenceChangeListener(listener, getString(manageKeyResId));
  }

  @Override public void onPause() {
    super.onPause();
    presenter.onPause();
    presenter.unregisterSharedPreferenceChangeListener(listener);
  }
}
