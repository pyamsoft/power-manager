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

package com.pyamsoft.powermanager.trigger.db;

import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import io.reactivex.Flowable;
import java.util.List;

public interface PowerTriggerDB {

  @CheckResult @NonNull Flowable<Long> insert(@NonNull PowerTriggerEntry entry);

  @CheckResult @NonNull Flowable<Integer> updateAvailable(boolean available, int percent);

  @CheckResult @NonNull Flowable<Integer> updateEnabled(boolean enabled, int percent);

  @NonNull @CheckResult Flowable<List<PowerTriggerEntry>> queryAll();

  @NonNull @CheckResult Flowable<PowerTriggerEntry> queryWithPercent(int percent);

  @CheckResult @NonNull Flowable<Integer> deleteWithPercent(int percent);

  @CheckResult @NonNull Flowable<Integer> deleteAll();

  @CheckResult @NonNull Flowable<Boolean> deleteDatabase();
}
