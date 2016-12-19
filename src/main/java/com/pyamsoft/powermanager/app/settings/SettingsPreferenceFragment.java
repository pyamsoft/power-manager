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
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.preference.Preference;
import android.support.v7.preference.SwitchPreferenceCompat;
import android.view.View;
import android.widget.Toast;
import com.pyamsoft.powermanager.R;
import com.pyamsoft.powermanager.app.service.ForegroundService;
import com.pyamsoft.pydroid.app.PersistLoader;
import com.pyamsoft.pydroid.app.fragment.ActionBarSettingsPreferenceFragment;
import com.pyamsoft.pydroid.util.AppUtil;
import com.pyamsoft.pydroid.util.PersistentCache;
import timber.log.Timber;

public class SettingsPreferenceFragment extends ActionBarSettingsPreferenceFragment
    implements SettingsPreferencePresenter.SettingsPreferenceView {

  @NonNull public static final String TAG = "SettingsPreferenceFragment";
  @NonNull private static final String KEY_PRESENTER = "key_settings_presenter";
  @SuppressWarnings("WeakerAccess") SettingsPreferencePresenter presenter;
  private long loadedKey;
  private SwitchPreferenceCompat useRoot;

  @CheckResult @NonNull SettingsPreferencePresenter getPresenter() {
    if (presenter == null) {
      throw new NullPointerException("Presenter is NULL");
    }
    return presenter;
  }

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    loadedKey = PersistentCache.get()
        .load(KEY_PRESENTER, savedInstanceState,
            new PersistLoader.Callback<SettingsPreferencePresenter>() {
              @NonNull @Override public PersistLoader<SettingsPreferencePresenter> createLoader() {
                return new SettingsPreferencePresenterLoader();
              }

              @Override
              public void onPersistentLoaded(@NonNull SettingsPreferencePresenter persist) {
                presenter = persist;
              }
            });
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    final Preference clearDb = findPreference(getString(R.string.clear_db_key));
    clearDb.setOnPreferenceClickListener(preference -> {
      Timber.d("Clear DB onClick");
      presenter.requestClearDatabase();
      return true;
    });

    useRoot = (SwitchPreferenceCompat) findPreference(getString(R.string.use_root_key));
    useRoot.setOnPreferenceChangeListener((preference, newValue) -> {
      if (newValue instanceof Boolean) {
        final boolean b = (boolean) newValue;
        if (b) {
          presenter.checkRoot(true, true);
          return false;
        } else {
          return true;
        }
      }
      return false;
    });
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
  }

  @Override public void onStop() {
    super.onStop();
    presenter.unbindView();
  }

  @Override protected int getRootViewContainer() {
    return R.id.main_container;
  }

  @NonNull @Override protected String getApplicationName() {
    return getString(R.string.app_name);
  }

  @Override protected int getPreferenceXmlResId() {
    return R.xml.preferences;
  }

  @Override public void onDestroy() {
    super.onDestroy();
    if (!getActivity().isChangingConfigurations()) {
      PersistentCache.get().unload(loadedKey);
    }
  }

  @Override public void onSaveInstanceState(Bundle outState) {
    PersistentCache.get()
        .saveKey(outState, KEY_PRESENTER, loadedKey, SettingsPreferencePresenter.class);
    super.onSaveInstanceState(outState);
  }

  @Override
  public void onRootCallback(boolean causedByUser, boolean hasPermission, boolean rootEnable) {
    if (rootEnable) {
      useRoot.setChecked(hasPermission);

      if (causedByUser && !hasPermission) {
        Toast.makeText(getContext(), "Must grant root permission via SuperUser application",
            Toast.LENGTH_SHORT).show();
      }
    } else {
      useRoot.setChecked(false);
    }
  }
}
