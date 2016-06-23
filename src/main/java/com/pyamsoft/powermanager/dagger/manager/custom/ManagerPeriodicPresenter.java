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

package com.pyamsoft.powermanager.dagger.manager.custom;

import android.support.annotation.NonNull;
import com.pyamsoft.powermanager.app.manager.custom.ManagerTimePresenter;
import javax.inject.Inject;

final class ManagerPeriodicPresenter extends ManagerTimePresenter {

  @NonNull private final ManagerPeriodicInteractor interactor;

  @Inject public ManagerPeriodicPresenter(@NonNull ManagerPeriodicInteractor interactor) {
    this.interactor = interactor;
  }

  @Override public void updateTime(@NonNull String key, long time, boolean updateVisual,
      boolean updateSummary) {
    interactor.setPeriodicTime(key, time);
    updateTime(time, updateVisual, updateSummary);
  }

  @Override public void setTimeFromPreference(@NonNull String key) {
    final long time = interactor.getPeriodicTime(key);
    setTimeText(time);
  }
}