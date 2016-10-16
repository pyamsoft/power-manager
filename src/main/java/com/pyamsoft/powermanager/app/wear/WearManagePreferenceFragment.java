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

package com.pyamsoft.powermanager.app.wear;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.pyamsoft.powermanager.R;
import com.pyamsoft.powermanager.app.base.BaseManagePreferenceFragment;
import com.pyamsoft.powermanager.app.base.BaseManagePreferencePresenter;
import com.pyamsoft.powermanager.app.preference.CustomTimeInputPreference;
import com.pyamsoft.pydroid.app.PersistLoader;

public class WearManagePreferenceFragment extends BaseManagePreferenceFragment {

  @NonNull static final String TAG = "WearManagePreferenceFragment";

  @Override protected void injectDependencies() {

  }

  @Override protected boolean onManagePreferenceChanged(boolean b) {
    return true;
  }

  @Override protected boolean onPresetTimePreferenceChanged(@NonNull String presetDelay,
      @Nullable CustomTimeInputPreference customTimePreference) {
    return true;
  }

  @NonNull @Override
  protected PersistLoader<BaseManagePreferencePresenter> createPresenterLoader(Context context) {
    return new WearPresenterLoader(context);
  }

  @Override protected int getManageKeyResId() {
    return R.string.manage_wearable_key;
  }

  /**
   * Because this module has no Custom time ability, these are reversed so that the logic stays put
   */
  @Override protected int getPresetTimeKeyResId() {
    return R.string.wearable_time_key;
  }

  /**
   * Because this module has no Custom time ability, these are reversed so that the logic stays put
   */
  @Override protected int getTimeKeyResId() {
    return R.string.preset_delay_wearable_key;
  }

  @Override protected int getPreferencesResId() {
    return R.xml.manage_wear;
  }
}