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

package com.pyamsoft.powermanager.sync;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.pyamsoft.powermanager.Injector;
import com.pyamsoft.powermanager.R;
import com.pyamsoft.powermanager.sync.preference.SyncCustomTimePreference;
import com.pyamsoft.powermanager.uicore.ManagePreferenceFragment;
import com.pyamsoft.powermanager.uicore.ManagePreferencePresenter;
import com.pyamsoft.powermanager.uicore.preference.CustomTimeInputPreference;
import javax.inject.Inject;
import javax.inject.Named;

public class SyncManagePreferenceFragment extends ManagePreferenceFragment {

  @Inject @Named("sync_manage_pref") ManagePreferencePresenter presenter;

  @NonNull @Override protected ManagePreferencePresenter providePresenter() {
    return presenter;
  }

  @Override protected void injectDependencies() {
    Injector.get().provideComponent().plusSyncScreenComponent().inject(this);
  }

  @Override protected int provideManageKeyResId() {
    return R.string.manage_sync_key;
  }

  @Override protected int provideManageDefaultValueResId() {
    return R.bool.manage_sync_default;
  }

  @Override protected int providePresetTimeKeyResId() {
    return R.string.preset_delay_sync_key;
  }

  @Override protected int providePresetTimeDefaultResId() {
    return R.string.sync_time_default;
  }

  @Override protected int providePresetEntriesResId() {
    return R.array.preset_delay_sync_names;
  }

  @Override protected int providePresetValuesResId() {
    return R.array.preset_delay_sync_values;
  }

  @Nullable @Override protected CustomTimeInputPreference provideCustomTimePreference() {
    return new SyncCustomTimePreference(getActivity(), R.string.sync_time_key);
  }

  @Override protected int provideIgnoreChargingKey() {
    return R.string.ignore_charging_sync_key;
  }

  @Override protected int provideIgnoreChargingDefaultResId() {
    return R.bool.ignore_charging_sync_default;
  }

  @NonNull @Override protected String getModuleName() {
    return "Sync";
  }
}
