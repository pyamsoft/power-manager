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
import com.pyamsoft.powermanager.dagger.manager.ManagerManageInteractor;
import javax.inject.Inject;

public final class ManagerManagePresenter extends ManagerSettingsPresenter<ManagerManageView> {

  @NonNull private final ManagerManageInteractor interactor;

  @Inject public ManagerManagePresenter(@NonNull ManagerManageInteractor interactor) {
    super(interactor);
    this.interactor = interactor;
  }

  public final void setManagedFromPreference(@NonNull String key) {
    final boolean enabled = interactor.isManaged(key);
    if (enabled) {
      getView().enableManaged();
    } else {
      getView().disableManaged();
    }
  }

  public final void setCustomDelayTimeStateFromPreference(@NonNull String key, boolean isManaged) {
    final boolean customTime = interactor.isCustomDelayTime(key);
    updateCustomDelayTimeView(customTime && isManaged);
  }

  public final void updateCustomDelayTimeView(boolean newState) {
    if (newState) {
      getView().enableCustomDelayTime();
    } else {
      getView().disableCustomDelayTime();
    }
  }

  public final void updateNotificationOnManageStateChange() {
    interactor.updateNotificationOnManageStateChange();
  }
}
