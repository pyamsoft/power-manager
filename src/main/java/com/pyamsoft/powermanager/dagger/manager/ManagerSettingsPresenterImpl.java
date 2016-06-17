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

package com.pyamsoft.powermanager.dagger.manager;

import android.support.annotation.NonNull;
import com.pyamsoft.powermanager.app.manager.ManagerSettingsPresenter;
import com.pyamsoft.pydroid.base.PresenterImpl;
import javax.inject.Inject;

final class ManagerSettingsPresenterImpl extends PresenterImpl<ManagerSettingsPresenter.ManagerView>
    implements ManagerSettingsPresenter {

  @NonNull private final ManagerSettingsInteractor interactor;

  @Inject public ManagerSettingsPresenterImpl(@NonNull ManagerSettingsInteractor interactor) {
    this.interactor = interactor;
  }

  @Override public void setDelayTime(@NonNull String key, long time) {
    interactor.setDelayTime(key, time);
  }

  @Override public long getDelayTime(@NonNull String key) {
    return interactor.getDelayTime(key);
  }
}
