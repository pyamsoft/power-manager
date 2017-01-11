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

package com.pyamsoft.powermanager.doze;

import android.support.annotation.NonNull;
import com.pyamsoft.powermanager.R;
import com.pyamsoft.powermanager.uicore.PeriodPreferencePresenter;
import com.pyamsoft.powermanager.uicore.PeriodicPreferenceFragment;
import com.pyamsoft.pydroid.FuncNone;

public class DozePeriodicPreferenceFragment extends PeriodicPreferenceFragment {

  @NonNull private static final String TAG = "DozePeriodicPreferenceFragment";

  @Override protected int getPreferencesResId() {
    return R.xml.periodic_doze;
  }

  @Override protected int getPeriodicKeyResId() {
    return R.string.periodic_doze_key;
  }

  @Override protected int getPresetDisableTimeKeyResId() {
    return R.string.preset_periodic_doze_disable_key;
  }

  @Override protected int getPresetEnableTimeKeyResId() {
    return R.string.preset_periodic_doze_enable_key;
  }

  @Override protected int getEnableTimeKeyResId() {
    return R.string.periodic_doze_enable_key;
  }

  @Override protected int getDisableTimeKeyResId() {
    return R.string.periodic_doze_disable_key;
  }

  @NonNull @Override protected FuncNone<PeriodPreferencePresenter> createPresenterLoader() {
    return new DozePeriodPresenterLoader();
  }

  @NonNull @Override protected String getPresenterKey() {
    return TAG + "presenter_key";
  }

  @NonNull @Override protected String getModuleName() {
    return "Doze";
  }
}
