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
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.pyamsoft.powermanager.R;
import com.pyamsoft.powermanager.Singleton;
import com.pyamsoft.powermanager.dagger.manager.backend.ManagerDoze;
import com.pyamsoft.pydroid.base.fragment.ActionBarPreferenceFragment;
import com.pyamsoft.pydroid.util.AppUtil;
import javax.inject.Inject;
import timber.log.Timber;

public class DozeFragment extends ActionBarPreferenceFragment {

  @NonNull public static final String TAG = "DozeFragment";
  @Inject ManagerDoze managerDoze;

  @Override public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
    Singleton.Dagger.with(getContext()).plusManager().inject(this);
    addPreferencesFromResource(R.xml.doze);
  }

  @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    setActionBarUpEnabled(true);
    return super.onCreateView(inflater, container, savedInstanceState);
  }

  @Override public void onDestroyView() {
    super.onDestroyView();
    setActionBarUpEnabled(false);
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    showInfoDialogForDoze();
  }

  private void showInfoDialogForDoze() {
    if (managerDoze.isDozeAvailable()) {
      Timber.d("Display dialog about doze mode on Marshmallow");
      AppUtil.guaranteeSingleDialogFragment(getActivity(), new DozeDialog(), "force_doze");
    }
  }
}
