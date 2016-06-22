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

import android.content.Context;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.birbit.android.jobqueue.Job;
import com.birbit.android.jobqueue.Params;
import com.birbit.android.jobqueue.RetryConstraint;
import java.util.Arrays;
import java.util.Set;
import timber.log.Timber;

abstract class DeviceJob extends Job {

  static final int JOB_TYPE_ENABLE = 1;
  static final int JOB_TYPE_DISABLE = 2;
  static final int PRIORITY = 1;

  @NonNull private final Context appContext;
  private final int jobType;
  private final boolean originalState;
  private final boolean periodic;

  protected DeviceJob(@NonNull Context context, @NonNull Params params, int jobType,
      boolean originalState, boolean periodic) {
    super(params.setRequiresNetwork(false));
    this.appContext = context.getApplicationContext();
    this.jobType = jobType;
    this.originalState = originalState;
    this.periodic = periodic;
  }

  @CheckResult @NonNull final Context getContext() {
    return appContext;
  }

  @CheckResult final boolean isOriginalState() {
    return originalState;
  }

  @CheckResult final boolean isPeriodic() {
    return periodic;
  }

  @Override public void onRun() throws Throwable {
    Timber.d("Run job");
    switch (jobType) {
      case JOB_TYPE_ENABLE:
        Timber.d("Enable job: %d", jobType);
        enable();
        break;
      case JOB_TYPE_DISABLE:
        Timber.d("Disable job: %d", jobType);
        disable();
        break;
      default:
        Timber.e("No job specified: %d", jobType);
    }
  }

  @Override public void onAdded() {
    Timber.d("Job is Added");
  }

  @Override protected void onCancel(int cancelReason, @Nullable Throwable throwable) {
    final Set<String> tags = getTags();
    if (tags != null) {
      Timber.w("Job is cancelled %s %s", getId(), Arrays.toString(tags.toArray()));
    }
    if (throwable != null) {
      Timber.e(throwable, "JOB CANCELLED");
    }
  }

  @Override
  protected RetryConstraint shouldReRunOnThrowable(@NonNull Throwable throwable, int runCount,
      int maxRunCount) {
    Timber.w("Cancel job on retry attempt");
    return RetryConstraint.CANCEL;
  }

  protected abstract void enable();

  protected abstract void disable();
}
