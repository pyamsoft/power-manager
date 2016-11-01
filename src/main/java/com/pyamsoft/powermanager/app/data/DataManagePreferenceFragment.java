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

package com.pyamsoft.powermanager.app.data;

import android.os.Build;
import android.support.annotation.NonNull;
import android.widget.Toast;
import com.pyamsoft.powermanager.R;
import com.pyamsoft.powermanager.app.base.ManagePreferenceFragment;
import com.pyamsoft.powermanager.app.base.ManagePreferencePresenter;
import com.pyamsoft.pydroid.app.PersistLoader;
import timber.log.Timber;

public class DataManagePreferenceFragment extends ManagePreferenceFragment {

  @Override protected void injectDependencies() {

  }

  @NonNull @Override
  protected PersistLoader<ManagePreferencePresenter> createPresenterLoader() {
    return new DataManagePresenterLoader();
  }

  @Override protected int getManageKeyResId() {
    return R.string.manage_data_key;
  }

  @Override protected int getPresetTimeKeyResId() {
    return R.string.preset_delay_data_key;
  }

  @Override protected int getTimeKeyResId() {
    return R.string.data_time_key;
  }

  @Override protected int getPreferencesResId() {
    return R.xml.manage_data;
  }

  @Override protected boolean checkManagePermission() {
    Timber.d("Data checks manage permission on API > 19");
    return Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT;
  }

  @Override protected void onShowManagePermissionNeededMessage() {
    Toast.makeText(getContext(), "Needs root to manage Mobile Data", Toast.LENGTH_SHORT).show();
  }
}
