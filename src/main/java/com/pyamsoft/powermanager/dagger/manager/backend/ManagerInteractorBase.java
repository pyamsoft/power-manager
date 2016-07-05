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

import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import com.birbit.android.jobqueue.TagConstraint;
import com.pyamsoft.powermanager.PowerManager;
import com.pyamsoft.powermanager.PowerManagerPreferences;
import rx.Observable;
import timber.log.Timber;

abstract class ManagerInteractorBase implements ManagerInteractor {

  private boolean originalState = false;
  @NonNull private final PowerManagerPreferences preferences;

  protected ManagerInteractorBase(@NonNull PowerManagerPreferences preferences) {
    this.preferences = preferences;
  }

  @NonNull @CheckResult public PowerManagerPreferences getPreferences() {
    return preferences;
  }

  @NonNull @CheckResult
  protected final Observable<ManagerInteractor> cancelJobs(@NonNull String tag) {
    return Observable.defer(() -> {
      Timber.d("Attempt job cancel %s", tag);
      PowerManager.getInstance().getJobManager().cancelJobs(TagConstraint.ANY, tag);
      return Observable.just(this);
    });
  }

  @Override public final void setOriginalState(boolean originalState) {
    this.originalState = originalState;
  }

  @NonNull @Override public Observable<Boolean> isOriginalState() {
    return Observable.defer(() -> Observable.just(originalState));
  }

  @CheckResult @NonNull abstract Observable<Long> getPeriodicEnableTime();

  @CheckResult @NonNull abstract Observable<Long> getPeriodicDisableTime();
}
