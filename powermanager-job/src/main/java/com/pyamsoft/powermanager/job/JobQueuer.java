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

public interface JobQueuer {

  @NonNull String TRIGGER_JOB_TAG = "trigger_job";
  @NonNull String DOZE_JOB_TAG = "doze_job";
  @NonNull String AIRPLANE_JOB_TAG = "airplane_job";
  @NonNull String WIFI_JOB_TAG = "wifi_job";
  @NonNull String DATA_JOB_TAG = "data_job";
  @NonNull String BLUETOOTH_JOB_TAG = "bluetooth_job";
  @NonNull String SYNC_JOB_TAG = "sync_job";

  void cancel(@NonNull String tag);

  void queue(@NonNull JobQueuerEntry entry);

  void queueRepeating(@NonNull JobQueuerEntry entry);
}
