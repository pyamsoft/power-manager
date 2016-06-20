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

package com.pyamsoft.powermanager.app.manager;

import android.support.annotation.NonNull;
import com.pyamsoft.powermanager.dagger.manager.ManagerSettingsInteractor;
import com.pyamsoft.pydroid.base.Presenter;
import javax.inject.Inject;

public final class ManagerSettingsPresenter
    extends Presenter<ManagerSettingsPresenter.ManagerSettingsView> {

  @NonNull private final ManagerSettingsInteractor interactor;

  @Inject public ManagerSettingsPresenter(@NonNull ManagerSettingsInteractor interactor) {
    this.interactor = interactor;
  }

  public final void setCustomTimeStateFromPreference(@NonNull String key, boolean isManaged) {
    final boolean customTime = interactor.isCustomTime(key);
    updateCustomTime(customTime && isManaged);
  }

  public final void updateCustomTime(boolean newState) {
    if (newState) {
      getView().enableCustomTime();
    } else {
      getView().disableCustomTime();
    }
  }

  public interface ManagerSettingsView {

    void enableCustomTime();

    void disableCustomTime();
  }
}
