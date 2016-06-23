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
import android.support.v7.preference.CheckBoxPreference;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.SwitchPreferenceCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.pyamsoft.powermanager.PowerManager;
import com.pyamsoft.powermanager.R;
import com.pyamsoft.powermanager.app.manager.custom.ManagerDelayPreference;
import com.pyamsoft.powermanager.app.manager.custom.ManagerPeriodicDisablePreference;
import com.pyamsoft.powermanager.app.manager.custom.ManagerTimePreference;
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
  private ManagerPeriodicDisablePreference periodicDisablePreference;

  @XmlRes private int xmlResId;
  @StringRes private int manageKeyResId;
  @StringRes private int timeKeyResId;
  @StringRes private int presetTimeKeyResId;
  @StringRes private int periodicKeyResId;
  @StringRes private int periodicDisableKeyResId;
  @StringRes private int presetPeriodicDisableKeyResId;

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
    return super.onCreateView(inflater, container, savedInstanceState);
  }

  private void setUpCustomEditTextPreference(@NonNull ManagerTimePreference customPreference,
      @NonNull Preference presetTimePreference, @NonNull Preference enablePreference,
      @NonNull ValueWrapper<Long> presetPreferenceChangedValueWrapper,
      @NonNull ValueWrapper<Boolean> enablePreferenceChangedValueWrapper) {
    presetTimePreference.setOnPreferenceChangeListener((preference, o) -> {
      if (o instanceof String) {
        final String string = (String) o;
        final long time = Long.parseLong(string);

        if (time != -1) {
          customPreference.updateTime(time);
        }

        presetPreferenceChangedValueWrapper.run(time);
        return true;
      }
      return false;
    });

    enablePreference.setOnPreferenceChangeListener((preference, o) -> {
      if (o instanceof Boolean) {
        enablePreferenceChangedValueWrapper.run((Boolean) o);
        return true;
      }
      return false;
    });
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    setUpCustomEditTextPreference(delayPreference, presetDelayPreference, managePreference,
        new ValueWrapper<Long>() {
          @Override void run(Long param) {
            presenter.updateCustomDelayTimeView(param == -1 && managePreference.isChecked());
          }
        }, new ValueWrapper<Boolean>() {
          @Override void run(Boolean param) {
            presenter.updateCustomDelayTimeView(param);
          }
        });

    setUpCustomEditTextPreference(periodicDisablePreference, presetPeriodicDisablePreference,
        periodicPreference, new ValueWrapper<Long>() {
          @Override void run(Long param) {
            presenter.updateCustomPeriodicDisableTimeView(
                param == -1 && periodicPreference.isChecked());
          }
        }, new ValueWrapper<Boolean>() {
          @Override void run(Boolean param) {
            presenter.updateCustomPeriodicDisableTimeView(param);
          }
        });

    presenter.setCustomDelayTimeStateFromPreference(getString(manageKeyResId),
        managePreference.isChecked());

    presenter.setCustomPeriodicDisableTimeStateFromPreference(getString(periodicKeyResId),
        periodicPreference.isChecked());
  }

  @Override public void onDestroyView() {
    super.onDestroyView();
    delayPreference.unbindView();
    periodicDisablePreference.unbindView();
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
    presetDelayPreference = (ListPreference) findPreference(getString(presetTimeKeyResId));
    delayPreference = (ManagerDelayPreference) findPreference(getString(timeKeyResId));
    periodicPreference = (CheckBoxPreference) findPreference(getString(periodicKeyResId));
    presetPeriodicDisablePreference =
        (ListPreference) findPreference(getString(presetPeriodicDisableKeyResId));
    periodicDisablePreference =
        (ManagerPeriodicDisablePreference) findPreference(getString(periodicDisableKeyResId));
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

  static abstract class ValueWrapper<T> {

    abstract void run(T param);
  }
}
