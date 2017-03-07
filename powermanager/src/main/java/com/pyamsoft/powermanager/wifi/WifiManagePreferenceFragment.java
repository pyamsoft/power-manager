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
import com.pyamsoft.powermanager.Injector;
import com.pyamsoft.powermanager.R;
import com.pyamsoft.powermanager.uicore.ManagePreferenceFragment;
import com.pyamsoft.powermanager.uicore.ManagePreferencePresenter;
import javax.inject.Inject;
import javax.inject.Named;

public class WifiManagePreferenceFragment extends ManagePreferenceFragment {

  @NonNull private static final String TAG = "WifiManagePreferenceFragment";
  @Inject @Named("wifi_manage_pref") ManagePreferencePresenter presenter;

  @NonNull @Override protected ManagePreferencePresenter providePresenter() {
    return presenter;
  }

  @Override protected void injectDependencies() {
    Injector.get().provideComponent().plusWifiScreenComponent().inject(this);
  }

  @Override protected int provideManageKeyResId() {
    return R.string.manage_wifi_key;
  }

  @Override protected int providePresetTimeKeyResId() {
    return R.string.preset_delay_wifi_key;
  }

  @Override protected int provideTimeKeyResId() {
    return R.string.wifi_time_key;
  }

  @Override protected int provideIgnoreChargingKey() {
    return R.string.ignore_charging_wifi_key;
  }

  @Override protected int getPreferencesResId() {
    return R.xml.manage_wifi;
  }

  @NonNull @Override protected String getPresenterKey() {
    return TAG + "presenter_key";
  }

  @NonNull @Override protected String getModuleName() {
    return "WiFi";
  }
}
