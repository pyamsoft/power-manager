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

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.birbit.android.jobqueue.Job;
import com.birbit.android.jobqueue.Params;
import com.birbit.android.jobqueue.RetryConstraint;
import java.util.Arrays;
import java.util.Set;
import timber.log.Timber;

abstract class BaseJob extends Job {

  @NonNull static final String ALL_TAG = "ALL";

  BaseJob(Params params) {
    super(params.setRequiresNetwork(false).addTags(ALL_TAG));
  }

  @Override public void onAdded() {
    final Set<String> tags = getTags();
    String tagString;
    if (tags != null) {
      tagString = Arrays.toString(tags.toArray());
    } else {
      tagString = "<NO TAGS>";
    }
    Timber.w("Job is added %s %s (%d)", getId(), tagString, getDelayInMs());
  }

  @Override protected void onCancel(int cancelReason, @Nullable Throwable throwable) {
    final Set<String> tags = getTags();
    String tagString;
    if (tags != null) {
      tagString = Arrays.toString(tags.toArray());
    } else {
      tagString = "<NO TAGS>";
    }
    Timber.w("Job is cancelled %s %s (%d)", getId(), tagString, getDelayInMs());

    if (throwable != null) {
      Timber.e(throwable, "JOB CANCELLED");
    }
  }

  @Override
  protected final RetryConstraint shouldReRunOnThrowable(@NonNull Throwable throwable, int runCount,
      int maxRunCount) {
    Timber.w("Cancel job on retry attempt");
    return RetryConstraint.CANCEL;
  }
}
