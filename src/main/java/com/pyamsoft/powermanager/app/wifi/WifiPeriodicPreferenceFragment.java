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

package com.pyamsoft.powermanager.app.wifi;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.pyamsoft.powermanager.R;
import com.pyamsoft.powermanager.app.base.BasePeriodPreferencePresenter;
import com.pyamsoft.powermanager.app.base.BasePeriodicPreferenceFragment;
import com.pyamsoft.powermanager.app.preference.CustomTimeInputPreference;
import com.pyamsoft.pydroid.base.PersistLoader;

public class WifiPeriodicPreferenceFragment extends BasePeriodicPreferenceFragment {

  @NonNull @Override protected PersistLoader<BasePeriodPreferencePresenter> createPresenterLoader(
      @NonNull Context context) {
    return new WifiPeriodPresenterLoader(context);
  }

  @Override protected boolean onPresetEnableTimePreferenceChanged(String presetDelay,
      @Nullable CustomTimeInputPreference customEnableTimePreference) {
    return true;
  }

  @Override protected boolean onPresetDisableTimePreferenceChanged(@NonNull String presetDelay,
      @Nullable CustomTimeInputPreference customDisableTimePreference) {
    return true;
  }

  @Override protected boolean onPeriodicPreferenceChanged(boolean periodic) {
    return true;
  }

  @Override protected int getPreferencesResId() {
    return R.xml.periodic_wifi;
  }

  @Override protected int getPeriodicKeyResId() {
    return R.string.periodic_wifi_key;
  }

  @Override protected int getPresetDisableTimeKeyResId() {
    return R.string.preset_periodic_wifi_disable_key;
  }

  @Override protected int getPresetEnableTimeKeyResId() {
    return R.string.preset_periodic_wifi_enable_key;
  }

  @Override protected int getEnableTimeKeyResId() {
    return R.string.periodic_wifi_enable_key;
  }

  @Override protected int getDisableTimeKeyResId() {
    return R.string.periodic_wifi_disable_key;
  }
}
