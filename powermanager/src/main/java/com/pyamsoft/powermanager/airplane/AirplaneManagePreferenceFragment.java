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

package com.pyamsoft.powermanager.airplane;

import android.support.annotation.NonNull;
import android.widget.Toast;
import com.pyamsoft.powermanager.Injector;
import com.pyamsoft.powermanager.R;
import com.pyamsoft.powermanager.uicore.ManagePreferenceFragment;
import javax.inject.Inject;
import javax.inject.Named;
import timber.log.Timber;

public class AirplaneManagePreferenceFragment extends ManagePreferenceFragment {

  @NonNull private static final String TAG = "AirplaneManagePreferenceFragment";
  @Inject @Named("airplane_manage_pref") ManagePreferencePresenter presenter;

  @Override protected int getManageKeyResId() {
    return R.string.manage_airplane_key;
  }

  @Override protected int getPresetTimeKeyResId() {
    return R.string.preset_delay_airplane_key;
  }

  @Override protected int getTimeKeyResId() {
    return R.string.airplane_time_key;
  }

  @Override protected int getIgnoreChargingKey() {
    return R.string.ignore_charging_airplane_key;
  }

  @Override protected int getPreferencesResId() {
    return R.xml.manage_airplane;
  }

  @NonNull @Override protected String getPresenterKey() {
    return TAG + "key_presenter";
  }

  @Override protected boolean checkManagePermission() {
    Timber.d("Airplane checks manage permission");
    return true;
  }

  @Override protected void onShowManagePermissionNeededMessage() {
    Toast.makeText(getContext(),
        "Enable SuperUser from the Settings module to manage Airplane Mode", Toast.LENGTH_SHORT)
        .show();
  }

  @NonNull @Override protected ManagePreferencePresenter providePresenter() {
    return presenter;
  }

  @Override protected void injectDependencies() {
    Injector.get().provideComponent().plusAirplaneScreenComponent().inject(this);
  }

  @NonNull @Override protected String getModuleName() {
    return "Airplane Mode";
  }
}
