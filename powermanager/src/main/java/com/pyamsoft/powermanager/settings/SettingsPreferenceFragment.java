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

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.preference.Preference;
import android.support.v7.preference.SwitchPreferenceCompat;
import android.view.View;
import android.widget.Toast;
import com.pyamsoft.powermanager.Injector;
import com.pyamsoft.powermanager.PowerManager;
import com.pyamsoft.powermanager.R;
import com.pyamsoft.powermanager.service.ForegroundService;
import com.pyamsoft.pydroid.ui.helper.ProgressOverlay;
import com.pyamsoft.pydroid.util.AppUtil;
import com.pyamsoft.pydroid.util.CircularRevealFragmentUtil;
import javax.inject.Inject;
import timber.log.Timber;

public class SettingsPreferenceFragment extends AppBarColoringSettingsFragment {

  @NonNull public static final String TAG = "Settings";
  @SuppressWarnings("WeakerAccess") @Inject SettingsPreferencePresenter presenter;
  SwitchPreferenceCompat useRoot;
  @NonNull ProgressOverlay overlay = ProgressOverlay.empty();

  @CheckResult @NonNull public static Fragment newInstance(View view, View rootView) {
    Fragment fragment = new SettingsPreferenceFragment();
    fragment.setArguments(bundleArguments(view, rootView));
    return fragment;
  }

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    Injector.get().provideComponent().plusSettingsPreferenceComponent().inject(this);
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    CircularRevealFragmentUtil.runCircularRevealOnViewCreated(view, getArguments());

    final Preference clearDb = findPreference(getString(R.string.clear_db_key));
    clearDb.setOnPreferenceClickListener(preference -> {
      Timber.d("Clear DB onClick");
      presenter.requestClearDatabase(type -> AppUtil.guaranteeSingleDialogFragment(getActivity(),
          ConfirmationDialog.newInstance(type), "confirm_dialog"));
      return true;
    });

    useRoot = (SwitchPreferenceCompat) findPreference(getString(R.string.use_root_key));
    useRoot.setOnPreferenceChangeListener((preference, newValue) -> {
      if (newValue instanceof Boolean) {
        final boolean b = (boolean) newValue;
        if (b) {
          checkRoot(false);
          return false;
        } else {
          return true;
        }
      }
      return false;
    });
  }

  @Override public void onStart() {
    super.onStart();
    presenter.bindView(null);
    presenter.registerOnBus(type -> presenter.processClearRequest(type,
        new SettingsPreferencePresenter.ClearRequestCallback() {
          @Override public void onClearAll() {
            Timber.d("Everything is cleared, kill self");
            getActivity().getApplicationContext()
                .stopService(
                    new Intent(getContext().getApplicationContext(), ForegroundService.class));
            final ActivityManager activityManager =
                (ActivityManager) getContext().getApplicationContext()
                    .getSystemService(Context.ACTIVITY_SERVICE);
            activityManager.clearApplicationUserData();
          }

          @Override public void onClearDatabase() {
            Timber.d("Cleared the trigger database");
          }
        }));
    checkRoot(true);
  }

  void checkRoot(boolean enabled) {
    SettingsPreferencePresenter.RootCallback callback =
        new SettingsPreferencePresenter.RootCallback() {

          @Override public void onBegin() {
            overlay = ProgressOverlay.Helper.dispose(overlay);
            overlay =
                new ProgressOverlay.Builder().build(getActivity());
          }

          @Override public void onRootCallback(boolean causedByUser, boolean hasPermission,
              boolean rootEnable) {
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

          @Override public void onComplete() {
            overlay = ProgressOverlay.Helper.dispose(overlay);
          }
        };
    if (enabled) {
      presenter.checkRootEnabled(callback);
    } else {
      presenter.checkRoot(true, true, callback);
    }
  }

  @Override public void onDestroyView() {
    super.onDestroyView();
    overlay = ProgressOverlay.Helper.dispose(overlay);
  }

  @Override public void onStop() {
    super.onStop();
    presenter.unbindView();
  }

  @Override public void onResume() {
    super.onResume();
    setActionBarUpEnabled(true);
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

  @Override protected int provideAppBarColor() {
    return R.color.pink500;
  }

  @Override protected int provideStatusBarColor() {
    return R.color.pink700;
  }

  @Override protected boolean onClearAllPreferenceClicked() {
    presenter.requestClearAll(type -> AppUtil.guaranteeSingleDialogFragment(getActivity(),
        ConfirmationDialog.newInstance(type), "confirm_dialog"));
    return true;
  }
}
