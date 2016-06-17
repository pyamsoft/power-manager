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
import android.support.annotation.WorkerThread;
import com.birbit.android.jobqueue.TagConstraint;
import com.pyamsoft.powermanager.PowerManager;
import timber.log.Timber;

abstract class ManagerInteractorBase implements ManagerInteractor {

  private boolean originalState = false;

  @WorkerThread protected final void cancelJobs(@NonNull String tag) {
    Timber.d("Attempt job cancel %s", tag);
    PowerManager.getInstance().getJobManager().cancelJobs(TagConstraint.ANY, tag);
  }

  @Override public final void setOriginalState(boolean originalState) {
    this.originalState = originalState;
  }

  @Override public final boolean isOriginalStateEnabled() {
    return originalState;
  }
}
