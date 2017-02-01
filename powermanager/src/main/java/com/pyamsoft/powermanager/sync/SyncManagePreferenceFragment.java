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

package com.pyamsoft.powermanager.sync;

import android.support.annotation.NonNull;
import com.pyamsoft.powermanager.Injector;
import com.pyamsoft.powermanager.R;
import com.pyamsoft.powermanager.uicore.ManagePreferenceFragment;
import com.pyamsoft.powermanager.uicore.ManagePreferencePresenter;
import com.pyamsoft.pydroid.FuncNone;
import javax.inject.Inject;
import javax.inject.Named;

public class SyncManagePreferenceFragment extends ManagePreferenceFragment {

  @NonNull private static final String TAG = "SyncManagePreferenceFragment";
  @Inject @Named("sync_manage_pref") ManagePreferencePresenter presenter;

  @NonNull @Override protected ManagePreferencePresenter providePresenter() {
    return presenter;
  }

  @Override protected void injectDependencies() {
    Injector.get().provideComponent().plusSyncScreenComponent().inject(this);
  }

  @Override protected int getManageKeyResId() {
    return R.string.manage_sync_key;
  }

  @Override protected int getPresetTimeKeyResId() {
    return R.string.preset_delay_sync_key;
  }

  @Override protected int getTimeKeyResId() {
    return R.string.sync_time_key;
  }

  @Override protected int getIgnoreChargingKey() {
    return R.string.ignore_charging_sync_key;
  }

  @Override protected int getPreferencesResId() {
    return R.xml.manage_sync;
  }

  @NonNull @Override protected String getPresenterKey() {
    return TAG + "presenter_key";
  }

  @NonNull @Override protected String getModuleName() {
    return "Sync";
  }
}
