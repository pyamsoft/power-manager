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

package com.pyamsoft.powermanager.dagger.manager.backend;

import android.support.annotation.NonNull;
import javax.inject.Inject;
import javax.inject.Named;
import rx.Scheduler;
import timber.log.Timber;

public final class ManagerWifi extends WearableManager {

  @NonNull private final WearableManagerInteractor interactor;

  @Inject ManagerWifi(@NonNull @Named("wifi") WearableManagerInteractor interactor,
      @NonNull @Named("io") Scheduler ioScheduler,
      @NonNull @Named("main") Scheduler mainScheduler) {
    super(interactor, ioScheduler, mainScheduler);
    Timber.d("new ManagerWifi");
    this.interactor = interactor;
  }

  @Override void onEnableComplete() {
    Timber.d("Enable complete");
    cleanup();
  }

  @Override void onDisableComplete() {
    Timber.d("Disable complete");
    cleanup();
  }
}