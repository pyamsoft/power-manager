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

package com.pyamsoft.powermanager.bluetooth;

import android.support.annotation.NonNull;
import com.pyamsoft.powermanager.uicore.ManagePreferenceFragment;
import com.pyamsoft.powermanager.uicore.ManagePreferencePresenter;
import com.pyamsoft.powermanager.R;
import com.pyamsoft.pydroid.FuncNone;

public class BluetoothManagePreferenceFragment extends ManagePreferenceFragment {

  @NonNull @Override protected FuncNone<ManagePreferencePresenter> createPresenterLoader() {
    return new BluetoothManagePresenterLoader();
  }

  @Override protected int getManageKeyResId() {
    return R.string.manage_bluetooth_key;
  }

  @Override protected int getPresetTimeKeyResId() {
    return R.string.preset_delay_bluetooth_key;
  }

  @Override protected int getTimeKeyResId() {
    return R.string.bluetooth_time_key;
  }

  @Override protected int getIgnoreChargingKey() {
    return R.string.ignore_charging_bluetooth_key;
  }

  @Override protected int getPreferencesResId() {
    return R.xml.manage_bluetooth;
  }

  @NonNull @Override protected String getModuleName() {
    return "Bluetooth";
  }
}
