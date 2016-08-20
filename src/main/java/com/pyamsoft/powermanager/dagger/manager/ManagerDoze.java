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
import android.support.annotation.Nullable;
import com.pyamsoft.powermanager.app.manager.ExclusiveManager;
import javax.inject.Inject;
import rx.Scheduler;
import timber.log.Timber;

final class ManagerDoze extends ManagerBase implements ExclusiveManager {

  @NonNull private final ExclusiveManagerInteractor interactor;

  @Inject ManagerDoze(@NonNull ExclusiveManagerInteractor interactor,
      @NonNull Scheduler subscribeScheduler, @NonNull Scheduler observerScheduler) {
    super(interactor, subscribeScheduler, observerScheduler);
    this.interactor = interactor;
  }

  @Override public void queueExclusiveSet(@Nullable NonExclusiveCallback callback) {
    queueSet();
    if (interactor.isExclusive()) {
      Timber.d("ManagerDoze is exclusive");
    } else {
      Timber.d("ManagerDoze is not exclusive");
      if (callback == null) {
        Timber.e("Callback is null but ManagerDoze is not exclusive");
      } else {
        callback.call();
      }
    }
  }

  @Override
  public void queueExclusiveUnset(boolean deviceCharging, @Nullable NonExclusiveCallback callback) {
    queueUnset(deviceCharging);
    if (interactor.isExclusive()) {
      Timber.d("ManagerDoze is exclusive");
    } else {
      Timber.d("ManagerDoze is not exclusive");
      if (callback == null) {
        Timber.e("Callback is null but ManagerDoze is not exclusive");
      } else {
        callback.call();
      }
    }
  }
}
