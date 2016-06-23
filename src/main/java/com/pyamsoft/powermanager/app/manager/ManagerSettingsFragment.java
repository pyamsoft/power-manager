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
import android.support.v7.preference.CheckBoxPreference;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.SwitchPreferenceCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.pyamsoft.powermanager.PowerManager;
import com.pyamsoft.powermanager.R;
import com.pyamsoft.powermanager.app.manager.custom.ManagerDelayPreference;
import com.pyamsoft.powermanager.app.manager.custom.ManagerPeriodicPreference;
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
  @Inject ManagerSettingsPresenter presenter;

  private SwitchPreferenceCompat managePreference;
  private ListPreference presetDelayPreference;
  private ManagerDelayPreference delayPreference;

  private CheckBoxPreference periodicPreference;
  private ListPreference presetPeriodicDisablePreference;
  private ManagerPeriodicPreference periodicDisablePreference;
  private ListPreference presetPeriodicEnablePreference;
  private ManagerPeriodicPreference periodicEnablePreference;

  @XmlRes private int xmlResId;
  @StringRes private int manageKeyResId;
  @StringRes private int timeKeyResId;
  @StringRes private int presetTimeKeyResId;
  @StringRes private int periodicKeyResId;
  @StringRes private int periodicDisableKeyResId;
  @StringRes private int presetPeriodicDisableKeyResId;
  @StringRes private int periodicEnableKeyResId;
  @StringRes private int presetPeriodicEnableKeyResId;
  private SharedPreferences.OnSharedPreferenceChangeListener listener;

  @CheckResult @NonNull public static ManagerSettingsFragment newInstance(@NonNull String type) {
    final Bundle args = new Bundle();
    final ManagerSettingsFragment fragment = new ManagerSettingsFragment();
    args.putString(FRAGMENT_TYPE, type);
    fragment.setArguments(args);
    return fragment;
  }

  @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    presenter.bindView(this);
    delayPreference.bindView();
    periodicDisablePreference.bindView();
    periodicEnablePreference.bindView();
    return super.onCreateView(inflater, container, savedInstanceState);
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    managePreference.setOnPreferenceChangeListener((preference, o) -> {
      if (o instanceof Boolean) {
        final boolean checked = (boolean) o;

        // Disable delay custom when unchecked
        // Enable delay custom when checked and custom delay time
        final String presetDelay = presetDelayPreference.getValue();
        final long delayTime = Long.parseLong(presetDelay);
        if (!checked || delayTime == -1) {
          presenter.updateCustomDelayTimeView(checked);
        }

        // Disable delay custom when unchecked
        // Enable delay custom when checked and custom delay time and periodic checked
        final boolean periodicChecked = periodicPreference.isChecked();
        final String presetDisable = presetPeriodicDisablePreference.getValue();
        final long disableTime = Long.parseLong(presetDisable);
        if (!checked || disableTime == -1 && periodicChecked) {
          presenter.updateCustomPeriodicDisableTimeView(checked);
        }

        final String presetEnable = presetPeriodicEnablePreference.getValue();
        final long enableTime = Long.parseLong(presetEnable);
        if (!checked || enableTime == -1 && periodicChecked) {
          presenter.updateCustomPeriodicEnableTimeView(checked);
        }

        // Last update the notification
        presenter.updateNotificationOnManageStateChange();
        return true;
      }
      return false;
    });

    presetDelayPreference.setOnPreferenceChangeListener((preference, o) -> {
      if (o instanceof String) {
        final String string = (String) o;
        final long time = Long.parseLong(string);

        if (time != -1) {
          delayPreference.updateTime(time);
        }

        presenter.updateCustomDelayTimeView(time == -1 && managePreference.isChecked());
        return true;
      }
      return false;
    });

    periodicPreference.setOnPreferenceChangeListener((preference, o) -> {
      if (o instanceof Boolean) {
        final boolean checked = (boolean) o;

        // Disable delay custom when unchecked
        // Enable delay custom when checked and custom delay time and periodic checked
        final boolean manageChecked = managePreference.isChecked();
        final String presetDisable = presetPeriodicDisablePreference.getValue();
        final long disableTime = Long.parseLong(presetDisable);
        if (!checked || disableTime == -1 && manageChecked) {
          presenter.updateCustomPeriodicDisableTimeView(checked);
        }

        final String presetEnable = presetPeriodicEnablePreference.getValue();
        final long enableTime = Long.parseLong(presetEnable);
        if (!checked || enableTime == -1 && manageChecked) {
          presenter.updateCustomPeriodicEnableTimeView(checked);
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

        presenter.updateCustomPeriodicDisableTimeView(
            time == -1 && managePreference.isChecked() && periodicPreference.isChecked());
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

        presenter.updateCustomPeriodicEnableTimeView(
            time == -1 && managePreference.isChecked() && periodicPreference.isChecked());
        return true;
      }
      return false;
    });

    presenter.setCustomDelayTimeStateFromPreference(getString(manageKeyResId),
        managePreference.isChecked());

    presenter.setCustomPeriodicDisableTimeStateFromPreference(getString(periodicKeyResId),
        periodicPreference.isChecked() && managePreference.isChecked());

    presenter.setCustomPeriodicEnableTimeStateFromPreference(getString(periodicKeyResId),
        periodicPreference.isChecked() && managePreference.isChecked());
  }

  @Override public void onDestroyView() {
    super.onDestroyView();
    delayPreference.unbindView();
    periodicDisablePreference.unbindView();
    periodicEnablePreference.unbindView();
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
        periodicKeyResId = R.string.periodic_wifi_key;
        periodicDisableKeyResId = R.string.periodic_wifi_disable_key;
        presetPeriodicDisableKeyResId = R.string.preset_periodic_wifi_disable_key;
        periodicEnableKeyResId = R.string.periodic_wifi_enable_key;
        presetPeriodicEnableKeyResId = R.string.preset_periodic_wifi_enable_key;
        break;
      case TYPE_DATA:
        Timber.d("Manage fragment for Data");
        xmlResId = R.xml.manage_data;
        manageKeyResId = R.string.manage_data_key;
        timeKeyResId = R.string.data_time_key;
        presetTimeKeyResId = R.string.preset_delay_data_key;
        periodicKeyResId = R.string.periodic_data_key;
        periodicDisableKeyResId = R.string.periodic_data_disable_key;
        presetPeriodicDisableKeyResId = R.string.preset_periodic_data_disable_key;
        periodicEnableKeyResId = R.string.periodic_data_enable_key;
        presetPeriodicEnableKeyResId = R.string.preset_periodic_data_enable_key;
        break;
      case TYPE_BLUETOOTH:
        Timber.d("Manage fragment for Bluetooth");
        xmlResId = R.xml.manage_bluetooth;
        manageKeyResId = R.string.manage_bluetooth_key;
        timeKeyResId = R.string.bluetooth_time_key;
        presetTimeKeyResId = R.string.preset_delay_bluetooth_key;
        periodicKeyResId = R.string.periodic_bluetooth_key;
        periodicDisableKeyResId = R.string.periodic_bluetooth_disable_key;
        presetPeriodicDisableKeyResId = R.string.preset_periodic_bluetooth_disable_key;
        periodicEnableKeyResId = R.string.periodic_bluetooth_enable_key;
        presetPeriodicEnableKeyResId = R.string.preset_periodic_bluetooth_enable_key;
        break;
      case TYPE_SYNC:
        Timber.d("Manage fragment for Sync");
        xmlResId = R.xml.manage_sync;
        manageKeyResId = R.string.manage_sync_key;
        timeKeyResId = R.string.sync_time_key;
        presetTimeKeyResId = R.string.preset_delay_sync_key;
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
    DaggerManagerSettingsComponent.builder()
        .powerManagerComponent(PowerManager.getInstance().getPowerManagerComponent())
        .build()
        .inject(this);

    findCorrectPreferences();
    addPreferencesFromResource(xmlResId);

    resolvePreferences();
    listener = (sharedPreferences, pref) -> {
      final String key = getString(manageKeyResId);
      if (pref.equals(key)) {
        presenter.setManagedFromPreference(key);
      }
    };
  }

  private void resolvePreferences() {
    managePreference = (SwitchPreferenceCompat) findPreference(getString(manageKeyResId));
    presetDelayPreference = (ListPreference) findPreference(getString(presetTimeKeyResId));
    delayPreference = (ManagerDelayPreference) findPreference(getString(timeKeyResId));
    periodicPreference = (CheckBoxPreference) findPreference(getString(periodicKeyResId));
    presetPeriodicDisablePreference =
        (ListPreference) findPreference(getString(presetPeriodicDisableKeyResId));
    periodicDisablePreference =
        (ManagerPeriodicPreference) findPreference(getString(periodicDisableKeyResId));
    presetPeriodicEnablePreference =
        (ListPreference) findPreference(getString(presetPeriodicEnableKeyResId));
    periodicEnablePreference =
        (ManagerPeriodicPreference) findPreference(getString(periodicEnableKeyResId));
  }

  @Override public void enableCustomDelayTime() {
    Timber.d("Enable custom delay");
    delayPreference.setEnabled(true);
  }

  @Override public void disableCustomDelayTime() {
    Timber.d("Disable custom delay");
    delayPreference.setEnabled(false);
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

  @Override public void enableManaged() {
    Timber.d("Enable managed");
    managePreference.setChecked(true);
  }

  @Override public void disableManaged() {
    Timber.d("Disable managed");
    managePreference.setChecked(false);
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
