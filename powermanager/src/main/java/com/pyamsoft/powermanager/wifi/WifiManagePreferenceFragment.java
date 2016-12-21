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

package com.pyamsoft.powermanager.wifi;

import android.support.annotation.NonNull;
import com.pyamsoft.powermanager.R;
import com.pyamsoft.powermanager.base.ManagePreferenceFragment;
import com.pyamsoft.powermanagerpresenter.base.ManagePreferencePresenter;
import com.pyamsoft.powermanagerpresenter.wifi.WifiManagePresenterLoader;
import com.pyamsoft.pydroid.app.PersistLoader;

public class WifiManagePreferenceFragment extends ManagePreferenceFragment {

  @NonNull @Override protected PersistLoader<ManagePreferencePresenter> createPresenterLoader() {
    return new WifiManagePresenterLoader();
  }

  @Override protected int getManageKeyResId() {
    return R.string.manage_wifi_key;
  }

  @Override protected int getPresetTimeKeyResId() {
    return R.string.preset_delay_wifi_key;
  }

  @Override protected int getTimeKeyResId() {
    return R.string.wifi_time_key;
  }

  @Override protected int getIgnoreChargingKey() {
    return R.string.ignore_charging_wifi_key;
  }

  @Override protected int getPreferencesResId() {
    return R.xml.manage_wifi;
  }

  @NonNull @Override protected String getModuleName() {
    return "WiFi";
  }
}