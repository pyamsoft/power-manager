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

package com.pyamsoft.powermanager.base.logger;

import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import rx.Observable;

interface LoggerInteractor {

  @NonNull String AIRPLANE_LOG_ID = "AIRPLANE";
  @NonNull String BLUETOOTH_LOG_ID = "BLUETOOTH";
  @NonNull String DATA_LOG_ID = "DATA";
  @NonNull String DOZE_LOG_ID = "DOZE";
  @NonNull String MANAGER_LOG_ID = "MANAGER";
  @NonNull String SYNC_LOG_ID = "SYNC";
  @NonNull String TRIGGER_LOG_ID = "TRIGGER";
  @NonNull String WIFI_LOG_ID = "WIFI";

  @NonNull @CheckResult Observable<Boolean> isLoggingEnabled();

  @NonNull @CheckResult Observable<Boolean> deleteLog();

  @NonNull @CheckResult Observable<String> getLogContents();

  @NonNull @CheckResult Observable<Boolean> appendToLog(@NonNull String message);

  @NonNull @CheckResult String getLogId();
}
