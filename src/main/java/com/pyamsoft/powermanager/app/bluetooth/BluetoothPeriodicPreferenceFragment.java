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

package com.pyamsoft.powermanager.app.bluetooth;

import android.content.Context;
import android.support.annotation.NonNull;
import com.pyamsoft.powermanager.R;
import com.pyamsoft.powermanager.app.base.BasePeriodPreferencePresenter;
import com.pyamsoft.powermanager.app.base.BasePeriodicPreferenceFragment;
import com.pyamsoft.pydroid.base.PersistLoader;

public class BluetoothPeriodicPreferenceFragment extends BasePeriodicPreferenceFragment {

  @NonNull @Override
  protected PersistLoader<BasePeriodPreferencePresenter> createPresenterLoader(@NonNull Context context) {
    return new BluetoothPeriodPresenterLoader(context);
  }

  @Override protected int getPreferencesResId() {
    return R.xml.periodic_bluetooth;
  }

  @Override protected int getPeriodicKeyResId() {
    return R.string.periodic_bluetooth_key;
  }

  @Override protected int getPresetDisableTimeKeyResId() {
    return R.string.preset_periodic_bluetooth_disable_key;
  }

  @Override protected int getPresetEnableTimeKeyResId() {
    return R.string.preset_periodic_bluetooth_enable_key;
  }

  @Override protected int getEnableTimeKeyResId() {
    return R.string.periodic_bluetooth_enable_key;
  }

  @Override protected int getDisableTimeKeyResId() {
    return R.string.periodic_bluetooth_disable_key;
  }
}
