/*
 * Copyright 2017 Peter Kenji Yamanaka
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

package com.pyamsoft.powermanager.wear;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.pyamsoft.powermanager.Injector;
import com.pyamsoft.powermanager.R;
import com.pyamsoft.powermanager.uicore.ManagePreferenceFragment;
import com.pyamsoft.powermanager.uicore.ManagePreferencePresenter;
import com.pyamsoft.powermanager.uicore.preference.CustomTimeInputPreference;
import javax.inject.Inject;
import javax.inject.Named;

public class WearManagePreferenceFragment extends ManagePreferenceFragment {

  @Inject @Named("wear_manage_pref") ManagePreferencePresenter presenter;

  @NonNull @Override protected ManagePreferencePresenter providePresenter() {
    return presenter;
  }

  @Override protected void injectDependencies() {
    Injector.get().provideComponent().plusWearScreenComponent().inject(this);
  }

  @Override protected int provideManageKeyResId() {
    return R.string.manage_wearable_key;
  }

  /**
   * Because this module has no Custom time ability, these are reversed so that the logic stays put
   */
  @Override protected int providePresetTimeKeyResId() {
    return R.string.wearable_time_key;
  }

  @Override protected int providePresetTimeDefaultResId() {
    return R.string.wearable_time_default;
  }

  @Override protected int providePresetEntriesResId() {
    return R.array.preset_delay_wearable_names;
  }

  @Override protected int providePresetValuesResId() {
    return R.array.preset_delay_wearable_values;
  }

  @Override protected int provideManageDefaultValueResId() {
    return R.bool.manage_wearable_default;
  }

  @Nullable @Override protected CustomTimeInputPreference provideCustomTimePreference() {
    return null;
  }

  @Override protected int provideIgnoreChargingKey() {
    return 0;
  }

  @Override protected int provideIgnoreChargingDefaultResId() {
    return 0;
  }

  @NonNull @Override protected String getModuleName() {
    return "Wearable";
  }
}
