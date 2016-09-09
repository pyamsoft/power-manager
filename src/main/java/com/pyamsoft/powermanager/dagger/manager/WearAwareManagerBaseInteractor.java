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

import android.content.Context;
import android.support.annotation.NonNull;
import com.birbit.android.jobqueue.JobManager;
import com.pyamsoft.powermanager.PowerManagerPreferences;
import com.pyamsoft.powermanager.app.observer.BooleanInterestObserver;
import rx.Observable;

abstract class WearAwareManagerBaseInteractor extends ManagerBaseInteractor
    implements WearAwareManagerInteractor {

  @NonNull final BooleanInterestObserver wearManageObserver;
  @NonNull final BooleanInterestObserver wearStateObserver;

  WearAwareManagerBaseInteractor(@NonNull JobManager jobManager, @NonNull Context context,
      @NonNull PowerManagerPreferences preferences, @NonNull BooleanInterestObserver manageObserver,
      @NonNull BooleanInterestObserver stateObserver,
      @NonNull BooleanInterestObserver wearManageObserver,
      @NonNull BooleanInterestObserver wearStateObserver) {
    super(jobManager, context, preferences, manageObserver, stateObserver);
    this.wearManageObserver = wearManageObserver;
    this.wearStateObserver = wearStateObserver;
  }

  @NonNull @Override public Observable<Boolean> isWearEnabled() {
    return Observable.defer(() -> Observable.just(wearStateObserver.is()));
  }

  @NonNull @Override public Observable<Boolean> isWearManaged() {
    return Observable.defer(() -> Observable.just(wearManageObserver.is()));
  }

  @Override public void cleanup() {
    wearStateObserver.unregister(getClass().getName());
  }
}
