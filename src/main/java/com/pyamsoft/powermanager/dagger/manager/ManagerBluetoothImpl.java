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
import com.pyamsoft.powermanager.PowerManager;
import com.pyamsoft.powermanager.app.manager.ManagerBluetooth;
import javax.inject.Inject;
import javax.inject.Named;
import timber.log.Timber;

final class ManagerBluetoothImpl extends ManagerBaseImpl implements ManagerBluetooth {

  @NonNull private final ManagerInteractor interactor;

  @Inject ManagerBluetoothImpl(@NonNull @Named("bluetooth") ManagerInteractor interactor) {
    Timber.d("new ManagerBluetooth");
    this.interactor = interactor;
  }

  @Override public void enable() {
    enable(0);
  }

  @Override public void enable(long time) {
    Timber.d("Queue Bluetooth enable");
    interactor.cancelJobs();
    PowerManager.getInstance().getJobManager().addJobInBackground(interactor.createEnableJob(time));
  }

  @Override public void disable() {
    disable(interactor.getDelayTime() * 1000);
  }

  @Override public void disable(long time) {
    Timber.d("Queue Bluetooth disable");
    interactor.cancelJobs();
    PowerManager.getInstance()
        .getJobManager()
        .addJobInBackground(interactor.createDisableJob(time));
  }

  @Override public boolean isEnabled() {
    return interactor.isEnabled();
  }

  @Override public boolean isManaged() {
    return interactor.isManaged();
  }
}
