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
import com.pyamsoft.powermanager.Injector;
import com.pyamsoft.powermanager.R;
import com.pyamsoft.powermanager.sync.preference.SyncCustomTimePreference;
import com.pyamsoft.powermanager.uicore.PeriodPreferencePresenter;
import com.pyamsoft.powermanager.uicore.PeriodicPreferenceFragment;
import com.pyamsoft.powermanager.uicore.preference.CustomTimeInputPreference;
import javax.inject.Inject;
import javax.inject.Named;

public class SyncPeriodicPreferenceFragment extends PeriodicPreferenceFragment {

  @Inject @Named("sync_period_pref") PeriodPreferencePresenter presenter;

  @NonNull @Override protected PeriodPreferencePresenter providePresenter() {
    return presenter;
  }

  @Override protected void injectDependencies() {
    Injector.get().provideComponent().plusSyncScreenComponent().inject(this);
  }

  @Override protected int providePeriodicKeyResId() {
    return R.string.periodic_sync_key;
  }

  @Override protected int providePeriodicDefaultResId() {
    return R.bool.periodic_sync_default;
  }

  @Override protected int providePresetEnableTimeKeyResId() {
    return R.string.preset_periodic_sync_enable_key;
  }

  @Override protected int provideEnableDefaultResId() {
    return R.string.periodic_sync_enable_default;
  }

  @NonNull @Override protected CustomTimeInputPreference provideCustomEnableTimePreference() {
    return new SyncCustomTimePreference(getActivity(), R.string.periodic_sync_enable_key);
  }

  @Override protected int providePresetDisableTimeKeyResId() {
    return R.string.preset_periodic_sync_disable_key;
  }

  @Override protected int provideDisableDefaultResId() {
    return R.string.periodic_sync_disable_default;
  }

  @NonNull @Override protected CustomTimeInputPreference provideCustomDisableTimePreference() {
    return new SyncCustomTimePreference(getActivity(), R.string.periodic_sync_disable_key);
  }

  @Override protected int providePresetNamesResId() {
    return R.array.preset_periodic_sync_names;
  }

  @Override protected int providePresetValuesResId() {
    return R.array.preset_periodic_sync_values;
  }

  @NonNull @Override protected String getModuleName() {
    return "Sync";
  }
}
