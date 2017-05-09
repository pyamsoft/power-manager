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
import com.evernote.android.job.JobManager;
import com.evernote.android.job.util.support.PersistableBundleCompat;
import javax.inject.Inject;

class DelayedJobQueuerImpl extends BaseJobQueuer {

  @Inject DelayedJobQueuerImpl(@NonNull JobManager jobManager) {
    super(jobManager);
  }

  @Override void runInstantJob(@NonNull String tag, @NonNull PersistableBundleCompat extras) {
    throw new RuntimeException(
        tag + ": Cannot schedule instant jobs with DelayedJobQueuerImpl, use InstantJobQueuerImpl");
  }
}
