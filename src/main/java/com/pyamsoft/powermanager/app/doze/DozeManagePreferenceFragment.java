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
import android.support.annotation.NonNull;
import android.widget.Toast;
import com.pyamsoft.powermanager.PowerManagerSingleInitProvider;
import com.pyamsoft.powermanager.R;
import com.pyamsoft.powermanager.app.base.ManagePreferenceFragment;
import com.pyamsoft.powermanager.app.base.ManagePreferencePresenter;
import com.pyamsoft.pydroid.app.PersistLoader;
import com.pyamsoft.pydroid.util.AppUtil;
import timber.log.Timber;

public class DozeManagePreferenceFragment extends ManagePreferenceFragment {

  @NonNull static final String TAG = "DozeManagePreferenceFragment";

  @Override protected void injectDependencies() {
    PowerManagerSingleInitProvider.get().provideComponent().plusDozeScreenComponent().inject(this);
  }

  @NonNull @Override
  protected PersistLoader<ManagePreferencePresenter> createPresenterLoader() {
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

  @Override public void onResume() {
    super.onResume();
    onSelected();
  }

  @Override public void onPause() {
    super.onPause();
    onUnselected();
  }

  @Override protected boolean checkManagePermission() {
    Timber.d("Doze checks manage permission");
    return true;
  }

  @Override protected void onShowManagePermissionNeededMessage() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
      AppUtil.guaranteeSingleDialogFragment(getFragmentManager(), new DozeExplanationDialog(),
          "doze_explain");
    } else {
      Toast.makeText(getContext(), "Doze is only available on Android M (23) and hider",
          Toast.LENGTH_SHORT).show();
    }
  }
}
