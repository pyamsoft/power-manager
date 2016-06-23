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
import com.pyamsoft.powermanager.dagger.manager.ManagerPeriodicInteractor;
import javax.inject.Inject;

public final class ManagerPeriodicPresenter extends ManagerSettingsPresenter<ManagerPeriodicView> {

  @NonNull private final ManagerPeriodicInteractor interactor;

  @Inject public ManagerPeriodicPresenter(@NonNull ManagerPeriodicInteractor interactor) {
    super(interactor);
    this.interactor = interactor;
  }

  public final void setPeriodicFromPreference(@NonNull String key) {
    final boolean enabled = interactor.isManaged(key);
    if (enabled) {
      getView().enablePeriodic();
    } else {
      getView().disablePeriodic();
    }
  }

  public final void setCustomPeriodicDisableTimeStateFromPreference(@NonNull String managedKey,
      @NonNull String key, boolean isPeriodic) {
    final boolean customTime = interactor.isCustomPeriodicDisableTime(key);
    updateCustomPeriodicDisableTimeView(managedKey, customTime && isPeriodic);
  }

  public final void updateCustomPeriodicDisableTimeView(@NonNull String managedKey,
      boolean newState) {
    final boolean isManaged = interactor.isManaged(managedKey);
    if (newState && isManaged) {
      getView().enablePeriodicDisableTime();
    } else {
      getView().disablePeriodicDisableTime();
    }
  }

  public final void setCustomPeriodicEnableTimeStateFromPreference(@NonNull String managedKey,
      @NonNull String key, boolean isPeriodic) {
    final boolean customTime = interactor.isCustomPeriodicEnableTime(key);
    updateCustomPeriodicEnableTimeView(managedKey, customTime && isPeriodic);
  }

  public final void updateCustomPeriodicEnableTimeView(@NonNull String managedKey,
      boolean newState) {
    final boolean isManaged = interactor.isManaged(managedKey);
    if (newState && isManaged) {
      getView().enablePeriodicEnableTime();
    } else {
      getView().disablePeriodicEnableTime();
    }
  }
}
