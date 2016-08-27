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

package com.pyamsoft.powermanager.app.sync;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.Loader;
import com.pyamsoft.powermanager.R;
import com.pyamsoft.powermanager.app.base.BaseManagePreferenceFragment;
import com.pyamsoft.powermanager.app.base.BaseManagePreferencePresenter;

public class SyncManagePreferenceFragment extends BaseManagePreferenceFragment {

  @NonNull @Override
  protected Loader<BaseManagePreferencePresenter> createPresenterLoader(Context context) {
    return new SyncManagePresenterLoader(context);
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

  @Override protected int getPreferencesResId() {
    return R.xml.manage_sync;
  }
}
