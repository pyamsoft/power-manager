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

package com.pyamsoft.powermanager.job;

import android.support.annotation.NonNull;
import com.evernote.android.job.Job;
import com.pyamsoft.powermanager.model.BooleanInterestModifier;
import com.pyamsoft.powermanager.model.BooleanInterestObserver;
import com.pyamsoft.powermanager.model.Logger;

class WifiJob extends BaseJob {

  @NonNull private final Logger logger;
  @NonNull private final BooleanInterestObserver stateObserver;
  @NonNull private final BooleanInterestModifier stateModifier;

  WifiJob(@NonNull Logger logger, @NonNull BooleanInterestObserver stateObserver,
      @NonNull BooleanInterestModifier stateModifier) {
    this.logger = logger;
    this.stateObserver = stateObserver;
    this.stateModifier = stateModifier;
  }

  @NonNull @Override Logger getLogger() {
    return logger;
  }

  @NonNull @Override BooleanInterestObserver getObserver() {
    return stateObserver;
  }

  @NonNull @Override BooleanInterestModifier getModifier() {
    return stateModifier;
  }

  @Override void inject() {
  }

  static class ManagedJob extends Job {

    @NonNull private final Logger logger;
    @NonNull private final BooleanInterestObserver stateObserver;
    @NonNull private final BooleanInterestModifier stateModifier;

    ManagedJob(@NonNull Logger logger, @NonNull BooleanInterestObserver stateObserver,
        @NonNull BooleanInterestModifier stateModifier) {
      this.logger = logger;
      this.stateObserver = stateObserver;
      this.stateModifier = stateModifier;
    }

    @NonNull @Override protected Result onRunJob(Params params) {
      new WifiJob(logger, stateObserver, stateModifier) {
        @Override boolean isStopped() {
          return isCanceled();
        }
      }.run(params.getTag(), params.getExtras());
      return Result.SUCCESS;
    }
  }
}
