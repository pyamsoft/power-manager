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

import android.app.ActivityManager;
import android.content.Context;
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
import com.pyamsoft.powermanager.app.modifier.BooleanInterestModifier;
import com.pyamsoft.powermanager.app.observer.BooleanInterestObserver;
import com.pyamsoft.powermanager.app.receiver.BootReceiver;
import com.pyamsoft.powermanager.app.service.ForegroundService;
import com.pyamsoft.pydroid.base.app.PersistLoader;
import com.pyamsoft.pydroid.base.fragment.ActionBarSettingsPreferenceFragment;
import com.pyamsoft.pydroid.util.AppUtil;
import com.pyamsoft.pydroid.util.PersistentCache;
import javax.inject.Inject;
import javax.inject.Named;
import timber.log.Timber;

public class SettingsPreferenceFragment extends ActionBarSettingsPreferenceFragment
    implements SettingsPreferencePresenter.SettingsPreferenceView {

  @NonNull public static final String TAG = "SettingsPreferenceFragment";
  @NonNull private static final String OBS_TAG = "settings_wear_obs";
  @NonNull private static final String KEY_PRESENTER = "key_settings_presenter";
  @SuppressWarnings("WeakerAccess") Intent service;
  @SuppressWarnings("WeakerAccess") SettingsPreferencePresenter presenter;
  @Inject @Named("obs_wear_manage") BooleanInterestObserver wearObserver;
  @Inject @Named("mod_wear_manage") BooleanInterestModifier wearModifier;
  @SuppressWarnings("WeakerAccess") CheckBoxPreference wearPreference;
  private long loadedKey;

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    loadedKey = PersistentCache.load(KEY_PRESENTER, savedInstanceState,
        new PersistLoader.Callback<SettingsPreferencePresenter>() {
          @NonNull @Override public PersistLoader<SettingsPreferencePresenter> createLoader() {
            return new SettingsPreferencePresenterLoader(getContext());
          }

          @Override public void onPersistentLoaded(@NonNull SettingsPreferencePresenter persist) {
            presenter = persist;
          }
        });
  }

  @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    service = new Intent(getContext().getApplicationContext(), ForegroundService.class);
    Singleton.Dagger.with(getContext()).plusSettingsPreferenceComponent().inject(this);
    return super.onCreateView(inflater, container, savedInstanceState);
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    final Preference clearDb = findPreference(getString(R.string.clear_db_key));
    clearDb.setOnPreferenceClickListener(preference -> {
      Timber.d("Clear DB onClick");
      presenter.requestClearDatabase();
      return true;
    });

    final Preference resetAll = findPreference(getString(R.string.clear_all_key));
    resetAll.setOnPreferenceClickListener(preference -> {
      Timber.d("Reset settings onClick");
      presenter.requestClearAll();
      return true;
    });

    final Preference upgradeInfo = findPreference(getString(R.string.upgrade_info_key));
    upgradeInfo.setOnPreferenceClickListener(preference -> showChangelog());

    final SwitchPreferenceCompat showAds =
        (SwitchPreferenceCompat) findPreference(getString(R.string.adview_key));
    showAds.setOnPreferenceChangeListener((preference, newValue) -> toggleAdVisibility(newValue));

    final SwitchPreferenceCompat startBoot =
        (SwitchPreferenceCompat) findPreference(getString(R.string.boot_key));
    startBoot.setChecked(BootReceiver.isBootEnabled(getContext()));
    startBoot.setOnPreferenceClickListener(preference -> {
      final boolean currentState = BootReceiver.isBootEnabled(getContext());
      BootReceiver.setBootEnabled(getContext(), !currentState);
      return true;
    });

    final Preference notification = findPreference(getString(R.string.full_notification_key));
    notification.setOnPreferenceChangeListener((preference, newValue) -> {
      if (newValue instanceof Boolean) {
        final boolean state = (boolean) newValue;
        service.removeExtra(ForegroundService.EXTRA_NOTIFICATION);
        service.putExtra(ForegroundService.EXTRA_NOTIFICATION, state);
        getContext().getApplicationContext().startService(service);
        return true;
      }
      return false;
    });

    wearPreference = (CheckBoxPreference) findPreference(getString(R.string.manage_wearable_key));
    wearPreference.setOnPreferenceClickListener(preference -> {
      // Do this just to trigger a notification refresh
      if (wearObserver.is()) {
        wearModifier.set();
      } else {
        wearModifier.unset();
      }
      return true;
    });
  }

  @Override public void onCreatePreferences(@Nullable Bundle bundle, @Nullable String s) {
    addPreferencesFromResource(R.xml.preferences);
  }

  @Override public void showConfirmDialog(int type) {
    AppUtil.guaranteeSingleDialogFragment(getFragmentManager(),
        ConfirmationDialog.newInstance(type), "confirm_dialog");
  }

  @Override public void onClearAll() {
    Timber.d("Everything is cleared, kill self");
    getActivity().getApplicationContext()
        .stopService(new Intent(getContext().getApplicationContext(), ForegroundService.class));
    final ActivityManager activityManager = (ActivityManager) getContext().getApplicationContext()
        .getSystemService(Context.ACTIVITY_SERVICE);
    activityManager.clearApplicationUserData();
  }

  @Override public void onClearDatabase() {
    Timber.d("Cleared the trigger database");
  }

  @Override public void onStart() {
    super.onStart();
    presenter.bindView(this);

    if (wearObserver.is()) {
      wearPreference.setChecked(true);
    } else {
      wearPreference.setChecked(false);
    }

    wearObserver.register(OBS_TAG, () -> wearPreference.setChecked(true),
        () -> wearPreference.setChecked(false));
  }

  @Override public void onStop() {
    super.onStop();
    presenter.unbindView();
    wearObserver.unregister(OBS_TAG);
  }

  @Override public void onDestroy() {
    super.onDestroy();
    if (!getActivity().isChangingConfigurations()) {
      PersistentCache.unload(loadedKey);
    }
  }

  @Override public void onSaveInstanceState(Bundle outState) {
    PersistentCache.saveKey(KEY_PRESENTER, outState, loadedKey);
    super.onSaveInstanceState(outState);
  }
}
