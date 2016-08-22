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

package com.pyamsoft.powermanager.app.doze;

import android.support.annotation.NonNull;
import com.pyamsoft.powermanager.PowerManager;
import com.pyamsoft.powermanager.R;
import com.pyamsoft.powermanager.Singleton;
import com.pyamsoft.powermanager.app.base.BaseManagePreferenceFragment;
import com.pyamsoft.powermanager.dagger.managepreference.BaseManagePreferencePresenter;
import javax.inject.Inject;
import javax.inject.Named;

public class DozeManagePreferenceFragment extends BaseManagePreferenceFragment {

  @NonNull static final String TAG = "DozeManagePreferenceFragment";
  @Inject @Named("doze_manage_pref") BaseManagePreferencePresenter presenter;

  @Override protected void injectPresenter() {
    Singleton.Dagger.with(getContext()).plusManagePreferenceComponent().inject(this);
  }

  @NonNull @Override protected BaseManagePreferencePresenter providePresenter() {
    return presenter;
  }

  @Override protected boolean onManagePreferenceChanged(boolean b) {
    return PowerManager.hasDozePermission(getContext());
  }

  @Override protected int getManageKeyResId() {
    return R.string.manage_doze_key;
  }

  @Override protected int getPresetTimeKeyResId() {
    return R.string.preset_delay_doze_key;
  }

  @Override protected int getTimeKeyResId() {
    return R.string.doze_time_key;
  }

  @Override protected int getPreferencesResId() {
    return R.xml.manage_doze;
  }
}
