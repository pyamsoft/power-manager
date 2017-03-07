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

package com.pyamsoft.powermanager.sync.preference;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import com.pyamsoft.powermanager.Injector;
import com.pyamsoft.powermanager.uicore.preference.CustomTimeInputPreference;
import com.pyamsoft.powermanager.uicore.preference.CustomTimePreferencePresenter;
import javax.inject.Inject;
import javax.inject.Named;

public class SyncCustomTimePreference extends CustomTimeInputPreference {

  @SuppressWarnings("WeakerAccess") @Named("sync_custom_delay") @Inject
  CustomTimePreferencePresenter delayPresenter;
  @SuppressWarnings("WeakerAccess") @Named("sync_custom_enable") @Inject
  CustomTimePreferencePresenter enablePresenter;
  @SuppressWarnings("WeakerAccess") @Named("sync_custom_disable") @Inject
  CustomTimePreferencePresenter disablePresenter;

  public SyncCustomTimePreference(Context context, @StringRes int keyResId) {
    super(context, keyResId);
  }

  @NonNull @Override protected String getName() {
    return "Sync";
  }

  @Override protected void injectDependencies() {
    Injector.get().provideComponent().plusSyncPreferenceComponent().inject(this);
  }

  @NonNull @Override protected CustomTimePreferencePresenter provideEnablePresenter() {
    return enablePresenter;
  }

  @NonNull @Override protected CustomTimePreferencePresenter provideDisablePresenter() {
    return disablePresenter;
  }

  @NonNull @Override protected CustomTimePreferencePresenter provideDelayPresenter() {
    return delayPresenter;
  }
}
