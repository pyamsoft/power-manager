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

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.preference.CheckBoxPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.SwitchPreferenceCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.pyamsoft.powermanager.R;
import com.pyamsoft.powermanager.Singleton;
import com.pyamsoft.powermanager.app.observer.InterestObserver;
import com.pyamsoft.powermanager.app.receiver.BootReceiver;
import com.pyamsoft.powermanager.app.service.ForegroundService;
import com.pyamsoft.powermanager.dagger.observer.manage.WearableManageObserver;
import com.pyamsoft.powermanager.dagger.settings.SettingsPresenter;
import com.pyamsoft.pydroid.base.fragment.ActionBarSettingsPreferenceFragment;
import com.pyamsoft.pydroid.util.AppUtil;
import javax.inject.Inject;
import timber.log.Timber;

public final class SettingsFragment extends ActionBarSettingsPreferenceFragment
    implements SettingsPresenter.MainSettingsView, WearableManageObserver.View {

  @NonNull public static final String TAG = "settings";
  @NonNull private final Intent batterySettingsIntent =
      new Intent(Intent.ACTION_POWER_USAGE_SUMMARY);
  @Inject SettingsPresenter presenter;
  private InterestObserver wearableObserver;
  private CheckBoxPreference wearableManage;

  @Override public void onCreatePreferences(Bundle bundle, String s) {
    Singleton.Dagger.with(getContext()).plusSettings().inject(this);
    wearableObserver =
        Singleton.Dagger.with(getContext()).plusManageObserver().provideWearableManageObserver();

    addPreferencesFromResource(R.xml.preferences);
  }

  @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    setActionBarUpEnabled(true);
    final View view = super.onCreateView(inflater, container, savedInstanceState);

    //noinspection unchecked
    wearableObserver.setView(this);
    wearableObserver.register();
    presenter.bindView(this);

    return view;
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    final Preference boot = getPreferenceScreen().findPreference(getString(R.string.boot_key));
    boot.setDefaultValue(BootReceiver.isBootEnabled(getContext()));
    boot.setOnPreferenceClickListener(preference -> {
      BootReceiver.setBootEnabled(getContext(), !BootReceiver.isBootEnabled(getContext()));
      return false;
    });

    final Preference clearDb = findPreference(getString(R.string.clear_db_key));
    clearDb.setOnPreferenceClickListener(preference -> {
      Timber.d("Clear DB onClick");
      presenter.clearDatabase();
      return true;
    });

    final Preference resetAll = findPreference(getString(R.string.clear_all_key));
    resetAll.setOnPreferenceClickListener(preference -> {
      Timber.d("Reset settings onClick");
      presenter.clearAll();
      return true;
    });

    final Preference upgradeInfo = findPreference(getString(R.string.upgrade_info_key));
    upgradeInfo.setOnPreferenceClickListener(preference -> showChangelog());

    final Preference batterySettings = findPreference(getString(R.string.battery_settings_key));
    batterySettings.setOnPreferenceClickListener(preference -> {
      getActivity().startActivity(batterySettingsIntent);
      return true;
    });

    final Preference fullNotification = findPreference(getString(R.string.full_notification_key));
    fullNotification.setOnPreferenceChangeListener((preference, o) -> {
      if (o instanceof Boolean) {
        final boolean state = (boolean) o;
        Timber.d("Full notification preference change: %s", state);
        final Intent serviceIntent = new Intent(getContext(), ForegroundService.class).putExtra(
            ForegroundService.EXTRA_NOTIFICATION, state);
        getContext().startService(serviceIntent);
        return true;
      }

      Timber.e("Could not update preference full_notification");
      return false;
    });

    wearableManage = (CheckBoxPreference) findPreference(getString(R.string.manage_wearable_key));
    wearableManage.setOnPreferenceClickListener(preference -> {
      Timber.d("Update service on preference change");
      final Intent serviceIntent = new Intent(getContext(), ForegroundService.class);
      getContext().startService(serviceIntent);
      return true;
    });

    final SwitchPreferenceCompat showAds =
        (SwitchPreferenceCompat) findPreference(getString(R.string.adview_key));
    showAds.setOnPreferenceChangeListener((preference, newValue) -> toggleAdVisibility(newValue));
  }

  @Override public void onDestroyView() {
    super.onDestroyView();
    setActionBarUpEnabled(false);
    presenter.unbindView();
    wearableObserver.unregister();
  }

  @Override public void onResume() {
    super.onResume();
    presenter.resume();
  }

  @Override public void onPause() {
    super.onPause();
    presenter.pause();
  }

  @Override public void showConfirmDialog(int type) {
    AppUtil.guaranteeSingleDialogFragment(getFragmentManager(),
        ConfirmationDialog.newInstance(type), "confirm");
  }

  @Override public void onClearAll() {
    getContext().getApplicationContext()
        .stopService(new Intent(getContext().getApplicationContext(), ForegroundService.class));
    android.os.Process.killProcess(android.os.Process.myPid());
  }

  @Override public void onClearDatabase() {
    Timber.d("Database is cleared and deleted");
  }

  @Override public void onWearableManageEnabled() {
    Timber.d("manage wearable");
    wearableManage.setChecked(true);
  }

  @Override public void onWearableManageDisabled() {
    Timber.d("unmanage wearable");
    wearableManage.setChecked(false);
  }
}
