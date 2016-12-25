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

package com.pyamsoft.powermanager.presenter.queuer;

import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import com.pyamsoft.powermanagermodel.QueuerType;

public interface Queuer {

  void cancel();

  @CheckResult @NonNull Queuer setType(@NonNull QueuerType queuerType);

  @CheckResult @NonNull Queuer setDelayTime(long time);

  @CheckResult @NonNull Queuer setPeriodic(boolean periodic);

  @CheckResult @NonNull Queuer setIgnoreCharging(boolean ignore);

  @CheckResult @NonNull Queuer setPeriodicEnableTime(long time);

  @CheckResult @NonNull Queuer setPeriodicDisableTime(long time);

  void queue();
}
