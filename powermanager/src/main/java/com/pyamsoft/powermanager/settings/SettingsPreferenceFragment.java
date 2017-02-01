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

package com.pyamsoft.powermanager.settings;

import android.os.Bundle;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.preference.Preference;
import android.support.v7.preference.SwitchPreferenceCompat;
import android.view.View;
import android.widget.Toast;
import com.pyamsoft.powermanager.Injector;
import com.pyamsoft.powermanager.PowerManager;
import com.pyamsoft.powermanager.R;
import com.pyamsoft.pydroid.ui.app.fragment.ActionBarSettingsPreferenceFragment;
import com.pyamsoft.pydroid.util.AppUtil;
import javax.inject.Inject;
import timber.log.Timber;

public class SettingsPreferenceFragment extends ActionBarSettingsPreferenceFragment
    implements SettingsPreferencePresenter.RootCallback,
    SettingsPreferencePresenter.ConfirmDialogCallback {

  @NonNull public static final String TAG = "SettingsPreferenceFragment";
  @SuppressWarnings("WeakerAccess") @Inject SettingsPreferencePresenter presenter;
  private SwitchPreferenceCompat useRoot;

  @CheckResult @NonNull SettingsPreferencePresenter getPresenter() {
    if (presenter == null) {
      throw new NullPointerException("Presenter is NULL");
    }
    return presenter;
  }

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    Injector.get().provideComponent().plusSettingsPreferenceComponent().inject(this);
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    final Preference clearDb = findPreference(getString(R.string.clear_db_key));
    clearDb.setOnPreferenceClickListener(preference -> {
      Timber.d("Clear DB onClick");
      presenter.requestClearDatabase(this);
      return true;
    });

    useRoot = (SwitchPreferenceCompat) findPreference(getString(R.string.use_root_key));
    useRoot.setOnPreferenceChangeListener((preference, newValue) -> {
      if (newValue instanceof Boolean) {
        final boolean b = (boolean) newValue;
        if (b) {
          presenter.checkRoot(true, true, this);
          return false;
        } else {
          return true;
        }
      }
      return false;
    });
  }

  @Override public void showConfirmDialog(int type) {
    AppUtil.guaranteeSingleDialogFragment(getActivity(), ConfirmationDialog.newInstance(type),
        "confirm_dialog");
  }

  @Override public void onStart() {
    super.onStart();
    presenter.bindView(null);
    presenter.checkRootEnabled(this);
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
    PowerManager.getRefWatcher(this).watch(this);
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

  @Override protected boolean onClearAllPreferenceClicked() {
    presenter.requestClearAll(this);
    return true;
  }
}
