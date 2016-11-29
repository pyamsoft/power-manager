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

package com.pyamsoft.powermanager.dagger.trigger;

import android.content.ContentValues;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import com.pyamsoft.powermanager.model.sql.PowerTriggerEntry;
import java.util.List;
import rx.Observable;

public interface PowerTriggerDB {

  @CheckResult @NonNull Observable<Long> insert(final @NonNull ContentValues contentValues);

  @CheckResult @NonNull Observable<Integer> update(final @NonNull ContentValues contentValues,
      final int percent);

  @NonNull @CheckResult Observable<List<PowerTriggerEntry>> queryAll();

  @NonNull @CheckResult Observable<PowerTriggerEntry> queryWithPercent(int percent);

  @CheckResult @NonNull Observable<Integer> deleteWithPercent(final int percent);

  @CheckResult @NonNull Observable<Integer> deleteAll();

  void deleteDatabase();
}
