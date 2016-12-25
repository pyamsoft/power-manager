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
import com.pyamsoft.powermanager.R;
import com.pyamsoft.powermanager.base.ManagePreferenceFragment;
import com.pyamsoft.powermanager.presenter.airplane.AirplaneManagePresenterLoader;
import com.pyamsoft.powermanager.presenter.base.ManagePreferencePresenter;
import com.pyamsoft.pydroid.app.PersistLoader;
import timber.log.Timber;

public class AirplaneManagePreferenceFragment extends ManagePreferenceFragment {

  @NonNull @Override protected PersistLoader<ManagePreferencePresenter> createPresenterLoader() {
    return new AirplaneManagePresenterLoader();
  }

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

  @Override protected boolean checkManagePermission() {
    Timber.d("Airplane checks manage permission");
    return true;
  }

  @Override protected void onShowManagePermissionNeededMessage() {
    Toast.makeText(getContext(),
        "Enable SuperUser from the Settings module to manage Airplane Mode", Toast.LENGTH_SHORT)
        .show();
  }

  @NonNull @Override protected String getModuleName() {
    return "Airplane Mode";
  }
}
