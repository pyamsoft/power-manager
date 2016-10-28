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

package com.pyamsoft.powermanager.dagger.job;

import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import com.pyamsoft.powermanager.app.modifier.BooleanInterestModifier;
import com.pyamsoft.powermanager.app.observer.BooleanInterestObserver;
import com.pyamsoft.powermanager.app.wrapper.JobSchedulerCompat;

public final class JobHelper {

  private JobHelper() {
    throw new RuntimeException("No instances");
  }

  @CheckResult @NonNull
  public static EnableJob createManagerEnableJob(@NonNull JobSchedulerCompat jobSchedulerCompat,
      @NonNull String tag, @NonNull BooleanInterestObserver observer,
      @NonNull BooleanInterestModifier modifier) {
    return new EnableJob(jobSchedulerCompat, tag, 100L, false, 0, 0, observer, modifier);
  }

  @CheckResult @NonNull
  public static DisableJob createManagerDisableJob(@NonNull JobSchedulerCompat jobSchedulerCompat,
      @NonNull String tag, long delayTimeInMillis, boolean periodic, long periodicEnableInSeconds,
      long periodicDisableInSeconds, @NonNull BooleanInterestObserver observer,
      @NonNull BooleanInterestModifier modifier) {
    return new DisableJob(jobSchedulerCompat, tag, delayTimeInMillis, periodic,
        periodicEnableInSeconds, periodicDisableInSeconds, observer, modifier);
  }
}
