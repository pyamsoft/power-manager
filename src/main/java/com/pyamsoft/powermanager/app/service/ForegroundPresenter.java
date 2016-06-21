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

package com.pyamsoft.powermanager.app.service;

import android.app.Notification;
import android.support.annotation.NonNull;
import com.pyamsoft.powermanager.dagger.service.ForegroundInteractor;
import com.pyamsoft.pydroid.base.Presenter;
import javax.inject.Inject;

public final class ForegroundPresenter extends Presenter<ForegroundPresenter.ForegroundProvider> {

  @NonNull private final ForegroundInteractor interactor;

  @Inject public ForegroundPresenter(@NonNull ForegroundInteractor interactor) {
    this.interactor = interactor;
  }

  public final void onStartNotification() {
    final Notification notification = interactor.createNotification();
    getView().startNotificationInForeground(notification);
  }

  public final void updateWearableAction() {
    interactor.updateWearablePreferenceStatus();
  }

  public final void updateWifiAction() {
    interactor.updateWifiPreferenceStatus();
  }

  public final void updateDataAction() {
    interactor.updateDataPreferenceStatus();
  }

  public final void updateBluetoothAction() {
    interactor.updateBluetoothPreferenceStatus();
  }

  public final void updateSyncAction() {
    interactor.updateSyncPreferenceStatus();
  }

  public interface ForegroundProvider {

    void startNotificationInForeground(@NonNull Notification notification);
  }
}
