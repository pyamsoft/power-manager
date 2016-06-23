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

  public final void setCustomPeriodicDisableTimeStateFromPreference(@NonNull String key,
      boolean isPeriodic) {
    final boolean customTime = interactor.isCustomPeriodicDisableTime(key);
    updateCustomPeriodicDisableTimeView(customTime && isPeriodic);
  }

  public final void updateCustomPeriodicDisableTimeView(boolean newState) {
    if (newState) {
      getView().enablePeriodicDisableTime();
    } else {
      getView().disablePeriodicDisableTime();
    }
  }

  public final void setCustomPeriodicEnableTimeStateFromPreference(@NonNull String key,
      boolean isPeriodic) {
    final boolean customTime = interactor.isCustomPeriodicEnableTime(key);
    updateCustomPeriodicEnableTimeView(customTime && isPeriodic);
  }

  public final void updateCustomPeriodicEnableTimeView(boolean newState) {
    if (newState) {
      getView().enablePeriodicEnableTime();
    } else {
      getView().disablePeriodicEnableTime();
    }
  }

  public interface ManagerSettingsView {

    void enableCustomDelayTime();

    void disableCustomDelayTime();

    void enablePeriodicDisableTime();

    void disablePeriodicDisableTime();

    void enablePeriodicEnableTime();

    void disablePeriodicEnableTime();
  }
}
