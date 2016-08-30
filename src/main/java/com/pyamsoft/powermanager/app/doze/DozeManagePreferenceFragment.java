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

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.Loader;
import com.pyamsoft.powermanager.PowerManager;
import com.pyamsoft.powermanager.R;
import com.pyamsoft.powermanager.app.base.BaseManagePreferenceFragment;
import com.pyamsoft.powermanager.app.base.BaseManagePreferencePresenter;
import com.pyamsoft.pydroid.util.AppUtil;

public class DozeManagePreferenceFragment extends BaseManagePreferenceFragment {

  @NonNull static final String TAG = "DozeManagePreferenceFragment";

  @Override protected boolean onManagePreferenceChanged(boolean b) {
    if (b) {
      final boolean hasPermission = PowerManager.hasDozePermission(getContext());
      if (!hasPermission) {
        AppUtil.guaranteeSingleDialogFragment(getFragmentManager(), new DozeExplanationDialog(),
            "doze_explain");
      }
      return hasPermission;
    } else {
      return true;
    }
  }

  @NonNull @Override
  protected Loader<BaseManagePreferencePresenter> createPresenterLoader(Context context) {
    return new DozePresenterLoader(context);
  }

  @Override protected int getManageKeyResId() {
    return R.string.manage_doze_key;
  }

  @Override protected int getPresetTimeKeyResId() {
    return R.string.doze_time_key;
  }

  @Override protected int getTimeKeyResId() {
    return R.string.preset_delay_doze_key;
  }

  @Override protected int getPreferencesResId() {
    return R.xml.manage_doze;
  }
}
