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

package com.pyamsoft.powermanager.app.manager.manage;

import android.os.Bundle;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.annotation.XmlRes;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.SwitchPreferenceCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.pyamsoft.powermanager.R;
import com.pyamsoft.powermanager.Singleton;
import com.pyamsoft.powermanager.app.manager.ManagerSettingsPagerAdapter;
import com.pyamsoft.powermanager.dagger.manager.backend.ManagerData;
import com.pyamsoft.powermanager.app.manager.preference.ManagerDelayPreference;
import com.pyamsoft.powermanager.app.observer.InterestObserver;
import com.pyamsoft.powermanager.dagger.manager.manage.ManagerManagePresenter;
import com.pyamsoft.powermanager.dagger.observer.manage.BluetoothManageObserver;
import com.pyamsoft.powermanager.dagger.observer.manage.DataManageObserver;
import com.pyamsoft.powermanager.dagger.observer.manage.ManageObserverComponent;
import com.pyamsoft.powermanager.dagger.observer.manage.SyncManageObserver;
import com.pyamsoft.powermanager.dagger.observer.manage.WifiManageObserver;
import com.pyamsoft.pydroid.base.fragment.ActionBarPreferenceFragment;
import com.pyamsoft.pydroid.util.AppUtil;
import javax.inject.Inject;
import timber.log.Timber;

public class ManagerManageFragment extends ActionBarPreferenceFragment
    implements ManagerManageView, WifiManageObserver.View, DataManageObserver.View,
    BluetoothManageObserver.View, SyncManageObserver.View {

  @Inject ManagerManagePresenter presenter;

  private SwitchPreferenceCompat managePreference;
  private ListPreference presetDelayPreference;
  private ManagerDelayPreference delayPreference;
  private InterestObserver manageObserver;

  @XmlRes private int xmlResId;
  @StringRes private int manageKeyResId;
  @StringRes private int timeKeyResId;
  @StringRes private int presetTimeKeyResId;
  private String fragmentType;

  @CheckResult @NonNull public static ManagerManageFragment newInstance(@NonNull String type) {
    final Bundle args = new Bundle();
    final ManagerManageFragment fragment = new ManagerManageFragment();
    args.putString(ManagerSettingsPagerAdapter.FRAGMENT_TYPE, type);
    fragment.setArguments(args);
    return fragment;
  }

  private void showInfoDialogForLollipop() {
    if (!ManagerData.checkWriteSettingsPermission(getContext())
        && ManagerData.needsPermissionToToggle()
        && ManagerSettingsPagerAdapter.TYPE_DATA.equals(fragmentType)) {
      Timber.d("Display dialog about data toggle on Lollipop+");
      AppUtil.guaranteeSingleDialogFragment(getActivity(), new LollipopDataDialog(),
          "lollipop_data");
    }
  }

  @Override public void onDestroyView() {
    super.onDestroyView();
    setActionBarUpEnabled(false);
    manageObserver.unregister();
    delayPreference.unbindView();
    presenter.unbindView();
  }

  private void findCorrectPreferences() {
    final ManageObserverComponent manageObserverComponent =
        Singleton.Dagger.with(getContext()).plusManageObserver();
    fragmentType = getArguments().getString(ManagerSettingsPagerAdapter.FRAGMENT_TYPE, null);
    switch (fragmentType) {
      case ManagerSettingsPagerAdapter.TYPE_WIFI:
        Timber.d("Manage fragment for Wifi");
        manageObserver = manageObserverComponent.provideWifiManagerObserver();
        xmlResId = R.xml.manage_wifi;
        manageKeyResId = R.string.manage_wifi_key;
        timeKeyResId = R.string.wifi_time_key;
        presetTimeKeyResId = R.string.preset_delay_wifi_key;
        break;
      case ManagerSettingsPagerAdapter.TYPE_DATA:
        Timber.d("Manage fragment for Data");
        manageObserver = manageObserverComponent.provideDataManagerObserver();
        xmlResId = R.xml.manage_data;
        manageKeyResId = R.string.manage_data_key;
        timeKeyResId = R.string.data_time_key;
        presetTimeKeyResId = R.string.preset_delay_data_key;
        break;
      case ManagerSettingsPagerAdapter.TYPE_BLUETOOTH:
        Timber.d("Manage fragment for Bluetooth");
        manageObserver = manageObserverComponent.provideBluetoothManagerObserver();
        xmlResId = R.xml.manage_bluetooth;
        manageKeyResId = R.string.manage_bluetooth_key;
        timeKeyResId = R.string.bluetooth_time_key;
        presetTimeKeyResId = R.string.preset_delay_bluetooth_key;
        break;
      case ManagerSettingsPagerAdapter.TYPE_SYNC:
        Timber.d("Manage fragment for Sync");
        manageObserver = manageObserverComponent.provideSyncManagerObserver();
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
    Singleton.Dagger.with(getContext()).plusManagerManage().inject(this);

    findCorrectPreferences();
    addPreferencesFromResource(xmlResId);

    resolvePreferences();
  }

  private void resolvePreferences() {
    managePreference = (SwitchPreferenceCompat) findPreference(getString(manageKeyResId));
    presetDelayPreference = (ListPreference) findPreference(getString(presetTimeKeyResId));
    delayPreference = (ManagerDelayPreference) findPreference(getString(timeKeyResId));
  }

  @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    final View view = super.onCreateView(inflater, container, savedInstanceState);

    //noinspection unchecked
    manageObserver.setView(this);
    manageObserver.register();
    presenter.bindView(this);
    delayPreference.bindView();

    return view;
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    setActionBarUpEnabled(true);
    showInfoDialogForLollipop();

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

    presenter.setCustomDelayTimeStateFromPreference(getString(manageKeyResId),
        managePreference.isChecked());
    presenter.setManagedFromPreference(getString(manageKeyResId));
  }

  @Override public void enableCustomDelayTime() {
    Timber.d("Enable custom delay");
    delayPreference.setEnabled(true);
  }

  @Override public void disableCustomDelayTime() {
    Timber.d("Disable custom delay");
    delayPreference.setEnabled(false);
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
    presenter.resume();
  }

  @Override public void onPause() {
    super.onPause();
    presenter.pause();
  }

  @Override public void onDataManageEnabled() {
    Timber.d("Data manage enabled");
    enableManaged();
  }

  @Override public void onDataManageDisabled() {
    Timber.d("Data manage disabled");
    disableManaged();
  }

  @Override public void onSyncManageEnabled() {
    Timber.d("Sync manage enabled");
    enableManaged();
  }

  @Override public void onSyncManageDisabled() {
    Timber.d("Wifi manage disabled");
    disableManaged();
  }

  @Override public void onWifiManageEnabled() {
    Timber.d("Wifi manage enabled");
    enableManaged();
  }

  @Override public void onWifiManageDisabled() {
    Timber.d("Wifi manage disabled");
    disableManaged();
  }

  @Override public void onBluetoothManageEnabled() {
    Timber.d("Bluetooth manage enabled");
    enableManaged();
  }

  @Override public void onBluetoothManageDisabled() {
    Timber.d("Wifi manage disabled");
    disableManaged();
  }
}
