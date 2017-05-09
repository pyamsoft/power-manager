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

/**
 * Created by pyamsoft on 5/8/17.
 */

class InstantJobQueuerImpl extends BaseJobQueuer {

  @NonNull private final JobHandler jobHandler;

  @Inject InstantJobQueuerImpl(@NonNull JobManager jobManager, @NonNull JobHandler jobHandler) {
    super(jobManager);
    this.jobHandler = jobHandler;
  }

  @Override void runInstantJob(@NonNull String tag, @NonNull PersistableBundleCompat extras) {
    jobHandler.newRunner(() -> Boolean.FALSE).run(tag, extras);
  }
}
