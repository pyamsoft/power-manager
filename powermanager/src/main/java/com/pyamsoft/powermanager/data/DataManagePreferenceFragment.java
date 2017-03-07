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

package com.pyamsoft.powermanager.data;

import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.Toast;
import com.pyamsoft.powermanager.Injector;
import com.pyamsoft.powermanager.R;
import com.pyamsoft.powermanager.data.preference.DataCustomTimePreference;
import com.pyamsoft.powermanager.uicore.ManagePreferenceFragment;
import com.pyamsoft.powermanager.uicore.ManagePreferencePresenter;
import com.pyamsoft.powermanager.uicore.preference.CustomTimeInputPreference;
import javax.inject.Inject;
import javax.inject.Named;
import timber.log.Timber;

public class DataManagePreferenceFragment extends ManagePreferenceFragment {

  @Inject @Named("data_manage_pref") ManagePreferencePresenter presenter;

  @Override protected int provideManageKeyResId() {
    return R.string.manage_data_key;
  }

  @Override protected int provideManageDefaultValueResId() {
    return R.bool.manage_data_default;
  }

  @Override protected int providePresetTimeKeyResId() {
    return R.string.preset_delay_data_key;
  }

  @Override protected int providePresetTimeDefaultResId() {
    return R.string.data_time_default;
  }

  @Override protected int providePresetEntriesResId() {
    return R.array.preset_delay_data_names;
  }

  @Override protected int providePresetValuesResId() {
    return R.array.preset_delay_data_values;
  }

  @Nullable @Override protected CustomTimeInputPreference provideCustomTimePreference() {
    return new DataCustomTimePreference(getActivity());
  }

  @Override protected int provideIgnoreChargingKey() {
    return R.string.ignore_charging_data_key;
  }

  @Override protected int provideIgnoreChargingDefaultResId() {
    return R.bool.ignore_charging_data_default;
  }

  @Override protected boolean checkManagePermission() {
    Timber.d("Data checks manage permission on API > 19");
    return Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT;
  }

  @Override protected void onShowManagePermissionNeededMessage() {
    Toast.makeText(getContext(), "Enable SuperUser from the Settings module to manage Mobile Data",
        Toast.LENGTH_SHORT).show();
  }

  @NonNull @Override protected ManagePreferencePresenter providePresenter() {
    return presenter;
  }

  @Override protected void injectDependencies() {
    Injector.get().provideComponent().plusDataScreenComponent().inject(this);
  }

  @NonNull @Override protected String getModuleName() {
    return "Data";
  }
}
