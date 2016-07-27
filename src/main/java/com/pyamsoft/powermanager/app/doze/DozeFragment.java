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

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.preference.PreferenceFragmentCompat;
import com.pyamsoft.powermanager.R;
import com.pyamsoft.powermanager.app.manager.backend.ManagerDoze;
import com.pyamsoft.pydroid.util.AppUtil;
import timber.log.Timber;

public class DozeFragment extends PreferenceFragmentCompat {

  @NonNull public static final String TAG = "DozeFragment";

  @Override public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
    addPreferencesFromResource(R.xml.doze);

    showInfoDialogForDoze();
  }

  private void showInfoDialogForDoze() {
    if (!ManagerDoze.checkDumpsysPermission(getContext()) && ManagerDoze.isDozeAvailable()) {
      Timber.d("Display dialog about doze mode on Marshmallow");
      AppUtil.guaranteeSingleDialogFragment(getActivity(), new DozeDialog(), "force_doze");
    }
  }
}
