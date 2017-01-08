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

package com.pyamsoft.powermanager.manager;

import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.annotation.WorkerThread;
import rx.Observable;

interface ManagerInteractor {

  void destroy();

  @NonNull @CheckResult Observable<Boolean> cancelJobs();

  @NonNull @CheckResult Observable<Boolean> isManaged();

  @CheckResult boolean isIgnoreWhileCharging();

  @NonNull @CheckResult Observable<Boolean> isOriginalStateEnabled();

  @NonNull @CheckResult Observable<Boolean> isEnabled();

  void setOriginalStateEnabled(boolean enabled);

  @WorkerThread void queueEnableJob();

  @WorkerThread void queueDisableJob();

  @CheckResult @NonNull String getJobTag();
}
