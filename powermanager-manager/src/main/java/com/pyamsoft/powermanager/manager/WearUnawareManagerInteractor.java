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

package com.pyamsoft.powermanager.manager;

import android.support.annotation.NonNull;
import com.pyamsoft.powermanager.base.PowerManagerPreferences;
import com.pyamsoft.powermanager.job.JobQueuer;
import com.pyamsoft.powermanager.model.StateInterestObserver;
import io.reactivex.Observable;
import timber.log.Timber;

abstract class WearUnawareManagerInteractor extends ManagerInteractor {

  WearUnawareManagerInteractor(@NonNull JobQueuer jobQueuer,
      @NonNull PowerManagerPreferences preferences, @NonNull StateInterestObserver manageObserver,
      @NonNull StateInterestObserver stateObserver) {
    super(jobQueuer, preferences, manageObserver, stateObserver);
  }

  @NonNull @Override
  protected Observable<Boolean> accountForWearableBeforeDisable(boolean originalState) {
    return Observable.fromCallable(() -> {
      Timber.d("%s: Unaware of wearables,just pass through", getJobTag());
      return originalState;
    });
  }
}
