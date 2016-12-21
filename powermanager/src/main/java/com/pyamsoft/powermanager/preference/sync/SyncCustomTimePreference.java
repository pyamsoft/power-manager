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

package com.pyamsoft.powermanager.preference.sync;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import com.pyamsoft.powermanager.preference.CustomTimeInputPreference;
import com.pyamsoft.powermanagerpresenter.preference.CustomTimeInputPreferencePresenter;
import com.pyamsoft.powermanagerpresenter.preference.sync.SyncDelayPreferenceLoader;
import com.pyamsoft.powermanagerpresenter.preference.sync.SyncDisablePreferenceLoader;
import com.pyamsoft.powermanagerpresenter.preference.sync.SyncEnablePreferenceLoader;
import java.util.Locale;

public class SyncCustomTimePreference extends CustomTimeInputPreference {

  public SyncCustomTimePreference(Context context, AttributeSet attrs, int defStyleAttr,
      int defStyleRes) {
    super(context, attrs, defStyleAttr, defStyleRes);
  }

  public SyncCustomTimePreference(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
  }

  public SyncCustomTimePreference(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  public SyncCustomTimePreference(Context context) {
    super(context);
  }

  @NonNull @Override protected String getName() {
    return "Sync";
  }

  @NonNull @Override protected CustomTimeInputPreferencePresenter getDelayPresenter() {
    return new SyncDelayPreferenceLoader().loadPersistent();
  }

  @NonNull @Override protected CustomTimeInputPreferencePresenter getPeriodicEnablePresenter() {
    return new SyncEnablePreferenceLoader().loadPersistent();
  }

  @NonNull @Override protected CustomTimeInputPreferencePresenter getPeriodicDisablePresenter() {
    return new SyncDisablePreferenceLoader().loadPersistent();
  }
}
