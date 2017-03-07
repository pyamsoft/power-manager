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

package com.pyamsoft.powermanager.doze;

import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.Toast;
import com.pyamsoft.powermanager.Injector;
import com.pyamsoft.powermanager.R;
import com.pyamsoft.powermanager.doze.preference.DozeCustomTimePreference;
import com.pyamsoft.powermanager.uicore.ManagePreferenceFragment;
import com.pyamsoft.powermanager.uicore.ManagePreferencePresenter;
import com.pyamsoft.powermanager.uicore.preference.CustomTimeInputPreference;
import com.pyamsoft.pydroid.util.AppUtil;
import javax.inject.Inject;
import javax.inject.Named;
import timber.log.Timber;

public class DozeManagePreferenceFragment extends ManagePreferenceFragment {

  @Inject @Named("doze_manage_pref") ManagePreferencePresenter presenter;

  @Override protected int provideManageKeyResId() {
    return R.string.manage_doze_key;
  }

  @Override protected int provideManageDefaultValueResId() {
    return R.bool.manage_doze_default;
  }

  @Override protected int providePresetTimeKeyResId() {
    return R.string.preset_delay_doze_key;
  }

  @Override protected int providePresetTimeDefaultResId() {
    return R.string.doze_time_default;
  }

  @Override protected int providePresetEntriesResId() {
    return R.array.preset_delay_doze_names;
  }

  @Override protected int providePresetValuesResId() {
    return R.array.preset_delay_doze_values;
  }

  @Nullable @Override protected CustomTimeInputPreference provideCustomTimePreference() {
    return new DozeCustomTimePreference(getActivity(), R.string.doze_time_key);
  }

  @Override protected int provideIgnoreChargingKey() {
    return R.string.ignore_charging_doze_key;
  }

  @Override protected int provideIgnoreChargingDefaultResId() {
    return R.bool.ignore_charging_doze_default;
  }

  @Override protected boolean checkManagePermission() {
    Timber.d("Doze checks manage permission");
    return true;
  }

  @Override protected void onShowManagePermissionNeededMessage() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
      AppUtil.guaranteeSingleDialogFragment(getActivity(), new DozeExplanationDialog(),
          "doze_explain");
    } else {
      Toast.makeText(getContext(), "Doze is only available on Android M (23) and higher",
          Toast.LENGTH_SHORT).show();
    }
  }

  @NonNull @Override protected ManagePreferencePresenter providePresenter() {
    return presenter;
  }

  @Override protected void injectDependencies() {
    Injector.get().provideComponent().plusDozeScreenComponent().inject(this);
  }

  @NonNull @Override protected String getModuleName() {
    return "Doze";
  }
}
