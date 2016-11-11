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

package com.pyamsoft.powermanager.dagger.logger;

import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import rx.Observable;

interface LoggerInteractor {

  @NonNull @CheckResult Observable<Boolean> isLoggingEnabled();

  @NonNull @CheckResult Observable<String> getLogContents();

  @NonNull @CheckResult Observable<Boolean> appendToLog(@NonNull String message);

  @NonNull @CheckResult String getLogType();
}
