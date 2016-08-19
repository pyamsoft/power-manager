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

import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import com.pyamsoft.powermanager.dagger.base.BaseJob;
import rx.Observable;

public interface ManagerInteractor {

  void setOriginalState(boolean enabled);

  @NonNull @CheckResult Observable<Boolean> isOriginalState();

  @CheckResult @NonNull Observable<Boolean> isChargingIgnore();

  @CheckResult @NonNull Observable<Boolean> isEnabled();

  @CheckResult @NonNull Observable<Boolean> isManaged();

  @CheckResult @NonNull Observable<Boolean> isPeriodic();

  @CheckResult @NonNull Observable<Long> getDelayTime();

  @CheckResult @NonNull Observable<BaseJob> createEnableJob(long delayTime, boolean periodic);

  @CheckResult @NonNull Observable<BaseJob> createDisableJob(long delayTime, boolean periodic);

  void queueJob(@NonNull BaseJob job);

  @NonNull @CheckResult Observable<ManagerInteractor> cancelJobs();

  @CheckResult @NonNull Observable<Boolean> isDozeAvailable();

  @CheckResult @NonNull Observable<Boolean> isDozeIgnoreCharging();

  @CheckResult @NonNull Observable<Boolean> isDozeEnabled();

  @CheckResult @NonNull Observable<Boolean> isDozeExclusive();
}
