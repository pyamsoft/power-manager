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

package com.pyamsoft.powermanagerpresenter.preference.sync;

import android.support.annotation.NonNull;
import com.pyamsoft.powermanagerpresenter.Injector;
import com.pyamsoft.powermanagerpresenter.preference.CustomTimeInputPreferencePresenter;
import com.pyamsoft.powermanagerpresenter.preference.PreferenceLoader;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;

public class SyncPreferenceLoader extends PreferenceLoader {

  @SuppressWarnings("WeakerAccess") @Inject @Named("sync_custom_delay")
  Provider<CustomTimeInputPreferencePresenter> delayPresenter;

  @SuppressWarnings("WeakerAccess") @Inject @Named("sync_custom_enable")
  Provider<CustomTimeInputPreferencePresenter> enablePresenter;

  @SuppressWarnings("WeakerAccess") @Inject @Named("sync_custom_disable")
  Provider<CustomTimeInputPreferencePresenter> disablePresenter;

  public SyncPreferenceLoader() {
    Injector.get().provideComponent().plusCustomPreferenceComponent().inject(this);
  }

  @NonNull @Override protected CustomTimeInputPreferencePresenter provideDelayPresenter() {
    return delayPresenter.get();
  }

  @NonNull @Override protected CustomTimeInputPreferencePresenter provideDisablePresenter() {
    return disablePresenter.get();
  }

  @NonNull @Override protected CustomTimeInputPreferencePresenter provideEnablePresenter() {
    return enablePresenter.get();
  }
}
