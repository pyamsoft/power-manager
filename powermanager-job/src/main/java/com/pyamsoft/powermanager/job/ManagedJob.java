/*
 * Copyright 2017 Peter Kenji Yamanaka
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

package com.pyamsoft.powermanager.job;

import android.support.annotation.NonNull;
import com.evernote.android.job.Job;
import com.pyamsoft.pydroid.function.FuncNone;

class ManagedJob extends Job {

  @NonNull private final JobHandler jobHandler;
  @NonNull private final FuncNone<Boolean> stopper;

  ManagedJob(@NonNull JobHandler jobHandler) {
    this.jobHandler = jobHandler;
    stopper = new FuncNone<Boolean>() {
      @Override public Boolean call() {
        return null;
      }
    };
  }

  @NonNull @Override protected Result onRunJob(Params params) {
    jobHandler.newRunner(new FuncNone<Boolean>() {
      @Override public Boolean call() {
        return isCanceled() || isFinished();
      }
    }).run(params.getTag(), params.getExtras());
    return Result.SUCCESS;
  }
}
