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
import com.pyamsoft.pydroid.FuncNone;
import rx.Observable;

interface ManagerInteractor {

  @NonNull String DOZE_JOB_TAG = "doze_job";
  @NonNull String AIRPLANE_JOB_TAG = "airplane_job";
  @NonNull String WIFI_JOB_TAG = "wifi_job";
  @NonNull String DATA_JOB_TAG = "data_job";
  @NonNull String BLUETOOTH_JOB_TAG = "bluetooth_job";
  @NonNull String SYNC_JOB_TAG = "sync_job";

  void destroy();

  @NonNull @CheckResult Observable<Boolean> cancelJobs();

  @NonNull @CheckResult Observable<Boolean> isManaged();

  @NonNull @CheckResult FuncNone<Boolean> isIgnoreWhileCharging();

  @NonNull @CheckResult Observable<Boolean> isOriginalStateEnabled();

  @NonNull @CheckResult Observable<Boolean> isEnabled();

  void setOriginalStateEnabled(boolean enabled);

  @WorkerThread void queueEnableJob();

  @WorkerThread void queueDisableJob();

  @CheckResult @NonNull String getJobTag();
}
