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

package com.pyamsoft.powermanager.app.doze;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.preference.SwitchPreferenceCompat;
import android.view.View;
import android.widget.Toast;
import com.pyamsoft.powermanager.PowerManagerSingleInitProvider;
import com.pyamsoft.powermanager.R;
import com.pyamsoft.powermanager.app.base.BaseManagePreferenceFragment;
import com.pyamsoft.powermanager.app.base.BaseManagePreferencePresenter;
import com.pyamsoft.pydroid.app.PersistLoader;
import com.pyamsoft.pydroid.util.AppUtil;
import javax.inject.Inject;
import timber.log.Timber;

public class DozeManagePreferenceFragment extends BaseManagePreferenceFragment
    implements DozeOnlyPresenter.View {

  @NonNull static final String TAG = "DozeManagePreferenceFragment";

  @Inject DozeOnlyPresenter presenter;
  private SwitchPreferenceCompat forceDoze;

  @Override protected void injectDependencies() {
    PowerManagerSingleInitProvider.get().provideComponent().plusDozeScreenComponent().inject(this);
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    forceDoze = (SwitchPreferenceCompat) findPreference(getString(R.string.manage_doze_key));
    forceDoze.setOnPreferenceClickListener(preference -> {
      final boolean b = forceDoze.isChecked();
      if (b) {
        Timber.d("Check doze permission");
        presenter.checkDozePermission();
      }
      return true;
    });
  }

  @NonNull @Override
  protected PersistLoader<BaseManagePreferencePresenter> createPresenterLoader() {
    return new DozePresenterLoader();
  }

  @Override protected int getManageKeyResId() {
    return R.string.manage_doze_key;
  }

  /**
   * Because this module has no Custom time ability, these are reversed so that the logic stays put
   */
  @Override protected int getPresetTimeKeyResId() {
    return R.string.doze_time_key;
  }

  /**
   * Because this module has no Custom time ability, these are reversed so that the logic stays put
   */
  @Override protected int getTimeKeyResId() {
    return R.string.preset_delay_doze_key;
  }

  @Override protected int getPreferencesResId() {
    return R.xml.manage_doze;
  }

  @Override public void onStart() {
    super.onStart();
    presenter.bindView(this);

    // If forceDoze is checked, make sure we actually have permission
    // It is possible to check the switch, and then back out of the fragment before the callback completes.
    if (forceDoze.isChecked()) {
      presenter.checkDozePermission();
    }
  }

  @Override public void onResume() {
    super.onResume();
    onSelected();
  }

  @Override public void onPause() {
    super.onPause();
    onUnselected();
  }

  @Override public void onStop() {
    super.onStop();
    presenter.unbindView();
  }

  @Override public void onDestroy() {
    super.onDestroy();
    presenter.destroy();
  }

  @Override public void onDozePermissionCallback(boolean hasPermission) {
    Timber.d("Has doze permission: %s", hasPermission);
    if (!hasPermission) {
      forceDoze.setChecked(false);
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        AppUtil.guaranteeSingleDialogFragment(getFragmentManager(), new DozeExplanationDialog(),
            "doze_explain");
      } else {
        Toast.makeText(getContext(), "Doze is only available on Android M (23) and hider",
            Toast.LENGTH_SHORT).show();
      }
    }
  }
}
