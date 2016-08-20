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

package com.pyamsoft.powermanager.dagger.manager.job;

import android.support.annotation.NonNull;

abstract class DisableJob extends ManageJob {

  protected DisableJob(@NonNull String tag, long delayInSeconds, boolean periodic,
      long periodicEnableInSeconds, long periodicDisableInSeconds) {
    super(tag, JobType.DISABLE, delayInSeconds, periodic, periodicEnableInSeconds,
        periodicDisableInSeconds);
  }
}
